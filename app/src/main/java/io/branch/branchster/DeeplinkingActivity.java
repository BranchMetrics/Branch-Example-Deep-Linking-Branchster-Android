package io.branch.branchster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.PrefHelper;
import io.branch.referral.util.LinkProperties;

public class DeeplinkingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_creator);
    }

    @Override protected void onStart() {
        super.onStart();
        PrefHelper.Debug("BranchSDK", "DeeplinkingActivity.onStart");
        Branch.getInstance().initSession(branchReferralInitListener1, this.getIntent().getData(), this);
    }

    public Branch.BranchUniversalReferralInitListener branchReferralInitListener1 = new Branch.BranchUniversalReferralInitListener() {
        @Override
        public void onInitFinished(BranchUniversalObject branchUniversalObject,
                                   LinkProperties linkProperties, BranchError branchError) {
            PrefHelper.Debug("BranchSDK", "DeeplinkingActivity.onStart, onInitFinished, branchUniversalObject = " + branchUniversalObject + ", linkProperties = " + linkProperties + ", branchError = " + branchError);
            startActivity(new Intent(DeeplinkingActivity.this, MonsterViewerActivity.class));
        }
    };
}
