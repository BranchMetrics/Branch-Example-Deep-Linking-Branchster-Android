package io.branch.branchster;

import io.branch.branchster.Preferences.MonsterPreferences;
import io.branch.referral.Branch;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * This class is where the user can create their own monster. It is the first that the user sees if
 * they are opening the app for the first time, assuming that they haven't installed the app as a
 * result of being sent a deep link.
 */
public class MonsterCreatorActivity extends Activity {

    //
	EditText editName;

    // These are the direction button chevrons that surround the monster.
	ImageButton cmdUp;
	ImageButton cmdRight;
	ImageButton cmdDown;
	ImageButton cmdLeft;

    // This generic View is what changes colour when the user changes that aspect of the monster.
	View botLayerOneColor;

    // This ImageView appears above the color view, and masks off the shape of the monster.
	ImageView botLayerTwoBody;

    // The face is laid on top of the body mask graphic.
	ImageView botLayerThreeFace;

    // The buttons
	Button[] cmdColors;

    /* The color tubs contain one colour button each. They are slightly bigger than the Button
    * that they contain, and the background is the outline that is seen around a selected color. */
    LinearLayout[] cmdColorTubs;

    // The user hits this button to indicate that they have finished editing their monster.
	Button cmdDone;

    /* This MonsterPreferences is populated either by the user's actions as they customise their
    monster, or when data is received with the a deep link dictionary attached. */
	MonsterPreferences prefs;
	MonsterPartsFactory factory;

	int faceIndex;
	int bodyIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monster_creator);

        // Assign UI items to variables for manipulation later on.
		editName = (EditText) findViewById(R.id.editName);
		botLayerOneColor = (View) findViewById(R.id.botLayerOneColor);
		botLayerTwoBody = (ImageView) findViewById(R.id.botLayerTwoBody);
		botLayerThreeFace = (ImageView) findViewById(R.id.botLayerThreeFace);
		cmdDone = (Button) findViewById(R.id.cmdDone);
		cmdUp = (ImageButton) findViewById(R.id.cmdUp);
		cmdRight = (ImageButton) findViewById(R.id.cmdRight);
		cmdDown = (ImageButton) findViewById(R.id.cmdDown);
		cmdLeft = (ImageButton) findViewById(R.id.cmdLeft);

		cmdColors = new Button[]{
				(Button) findViewById(R.id.cmdColor0),
				(Button) findViewById(R.id.cmdColor1),
				(Button) findViewById(R.id.cmdColor2),
				(Button) findViewById(R.id.cmdColor3),
				(Button) findViewById(R.id.cmdColor4),
				(Button) findViewById(R.id.cmdColor5),
				(Button) findViewById(R.id.cmdColor6),
				(Button) findViewById(R.id.cmdColor7)
		};

        cmdColorTubs = new LinearLayout[]{
                (LinearLayout) findViewById(R.id.cmdColorTub0),
                (LinearLayout) findViewById(R.id.cmdColorTub1),
                (LinearLayout) findViewById(R.id.cmdColorTub2),
                (LinearLayout) findViewById(R.id.cmdColorTub3),
                (LinearLayout) findViewById(R.id.cmdColorTub4),
                (LinearLayout) findViewById(R.id.cmdColorTub5),
                (LinearLayout) findViewById(R.id.cmdColorTub6),
                (LinearLayout) findViewById(R.id.cmdColorTub7)
        };

        // Assign the prefs file.
		prefs = MonsterPreferences.getInstance(getApplicationContext());
		factory = MonsterPartsFactory.getInstance(getApplicationContext());

        // Iterate through the color buttons and set their color based on the predefined options.
		for (int i = 0; i < cmdColors.length; i++) {
            Button cmdColor = cmdColors[i];
			cmdColor.setBackgroundColor(factory.colorForIndex(i));

            // Assign a click listener for each.
			cmdColor.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					int idx = Integer.parseInt((String)v.getTag());
					prefs.setColorIndex(idx);
					botLayerOneColor.setBackgroundColor(factory.colorForIndex(idx));
                    setSelectedColourButton(idx);

				}
			});
		}

        // Go to the previous face when the user clicks the up arrow.
		cmdUp.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				faceIndex--;
			    if (faceIndex == -1) {
			        faceIndex = factory.sizeOfFaceArray() - 1;
			    }

			    prefs.setFaceIndex(faceIndex);
			    botLayerThreeFace.setImageBitmap(factory.imageForFace(faceIndex));
			}
		});

        // Go to the next face when the user clicks the down arrow.
		cmdDown.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				faceIndex++;
			    if (faceIndex == factory.sizeOfFaceArray()) {
			        faceIndex = 0;
			    }
			    
			    prefs.setFaceIndex(faceIndex);
			    botLayerThreeFace.setImageBitmap(factory.imageForFace(faceIndex));
			}
		});

        // Go to the previous body when the user clicks the left arrow.
		cmdLeft.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				bodyIndex--;
			    if (bodyIndex == -1) {
			    	bodyIndex = factory.sizeOfBodyArray() - 1;
			    }
			    
			    prefs.setBodyIndex(bodyIndex);
			    botLayerTwoBody.setImageBitmap(factory.imageForBody(bodyIndex));
			}
		});

        // Go to the next body when the user clicks the right arrow.
		cmdRight.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				bodyIndex++;
			    if (bodyIndex == factory.sizeOfBodyArray()) {
			    	bodyIndex = 0;
			    }
			    
			    prefs.setBodyIndex(bodyIndex);
			    botLayerTwoBody.setImageBitmap(factory.imageForBody(bodyIndex));
			}
		});

        // Save the monster name to prefs object, then open the MonsterViewerActivity.
		cmdDone.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editName.getText().toString().length() > 0) {
			        prefs.setMonsterName(editName.getText().toString());
			    } else {
			    	prefs.setMonsterName(getString(R.string.monster_name));
			    }
				
				Intent i = new Intent(getApplicationContext(), MonsterViewerActivity.class);
				startActivity(i);
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Branch.getInstance(getApplicationContext()).initSession();

        // Get the values from the monster prefs.
		botLayerOneColor.setBackgroundColor(factory.colorForIndex(prefs.getColorIndex()));
		botLayerTwoBody.setImageBitmap(factory.imageForBody(prefs.getBodyIndex()));
		botLayerThreeFace.setImageBitmap(factory.imageForFace(prefs.getFaceIndex()));
		editName.setText(prefs.getMonsterName());

        // Colour selector button initial state.
        setSelectedColourButton(prefs.getColorIndex());
	}

	@Override
	protected void onStop() {
		super.onStop();
		Branch.getInstance(getApplicationContext()).closeSession();
	}

    private void setSelectedColourButton(int idx){

        // Deselect all colour buttons.
        for (int j = 0; j < cmdColors.length; j++) {
            cmdColorTubs[j].setBackground(getResources().getDrawable(R.drawable.colour_button_off));
        }

        // Re-select the chosen colour button.
        cmdColorTubs[idx].setBackground(getResources().getDrawable(R.drawable.colour_button_on));

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
