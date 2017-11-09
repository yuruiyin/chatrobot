package com.fzu.chatrobot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * 管理Activity，实现应用程序完全退出功能
 * @author yury
 *
 */
public class ActivityCollector {
	
	/** 存放activity的list */
	private static List<Activity> activityList = new ArrayList<Activity>();
	
	/**
	 * 构造函数私有化，防止被实例化
	 */
	private ActivityCollector() {}
	
	/**
	 * 注册activity
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	/**
	 * 注销activity
	 * @param activity
	 */
	public static void removeActivity(Activity activity) {
		if (!activityList.isEmpty()) {
			activityList.remove(activity);
		}
	}
	
	/**
	 * 关闭应用所有的activity
	 */
	public static void finishAll() {
		for (Activity activity : activityList) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		activityList.clear();
	}
	
	/**
	 * 获取当前可见的activity
	 * @return 当前可见的activity
	 */
	public static Activity getTopActivity() {
		if (activityList.isEmpty()) {
			return null;
		}
		return activityList.get(activityList.size() - 1);
	}
	
}
