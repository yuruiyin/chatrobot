package com.fzu.chatrobot.utils;


import com.fzu.chatrobot.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 单例模式的toast
 */
public class MyToast {
	private static Toast toast = null;
	
	public static final void showToast(Context context, int resid) {
		showToast(context, context.getResources().getText(resid));
	}

	public static void showToast(Context context, CharSequence text) {
		
		LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.toast, null);
		TextView textView = (TextView) layout.findViewById(R.id.tv_toast);
		textView.setText(text);
		
		if (toast == null) {
			toast = new Toast(context);
			toast.setGravity(Gravity.CENTER, 0, 0);
		} 
		
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void hideToast() {
		if (toast != null) {
			toast.cancel();
		}
	}

}
