package com.fzu.chatrobot.parameter;

import android.content.Context;
import android.content.SharedPreferences;

import com.fzu.chatrobot.MyApplication;

/**
 * Created by Administrator on 2016/8/30.
 */
public class ParameterSettings {

	public static final String APP_PARAM = "AppParameter";

	private static ParameterSettings mInstance = null;
	private SharedPreferences mSharedPreferences;

	private ParameterSettings() {
		mSharedPreferences = MyApplication.getContext().getSharedPreferences(APP_PARAM, Context.MODE_PRIVATE);
	}

	public static ParameterSettings getInstance() {
		if (mInstance == null) {
			mInstance = new ParameterSettings();
		}
		return mInstance;
	}

	/**
	 * 设置SharedPreferences的值
	 * @param key 键
	 * @param value int类型值
	 * @return true--设置成功， false--设置失败
	 */
	public boolean setParameter(String key, int value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * 设置SharedPreferences的值
	 * @param key 键
	 * @param value String类型值
	 * @return true--设置成功， false--设置失败
	 */
	public boolean setParameter(String key, String value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * 获取SharedPreferences的值
	 * @param key 键
	 * @return int类型的值
	 */
	public int getParameterInt(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}

	/**
	 * 获取SharedPreferences的值
	 * @param key 键
	 * @return String类型的值
	 */
	public String getParameterString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}

}
