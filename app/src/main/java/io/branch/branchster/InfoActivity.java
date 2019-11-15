package io.branch.branchster;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static io.branch.branchster.SplashActivity.postNotif;

public class InfoActivity extends Activity {
    private Button mButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                postNotif(InfoActivity.this, MonsterViewerActivity.class);
            }
        });
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView)findViewById(R.id.version_name_txt)).setText(String.format(getString(R.string.version_name),versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
