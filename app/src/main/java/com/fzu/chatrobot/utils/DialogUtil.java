package com.fzu.chatrobot.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * 各种dialog
 * Created by yury on 2016/8/28.
 */
public class DialogUtil {

    private DialogUtil() {}

	/**
     * 弹出只包含取消按钮的对话框-对话框内容为apapter（listview）
     * @param context Activity context
     * @param title 对话框title
     * @param adapter adapter数据
     * @param listener item点击事件监听器
     * @return dialog
     */
    public static Dialog showDialogCancel(Context context, String title, ListAdapter adapter,
                                        final OnDialogItemClickListener listener) {
        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(which);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

	/**
	 * 弹出只包含取消按钮的对话框-对话框内容为CharSequence[]
     * @param context Activity context
     * @param title 对话框title
     * @param items CharSequence[]数据
     * @param listener item点击事件监听器
     * @return dialog
     */
    public static Dialog showDialogCancel(Context context, String title, CharSequence[] items,
                                        final OnDialogItemClickListener listener) {
        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(which);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

	/**
     * dialog中每个item点击监听器
     */
    public interface OnDialogItemClickListener {
		/**
         * 点击item事件回调
         * @param which 代表哪个item
         */
        void onClick(int which);
    }

}
