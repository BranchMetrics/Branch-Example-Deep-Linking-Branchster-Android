package io.branch.branchster.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.HashMap;

import io.branch.branchster.R;
import io.branch.indexing.BranchUniversalObject;

/**
 * Created by sojanpr on 11/18/15.
 */
public class MonsterImageView extends androidx.appcompat.widget.AppCompatImageView {
    public LayerDrawable monsterDrawable_;

    public MonsterImageView(Context context) {
        super(context);
        init();
    }

    public MonsterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        monsterDrawable_ = (LayerDrawable) getContext().getResources().getDrawable(R.drawable.monster_drawable);
        setBackground(monsterDrawable_);
    }

    public void updateColor(int colorIndex) {
        TypedArray ta = getContext().getResources().obtainTypedArray(R.array.colors);
        monsterDrawable_.setDrawableByLayerId(R.id.color_drawable_item, new ColorDrawable(ta.getColor(colorIndex, 0x00FF0000)));
        ta.recycle();
        invalidateDrawable(monsterDrawable_);
    }

    public void updateFace(int faceIndex) {
        TypedArray ta = getContext().getResources().obtainTypedArray(R.array.face_drawable_array);
        monsterDrawable_.setDrawableByLayerId(R.id.face_drawable_item, ta.getDrawable(faceIndex));
        ta.recycle();
        invalidateDrawable(monsterDrawable_);
    }

    public void updateBody(int bodyIndex) {
        TypedArray ta = getContext().getResources().obtainTypedArray(R.array.body_drawable_array);
        monsterDrawable_.setDrawableByLayerId(R.id.body_drawable_item, ta.getDrawable(bodyIndex));
        ta.recycle();
        invalidateDrawable(monsterDrawable_);
    }

    public void setMonster(BranchUniversalObject monsterObj) {
        init();// must be reinitialized when viewer activity is relaunched via a push notification
        HashMap<String, String> monsterMetadata = monsterObj.getContentMetadata().getCustomMetadata();
        int colorIdx = 0;
        int bodyIdx = 0;
        int faceIdx = 0;
        if (!TextUtils.isEmpty(monsterMetadata.get("color_index"))) {
            colorIdx = Integer.parseInt(monsterMetadata.get("color_index"));
        }
        if (!TextUtils.isEmpty(monsterMetadata.get("body_index"))) {
            bodyIdx = Integer.parseInt(monsterMetadata.get("body_index"));
        }
        if (!TextUtils.isEmpty(monsterMetadata.get("face_index"))) {
            faceIdx = Integer.parseInt(monsterMetadata.get("face_index"));
        }
        updateColor(colorIdx);
        updateBody(bodyIdx);
        updateFace(faceIdx);
    }
}
