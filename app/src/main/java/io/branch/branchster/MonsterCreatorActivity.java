package io.branch.branchster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.ArrayRes;

import io.branch.branchster.util.ColorController;
import io.branch.branchster.util.MonsterImageView;
import io.branch.branchster.util.MonsterPreferences;
import io.branch.indexing.BranchUniversalObject;

/**
 * This class is where the user can create their own monster. It is the first that the user sees if
 * they are opening the app for the first time, assuming that they haven't installed the app as a
 * result of being sent a deep link.
 */
public class MonsterCreatorActivity extends Activity {

    EditText editName;
    // Image view to show custom monster
    MonsterImageView monsterImageView_;


    /* This MonsterPreferences is populated either by the user's actions as they customise their
    monster, or when data is received with the a deep link dictionary attached. */
    MonsterPreferences prefs;


    private int faceIndex;
    private int bodyIndex;

    private int faceArrayLength, bodyArrayLength;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_creator);

        prefs = MonsterPreferences.getInstance(getApplicationContext());

        // Assign UI items to variables for manipulation later on.
        editName = findViewById(R.id.editName);
        monsterImageView_ = findViewById(R.id.monster_img_view);

        faceArrayLength = getTypedArrayLength(R.array.face_drawable_array);
        bodyArrayLength = getTypedArrayLength(R.array.body_drawable_array);

        // Go to the previous face when the user clicks the up arrow.
        findViewById(R.id.cmdUp).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceIndex--;
                if (faceIndex == -1) {
                    faceIndex = faceArrayLength - 1;
                }

                prefs.setFaceIndex(faceIndex);
                monsterImageView_.updateFace(faceIndex);
            }
        });

        // Go to the next face when the user clicks the down arrow.
        findViewById(R.id.cmdDown).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceIndex++;
                if (faceIndex == faceArrayLength) {
                    faceIndex = 0;
                }

                prefs.setFaceIndex(faceIndex);
                monsterImageView_.updateFace(faceIndex);
            }
        });

        // Go to the previous body when the user clicks the left arrow.
        findViewById(R.id.cmdLeft).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyIndex--;
                if (bodyIndex == -1) {
                    bodyIndex = bodyArrayLength - 1;
                }

                prefs.setBodyIndex(bodyIndex);
                monsterImageView_.updateBody(bodyIndex);
            }
        });

        // Go to the next body when the user clicks the right arrow.
        findViewById(R.id.cmdRight).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyIndex++;
                if (bodyIndex == bodyArrayLength) {
                    bodyIndex = 0;
                }

                prefs.setBodyIndex(bodyIndex);
                monsterImageView_.updateBody(bodyIndex);
            }
        });

        // Save the monster name to prefs object, then open the MonsterViewerActivity.
        findViewById(R.id.cmdDone).setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View v) {
                if (editName.getText().toString().length() > 0) {
                    prefs.setMonsterName(editName.getText().toString());
                } else {
                    prefs.setMonsterName(getString(R.string.monster_name));
                }
                // List this monster on google search
                prefs.getLatestMonsterObj().listOnGoogleSearch(MonsterCreatorActivity.this);
                Intent i = new Intent(getApplicationContext(), MonsterViewerActivity.class);
                i.putExtra(MonsterViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                startActivity(i);
                finish();
            }
        });

        new ColorController(this, monsterImageView_).start();
    }

    private int getTypedArrayLength(@ArrayRes int id) {
        TypedArray ta = getResources().obtainTypedArray(id);
        int length = ta.length();
        ta.recycle();
        return length;
    }

    @Override public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      finish();
                    }
                }).create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        BranchUniversalObject latestMonsterObj = prefs.getLatestMonsterObj();
        monsterImageView_.setMonster(latestMonsterObj);
        editName.setText(latestMonsterObj.getTitle());
    }
}
