package io.branch.branchster;

import android.app.Application;

import com.mparticle.MParticle;

/**
 * Created by marshall on 5/9/16.
 */
public class BranchsterApp extends Application {
    public void onCreate() {
        super.onCreate();

        MParticle.start(this);
    }
}
