package com.fzu.chatrobot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fzu.chatrobot.custom.ProgressWebView;
import com.fzu.chatrobot.custom.Topbar;
import com.fzu.chatrobot.utils.LogUtil;

/**
 * 自定义浏览器界面
 * Created by yury on 2016/9/5.
 */
public class WebViewActivity extends Activity {
	private static final String TAG = "WebViewActivity";

	private ProgressWebView progressWebView;
	private Topbar mTopbar;

	private String url;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview_layout);

		LogUtil.d(TAG, "this process id is " + Process.myPid());

		url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		initViews();
		initEvents();
	}

	private void initEvents() {
		// webview
		progressWebView.loadUrl(url);
		// topbar
//		mTopbar.setRightIsvisable(false);
		mTopbar.setTitleText(title);
		mTopbar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
			@Override
			public void leftClick() {
				exitThisProcess();
			}

			@Override
			public void rightClick() {
				progressWebView.reload();
			}
		});

	}

	private void initViews() {
		progressWebView = (ProgressWebView) findViewById(R.id.web_view);
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
	}

	private void exitThisProcess() {
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);  //退出虚拟机，可清除后台缓存的进程
	}

	@Override
	public void onBackPressed() {
//		exitThisProcess();
		if (progressWebView.canGoBack()) {
			progressWebView.goBack();
		} else {
//			Toast.makeText(WebViewActivity.this, "已经是首页", Toast.LENGTH_SHORT).show();
			exitThisProcess();
		}
	}
}
