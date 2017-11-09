package com.fzu.chatrobot;

import java.util.ArrayList;
import java.util.List;

import com.fzu.chatrobot.bean.AppInfo;
import com.fzu.chatrobot.utils.LogUtil;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class MyApplication extends Application {

	private static final String TAG = "MyApplication";

	private static Context mContext;
	
	private static long lastTime;  //上一次发送消息的时间
	
	private static List<AppInfo> mAppInfoList;  //存放已安装AppInfo列表

	private static String uniqueDeviceId;  //代表设备唯一标识

	private static boolean netState;    //标识网络是否通的全局变量

	@Override
	public void onCreate() {
		super.onCreate();
		lastTime = System.currentTimeMillis();
		mContext = getApplicationContext();
		mAppInfoList = new ArrayList<AppInfo>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				//获取已安装应用
				getAllInstallAppInfo();
				//获取联系人列表

			}
		}).start();

		getUniqueId();
		//初始化网络状态
		initNetState();
	}

	/**
	 * 初始化获取网络状态
	 */
	private void initNetState() {
		ConnectivityManager connectivityManager = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			netState = true;
		} else {
			netState = false;
		}
	}

	/**
	 * 设置网络状态
	 * @param value true--代表网络通，false代表网络不通
	 */
	public static void setNetState(boolean value) {
		netState = value;
	}

	/**
	 * 获取网络状态
	 * @return true--代表网络通，false代表网络不通
	 */
	public static boolean getNetState() {
		return netState;
	}

	/**
	 * 获取手机的唯一标识，比如IMEI
	 */
	private void getUniqueId() {
		// 获取手机IMEI
//		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//		imei = telephonyManager.getDeviceId();
		//		Toast.makeText(mContext, "IMEI: "+ imei, Toast.LENGTH_SHORT).show();
		//we make this look like a valid IMEI
		String mPreudoUniqueId = "35" +
				Build.BRAND.length() % 10 +
				Build.CPU_ABI.length() % 10 +
				Build.DEVICE.length() % 10 +
				Build.DISPLAY.length() % 10 +
				Build.HOST.length() % 10 +
				Build.PRODUCT.length() % 10 +
				Build.ID.length() % 10 +
				Build.MANUFACTURER.length() % 10 +
				Build.MODEL.length() % 10 +
				Build.TAGS.length() % 10 +
				Build.TYPE.length() % 10 +
				Build.BOARD.length() % 10 +
				Build.USER.length() % 10; //13 digits
		uniqueDeviceId = mPreudoUniqueId;
		LogUtil.d(TAG, " Pseudo-Unique ID: " + mPreudoUniqueId);
	}

	public static String getUniqueDeviceId() {
		return uniqueDeviceId;
	}

	/**
	 * 获取所有已安装的APP信息
	 */
	private void getAllInstallAppInfo() {
		PackageManager pm = getPackageManager();  //获取PackageManager对象
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获取所有ResolveInfo对象(里面可以获取APP的包名、label、主活动类名等信息)
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
		//根据name对resolveInfos进行排序，否则只会列出系统应用
//		Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo reInfo : resolveInfos) {
			String appLabel = (String) reInfo.loadLabel(pm);  //app label
			String packageName = reInfo.activityInfo.packageName; // app包名
			String mainActivityName = reInfo.activityInfo.name; //app主活动的类名
			Drawable icon = reInfo.loadIcon(pm);
			Intent launchIntent = new Intent(); //启动Intent，为其他APP提供
			launchIntent.setComponent(new ComponentName(packageName, mainActivityName));
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			AppInfo appInfo = new AppInfo();
			appInfo.setAppLabel(appLabel);
			appInfo.setAppIcon(icon);
			appInfo.setIntent(launchIntent);
			mAppInfoList.add(appInfo);
		}
		
	}
	
	/**
	 * 获取已安装APP的信息列表-对外暴露的方法
	 * @return AppInfo List
	 */
	public static List<AppInfo> getAppInfoList() {
		return mAppInfoList;
	}

	/**
	 * 获取上一条消息的时间
	 * @return 上一条消息时间
	 */
	public static long getLastTime() {
		return lastTime;
	}
	
	/**
	 * 设置上一条消息时间
	 * @param time 上一条消息时间
	 */
	public static void setLastTime(long time) {
		lastTime = time;
	}
	
	/**
	 * 获取上下文
	 * @return context
	 */
	public static Context getContext() {
		return mContext;
	}
	
	/**
	 * 退出应用
	 * (1) 关闭应用的所有的Activity
	 * (2) 结束当前进程
	 * @param exitCode 退出码
	 */
	public static void exitApplication(int exitCode) {
		ActivityCollector.finishAll();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(exitCode);  //退出虚拟机，可清除后台缓存的进程
	}

}
