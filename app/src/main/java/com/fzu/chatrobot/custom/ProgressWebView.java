package com.fzu.chatrobot.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fzu.chatrobot.R;

/**
 * 自定义包含进度条的WebView
 * Created by yury on 2016/9/7.
 */
public class ProgressWebView extends WebView {

	private ProgressBar mProgressBar;

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 新建一个水平进度条
		mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		// 指定进度条的布局，比如宽高和位置
		mProgressBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8, 0, 0));
		// 定义进度条的样式
		Drawable drawable = context.getResources().getDrawable(R.drawable.progress_bar_states);
		mProgressBar.setProgressDrawable(drawable);
		// 将进度条添加进webView中
		addView(mProgressBar);

		setWebChromeClient(new 	MyWebChromeClient());
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;  //点击其他链接依然在webView内部执行
			}
		});
		WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setDomStorageEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
		//是否支持缩放
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
	}

	public class MyWebChromeClient extends android.webkit.WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress >= 100) {
//				mProgressBar.setProgress(newProgress);
				mProgressBar.setVisibility(GONE);
			} else {
				if (mProgressBar.getVisibility() == GONE) {
					mProgressBar.setVisibility(VISIBLE);
				}
				mProgressBar.setProgress(newProgress);
			}
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		mProgressBar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
