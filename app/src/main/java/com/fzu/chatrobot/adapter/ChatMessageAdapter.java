package com.fzu.chatrobot.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fzu.chatrobot.MyApplication;
import com.fzu.chatrobot.R;
import com.fzu.chatrobot.bean.ChatMessage;
import com.fzu.chatrobot.bean.HeadPic;
import com.fzu.chatrobot.parameter.Parameter;
import com.fzu.chatrobot.parameter.ParameterSettings;
import com.fzu.chatrobot.utils.DialogUtil;
import com.fzu.chatrobot.utils.HideSoftKeyboard;
import com.fzu.chatrobot.utils.SpannableUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatMessageAdapter extends BaseAdapter {
//	private static final String TAG = "ChatMessageAdapter";
	
	private LayoutInflater mInflater;   //用来加载布局的
	private List<ChatMessage> mDatas;   //存放聊天的消息
	private Context myContext;

	private int robotPicId;             //机器人头像默认id
	private String robotName;           //机器人头像对应的名字
	private int userPicId;              //用户头像默认id

	public ChatMessageAdapter(Context context, List<ChatMessage> mDatas) {
		mInflater = LayoutInflater.from(context);
		myContext = context;
		this.mDatas = mDatas;
		updateHeadPicInfo();   //获取头像信息
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		ChatMessage chatMessage = mDatas.get(position);
		return chatMessage.getType();
	}
	
	@Override
	public int getViewTypeCount() {
		//adapter中的布局的个数，比如left_msg_item.xml和right_msg_item.xml
		return 2;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ChatMessage chatMessage = mDatas.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			if (getItemViewType(position) == 0) {  //left消息
				convertView = mInflater.inflate(R.layout.left_msg_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_from_msg_date);
				viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_from_msg);
				viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_from_pic);
				viewHolder.mName = (TextView) convertView.findViewById(R.id.id_from_name);

				final ImageView imageView = (ImageView) convertView.findViewById(R.id.id_from_pic);
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// 弹出对话框，让用户选择头像
//						Toast.makeText(myContext, viewHolder.mMsg.getText().toString(), Toast.LENGTH_SHORT).show();
						showDialogForChoosePic(0);
					}
				});
			} else {
				convertView = mInflater.inflate(R.layout.right_msg_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mDate = (TextView) convertView.findViewById(R.id.id_to_msg_date);
				viewHolder.mMsg = (TextView) convertView.findViewById(R.id.id_to_msg);
				viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_to_pic);
				viewHolder.mName = (TextView) convertView.findViewById(R.id.id_to_name);

				final ImageView imageView = (ImageView) convertView.findViewById(R.id.id_to_pic);
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// 弹出对话框，让用户选择头像
						showDialogForChoosePic(1);
					}
				});
			}
			convertView.setTag(viewHolder);
			// 注册长按事件，放这里避免多次注册长按事件。
			viewHolder.mMsg.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return showDialogAndCopy(viewHolder.mMsg);
				}
			});
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (getItemViewType(position) == 0) {
			viewHolder.mImageView.setImageResource(robotPicId);
			viewHolder.mName.setText(robotName);
		} else {
			viewHolder.mImageView.setImageResource(userPicId);
		}

		//设置数据
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
		if (chatMessage.getDate() != null) {
			viewHolder.mDate.setVisibility(View.VISIBLE);
			viewHolder.mDate.setText(df.format(chatMessage.getDate()));
		} else {
			viewHolder.mDate.setVisibility(View.GONE);
		}
		
		// 这里需要对msg里面的url进行超链接处理
//		LogUtil.d(TAG, "哈哈哈哈");
		if (!TextUtils.isEmpty(chatMessage.getMsg())) {
			if (getItemViewType(position) == 0) {  //left消息才进行url处理
				final String message = chatMessage.getMsg();
				viewHolder.mMsg.setText(SpannableUtil.getSpannableString(message,
						new SpannableUtil.OnUrlClickListener() {
							@Override
							public void onClick(String url) {
								gotoWebView(url, getWebViewTitle(message));
							}
						}));
				viewHolder.mMsg.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				viewHolder.mMsg.setText(chatMessage.getMsg());
			}
		} else {
			viewHolder.mMsg.setText("");
		}

		return convertView;
	}

	/**
	 * 通过图灵机器人返回的包括连接的消息，解析出webView的title，比如“相关新闻”
	 * @param message
	 * @return
	 */
	private String getWebViewTitle(String message) {
		int index = message.indexOf("找到");
		int enterIndex = message.indexOf("\n"); //保证肯定能找到
		return message.substring(index + 2, enterIndex);
	}


	/**
	 * 用户点击了textView中的URL，跳转到webView
	 * @param url 用户当前点击的url
	 * @param title webView的title
	 */
	private void gotoWebView(String url, String title) {
		// 1 打开浏览器
//		Uri uri = Uri.parse(url);
//		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		myContext.startActivity(intent);
		//2 使用webView
		HideSoftKeyboard.hideSoftKeyboard((Activity) myContext);
		Intent intent = new Intent();
		intent.setAction("com.fzu.chatrobot.action.WEBVIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("url", url);
		intent.putExtra("title", title);
		myContext.startActivity(intent);
	}

	/**
	 * 根据SharedPreferences中的值更新头像信息
	 */
	private void updateHeadPicInfo() {
		robotPicId = ParameterSettings.getInstance()
				.getParameterInt(Parameter.ROBOT_PIC_ID, Parameter.DEFAULT_ROBOT_PIC_ID);
		robotName = ParameterSettings.getInstance()
				.getParameterString(Parameter.ROBOT_NAME, Parameter.DEFAULT_ROBOT_NAME);
		userPicId = ParameterSettings.getInstance()
				.getParameterInt(Parameter.USER_PIC_ID, Parameter.DEFAULT_USER_PIC_ID);
	}

	/**
	 * 弹出对话框，并让用户选择头像
	 * @param type 0--代表左边头像， 1--代表右边头像
     */
	private void showDialogForChoosePic(final int type) {
		List<HeadPic> list = new ArrayList<HeadPic>();
		final HeadPicAdapter adapter;
		if (type == 0) {  //左边头像
			list.add(new HeadPic(R.drawable.robot_pic_red, "我是机器人小红", "小红"));
			list.add(new HeadPic(R.drawable.robot_pic_blue, "我是机器人小蓝", "小蓝"));
			list.add(new HeadPic(R.drawable.robot_pic_green, "我是机器人小绿", "小绿"));
		} else { //右边头像
			list.add(new HeadPic(R.drawable.boy_icon, "小鲜肉"));
			list.add(new HeadPic(R.drawable.girl_icon, "小萝莉"));
		}
		adapter = new HeadPicAdapter(myContext, list);

		String title = "请选择头像";
		DialogUtil.showDialogCancel(myContext, title, adapter, new DialogUtil.OnDialogItemClickListener() {
			@Override
			public void onClick(int which) {
				if (type == 0) {
					//保存在SharedPreferences中
					int robotPicId = adapter.getItem(which).getPicResId();
					String robotName = adapter.getItem(which).getName();
					ParameterSettings.getInstance().setParameter(
							Parameter.ROBOT_PIC_ID, robotPicId);
					ParameterSettings.getInstance().setParameter(
							Parameter.ROBOT_NAME, robotName);
					updateHeadPicInfo();
				} else {
					int userPicId = adapter.getItem(which).getPicResId();
					ParameterSettings.getInstance().setParameter(
							Parameter.USER_PIC_ID, userPicId);
					updateHeadPicInfo();
				}
				notifyDataSetChanged();
			}
		});
	}

	/**
	 * 弹出dialog并且复制消息到剪贴板
	 * @param textView 要复制的textView
	 * @return true or false
     */
	public boolean showDialogAndCopy(final TextView textView) {
		String[] items = {"复制"};
		String title = "提示";
		Dialog dialog = DialogUtil.showDialogCancel(myContext, title, items,
				new DialogUtil.OnDialogItemClickListener() {
			@Override
			public void onClick(int which) {
				//得到剪贴板管理器
				ClipboardManager cmb = (ClipboardManager) myContext.
						getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText("text", (textView.getText().toString().trim())));
				Toast.makeText(myContext, "已复制", Toast.LENGTH_SHORT).show();
			}
		});
		//修改dialog的窗口大小
		//先获取屏幕宽度和高度
		WindowManager wm = (WindowManager) myContext.
				getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int windowWidth = outMetrics.widthPixels;

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (int) (windowWidth * 0.6);
//				params.height = (int) (windowHeight * 0.3);
		dialog.getWindow().setAttributes(params);
		return true;
	}

	private final class ViewHolder {
		TextView mDate;
		TextView mMsg;
		ImageView mImageView;
		TextView mName;
	}

}
