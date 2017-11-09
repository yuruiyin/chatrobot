package com.fzu.chatrobot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2016/8/30.
 */
public class GuidanceAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mDatas;

	public GuidanceAdapter(Context context, List<String> mDatas) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public String getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return null;
	}
}
