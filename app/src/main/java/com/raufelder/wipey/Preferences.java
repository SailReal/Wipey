package com.raufelder.wipey;

import android.content.Context;
import android.content.SharedPreferences;

class Preferences {

	private static final String SHARED_PREFS = "SHARED_PREFS";
	private static final String WIPE_DEVICE = "WIPE_DEVICE";
	private static final String AMOUNT_FAILED_LOGIN = "AMOUNT_FAILED_LOGIN";

	protected static boolean isWipeActivated(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		return sharedPref.getBoolean(WIPE_DEVICE, true);
	}

	protected static void saveWipe(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		sharedPref.edit().putBoolean(WIPE_DEVICE, true).apply();
	}

	protected static void saveRestart(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		sharedPref.edit().putBoolean(WIPE_DEVICE, false).apply();
	}

	protected static int amountOfFailedLogin(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		return sharedPref.getInt(AMOUNT_FAILED_LOGIN, 10);
	}

	protected static void setAmountOfFailedLogin(Context context, int amount) {
		SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		sharedPref.edit().putInt(AMOUNT_FAILED_LOGIN, amount).apply();
	}
}
