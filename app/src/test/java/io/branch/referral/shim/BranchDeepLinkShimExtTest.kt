package io.branch.referral.shim

import io.branch.branchster.BranchDeepLinkShim
import io.branch.referral.BranchError
import io.branch.referral.BranchException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * These tests exercise [requestDeepLinkDataNullable] through its injectable [requestFn] seam,
 * not the real Branch singleton — org.json isn't backed by Android here, and there's no
 * MockWebServer/Robolectric in this module, so a fake callback stands in for the SDK's
 * network call.
 */
class BranchDeepLinkShimExtTest {

    @Test
    fun `error result is wrapped in BranchException with the same BranchError`() = runTest {
        val branchError = BranchError("Trouble reaching server. Please try again in a bit", -108)

        val exception = try {
            requestDeepLinkDataNullable(uri = null) { _, callback ->
                callback.onResult(null, branchError)
            }
            fail("expected BranchException")
            error("unreachable")
        } catch (e: BranchException) {
            e
        }

        // Same info the callback API would have handed the caller.
        assertEquals(branchError, exception.branchError)
        assertEquals(branchError.message, exception.branchError?.message)
    }

    @Test
    fun `success result resumes normally without throwing`() = runTest {
        val params = JSONObject().put("+clicked_branch_link", true)

        val result = requestDeepLinkDataNullable(uri = null) { _, callback ->
            callback.onResult(params, null)
        }

        assertTrue(result.getBoolean("+clicked_branch_link"))
    }

    @Test
    fun `cancelling after launch cannot stop the request from going out`() = runTest {
        // This shim has no requestQueue_ to reach into (it's SDK-internal), so unlike the
        // official requestDeepLinkData(uri): JSONObject suspend fun, it registers no
        // invokeOnCancellation handler. requestFn is called synchronously inside
        // suspendCancellableCoroutine, i.e. before the coroutine ever suspends — so by the
        // time any cancellation can run, the "request" has already been handed off.
        var requestFnInvoked = false
        var callbackResumedAfterCancel = false
        val callbackRef = arrayOfNulls<BranchDeepLinkShim.Callback>(1)

        val job = launch {
            try {
                requestDeepLinkDataNullable(uri = null) { _, callback ->
                    requestFnInvoked = true
                    callbackRef[0] = callback // simulate the network response arriving later
                }
            } catch (e: CancellationException) {
                throw e // expected — this is just normal coroutine cancellation unwinding
            } catch (e: Exception) {
                callbackResumedAfterCancel = true
            }
        }

        // Let the launched coroutine run up to its suspension point (it calls requestFn
        // synchronously, then suspends waiting on a callback that we're holding onto).
        testScheduler.runCurrent()
        assertTrue(requestFnInvoked)

        job.cancelAndJoin()

        // A response arriving after cancellation is a no-op: onResult checks cont.isCancelled
        // and returns early, so it never resumes (and never throws) into the cancelled job.
        callbackRef[0]?.onResult(JSONObject(), null)
        assertFalse(callbackResumedAfterCancel)
    }
}
