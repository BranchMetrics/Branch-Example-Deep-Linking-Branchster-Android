package io.branch.branchster;

import android.net.Uri;
import androidx.annotation.Nullable;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

/**
 * Workaround for a Kotlin-only compile blocker on Branch SDK 6.0.0-beta.1.
 *
 * Branch.requestDeepLinkData(Uri, callback) is annotated @NonNull Uri, but the
 * deferred-deep-link case REQUIRES passing a null Uri (that's how the SDK is told
 * "ask the server if this device has a pending click"). The annotation is Java-only
 * metadata — javac does not enforce it — so this Java shim can call the exact same
 * method with a null argument and it will compile and run correctly. Kotlin cannot
 * do this directly because its null-safety is enforced by the compiler, not just
 * advisory like Java's @NonNull.
 *
 * Per the SDK behavior described in the migration doc: neither the callback method
 * nor its internals reject a null Uri — a null Uri produces exactly the payload the
 * deferred-match flow needs. This shim exists purely to get past the annotation, not
 * to change any runtime behavior.
 *
 * Delete this class once Branch ships the real fix (tracked as an open item against
 * EMT as of beta.1) and switch back to calling requestDeepLinkData directly from Kotlin.
 */
public final class BranchDeepLinkShim {

    private BranchDeepLinkShim() {
        // static utility, no instances
    }

    public interface Callback {
        void onResult(@Nullable org.json.JSONObject params, @Nullable BranchError error);
    }

    /**
     * Same call as Branch.getInstance().requestDeepLinkData(uri, callback), except
     * uri is permitted to be null here. Pass null for the deferred-deep-link check
     * (e.g. a cold start with no URI in the launch intent).
     */
    public static void requestDeepLinkData(@Nullable Uri uri, final Callback callback) {
        Branch.getInstance().requestDeepLinkData(uri, new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(org.json.JSONObject referringParams, BranchError error) {
                callback.onResult(referringParams, error);
            }
        });
    }
}
