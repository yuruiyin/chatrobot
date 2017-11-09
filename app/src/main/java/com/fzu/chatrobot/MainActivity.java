package com.fzu.chatrobot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import com.fzu.chatrobot.adapter.AppInfoAdapter;
import com.fzu.chatrobot.adapter.ChatMessageAdapter;
import com.fzu.chatrobot.bean.AppInfo;
import com.fzu.chatrobot.bean.ChatMessage;
import com.fzu.chatrobot.bean.MatchContacts;
import com.fzu.chatrobot.custom.MyListView;
import com.fzu.chatrobot.custom.ResizeLayout;
import com.fzu.chatrobot.custom.ResizeLayout.OnResizeListener;
import com.fzu.chatrobot.custom.Topbar;
import com.fzu.chatrobot.http.HttpUtil;
import com.fzu.chatrobot.parameter.Parameter;
import com.fzu.chatrobot.parameter.ParameterSettings;
import com.fzu.chatrobot.utils.DialogUtil;
import com.fzu.chatrobot.utils.HideSoftKeyboard;
import com.fzu.chatrobot.utils.JsonParser;
import com.fzu.chatrobot.utils.LogUtil;
import com.fzu.chatrobot.utils.StringUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

public class MainActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static MyListView mMsgs;
	private static ChatMessageAdapter mAdapter;
	private static List<ChatMessage> mDatas;

	private EditText mInputMsg;
	private Button mSendMsg;
	private ImageButton mSpeak;
	private long exitTime = 0;
	private Topbar mTopbar;
	private Context mContext;

	private NetworkReceiver networkReceiver;

	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	//图灵服务器API Manager
	private TuringApiManager mTuringApiManager;

	// Handler必须设为static，防止内存泄漏，即若有消息在消息队列中，那么handler就无法被回收
	// 这样会导致使用该Handler的Activity或Service无法被回收，即便是它们的onDestroy方法被调用。
	private MyHandler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		LogUtil.d(TAG, "this process id is " + Process.myPid());

		mContext = this;
		mHandler = new MyHandler(MainActivity.this);

		initViews();  //初始化布局
		initDatas();  //初始化listview数据
		initEvents(); //初始化各个View的事件
		initTuringAndSpeechSdk();  //初始化Turing SDK
		regReceiver();
	}

	/**
	 * 注册广播
	 */
	private void regReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		networkReceiver = new NetworkReceiver();
		registerReceiver(networkReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(networkReceiver);  //解注册广播接收器
	}

	/**
	 * 初始化科大讯飞语音听写、图灵语义理解SDK
	 */
	private void initTuringAndSpeechSdk() {
		// 1.初始化--创建语音配置对象
		SpeechUtility.createUtility(mContext, "appid="+ getString(R.string.app_id));
		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
		mIatDialog = new RecognizerDialog(mContext, mInitListener);
		mIatDialog.setListener(mRecognizerDialogListener);  //设置语音听写监听器

		// 2.初始化图灵SDK
		initTuring();
	}

	/**
	 * 初始化图灵SDK
	 */
	private void initTuring() {
		SDKInitBuilder builder = new SDKInitBuilder(this)
				.setSecret(MyConstants.SECRET).setTuringKey(MyConstants.API_KEY)
				.setUniqueId(MyApplication.getUniqueDeviceId());
		SDKInit.init(builder,new com.turing.androidsdk.InitListener() {
			@Override
			public void onFail(String error) {
				LogUtil.d(TAG, error);
			}
			@Override
			public void onComplete() {
				// 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
				mTuringApiManager = new TuringApiManager(MainActivity.this);
				mTuringApiManager.setHttpListener(myHttpConnectionListener);
			}
		});
	}

	/**
	 * 网络请求回调
	 */
	HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {
		@Override
		public void onSuccess(RequestResult result) {
			//请求图灵服务器成功回调
			ChatMessage chatMessage;
			Message m;
			if (result != null) {
				LogUtil.d(TAG, result.getContent().toString());
				chatMessage = HttpUtil.getChatMessageFromResponse(
						result.getContent().toString());
				//子线程中不允许更新UI，应该使用异步消息处理机制
				m = Message.obtain();
				m.what = MyConstants.REQUEST_SUCCESS;
				m.obj = chatMessage;
				mHandler.sendMessage(m);
			} else {
				m = Message.obtain();
				m.what = MyConstants.REQUEST_ERROR;
				m.obj = "服务器繁忙，请稍后再试!";
				mHandler.sendMessage(m);
			}
		}

		@Override
		public void onError(ErrorMessage errorMessage) {
			LogUtil.d(TAG, errorMessage.getMessage());
			//子线程中不允许更新UI，应该使用异步消息处理机制
			String errMsg;
			if (!MyApplication.getNetState()) {
				errMsg = "您的设备网络不通，请检查是否打开网络连接！";
			} else{
				errMsg = "服务器繁忙，请稍后再试!";
			}
			Message m = Message.obtain();
			m.what = MyConstants.REQUEST_ERROR;
			m.obj = errMsg;
			mHandler.sendMessage(m);
		}
	};

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		//识别成功回调
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}
		//识别错误回调
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	/**
	 * 处理云端返回的语音识别结果
	 * @param results 云端返回的语音识别结果
	 */
	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuilder resultBuilder = new StringBuilder();
		for (String key : mIatResults.keySet()) {
			resultBuilder.append(mIatResults.get(key));
		}

		mInputMsg.setText(resultBuilder.toString());
	}

	/**
	 * 检查软键盘的状态，若软键盘弹出，则需要更新ListView的最后一个Item的显示
	 */
	private void checkSoftKeyboardState() {
		ResizeLayout mainLayout = (ResizeLayout) findViewById(R.id.root_layout);
		mainLayout.setOnResizeListener(new OnResizeListener() {
			@Override
			public void onResize(int w, int h, int oldw, int oldh) {
				int change = MyConstants.BIGGER;
				if (h < oldh) {
					change = MyConstants.SMALLER;
				}
				//这里是在布局内，要改变Ui布局，需要在Activity中使用Handler对象修改。
				Message msg = Message.obtain();
				msg.what = MyConstants.MSG_RESIZE;
				msg.obj = change;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 获取要开启的APP中最为匹配的前几个APP
	 * @param toMsg 用户发送的消息
	 * @return 最匹配的AppInfo list
	 */
	private List<AppInfo> getMostMatchApps(final String toMsg) {
		String msg = toMsg.toLowerCase(Locale.US);
		List<AppInfo> resAppInfoList = new ArrayList<AppInfo>();
		int openWordIndex = -1;
		if (msg.contains("打开")) {
			openWordIndex = msg.indexOf("打开");
		} else if (msg.contains("开启")) {
			openWordIndex = msg.indexOf("开启");
		}

		if (openWordIndex == -1 || openWordIndex + 2 == msg.length()) {
			return null;
		}

		msg = msg.substring(openWordIndex + 2, msg.length());  //截取"打开"或"开启"后面的字符串
		List<AppInfo> appInfoList = MyApplication.getAppInfoList();
		for (AppInfo appInfo : appInfoList) {
			//求最长公共子串,采用DP
			int lcsLength = StringUtil.getLCSLength(msg, appInfo.getAppLabel());
			if (lcsLength >= 2  || lcsLength == 1 && lcsLength == appInfo.getAppLabel().length()) {
				appInfo.setMatchDegree(lcsLength * 1.0 / appInfo.getAppLabel().length());
				resAppInfoList.add(appInfo);
			}
		}

		return resAppInfoList;
	}

	/**
	 * 判断用户发送的消息是否为打开APP的操作,若未打开操作，则判断要打开的app是否存在
	 * @return OPEN_APP_SUCCESS--代表打开APP成功，OPEN_APP_ERROR--要打开的app不存在，
	 * 			NOT_OPEN_APP--不是打开或开启操作
	 */
	private int isOpenOtherApp(final String toMsg) {
		String msg = toMsg.toLowerCase(Locale.US);
		if (StringUtil.isOpenApp(msg)) {
			// 首先，获取与toMsg匹配的前几个AppInfo
			final List<AppInfo> matchAppInfoList = getMostMatchApps(msg);
			if (matchAppInfoList == null || matchAppInfoList.size() == 0) {
				return MyConstants.OPEN_APP_ERROR;
			}
			//首先，对appInfo列表按照匹配度排序
			Collections.sort(matchAppInfoList, new Comparator<AppInfo>() {
				@Override
				public int compare(AppInfo lhs, AppInfo rhs) {
					return lhs.compareTo(rhs);
				}
			});

			final AppInfoAdapter adapter = new AppInfoAdapter(mContext, matchAppInfoList);
			String title = "请选择要打开的APP";
			// 弹出对话框，让用户选择要开启的app
			DialogUtil.showDialogCancel(mContext, title, adapter, new DialogUtil.OnDialogItemClickListener() {
				@Override
				public void onClick(int which) {
					mContext.startActivity(adapter.getItem(which).getIntent());
				}
			});
			//把软键盘收回
			HideSoftKeyboard.hideSoftKeyboard((Activity) mContext);
			return MyConstants.OPEN_APP_SUCCESS;
		} else {
			return MyConstants.NOT_OPEN_APP;
		}
	}

	/**
	 * 封装发送和接受消息需要执行的操作
	 * @param msgContent 消息内容
	 * @param msgType 消息类型
	 */
	private static void handleMsg(String msgContent, int msgType) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(msgType);
		chatMessage.setMsg(msgContent);
		long curTime = System.currentTimeMillis();
		//只有两次发送消息的时间超过设定的时间间隔，才会显示时间
		if (curTime - MyApplication.getLastTime() >= MyConstants.interval) {
			chatMessage.setDate(new Date());
		} else {
			chatMessage.setDate(null);
		}
		MyApplication.setLastTime(curTime);
		mDatas.add(chatMessage);
		mAdapter.notifyDataSetChanged();
		mMsgs.setSelection(mDatas.size() - 1);
	}

	/**
	 * 请求图灵服务器
	 * @param toMsg 要请求的消息
	 */
	private void handleHttpRequest(String toMsg) {
		if (mTuringApiManager != null) {
			mTuringApiManager.requestTuringAPI(toMsg);
		} else {
			//此时，认为网络不可用，不要去请求图灵服务器，直接本地处理
			Message m = Message.obtain();
			m.what = MyConstants.REQUEST_ERROR;
			m.obj = "您的设备网络不通，请检查是否打开网络连接！";
			mHandler.sendMessage(m);
		}
	}

	/**
	 * 封装打电话操作
	 * @param telphoneNum 电话号码
     */
	private void telphoneOperator(String telphoneNum) {
		//把软键盘收回
		HideSoftKeyboard.hideSoftKeyboard((Activity) mContext);
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + telphoneNum));
		startActivity(callIntent);
	}

	/**
	 * 判断用户是否要打电话给某某
	 * @param toMsg 用户请求的内容
	 * @return 1--代表打电话成功，0--代表手机上找不到要拨打的联系人号码，-1不是打电话操作
     */
	private int isRequireCall(String toMsg) {
		if (StringUtil.isCallMessage(toMsg)) {
			//获取“给”字后面的人名（电话簿里面记录的）或者电话号码
			int geiWordIndex = toMsg.indexOf("给");
			int callWordIndex1 = toMsg.indexOf("打电话");
			int callWordIndex2 = toMsg.indexOf("打个电话");
			int callWordIndex = callWordIndex1 != -1 ? callWordIndex1 : callWordIndex2;
			String callTarget;  //要拨打的目标，人名或电话号码
			if (geiWordIndex < callWordIndex) {
				//如"给XXX打电话"或"给XXX打个电话"
				callTarget = toMsg.substring(geiWordIndex + 1, callWordIndex);
			} else {
				//如"打电话给XXX"或"打个电话给XXX"
				callTarget = toMsg.substring(geiWordIndex + 1, toMsg.length());
			}
			if (TextUtils.isEmpty(callTarget)) {
				return MyConstants.PHONE_ERROR;
			}
			// 判断用户是否给了电话号码
			if(StringUtil.isPhoneNumber(callTarget)) {
				telphoneOperator(callTarget);  //拨号
				return MyConstants.PHONE_SUCCESS;
			}
			// 不是电话号码，那就寻找电话簿，看看是否能找到该联系人
			return readContacts(callTarget, MyConstants.PHONE_TYPE);
		} else {
			return MyConstants.NOT_PHONE;  //不是打电话需求
		}
	}

	/**
	 * 查询要拨打（发短信）的联系人号码, 如找到，直接拨号
	 * @param contactsName 联系人名字
	 * @param type 判断是否打电话还是发短信
	 * @return 1--找到联系人  0--找不到联系人
     */
	private int readContacts(String contactsName, final int type) {
		Cursor cursor = null;
		Set<MatchContacts> contactsSet = new HashSet<MatchContacts>();  //匹配到的联系人列表
		try {
			cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, null, null, null);
			while (cursor.moveToNext()) {
				String displayName = cursor.getString(cursor.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				int lcsLen = StringUtil.getLCSLength(contactsName, displayName);
				if (lcsLen > 0) {  //找打一个字匹配的都认为有可能是用户要拨打的联系人
					String phoneNum = cursor.getString(cursor.getColumnIndex(
							ContactsContract.CommonDataKinds.Phone.NUMBER));
					double matchDegree = lcsLen * 1.0 / displayName.length();
					contactsSet.add(new MatchContacts(displayName, phoneNum, matchDegree));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		if (contactsSet == null || contactsSet.size() == 0) {
			//找不到联系人
			return type == MyConstants.PHONE_TYPE ? MyConstants.PHONE_ERROR : MyConstants.SMS_ERROR;
		}

		//对contactsSet进行排序，根据匹配度从大到小排序
		TreeSet<MatchContacts> contactsTreeSet = new TreeSet<MatchContacts>(new Comparator<MatchContacts>() {
			@Override
			public int compare(MatchContacts lhs, MatchContacts rhs) {
				return lhs.compareTo(rhs);
			}
		});
		for (MatchContacts m : contactsSet) {
			contactsTreeSet.add(m);
		}

		//弹出对话框列表，让用户自己选择
		final String[] contactsStr = new String[contactsTreeSet.size()];
		int num = 0;
		for (MatchContacts contacts : contactsTreeSet) {
			contactsStr[num++] = contacts.getDisplayName() + "\n" + contacts.getPhoneNum();
		}
		String title = type == MyConstants.PHONE_TYPE ? "请选择联系人" : "请选择收信人";
		DialogUtil.showDialogCancel(mContext, title, contactsStr, new DialogUtil.OnDialogItemClickListener() {
			@Override
			public void onClick(int which) {
				String curContacts = contactsStr[which];
				String curPhoneNum = curContacts.substring(curContacts.indexOf("\n") + 1,
						curContacts.length());
				if (type == MyConstants.PHONE_TYPE) {
					telphoneOperator(curPhoneNum); // 拨号
				} else if (type == MyConstants.SMS_TYPE) {
					smsOperator(curPhoneNum);
				}
			}
		});
		//把软键盘收回
		HideSoftKeyboard.hideSoftKeyboard((Activity) mContext);
		return type == MyConstants.PHONE_TYPE ? MyConstants.PHONE_SUCCESS : MyConstants.SMS_SUCCESS;
	}

	/**
	 * 封装发短信操作
	 * @param telphoneNum 电话号码
	 */
	private void smsOperator(String telphoneNum) {
		//把软键盘收回
		HideSoftKeyboard.hideSoftKeyboard((Activity) mContext);
		Uri uri = Uri.parse("smsto:" + telphoneNum);
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
		startActivity(smsIntent);
	}

	/**
	 * 判断是否为发短信操作
	 * @param toMsg 用户请求的内容
	 * @return 1--代表发短信成功，0--代表手机上找不到指定的联系人，-1不是发短信操作
	 */
	private int isRequireSMS(String toMsg) {
		if (StringUtil.isSMSMessage(toMsg)) {
			//获取“给”字后面的联系人姓名或者电话号码
			int geiWordIndex = toMsg.indexOf("给");
			int smsWordIndex1 = toMsg.indexOf("发短信");
			int smsWordIndex2 = toMsg.indexOf("发个短信");
			int smsWordIndex3 = toMsg.indexOf("发条短信");
			int smsWordIndex;
			String smsTarget; //收信人
			if (smsWordIndex1 != -1) {
				smsWordIndex = smsWordIndex1;
			} else if (smsWordIndex2 != -1) {
				smsWordIndex = smsWordIndex2;
			} else {
				smsWordIndex = smsWordIndex3;
			}

			if (geiWordIndex < smsWordIndex) {  //如"给XXX发短信"这种形式
				smsTarget = toMsg.substring(geiWordIndex + 1, smsWordIndex);
			} else {  //如“发短信给XXX”这种形式
				smsTarget = toMsg.substring(geiWordIndex + 1, toMsg.length());
			}

			if (TextUtils.isEmpty(smsTarget)) {
				return MyConstants.SMS_ERROR;
			}

			if (StringUtil.isPhoneNumber(smsTarget)) {  //用户直接提供了收信人的号码
				smsOperator(smsTarget);
				return MyConstants.SMS_SUCCESS;
			}
			//到手机电话簿里寻找联系人
			return readContacts(smsTarget, MyConstants.SMS_TYPE);
		} else {
			return MyConstants.NOT_SMS;
		}
	}

	/**
	 * 发送消息
	 */
	private void handleSendMsg() {
		String toMsg = mInputMsg.getText().toString(); //获取输入框的内容
		if (TextUtils.isEmpty(toMsg)) {
			return;
		}
		//发送消息，创建ChatMessage对象，并使用adapter通知ListView修改
		handleMsg(toMsg, ChatMessage.TYPE_SENT);
		mInputMsg.setText("");   //发送出去之后，输入框置空

		//判断发送消息是否为空字符串（包含多个空格构成）
		if (TextUtils.isEmpty(toMsg.trim())) {
			handleMsg("宝宝真心不知道您在说啥", ChatMessage.TYPE_RECEIVED);
			return;
		}

		//判断该消息是否是打开APP的操作
		int res = isOpenOtherApp(toMsg);
		if (res == MyConstants.OPEN_APP_SUCCESS) {
			handleMsg("好的", ChatMessage.TYPE_RECEIVED);
			return;
		} else if (res == MyConstants.OPEN_APP_ERROR) {
			handleMsg("由于您未安装相关APP，因此无法为您打开", ChatMessage.TYPE_RECEIVED);
			return;
		}
		//返回MyConstants.NOT_OPEN_APP,说明不是打开或开启app的操作

		//判断是否为打电话操作
		int res1 = isRequireCall(toMsg);
		if (res1 == MyConstants.PHONE_SUCCESS) {
			handleMsg("好的", ChatMessage.TYPE_RECEIVED);
			return;
		} else if (res1 == MyConstants.PHONE_ERROR) {
			handleMsg("由于无法找到相关联系人，因此无法为您拨打", ChatMessage.TYPE_RECEIVED);
			return;
		}
		//返回MyConstants.NOT_PHONE,说明不是打电话操作

		//判断是否为发短信操作
		int res2 = isRequireSMS(toMsg);
		if (res2 == MyConstants.SMS_SUCCESS) {
			handleMsg("好的", ChatMessage.TYPE_RECEIVED);
			return;
		} else if (res2 == MyConstants.SMS_ERROR) {
			handleMsg("由于无法找到相关联系人，因此无法为您发送短信", ChatMessage.TYPE_RECEIVED);
			return;
		}
		//返回MyConstants.NOT_SMS,说明不是发短信操作

		//请求图灵服务器
		handleHttpRequest(toMsg);
	}

	/**
	 * 处理语音操作
	 */
	private void handleSpeak() {
		mInputMsg.setText(null); // 清空显示内容
		mIatResults.clear(); // 清空hashmap
		// 显示听写对话框
//		mIatDialog.setListener(mRecognizerDialogListener);
		mIatDialog.show();
		showTip(getString(R.string.text_begin));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_send_msg:
			handleSendMsg();  //处理发送按钮的点击事件
			break;
		case R.id.id_speak:
			handleSpeak();    //点击“说话”按钮
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化View的事件
	 */
	private void initEvents() {
		mSendMsg.setOnClickListener(this);   // 注册发送按钮的监听器
		// 为该Activity中的每个控件注册关闭软键盘的监听器
		setOnListenerForCloseKeyboard(findViewById(R.id.id_listview_msgs));
		//检测软键盘的弹出和隐藏状态,若弹出软键盘，则重新定位listView
		checkSoftKeyboardState();
		//监听输入框的变化，若输入框不为空，则改变button背景图片
		mInputMsg.addTextChangedListener(this);
		//注册“说话”按钮的点击事件
		mSpeak.setOnClickListener(this);
		//注册topbar的监听器
		mTopbar.setLeftIsvisable(false);
		mTopbar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
			@Override
			public void leftClick() {
				// do nothing
			}
			@Override
			public void rightClick() {
				gotoGuidanceActivity();
			}
		});
	}

	/**
	 * 初始化数据：包括ListView、常用app名称集合
	 */
	private void initDatas() {
		//ListView
		String robotName = ParameterSettings.getInstance().
				getParameterString(Parameter.ROBOT_NAME, Parameter.DEFAULT_ROBOT_NAME);
		mDatas = new ArrayList<ChatMessage>();
		mDatas.add(new ChatMessage(new Date(), "主银，机器人" + robotName +
				"为您服务", ChatMessage.TYPE_RECEIVED));
		mAdapter = new ChatMessageAdapter(this, mDatas);
		mMsgs.setAdapter(mAdapter);
	}

	/**
	 * 初始化View，通过id获取各View对象
	 */
	private void initViews() {
		mMsgs = (MyListView) findViewById(R.id.id_listview_msgs);
		mInputMsg = (EditText) findViewById(R.id.id_input_msg);
		mSendMsg = (Button) findViewById(R.id.id_send_msg);
		mSpeak = (ImageButton) findViewById(R.id.id_speak);
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
	}

	@Override
	protected void afterEditTextChanged(Editable s) {
		//输入框改变时候，判断输入框的字符个数, 若个数大于0，则修改发送按钮的背景
		int curInputCount = s.length();
		if (curInputCount > 0) {
			mSendMsg.setBackgroundResource(R.drawable.send_btn_has_input);
		} else {
			mSendMsg.setBackgroundResource(R.drawable.send_btn_no_input);
		}
	}

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - exitTime > 2000) {
			showTip("再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			MyApplication.exitApplication(0);
		}
	}

	/**
	 * 跳转到使用指南界面
	 */
	private void gotoGuidanceActivity() {
		Intent intent = new Intent(MainActivity.this, GuidanceActivity.class);
		startActivity(intent);
	}

	static class MyHandler extends Handler {

		private WeakReference<MainActivity> mWeakActivity;

		public MyHandler(MainActivity activity) {
			mWeakActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainActivity activity = mWeakActivity.get();
			if (activity == null) {
				LogUtil.d(TAG, "MainActivity is null");
			} else {
				switch (msg.what) {
					case MyConstants.REQUEST_SUCCESS:   //成功获取图灵机器人服务器发来的消息
						ChatMessage fromMessage = (ChatMessage) msg.obj;
						activity.mDatas.add(fromMessage);
						activity.mAdapter.notifyDataSetChanged();
						activity.mMsgs.setSelection(mDatas.size() - 1);
						break;
					case MyConstants.REQUEST_ERROR:     //获取失败
						activity.handleMsg((String)msg.obj, ChatMessage.TYPE_RECEIVED);
						break;
					case MyConstants.MSG_RESIZE:        //软键盘弹出或隐藏
						int state = Integer.parseInt(String.valueOf(msg.obj));
						if (state == MyConstants.SMALLER) {
//					Toast.makeText(mContext, "软键盘弹出", Toast.LENGTH_SHORT).show();
							activity.mMsgs.setSelection(mDatas.size() - 1);
						}
						break;
					default:
						break;
				}
			}
		}
	}

	class NetworkReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isAvailable()) {
				MyApplication.setNetState(true);
				//还需要判断图灵机器人SDK是否初始化过
				if (mTuringApiManager == null) {
					initTuring();
				}
			} else {
				MyApplication.setNetState(false);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LogUtil.d(TAG, "YYYY:onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
}
