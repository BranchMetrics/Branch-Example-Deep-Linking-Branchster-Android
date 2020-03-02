package io.branch.branchster;

import androidx.multidex.MultiDexApplication;

import io.branch.referral.Branch;

/**
 * Created by sahilverma on 3/7/17.
 */

public class BranchsterAndroidApplication extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
        Branch.enableLogging();
//        Branch.setPlayStoreReferrerCheckTimeout(3000);
//        Branch.disableInstantDeepLinking(false);
//        branch.setIdentity(UUID.randomUUID().toString());
    }
}
