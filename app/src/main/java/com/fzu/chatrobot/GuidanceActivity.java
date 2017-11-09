package com.fzu.chatrobot;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.fzu.chatrobot.custom.Topbar;

/**
 * 使用指南对应的Activity
 * Created by yury on 2016/8/30.
 */
public class GuidanceActivity extends BaseActivity {

	private String[] data = new String[14];

	private Topbar mTopbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guidance);

		initViews();
		initData();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.guidance_item, data);
		ListView listView = (ListView) findViewById(R.id.id_lv_guidance);
		listView.setAdapter(adapter);
	}

	private void initViews() {
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		mTopbar.setRightIsvisable(false);
		mTopbar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
			@Override
			public void leftClick() {
				finish();
			}

			@Override
			public void rightClick() {
				// do nothing
			}
		});
	}

	@Override
	protected void afterEditTextChanged(Editable s) {
		//do nothing
	}

	private void initData() {
		data[0] = "您可以按照以下方式来说：";
		data[1] = "给我老爸打个电话";
		data[2] = "发条短信给我老婆";
		data[3] = "打开QQ";
		data[4] = "打开微信";
		data[5] = "讲个鬼故事";
		data[6] = "讲个笑话";
		data[7] = "我要看新闻";
		data[8] = "福州到北京的高铁";
		data[9] = "明天福州到深圳的飞机";
		data[10] = "请问北京烤鸭怎么做";
		data[11] = "来张美美的风景图";
		data[12] = "今天北京天气怎么样";
		data[13] = "......";
	}
}
