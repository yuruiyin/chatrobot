package com.fzu.chatrobot.utils;

import com.fzu.chatrobot.MyApplication;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebView;

/**
 * 处理TextView中出现的多个url的点击事件
 * @author yury
 *
 */
public class SpannableUtil {

	/**
	 * 注册用户点击TextView上的URL的监听器
	 */
	public interface OnUrlClickListener {
		void onClick(String url);
	}
	
	public static SpannableString getSpannableString(String str, OnUrlClickListener listener) {
		if (str == null) {
			return new SpannableString("");
		}
		SpannableString spannableInfo = new SpannableString(str);
		
		// 寻找字符串中的所有url，http开头，\n符结束
		int curIndex = 0;
		while (true) {
			int startIndex = spannableInfo.toString().indexOf("http", curIndex);
			if (startIndex == -1) {
				//此次没找到，意味着接下来没有了，循环可以结束
				break;
			}
			curIndex = startIndex;
			int endIndex = spannableInfo.toString().indexOf("\n", curIndex);
			curIndex = endIndex;
			//设置span
			String curUrl = spannableInfo.toString().substring(startIndex, endIndex + 1);
			spannableInfo.setSpan(new Clickable(curUrl ,listener), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return spannableInfo;
	}
	
	private static class Clickable extends ClickableSpan {
		
		private String url;  //具体点击的url
		private OnUrlClickListener mListener;
		
		public Clickable(String url, OnUrlClickListener listener) {
			this.url = url;
			this.mListener = listener;
		}

		@Override
		public void onClick(View widget) {
			// 通过url来访问具体网页
			// 1 打开浏览器
//			Uri uri = Uri.parse(url);
//			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			MyApplication.getContext().startActivity(intent);
			//2 使用webView
			mListener.onClick(url);

		}
		
	}

}
