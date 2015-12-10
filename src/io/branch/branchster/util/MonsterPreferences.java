package io.branch.branchster.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import io.branch.branchster.R;
import io.branch.indexing.BranchUniversalObject;


public class MonsterPreferences {
    private static final String SHARED_PREF_FILE = "branchster_pref";

    private static MonsterPreferences prefHelper_;
    private SharedPreferences appSharedPrefs_;
    private Editor prefsEditor_;
    private Context context_;

    private MonsterPreferences(Context context) {
        this.context_ = context;
        this.appSharedPrefs_ = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        this.prefsEditor_ = this.appSharedPrefs_.edit();
    }

    public static MonsterPreferences getInstance(Context context) {
        if (prefHelper_ == null) {
            prefHelper_ = new MonsterPreferences(context);
        }
        return prefHelper_;
    }

    public void setMonsterName(String name) {
        this.writeStringToPrefs("monster_name", name);
    }

    public String getMonsterName() {
        return (String) this.readStringFromPrefs("monster_name");
    }

    public String getMonsterDescription() {

        return context_.getResources().getStringArray(R.array.description_array)[getFaceIndex()].replace("%@", getMonsterName());
    }

    public void setFaceIndex(int index) {
        this.writeIntegerToPrefs("face_index", index);
    }

    public void setFaceIndex(String index) {
        try {
            this.writeIntegerToPrefs("face_index", Integer.parseInt(index));
        } catch (NumberFormatException ignore) {
        }
    }

    public int getFaceIndex() {
        return this.readIntegerFromPrefs("face_index");
    }

    public void setBodyIndex(int index) {
        this.writeIntegerToPrefs("body_index", index);
    }

    public void setBodyIndex(String index) {
        try {
            this.writeIntegerToPrefs("body_index", Integer.parseInt(index));
        } catch (NumberFormatException ignore) {
        }
    }


    public int getBodyIndex() {
        return this.readIntegerFromPrefs("body_index");
    }

    public void setColorIndex(int index) {
        this.writeIntegerToPrefs("color_index", index);
    }

    public void setColorIndex(String index) {
        try {
            this.writeIntegerToPrefs("color_index", Integer.parseInt(index));
        } catch (NumberFormatException ignore) {
        }
    }

    public int getColorIndex() {
        return this.readIntegerFromPrefs("color_index");
    }

    private void writeIntegerToPrefs(String key, int value) {
        prefHelper_.prefsEditor_.putInt(key, value);
        prefHelper_.prefsEditor_.commit();
    }

    @SuppressWarnings("unused")
    private void writeBoolToPrefs(String key, boolean value) {
        prefHelper_.prefsEditor_.putBoolean(key, value);
        prefHelper_.prefsEditor_.commit();
    }

    private void writeStringToPrefs(String key, String value) {
        prefHelper_.prefsEditor_.putString(key, value);
        prefHelper_.prefsEditor_.commit();
    }

    private Object readStringFromPrefs(String key) {
        return prefHelper_.appSharedPrefs_.getString(key, "");
    }

    @SuppressWarnings("unused")
    private boolean readBoolFromPrefs(String key) {
        return prefHelper_.appSharedPrefs_.getBoolean(key, false);
    }

    private int readIntegerFromPrefs(String key) {
        return prefHelper_.appSharedPrefs_.getInt(key, 0);
    }


    public void saveMonster(BranchUniversalObject monster) {
        HashMap<String, String> referringParams = monster.getMetadata();
        setMonsterName(monster.getTitle());
        setFaceIndex(referringParams.get("face_index"));
        setBodyIndex(referringParams.get("body_index"));
        setColorIndex(referringParams.get("color_index"));
    }

    public BranchUniversalObject getLatestMonsterObj() {
        BranchUniversalObject myMonsterObject = new BranchUniversalObject()
                .setTitle(getMonsterName())
                .setContentDescription(getMonsterDescription())
                .setContentImageUrl("https://s3-us-west-1.amazonaws.com/branchmonsterfactory/" + (short) getColorIndex() + (short) getBodyIndex() + (short) getFaceIndex() + ".png")
                .addContentMetadata("color_index", String.valueOf(getColorIndex()))
                .addContentMetadata("body_index", String.valueOf(getBodyIndex()))
                .addContentMetadata("face_index", String.valueOf(getFaceIndex()))
                .addContentMetadata("monster", "true")
                .addContentMetadata("monster_name",getMonsterName());

        return myMonsterObject;
    }

}
