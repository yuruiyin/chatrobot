package com.fzu.chatrobot.bean;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * 已安装APP的信息
 * @author yury
 *
 */
public class AppInfo implements  Comparable<Object> {
	/** app的名称 */
	private String appLabel;

	/** APP的图标 */
	private Drawable appIcon;
	
	/** 给其他APP提供启动本APP的intent，封装了包名和主活动类名 */
	private Intent intent;

	/** 根据用户请求的内容与AppLabel的匹配程度 */
	private double matchDegree;
	
	public String getAppLabel() {
		return appLabel;
	}
	
	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public Intent getIntent() {
		return intent;
	}
	
	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public double getMatchDegree() {
		return matchDegree;
	}

	public void setMatchDegree(double matchDegree) {
		this.matchDegree = matchDegree;
	}

	@Override
	public int compareTo(Object another) {
		if (this == another) {
			return 0;
		}

		if (another != null && another instanceof AppInfo) {
			AppInfo a = (AppInfo) another;
			if (this.getMatchDegree() >= a.getMatchDegree()) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return -1;
		}
	}
}
