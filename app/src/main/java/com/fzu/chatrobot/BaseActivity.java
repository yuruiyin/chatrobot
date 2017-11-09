package com.fzu.chatrobot;

import com.fzu.chatrobot.utils.HideSoftKeyboard;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements TextWatcher {
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ActivityCollector.addActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
	public void showTip(final String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 为该Activity的每个控件注册关闭软键盘的监听器
	 * @param view 需要注册监听器的view
	 */
	protected void setOnListenerForCloseKeyboard(View view) {
		if (!(view instanceof EditText)) {
			//若当前控件不是EditText，则注册关闭软键盘监听器
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					HideSoftKeyboard.hideSoftKeyboard((Activity) mContext);
					return false;
				}
			});
		}
		
		//获取当前view的子view
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup)view).getChildCount(); i ++) {
				View childView = ((ViewGroup)view).getChildAt(i);
				setOnListenerForCloseKeyboard(childView);
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// EditText内容变化之前回调
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// EditText内容开始变化中回调
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// EditText内容变化之后回调
		afterEditTextChanged(s);
	}

	protected abstract void afterEditTextChanged(Editable s);

}
