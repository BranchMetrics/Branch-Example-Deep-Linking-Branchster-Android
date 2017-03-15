import android.app.Application;
import android.support.multidex.MultiDexApplication;

import io.branch.referral.Branch;

/**
 * Created by sahilverma on 3/7/17.
 */

public class BranchsterAndroidApplication extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
        Branch.enablePlayStoreReferrer(7000L);
        Branch.getAutoInstance(this);
    }
}
