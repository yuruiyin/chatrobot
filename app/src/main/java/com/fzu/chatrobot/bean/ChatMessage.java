package com.fzu.chatrobot.bean;

import java.util.Date;

/**
 * 聊天消息类
 * @author yuruiyin
 *
 */
public class ChatMessage {
	
	public static final int TYPE_RECEIVED = 0;
	
	public static final int TYPE_SENT = 1;
	
	private Date date;  //接收和发送消息的时间
	private String msg; //消息内容
	private int type;   //消息类型（接收or发送）
	
	public ChatMessage() {
	}
	
	public ChatMessage(Date date, String msg, int type) {
		this.date = date;
		this.msg = msg;
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
}
