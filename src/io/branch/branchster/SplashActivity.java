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

    static String TAG = SplashActivity.class.getSimpleName();
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

        // Instantiate an auth configuration object with credentials defined previously.
        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitter_key, twitter_secret);
		Fabric.with(this, new Twitter(authConfig));

        // Set the layout of the current activity.
		setContentView(R.layout.activity_splash);

        // Get loading messages to be iterated through from XML definitions.
		loadingMessages = getResources().getStringArray(R.array.loading_messages);

        // Assign the TextView that will hold the loading messages when onStart is triggered.
		txtLoading = (TextView) findViewById(R.id.txtLoading);



	}
	
	@Override
	protected void onStart() {
		super.onStart();

		branch = Branch.getInstance(this.getApplicationContext(), getResources().getString(R.string.bnc_app_key));

        // Initiate the session and create a listener to take action once a response is received.
		branch.initSession(new BranchReferralInitListener() {

            /**
             * This object defines action to be taken when the response to initSession is received.
             *
             * @param referringParams Contains a pre-parsed JSONObject that holds the data dictionary.
             * @param error Contains information relevant if an init was not possible.
             */
			@Override
			public void onInitFinished(JSONObject referringParams, BranchError error) {
				Log.i(TAG, "branch init complete!");
				try {

                    // Get an instance of the MonsterPreferences singleton class.
                    MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());

                    Intent i;

                    // If there is monster data in the dictionary of the received deep link...
                    if (referringParams.has("monster")) {

                        // Get the details of the received monster and add them to the prefs object.
						prefs.setMonsterName(referringParams.getString("monster_name"));
						prefs.setFaceIndex(referringParams.getInt("face_index"));
						prefs.setBodyIndex(referringParams.getInt("body_index"));
						prefs.setColorIndex(referringParams.getInt("color_index"));


						i = new Intent(getApplicationContext(), MonsterViewerActivity.class);

			        } else {

                        // If the monster name has not been set...
			            if (prefs.getMonsterName() == null) {

                            // Set an empty string value to avoid null pointer.
			                prefs.setMonsterName("");

                            // Then instantiate the intent to direct the user to create a monster.
			                i = new Intent(getApplicationContext(), MonsterCreatorActivity.class);

			            } else {

                            // Then instantiate the intent to view the received monster.
			            	i = new Intent(getApplicationContext(), MonsterViewerActivity.class);
			            }

                        // Start the intent however it has been defined.
			            startActivity(i);

			        }
				} catch (JSONException e) {
                    // If there is an error in the JSON data in the referring params, print stack.
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
