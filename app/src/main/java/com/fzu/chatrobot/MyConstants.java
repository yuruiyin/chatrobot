package com.fzu.chatrobot;

public class MyConstants {
	
	/** 图灵机器人服务器URL */
	public static final String URL = "http://www.tuling123.com/openapi/api";
	
	/** 图灵机器人的API_KEY */
	public static final String API_KEY = "33490be6235cd8f0cefbfb824b923732";

	/** 图灵机器人的secret-加密用 */
	public static final String SECRET = "62428cffa70c43d4";

	/** 图灵机器人的userid--任意字符串 */
	public static final String USERID = "1234567890ABCDEF";

	/** 若发送消息在该时间间隔之内，则界面上不显示时间 */
	public static final long interval = 30000;  //30秒
	
	/** 请求图灵服务器成功 */
	public static final int REQUEST_SUCCESS = 0;
	
	/** 请求图灵服务器失败 */
	public static final int REQUEST_ERROR = 1;
	
	/** 软键盘收回 */
	public static final int BIGGER = 0x110;
	
	/** 软键盘弹出 */
	public static final int SMALLER = 0x111;
	
	/** 布局大小发生变化-软键盘弹出收回时候 */
	public static final int MSG_RESIZE = 0X112;

	/** 打电话类型 */
	public static final int PHONE_TYPE = 0;

	/** 发短信类型 */
	public static final int SMS_TYPE = 1;

	/** 打开APP失败 */
	public static final int OPEN_APP_ERROR = 0;

	/** 打开APP成功 */
	public static final int OPEN_APP_SUCCESS = 1;

	/** 不是打开APP操作 */
	public static final int NOT_OPEN_APP = -1;

	/** 打电话失败 */
	public static final int PHONE_ERROR = 0;

	/** 打电话成功 */
	public static final int PHONE_SUCCESS = 1;

	/** 不是打电话操作 */
	public static final int NOT_PHONE = -1;

	/** 发短信失败 */
	public static final int SMS_ERROR = 0;

	/** 发短信成功 */
	public static final int SMS_SUCCESS = 1;

	/** 不是发短信操作 */
	public static final int NOT_SMS = -1;

}
