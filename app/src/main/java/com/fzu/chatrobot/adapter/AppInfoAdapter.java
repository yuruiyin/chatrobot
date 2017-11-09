package com.fzu.chatrobot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fzu.chatrobot.R;
import com.fzu.chatrobot.bean.AppInfo;

import java.util.List;

/**
 * 已安装应用信息
 * Created by yury on 2016/8/29.
 */
public class AppInfoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<AppInfo> mDatas;

	public AppInfoAdapter(Context context, List<AppInfo> mDatas) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public AppInfo getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppInfo appInfo = mDatas.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.app_info_item, parent, false);
			viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.id_app_icon);
			viewHolder.appLabel = (TextView) convertView.findViewById(R.id.id_app_label);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.appIcon.setImageDrawable(appInfo.getAppIcon());
		viewHolder.appLabel.setText(appInfo.getAppLabel());
		return convertView;
	}

	private static class ViewHolder {
		ImageView appIcon;
		TextView appLabel;
	}
}
