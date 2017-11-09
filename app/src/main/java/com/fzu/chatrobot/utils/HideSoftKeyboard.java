package com.fzu.chatrobot.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class HideSoftKeyboard {
	
	/**
	 * 隐藏软键盘
	 * @param activity Activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
		if (activity.getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager)
					activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(
					activity.getCurrentFocus().getWindowToken(), 0);
		}

	}
	
}
