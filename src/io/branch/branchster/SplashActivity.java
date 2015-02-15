package io.branch.branchster;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.branch.referral.Branch;
import io.branch.referral.Branch.BranchReferralInitListener;
import io.branch.referral.BranchError;

import io.fabric.sdk.android.Fabric;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SplashActivity extends Activity {

	TextView txtLoading;
	int messageIndex;
	String[] loadingMessages;
	Branch branch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Configure your Twitter Key and Secret as a string resource.
        String twitter_key = getResources().getString(R.string.twitter_key);
        String twitter_secret = getResources().getString(R.string.twitter_secret);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitter_key, twitter_secret);
		Fabric.with(this, new Twitter(authConfig));
		setContentView(R.layout.activity_splash);

        // Get loading messages from XML definitions.
		loadingMessages = getResources().getStringArray(R.array.loading_messages);
		
		txtLoading = (TextView) findViewById(R.id.txtLoading);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		branch = Branch.getInstance(this.getApplicationContext(), getResources().getString(R.string.branch_app_key));
		branch.initSession(new BranchReferralInitListener() {
			@Override
			public void onInitFinished(JSONObject referringParams, BranchError error) {
				Log.i("Branchster", "branch init complete!");
				try {
					MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
					Intent i;
					if (referringParams.has("monster")) {
						
						prefs.setMonsterName(referringParams.getString("monster_name"));
						prefs.setFaceIndex(referringParams.getInt("face_index"));
						prefs.setBodyIndex(referringParams.getInt("body_index"));
						prefs.setColorIndex(referringParams.getInt("color_index"));

						i = new Intent(getApplicationContext(), MonsterViewerActivity.class);

			        } else {

			            if (prefs.getMonsterName() == null) {
			                prefs.setMonsterName("");
			                
			                i = new Intent(getApplicationContext(), MonsterCreatorActivity.class);
			                // If no name has been saved, this user is new, so load the monster maker screen

			            } else {
			            	i = new Intent(getApplicationContext(), MonsterViewerActivity.class);
			            }
			            startActivity(i);
			        }
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, this.getIntent().getData(), this);
		
		new Thread() {
	        public void run() {
	            while (true) {
	                try {
	                    runOnUiThread(new Runnable() {

	                        @Override
	                        public void run() {
	                        	messageIndex = (messageIndex + 1) % loadingMessages.length;
	                        	txtLoading.setText(loadingMessages[messageIndex]);
	                        }
	                    });
	                    Thread.sleep(300);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		branch.closeSession();
	}

}
