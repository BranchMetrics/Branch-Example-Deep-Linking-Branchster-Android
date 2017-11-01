package io.branch.branchster.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.HashMap;

import io.branch.branchster.R;
import io.branch.indexing.BranchUniversalObject;

/**
 * Created by sojanpr on 11/18/15.
 */
public class MonsterImageView extends ImageView {
    Context context_;
    LayerDrawable monsterDrawable_;

    public MonsterImageView(Context context) {
        super(context);
        init(context);
    }

    public MonsterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        context_ = context;
        monsterDrawable_ = (LayerDrawable) context_.getResources().getDrawable(R.drawable.monster_drawable);
        setBackground(monsterDrawable_);
    }


    public MonsterImageView updateColor(int colorIndex) {
        monsterDrawable_.setDrawableByLayerId(R.id.color_drawable_item, new ColorDrawable(context_.getResources().obtainTypedArray(R.array.colors).getColor(colorIndex, 0x00FF0000)));
        invalidateDrawable(monsterDrawable_);
        return this;
    }

    public MonsterImageView updateFace(int faceIndex) {
        monsterDrawable_.setDrawableByLayerId(R.id.face_drawable_item, context_.getResources().obtainTypedArray(R.array.face_drawable_array).getDrawable(faceIndex));
        invalidateDrawable(monsterDrawable_);
        return this;
    }

    public MonsterImageView updateBody(int bodyIndex) {
        monsterDrawable_.setDrawableByLayerId(R.id.body_drawable_item, context_.getResources().obtainTypedArray(R.array.body_drawable_array).getDrawable(bodyIndex));
        invalidateDrawable(monsterDrawable_);
        return this;
    }

    public void setMonster(BranchUniversalObject monsterObj) {
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
