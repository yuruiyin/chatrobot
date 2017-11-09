package com.fzu.chatrobot.listener;

import com.fzu.chatrobot.bean.ChatMessage;

public interface HttpCallbackListener {
	/**
	 * 请求成功回调
	 * @param chatMessage 服务端返回的消息（本地处理过）
	 */
	void onFinish(ChatMessage chatMessage);
	
	/**
	 * 请求失败回调
	 * @param e 异常信息
	 */
	void onError(Exception e);
	
}	
