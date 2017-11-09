package com.fzu.chatrobot.custom;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fzu.chatrobot.R;
import com.fzu.chatrobot.utils.LogUtil;

public class Topbar extends RelativeLayout {
	private static final String TAG = "Topbar";
	
	private int topbarWidth;
	private int topbarHeight;
	
	private ImageButton leftButton, rightButton;
	private TextView tvTitle;
	
	//左边button属性
	private int leftTextColor;
	private Drawable leftBackground;
	private String leftText;
	//右边button属性
	private int rightTextColor;
	private Drawable rightBackground;
	private String rightText;
	
	private float titleTextSize;
	private int titleTextColor;
	private String title;
	
	private LayoutParams leftParams, rightParams, titleParams;
	
	private OnTopbarClickListener listener;
	
	public interface OnTopbarClickListener {
		public void leftClick();
		public void rightClick();
	}
	
	public void setOnTopbarClickListener(OnTopbarClickListener listener) {
		this.listener = listener;
	}

	public Topbar(Context context) {
		this(context, null);
	}
	
	public Topbar(final Context context, AttributeSet attrs) {
		super(context, attrs);
		//获取自定义属性的值
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Topbar);
		
		leftBackground = ta.getDrawable(R.styleable.Topbar_leftBackground);

		rightBackground = ta.getDrawable(R.styleable.Topbar_rightBackground);

		titleTextSize = ta.getDimension(R.styleable.Topbar_titleTextSize, 0);
		LogUtil.d(TAG, "titleTextSize"  + titleTextSize);
		titleTextColor = ta.getColor(R.styleable.Topbar_titleTextColor, 0);
		title = ta.getString(R.styleable.Topbar_title);
		
		// 回收资源
		ta.recycle();

		setBackgroundResource(R.drawable.topbar_bg1);
		
		leftButton = new ImageButton(context);
		rightButton = new ImageButton(context);
		tvTitle = new TextView(context);

		leftButton.setImageDrawable(leftBackground);
		rightButton.setImageDrawable(rightBackground);
		leftButton.setBackgroundColor(Color.argb(0, 0 , 0, 0));  //让Button背景透明
		rightButton.setBackgroundColor(Color.argb(0, 0 , 0, 0));
//		leftButton.setBackgroundDrawable(leftBackground);
//		rightButton.setBackgroundDrawable(rightBackground);
		leftButton.setScaleType(ImageView.ScaleType.FIT_START);
		rightButton.setScaleType(ImageView.ScaleType.FIT_END);

		tvTitle.setTextColor(titleTextColor);
		tvTitle.setTextSize(titleTextSize);
		tvTitle.setText(title);
		tvTitle.setGravity(Gravity.CENTER);
		
//		setBackgroundColor(Color.parseColor("#00BAF0"));

		
		leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		leftParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);

		addView(leftButton, leftParams);  //把leftButton以letfParams的形式添加到viewGroup中

		rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		
		addView(rightButton, rightParams);  //把rightButton以letfParams的形式添加到viewGroup中
		
		titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		titleParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		
		addView(tvTitle, titleParams);	
		
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.leftClick();
			}
		});

		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.rightClick();
			}
		});
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		topbarWidth = measureWidth(widthMeasureSpec);
		topbarHeight = measureHeight(heightMeasureSpec);

		leftParams.width = topbarWidth / 6;
//		leftParams.height = topbarHeight;
//		leftParams.leftMargin = 0;
//		rightParams.rightMargin = 0;
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

		//判断是否宽屏
//		boolean isLand = isLandFun();
		
		// 设置leftButton的大小和左边距,宽屏和竖屏采用不同的策略
//		if (isLand) {
//			leftParams.width = topbarWidth / 15;
//			leftParams.height = topbarHeight / 2;
//			leftParams.leftMargin = topbarWidth / 50;
//			// 设置标题的大小
//			tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
//		} else {
//			leftParams.width = topbarWidth / 7;
//			leftParams.height = topbarHeight / 2;
//			leftParams.leftMargin = topbarWidth / 35;
//			// 设置标题的大小
//			tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
//		}
		
//		LogUtil.d("Topbar", "topbarWidth: " + topbarWidth);
//		LogUtil.d("Topbar", "topbarHeight: " + topbarHeight);
		
	}
	
	private boolean isLandFun() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int width = outMetrics.widthPixels;
		int height = outMetrics.heightPixels;
		return width > height;
	}

	private int measureHeight(int heightMeasureSpec) {
		return MeasureSpec.getSize(heightMeasureSpec);
	}

	private int measureWidth(int widthMeasureSpec) {
		return MeasureSpec.getSize(widthMeasureSpec);
	}

	//控制左边button显示与否
	public void setLeftIsvisable(boolean flag) {
		if (flag) {
			leftButton.setVisibility(View.VISIBLE);
		} else {
			leftButton.setVisibility(View.GONE);
		}
	}
	
	public void setRightIsvisable(boolean flag) {
		if (flag) {
			rightButton.setVisibility(View.VISIBLE);
		} else {
			rightButton.setVisibility(View.GONE);
		}
	}

	public void setTitleText(String title) {
		tvTitle.setText(title);
	}
	
}
