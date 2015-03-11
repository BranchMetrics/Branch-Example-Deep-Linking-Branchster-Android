package io.branch.branchster;

import io.branch.referral.Branch;
import io.branch.referral.Branch.BranchLinkCreateListener;
import io.branch.referral.BranchError;
import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import com.facebook.Session;

public class MonsterViewerActivity extends FragmentActivity {

    private static String TAG = MonsterViewerActivity.class.getSimpleName();

    // Facebook
    private UiLifecycleHelper uiHelper;

	TextView txtName;
	TextView txtDescription;
	ImageButton cmdMessage;
	ImageButton cmdMail;
	ImageButton cmdTwitter;
	ImageButton cmdFacebook;
	TextView txtUrl;
	Button cmdChange;
	View botLayerOneColor;
	ImageView botLayerTwoBody;
	ImageView botLayerThreeFace;
	
	MonsterPreferences prefs;
	MonsterPartsFactory factory;
	
	String monsterName;
	String monsterDescription;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monster_viewer);
		
		prefs = MonsterPreferences.getInstance(getApplicationContext());
		factory = MonsterPartsFactory.getInstance(getApplicationContext());
		
		monsterName = prefs.getMonsterName();
		monsterDescription = prefs.getMonsterDescription();
		
		final JSONObject monsterMetadata = new JSONObject();
		try {
			monsterMetadata.put("color_index", prefs.getColorIndex());
			monsterMetadata.put("body_index", prefs.getBodyIndex());
			monsterMetadata.put("face_index", prefs.getFaceIndex());
			monsterMetadata.put("monster_name", monsterName);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

        // TextView that will contain the Link URL.
        txtUrl = (TextView) findViewById(R.id.txtURL);

		// track that the user viewed the monster view page
	    Branch.getInstance(getApplicationContext()).userCompletedAction("monster_view", monsterMetadata);

	    // load a URL just for display on the viewer page
	    Branch.getInstance(getApplicationContext()).getContentUrl("viewer", monsterMetadata, new BranchLinkCreateListener() {
			@Override
			public void onLinkCreate(String url, BranchError error) {
                txtUrl.setText(url);
			}
		});
		
		txtName = (TextView) findViewById(R.id.txtName);
		botLayerOneColor = findViewById(R.id.botLayerOneColor);
		botLayerTwoBody = (ImageView) findViewById(R.id.botLayerTwoBody);
		botLayerThreeFace = (ImageView) findViewById(R.id.botLayerThreeFace);
		txtDescription = (TextView) findViewById(R.id.txtDescription);
		cmdChange = (Button) findViewById(R.id.cmdChange);

		cmdMessage = (ImageButton) findViewById(R.id.cmdSMS);
		cmdMail = (ImageButton) findViewById(R.id.cmdMail);
		cmdTwitter = (ImageButton) findViewById(R.id.cmdTwitter);
		cmdFacebook = (ImageButton) findViewById(R.id.cmdFB);

		cmdChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MonsterCreatorActivity.class);
				startActivity(i);
			}
		});

        // Share via SMS.
    		cmdMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Branch.getInstance(getApplicationContext()).getContentUrl("sms", prepareBranchDict(), new BranchLinkCreateListener() {
					@Override
					public void onLinkCreate(String url, BranchError error) {
						Uri uri = Uri.parse("smsto:");  
						Intent intent = new Intent(Intent.ACTION_SENDTO, uri);  
						intent.putExtra("sms_body", "Check out my Branchster named " + monsterName + " at " + url);  
						startActivity(intent);
					}
				});
			}
		});

        // Share via Email.
		cmdMail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Branch.getInstance(getApplicationContext()).getContentUrl("email", prepareBranchDict(), new BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Check out my Branchster named " + monsterName);
                        intent.putExtra(Intent.EXTRA_TEXT, "I just created this Branchster named " + monsterName + " in the Branch Monster Factory.\n\nSee it here:\n" + url);
                        intent.setType ("text/plain");
                        startActivity(Intent.createChooser(intent, "Choose Email Client"));
                    }
                });
            }
        });

        // Share via Twitter.
        Fabric.with(this, new TweetComposer());
        final Context context = this;
        cmdTwitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Branch.getInstance(getApplicationContext()).getContentUrl("twitter", prepareBranchDict(), new BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                .text("Check out my Branchster named " + monsterName + " at " + url);
                        builder.show();
                    }
                });
            }
        });

        // Share via Facebook.
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        cmdFacebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Branch.getInstance(getApplicationContext()).getContentUrl("facebook", prepareBranchDict(), new BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        shareViaFacebook(url, "Check out my Branchster named " + monsterName);
                    }
                });
            }
        });
	}

    private void shareViaFacebook(String url, String description){

        if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            publishShareDialog(url, description);
        } else {
            Log.w(TAG, "Unable to launch Facebook App to share Branchster. Falling back to FeedDialog.");
            publishFeedDialog(url, description);
        }

    }

    // Facebook ShareDialog - the first choice for sharing via Facebook.
    private void publishShareDialog(String url, String description){
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink(url)
                .setDescription(description)
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }

    // Facebook FeedDialog fallback for if ShareDialog is not available.
    private void publishFeedDialog(String url, String description) {
        Bundle params = new Bundle();
        params.putString("description", description);
        params.putString("link", url);

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(getApplicationContext(),
                                        "Posted story, id: " + postId,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(getApplicationContext(),
                                        "Publish cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(getApplicationContext(),
                                    "Publish cancelled",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(getApplicationContext(),
                                    "Error posting story",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }

    // Required to receive the callback from the Facebook app if supported.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		Branch.getInstance(getApplicationContext()).initSession();
		
		botLayerOneColor.setBackgroundColor(factory.colorForIndex(prefs.getColorIndex()));
		botLayerTwoBody.setImageBitmap(factory.imageForBody(prefs.getBodyIndex()));
		botLayerThreeFace.setImageBitmap(factory.imageForFace(prefs.getFaceIndex()));
		txtName.setText(monsterName);
		txtDescription.setText(monsterDescription);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Branch.getInstance(getApplicationContext()).closeSession();
	}
	
	private JSONObject prepareBranchDict() {
		JSONObject monsterMetadata = new JSONObject();
		try {
			monsterMetadata.put("color_index", prefs.getColorIndex());
			monsterMetadata.put("body_index", prefs.getBodyIndex());
			monsterMetadata.put("face_index", prefs.getFaceIndex());
			monsterMetadata.put("monster_name", prefs.getMonsterName());
			monsterMetadata.put("monster", "true");
			monsterMetadata.put("og_title", "My Branchster: " + monsterName);
			monsterMetadata.put("og_description", monsterDescription);
			monsterMetadata.put("og_image_url", "https://s3-us-west-1.amazonaws.com/branchmonsterfactory/" + (short)prefs.getColorIndex() + (short)prefs.getBodyIndex() + (short)prefs.getFaceIndex() + ".png");      
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		return monsterMetadata;
	}

    @Override
    protected void onResume() {
        super.onResume();

        // Required for Facebook.
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Required for Facebook.
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Required for Facebook.
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Required for Facebook.
        uiHelper.onDestroy();
    }
}
