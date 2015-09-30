package io.branch.branchster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import io.branch.referral.Branch;
import io.branch.referral.Branch.BranchLinkCreateListener;
import io.branch.referral.BranchContentUrlBuilder;
import io.branch.referral.BranchError;
import io.branch.referral.indexing.RegisterViewBuilder;
import io.fabric.sdk.android.Fabric;

public class MonsterViewerActivity extends FragmentActivity {

    private static String TAG = MonsterViewerActivity.class.getSimpleName();

    // Facebook
    private UiLifecycleHelper uiHelper;

    Target branchsterTarget;

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
        new BranchContentUrlBuilder(this, "viewer")
                .addParameters("color_index", "" + prefs.getColorIndex())
                .addParameters("body_index", "" + prefs.getBodyIndex())
                .addParameters("face_index", "" + prefs.getFaceIndex())
                .addParameters("monster_name", monsterName)
                .setContentId(monsterName)
                .generateContentUrl(new BranchLinkCreateListener() {
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
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "Choose Email Client"));
                    }
                });
            }
        });



        // Share via Twitter.
        Fabric.with(this, new TweetComposer());
        cmdTwitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Branch.getInstance(getApplicationContext()).getContentUrl("twitter", prepareBranchDict(), new BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        String image_uri = "https://s3-us-west-1.amazonaws.com/branchmonsterfactory/" + (short)prefs.getColorIndex() + (short)prefs.getBodyIndex() + (short)prefs.getFaceIndex() + ".png";
                        getShareableImageForTwitter(image_uri, url);
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

        new RegisterViewBuilder(monsterName, monsterDescription, "https://s3-us-west-1.amazonaws.com/branchmonsterfactory/" + (short)prefs.getColorIndex() + (short)prefs.getBodyIndex() + (short)prefs.getFaceIndex() + ".png")
                .addExtra("color_index", "" + prefs.getColorIndex())
                .addExtra("body_index", "" + prefs.getBodyIndex())
                .addExtra("face_index", "" + prefs.getFaceIndex())
                 //Since Branchster uses Non Auto session
                .setContainerActivity(this)
                .setContentPath("BranchMonsterApp/MonsterView/")
                .register();
    }

    private void getShareableImageForTwitter(String image_url, final String branch_url){

        if (branchsterTarget == null) branchsterTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                handleBranchsterImage(bitmap, branch_url);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };

        Picasso.with(this).load(image_url).into(branchsterTarget);
    }

    public void handleBranchsterImage(Bitmap b, String branch_url) {
        Log.v(TAG,"Branchster image downloaded.");

        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Check out my Branchster named " + monsterName + " at " + branch_url);

        File file;
        File shareImgDir = new File(Environment.getExternalStorageDirectory().toString() + "/branchster");

        // If branchster temp directory exists, delete it and all contents - prevents accumulation over time.
        if (shareImgDir.isDirectory()) {
            String[] children = shareImgDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(shareImgDir, children[i]).delete();
            }
        }

        // Create directory for temporary image.
        shareImgDir.mkdirs();

        // Create a random number to append to filename to prevent collisions & caching.
        Random generator = new Random();

        // Range of random number.
        int n = 10000;
        n = generator.nextInt(n);

        // Create the filename of the image to share.
        String shareImgName = "share-" + n + ".jpg";
        file = new File(shareImgDir, shareImgName);
        if (file.exists()) file.delete();

        // Write the file to external storage.
        try {
            FileOutputStream out = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.image(Uri.parse(file.getAbsolutePath()));
        builder.show();

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
			monsterMetadata.put("$og_title", "My Branchster: " + monsterName);
			monsterMetadata.put("$og_description", monsterDescription);
			monsterMetadata.put("$og_image_url", "https://s3-us-west-1.amazonaws.com/branchmonsterfactory/" + (short)prefs.getColorIndex() + (short)prefs.getBodyIndex() + (short)prefs.getFaceIndex() + ".png");
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).create().show();
    }
}
