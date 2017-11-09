package com.fzu.chatrobot.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fzu.chatrobot.MyApplication;
import com.fzu.chatrobot.MyConstants;
import com.fzu.chatrobot.bean.ChatMessage;
import com.fzu.chatrobot.listener.HttpCallbackListener;
import com.fzu.chatrobot.utils.LogUtil;

public class HttpUtil {
	private static final String TAG = "HttpUtil";
	
	public static ChatMessage getChatMessageFromResponse(String response) {
		ChatMessage chatMessage = new ChatMessage();
		String msgStr;
		try {
			JSONObject responseJson = new JSONObject(response);
			int code = responseJson.getInt("code");
			String text = responseJson.getString("text");
			if (code == 100000) { //文本类
				msgStr = text.replaceAll("<br>", "\n");
			} else if(code == 200000) { //链接类
				String url = responseJson.getString("url");
				msgStr = text + "\n" + url + "\n";
			} else if (code == 302000) { //新闻类
				String list = responseJson.getString("list");
				JSONArray jsonArray = new JSONArray(list);
				StringBuilder msg = new StringBuilder();
				msg.append(text + "\n");
				JSONObject jsonObj;
				for (int i = 0; i < jsonArray.length(); i ++) {
					msg.append("(" + (i + 1) + ")");
					jsonObj = jsonArray.getJSONObject(i);
					msg.append(jsonObj.getString("article"));
					msg.append("(来自-" + jsonObj.getString("source") + ")\n");
					msg.append(jsonObj.getString("detailurl") + "\n");
				}
				msgStr = msg.toString();
			} else if (code == 308000) { //菜谱类
				String list = responseJson.getString("list");
				JSONArray jsonArray = new JSONArray(list);
				StringBuilder msg = new StringBuilder();
				msg.append(text + "\n");
				JSONObject jsonObj;
				for (int i = 0; i < jsonArray.length(); i ++) {
					msg.append("(" + (i + 1) + ")");
					jsonObj = jsonArray.getJSONObject(i);
					msg.append(jsonObj.getString("name") + "\n");
					msg.append(jsonObj.getString("detailurl") + "\n");
				}
				msgStr = msg.toString();
			} else {
				msgStr = "服务器繁忙，请稍后再试!";
			}

		} catch (JSONException e) {
			msgStr = "服务器繁忙，请稍后再试!";
			e.printStackTrace();
		}
		chatMessage.setMsg(msgStr);
		long curTime = System.currentTimeMillis();
		if (curTime - MyApplication.getLastTime() >= MyConstants.interval) {  //只有两次发送消息的时间超过15秒，才会显示时间
			chatMessage.setDate(new Date());
		} else {
			chatMessage.setDate(null);
		}
		MyApplication.setLastTime(curTime);
		chatMessage.setType(ChatMessage.TYPE_RECEIVED);
		return chatMessage;
	}
	
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					LogUtil.d(TAG, "url: " + address);
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					//读取获取到的输入流
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					ChatMessage chatMessage = getChatMessageFromResponse(response.toString());
					if (listener != null) {
						//回调onFinish()函数
						listener.onFinish(chatMessage);
					}
					
				} catch (Exception e) {
					if (listener != null) {
						//回调onError方法
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
		
	}

}
