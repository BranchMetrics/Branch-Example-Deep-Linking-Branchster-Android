package io.branch.branchster;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import io.branch.branchster.fragment.InfoFragment;
import io.branch.branchster.util.MonsterImageView;
import io.branch.branchster.util.MonsterPreferences;

public class MonsterViewerActivity extends FragmentActivity implements InfoFragment.OnFragmentInteractionListener {
    private static String TAG = MonsterViewerActivity.class.getSimpleName();


    MonsterImageView monsterImageView_;
    Map<String, String> myMonsterObject_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_viewer);

        monsterImageView_ = (MonsterImageView) findViewById(R.id.monster_img_view);

        // Change monster
        findViewById(R.id.cmdChange).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // More info
        findViewById(R.id.infoButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                InfoFragment infoFragment = InfoFragment.newInstance();
                ft.replace(R.id.container, infoFragment).addToBackStack("info_container").commit();
            }
        });

        //Share monster
        findViewById(R.id.share_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMyMonster();
            }
        });
    }

    public void onStart() {
        super.onStart();

        MonsterPreferences pref = MonsterPreferences.getInstance(this);
        myMonsterObject_ = pref.getLatestMonsterObject();

        if (myMonsterObject_ != null) {
            ((TextView) findViewById(R.id.txtName)).setText(myMonsterObject_.get("name"));
            ((TextView) findViewById(R.id.txtDescription)).setText(myMonsterObject_.get("description"));

            // set my monster image
            monsterImageView_.setMonster(myMonsterObject_);
        } else {
            Log.e(TAG, "Monster is null. Unable to view monster");
        }
    }

    public void onNewIntent(Intent intent) {
        super.setIntent(intent);
    }

    /**
     * Method to share my custom monster with sharing with Branch Share sheet
     */
    private void shareMyMonster() {
        // TODO
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            finish();
        }
    }


    @Override
    public void onFragmentInteraction() {
        //no-op
    }
}
