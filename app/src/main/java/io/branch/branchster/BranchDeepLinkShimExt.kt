package io.branch.referral.shim

import android.net.Uri
import io.branch.branchster.BranchDeepLinkShim
import io.branch.referral.BranchError
import io.branch.referral.BranchException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Suspend wrapper around BranchDeepLinkShim, so call sites read the same as the
 * official Branch.getInstance().requestDeepLinkData(uri) suspend extension.
 *
 * Same catch pattern as the official API: throws BranchException on failure,
 * wrapping the BranchError.
 *
 * One real difference from the official suspend extension, worth knowing before you
 * rely on it: the official version registers
 * invokeOnCancellation { requestQueue_.remove(request) }, which de-queues the
 * request if the coroutine is cancelled before it's sent. requestQueue_ is internal
 * to the Branch SDK and not reachable from app code, so this wrapper can't replicate
 * that. Cancelling the coroutine here just detaches the caller — the underlying
 * request already handed to the SDK is not pulled back out of the queue. This
 * matches how awaitLogEvent behaves in the official API, just not how
 * requestDeepLinkData behaves there. Not expected to matter for a one-shot cold
 * start / onNewIntent call, but don't assume mid-flight cancellation cancels the
 * network request too.
 */
suspend fun requestDeepLinkDataNullable(uri: Uri?): JSONObject =
    suspendCancellableCoroutine { cont ->
        BranchDeepLinkShim.requestDeepLinkData(uri, object : BranchDeepLinkShim.Callback {
            override fun onResult(params: JSONObject?, error: BranchError?) {
                if (cont.isCancelled) return
                when {
                    error != null -> cont.resumeWithException(BranchException(error))
                    params != null -> cont.resume(params)
                    else -> cont.resume(JSONObject())
                }
            }
        })
    }
