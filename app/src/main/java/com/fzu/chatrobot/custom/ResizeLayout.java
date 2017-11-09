package com.fzu.chatrobot.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ResizeLayout extends RelativeLayout {
	
	private OnResizeListener mListener;
	
	public interface OnResizeListener {
		void onResize(int w, int h, int oldw, int oldh);
	}
	
	public void setOnResizeListener(OnResizeListener l) {
		mListener = l;
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	/**
	 * 当窗口大小发生变化，比如软键盘的弹出和隐藏时候，会调用该方法
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		if (mListener != null) {
			mListener.onResize(w, h, oldw, oldh);
		}
	}
	

}
