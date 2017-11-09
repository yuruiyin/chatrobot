package com.fzu.chatrobot.utils;

import java.util.Locale;

public class StringUtil {
	
	private StringUtil() {}  //禁止实例化

	/**
	 * 判断字符串是否包含偶数个否定字（比如'不'、'莫'、'别'）
	 * @param str 要判断的字符串
	 * @return true--偶数个否定字 false--奇数个否定字
	 */
	private static boolean checkNoWordNum(String str) {
		int len = str.length();
		int noWordNum = 0;
		for (int i = 0; i < len; i ++) {
			if (str.charAt(i) == '不' || str.charAt(i) == '别' || str.charAt(i) == '莫') {
				noWordNum++;
			}
		}
		return noWordNum % 2 == 0;
	}

	/**
	 * 判断是否为发短信需求规定以如下6种形式出现的才认为是发短信操作
	 * (1) 给XXX发短信; (2) 给XXX发个短信; (3) 发个短信给XXX; (4) 发短信给XXX; (5) 给XXX发条短信; (6) 发条短信给XXX
	 * @param message 用户请求的内容
	 * @return true--是发短信需求  false--不是发短信需求
	 */
	public static boolean isSMSMessage(final String message) {
		if (!message.contains("给")) {
			return false;
		}

		if (!message.contains("发短信") && !message.contains("发个短信") &&
				!message.contains("发条短信")) {
			return false;
		}

		int geiWordIndex = message.indexOf("给");
		int smsWordIndex1 = message.indexOf("发短信");
		int smsWordIndex2 = message.indexOf("发个短信");
		int smsWordIndex3 = message.indexOf("发条短信");
		int smsWordIndex = -1;
		if (smsWordIndex1 != -1) {
			smsWordIndex = smsWordIndex1;
		} else if (smsWordIndex2 != -1) {
			smsWordIndex = smsWordIndex2;
		} else {
			smsWordIndex = smsWordIndex3;
		}

		int index = geiWordIndex < smsWordIndex ? geiWordIndex : smsWordIndex;
		return checkNoWordNum(message.substring(0, index));
	}

	/**
	 * 判断是否为电话号码
	 * @param str 要判断的字符串
	 * @return true--是电话号码 false-不是电话号码
     */
	public static boolean isPhoneNumber(final String str) {
		//暂且先认为全数字的就是电话号码吧
		for (int i = 0; i < str.length(); i ++) {
			if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断消息是否为打电话需求，规定以如下四种形式出现的才认为是打电话操作
	 * (1) 给XXX打个电话; (2) 给XXX打电话; (3) 打电话给XXX; (4) 打个电话给XXX
	 * @param message 用户发送的消息
	 * @return true--是打电话需求， false--不是打电话需求
     */
	public static boolean isCallMessage(final String message) {
		if (!message.contains("打电话") && !message.contains("打个电话")) {
			return false;
		}

		if (!message.contains("给")) {
			return false;
		}

		int geiWordIndex = message.indexOf("给");
		int callWordIndex1 = message.indexOf("打电话");
		int callWordIndex2 = message.indexOf("打个电话");
		int callWordIndex = callWordIndex1 != -1 ? callWordIndex1 : callWordIndex2;
		int index = geiWordIndex < callWordIndex ? geiWordIndex : callWordIndex;

		return checkNoWordNum(message.substring(0, index));
	}

	/**
	 * 解析用户发送文字的含义，判断是否有打开或开启APP的意思
	 * @param toMsg 用户发送的消息
	 * @return true--打开或开启APP，false--不打开或开启APP
	 */
	public static boolean isOpenApp(final String toMsg) {
		String msg = toMsg.toLowerCase(Locale.US);
		int openWordIndex = -1;
		if (msg.contains("打开")) {
			openWordIndex = msg.indexOf("打开");
		} else if (msg.contains("开启")) {
			openWordIndex = msg.indexOf("开启");
		}

		if (openWordIndex == -1) {
			return false;
		}

		return checkNoWordNum(msg.substring(0, openWordIndex));
	}



	/**
	 * 获取两个字符串的最长公共子串的长度
	 * @param string1 字符串1
	 * @param string2 字符串2
	 * @return 最长公共子串的长度
	 */
	public static int getLCSLength(String string1, String string2) {
		String str1 = string1.toLowerCase(Locale.US);
		String str2 = string2.toLowerCase(Locale.US);
		int len1 = str1.length();
		int len2 = str2.length();
		int maxLen = len1 > len2 ? len1 : len2;
		int res = 0;  //保存最长公共子串的长度
		int[] lenArray = new int[maxLen];  //保存公共子串的长度数组
		for (int i = 0; i < len1; i ++) {
			for (int j = len2 - 1; j >= 0; j --) {
				if (str1.charAt(i) == str2.charAt(j)) {
					if (i == 0 || j == 0) {
						lenArray[j] = 1;
					} else {
						lenArray[j] = lenArray[j - 1] + 1;
						lenArray[j - 1] = 0;
					}
					if (lenArray[j] > res) {
						res = lenArray[j];
					}
				} else {
					lenArray[j] = 0;
				}
			}
		}
		return res;
	}

}
