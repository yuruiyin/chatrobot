package com.fzu.chatrobot.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * 自定义ListView，可以实现弹性滑动
 * @author yury
 *
 */
public class MyListView extends ListView {
	//初始可拉动Y轴的距离
	private static final int MAX_Y_OVERSCROLL_DISTANCE =80;
	//实际可上下拉动Y轴的距离
	private int mMaxOverDistance; 
	private Context mContext;

	public MyListView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// TODO Auto-generated method stub
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverDistance,
				isTouchEvent);
	}
	
	private void initView() {
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		float density = metrics.density;
		mMaxOverDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	}

}
