package io.branch.branchster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import io.branch.branchster.util.MonsterPreferences;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class SplashActivity extends Activity {

    TextView txtLoading;
    int messageIndex;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get loading messages from XML definitions.
        final String[] loadingMessages = getResources().getStringArray(R.array.loading_messages);
        txtLoading = (TextView) findViewById(R.id.txtLoading);

        final Handler textLoadHandler = new Handler();
        Runnable txtLoader = new Runnable() {
            @Override
            public void run() {
                messageIndex = (messageIndex + 1) % loadingMessages.length;
                txtLoading.setText(loadingMessages[messageIndex]);
                textLoadHandler.postDelayed(this, 1000);
            }
        };
        textLoadHandler.post(txtLoader);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for any deep linked parameters
        Branch.getInstance().initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject monsterObj, LinkProperties linkProperties, BranchError error) {
                if (error == null) {
                    Log.i(TAG, "Branch init complete!");
                    Intent intent;

                    MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
                    if (monsterObj != null && monsterObj.getMetadata().containsKey("monster")) {
                        prefs.saveMonster(monsterObj);

                        intent = new Intent(SplashActivity.this, MonsterViewerActivity.class);
                        intent.putExtra(MonsterViewerActivity.MY_MONSTER_OBJ_KEY, monsterObj);

                    } else {
                        if (prefs.getMonsterName() == null) {
                            prefs.setMonsterName("");
                            intent = new Intent(SplashActivity.this, MonsterCreatorActivity.class);
                        } else {
                            // Create a default monster
                            intent = new Intent(SplashActivity.this, MonsterViewerActivity.class);
                            intent.putExtra(MonsterViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                        }
                    }
                    startActivity(intent);
                    finish();

                } else {
                    Log.e(TAG, "Branch service down = " + error.getMessage());
                    startActivity(new Intent(SplashActivity.this, MonsterCreatorActivity.class));
                    finish();
                }
            }
        }, this.getIntent().getData(), this);

    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }


}
