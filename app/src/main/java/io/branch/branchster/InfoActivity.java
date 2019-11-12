package io.branch.branchster;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import io.branch.branchster.util.MonsterPreferences;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.Defines;
import io.branch.referral.util.LinkProperties;

import static io.branch.branchster.SplashActivity.branchChannelID;

public class InfoActivity extends Activity implements View.OnClickListener {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.version_name_txt)).setText(String.format(getString(R.string.version_name),versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        String shortURL = "https://branchster.app.link/purply";
        Intent intent = new Intent(this, MonsterViewerActivity.class);
        intent.putExtra(Defines.Jsonkey.AndroidPushNotificationKey.getKey(),shortURL);
        intent.putExtra(Defines.Jsonkey.ForceNewBranchSession.getKey(), true);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, branchChannelID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("BranchTest")
                .setContentText("test notif, fingers crossed")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

//        Uri webpage = Uri.parse("http://branch.io?bmp=branchster-android");
//        Intent i = new Intent(Intent.ACTION_VIEW, webpage);
//        startActivity(i);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Branch.getInstance().reInitSession(this, branchReferralInitListener);
    }

    private Branch.BranchUniversalReferralInitListener branchReferralInitListener = new Branch.BranchUniversalReferralInitListener() {
        @Override public void onInitFinished(BranchUniversalObject branchUniversalObject,
                                             LinkProperties linkProperties, BranchError branchError) {
            if (!branchUniversalObject.getContentMetadata().getCustomMetadata().containsKey("$android_deeplink_path")) {
                MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
                prefs.saveMonster(branchUniversalObject);
                Intent intent = new Intent(InfoActivity.this, MonsterViewerActivity.class);
                intent.putExtra(MonsterViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                startActivity(intent);
                finish();
            }
        }
    };
}
