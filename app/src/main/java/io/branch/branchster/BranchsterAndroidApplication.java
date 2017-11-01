package io.branch.branchster;

import android.support.multidex.MultiDexApplication;

import java.util.Random;
import java.util.UUID;

import io.branch.referral.Branch;

/**
 * Created by sahilverma on 3/7/17.
 */

public class BranchsterAndroidApplication extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
        Branch branch = Branch.getAutoInstance(this);
        Branch.enableLogging();
        Branch.setPlayStoreReferrerCheckTimeout(3000);
        branch.setIdentity(UUID.randomUUID().toString());
    }
}
