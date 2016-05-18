package io.branch.branchster.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.branch.branchster.R;


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

    public void saveMonster(Map<String, String> monster) {
        setMonsterName(monster.get("name"));
        setFaceIndex(monster.get("face_index"));
        setBodyIndex(monster.get("body_index"));
        setColorIndex(monster.get("color_index"));
    }

    public void resetMonster() {
        Resources res = context_.getResources();
        Random rand = new Random();

        setMonsterName("");

        setFaceIndex(rand.nextInt(res.obtainTypedArray(R.array.face_drawable_array).length()));
        setBodyIndex(rand.nextInt(res.obtainTypedArray(R.array.body_drawable_array).length()));
        setColorIndex(rand.nextInt(res.obtainTypedArray(R.array.colors).length()));
    }

    public Map<String, String> getLatestMonsterObject() {
        HashMap<String, String> monster = new HashMap<>();

        monster.put("name", getMonsterName());
        monster.put("description", getMonsterDescription());

        monster.put("color_index", String.valueOf(getColorIndex()));
        monster.put("body_index", String.valueOf(getBodyIndex()));
        monster.put("face_index", String.valueOf(getFaceIndex()));

        return monster;
    }
}
