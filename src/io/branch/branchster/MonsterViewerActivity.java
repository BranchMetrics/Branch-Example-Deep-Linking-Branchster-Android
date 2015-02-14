package io.branch.branchster;

import io.branch.referral.Branch;
import io.branch.referral.Branch.BranchLinkCreateListener;
import io.branch.referral.BranchError;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MonsterViewerActivity extends Activity {

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
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		botLayerOneColor.setBackgroundColor(factory.colorForIndex(prefs.getColorIndex()));
		botLayerTwoBody.setImageBitmap(factory.imageForBody(prefs.getBodyIndex()));
		botLayerThreeFace.setImageBitmap(factory.imageForFace(prefs.getFaceIndex()));
		txtName.setText(monsterName);
		txtDescription.setText(monsterDescription);
	}

	@Override
	protected void onStop() {
		super.onStop();
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
}
