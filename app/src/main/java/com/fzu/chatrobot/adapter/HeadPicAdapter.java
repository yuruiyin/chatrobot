package com.fzu.chatrobot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.fzu.chatrobot.R;
import com.fzu.chatrobot.bean.HeadPic;

import java.util.List;

/**
 * 选择头像对话框中存放数据的adapter
 * Created by yury on 2016/8/28.
 */
public class HeadPicAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<HeadPic> mDatas;

    public HeadPicAdapter(Context context, List<HeadPic> mDatas) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public HeadPic getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HeadPic headPic = mDatas.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.head_pic_item, parent, false);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.id_head_pic);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.id_head_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mImageView.setImageResource(headPic.getPicResId());
        viewHolder.mTextView.setText(headPic.getPicInfo());
        return convertView;
    }

    private final class ViewHolder {
        ImageView mImageView;
        TextView mTextView;
    }
}
