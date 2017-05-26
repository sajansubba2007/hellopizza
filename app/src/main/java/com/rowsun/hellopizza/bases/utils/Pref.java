package com.rowsun.hellopizza.bases.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref {


    public static final String KEY_SOUND = "setting_sound";
    public static final String KEY_NOTIFO = "setting_notifo";
	public static final String KEY_FIRST_TIME  = "firsttime";
	public static final String KEY_MENU_CACHE = "menu_cache";
    Context ctx;
	SharedPreferences preferences;

	public Pref(Context ctx){
		this.ctx = ctx;
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		initializeDefaults();
	}

	public void initializeDefaults(){
        if(!containsKey(KEY_NOTIFO)){
            setBoolPreferences(KEY_NOTIFO, true);
        }
        if(!containsKey(KEY_SOUND)){
            setBoolPreferences(KEY_SOUND, true);
        }
        if(!containsKey(KEY_FIRST_TIME)){
            setBoolPreferences(KEY_FIRST_TIME, true);
        }
	}
	
	public boolean containsKey(String key){
		return preferences.contains(key);
	}
	
	public String getPreferences(String key){
		return preferences.getString(key, "");
	}
	
	public boolean setPreferences(String key, String value){
		return preferences.edit().putString(key, value).commit();
	}
	public int getIntPreferences(String key){
		return preferences.getInt(key, 0);
	}
	
	public boolean setIntPreferences(String key, int value){
		return preferences.edit().putInt(key, value).commit();
	}
	
	public long getLongPreferences(String key){
		return preferences.getLong(key, 0l);
	}
	
	public boolean setLongPreferences(String key, long value){
		return preferences.edit().putLong(key, value).commit();
	}
	
	public boolean getBoolPreferences(String key){
		return preferences.getBoolean(key, false);
	}
	
	public boolean setBoolPreferences(String key, boolean value){
		return preferences.edit().putBoolean(key, value).commit();
	}
	
	public boolean clearPreferences(){
		return preferences.edit().clear().commit();
	}

	public boolean isLoggedIn(){
		return !preferences.getString("u_token", "").isEmpty();
	}

}
