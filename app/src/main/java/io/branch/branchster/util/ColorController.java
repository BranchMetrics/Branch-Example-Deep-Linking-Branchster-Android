package io.branch.branchster.util;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import io.branch.branchster.R;

/**
 * An easy wrapper class for handling color picker functionality for monster views
 */
public class ColorController {

    Activity activity_;
    MonsterImageView monsterImageView_;
    // The buttons
    Button[] cmdColors;

    /* The color tubs contain one colour button each. They are slightly bigger than the Button
    * that they contain, and the background is the outline that is seen around a selected color. */
    LinearLayout[] cmdColorTubs;

    public ColorController(Activity activity, MonsterImageView monsterImageView){
        activity_ = activity;
        monsterImageView_ = monsterImageView;
    }
    public void start(){
        final MonsterPreferences prefs = MonsterPreferences.getInstance(activity_.getApplicationContext());


        cmdColors = new Button[]{
                (Button) activity_.findViewById(R.id.cmdColor0),
                (Button) activity_.findViewById(R.id.cmdColor1),
                (Button) activity_.findViewById(R.id.cmdColor2),
                (Button) activity_.findViewById(R.id.cmdColor3),
                (Button) activity_.findViewById(R.id.cmdColor4),
                (Button) activity_.findViewById(R.id.cmdColor5),
                (Button) activity_.findViewById(R.id.cmdColor6),
                (Button) activity_.findViewById(R.id.cmdColor7)
        };

        cmdColorTubs = new LinearLayout[]{
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub0),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub1),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub2),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub3),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub4),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub5),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub6),
                (LinearLayout) activity_.findViewById(R.id.cmdColorTub7)
        };


        // Iterate through the color buttons and set their color based on the predefined options.
        for (int i = 0; i < cmdColors.length; i++) {
            Button cmdColor = cmdColors[i];
            cmdColor.setBackgroundColor(activity_.getResources().obtainTypedArray(R.array.colors).getColor(i, 0xFF24A4DD));

            // Assign a click listener for each.
            cmdColor.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idx = Integer.parseInt((String)v.getTag());
                    prefs.setColorIndex(idx);
                    monsterImageView_.updateColor(idx);
                    setSelectedColourButton(idx);

                }
            });
        }

        setSelectedColourButton(prefs.getColorIndex());
    }

    private void setSelectedColourButton(int idx){
        // Deselect all colour buttons.
        for (int j = 0; j < cmdColors.length; j++) {
            cmdColorTubs[j].setBackground(activity_.getResources().getDrawable(R.drawable.colour_button_off));
        }
        // Re-select the chosen colour button.
        cmdColorTubs[idx].setBackground(activity_.getResources().getDrawable(R.drawable.colour_button_on));
    }

}
