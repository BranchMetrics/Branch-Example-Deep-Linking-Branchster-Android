package io.branch.branchster;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.branch.referral.Branch;

/**
 * Created by sojanpr on 11/15/16.
 */
public class BranchsterApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
