package io.branch.branchster.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import io.branch.branchster.MonsterPartsFactory;


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
	    return (String)this.readStringFromPrefs("monster_name");
	}

	public String getMonsterDescription() {
		return MonsterPartsFactory.getInstance(context_).descriptionForIndex(getFaceIndex()).replace("%@", getMonsterName());
	}

	public void setFaceIndex(int index) {
	    this.writeIntegerToPrefs("face_index", index);
	}

	public int getFaceIndex() {
	    return this.readIntegerFromPrefs("face_index");
	}

	public void setBodyIndex(int index) {
	    this.writeIntegerToPrefs("body_index", index);
	}

	public int getBodyIndex() {
	    return this.readIntegerFromPrefs("body_index");
	}

	public void setColorIndex(int index) {
	    this.writeIntegerToPrefs("color_index", index);
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
		return prefHelper_.appSharedPrefs_.getString(key, null);
	}
	
	@SuppressWarnings("unused")
	private boolean readBoolFromPrefs(String key) {
		return prefHelper_.appSharedPrefs_.getBoolean(key, false);
	}
	
	private int readIntegerFromPrefs(String key) {
		return prefHelper_.appSharedPrefs_.getInt(key, 0);
	}
}
