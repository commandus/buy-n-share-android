package com.commandus.buynshare;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * StickyChat application settings singleton class
 */
public class ApplicationSettings {
	public static final String PREFS_NAME = "buynshare";
	private static final String PREF_USER_ID = "userid";
	private static final String PREF_USER_PWD = "userpwd";
	private static final String PREF_FIRST_TIME = "firsttime";
	private static final String PREF_TTS_ON = "ttson";

	private final Context mContext;
	private String mUserId;
	private String mUserPwd;
	private boolean mTTSRunning;
	private boolean mTTSEnabled;

	private static ApplicationSettings mInstance = null;
	private static boolean mFirstTime;

	public synchronized static ApplicationSettings getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ApplicationSettings(context);
		}
		return mInstance;
	}

	private  ApplicationSettings(Context context) {
		mContext = context;
		load();
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String value) {
		mUserId = value;
	}

	public void save() {
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_USER_ID, mUserId);
		editor.putString(PREF_USER_PWD, mUserPwd);
		editor.putBoolean(PREF_FIRST_TIME, false);
		editor.putBoolean(PREF_TTS_ON, true);

		editor.apply();
	}

	public void load() {
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		mUserId = settings.getString(PREF_USER_ID, "");
		mUserPwd = settings.getString(PREF_USER_PWD, "");
		mFirstTime = settings.getBoolean(PREF_FIRST_TIME, true);
		mTTSEnabled = settings.getBoolean(PREF_TTS_ON, true);
	}

	public boolean isFirstTime() {
		return mFirstTime;
	}

	public void setFirstTime(boolean value) {
		mFirstTime = value;
	}

	/**
	 * Check is TTS enabled
	 * @return true
     */
	public boolean isTtsRunning() {
		return mTTSRunning;
	}

	public void setTTSRun(boolean on) {
		mTTSRunning = on;
	}

	public void enableTTS(boolean on) {
		mTTSEnabled = on;
		save();
	}

	public boolean isTtsEnabled() {
		return mTTSEnabled;
	}
}
