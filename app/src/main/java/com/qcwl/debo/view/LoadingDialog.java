package com.qcwl.debo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.qcwl.debo.R;

/**
 * 加载中Dialog
 * @author lexyhp
 */
public class LoadingDialog extends Dialog {

	private TextView tips_loading_msg;
	private int layoutResId;
	private String message = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * 上下文
	 * @param layoutResId
	 * 要传入的dialog布局文件的id
	 */
	public LoadingDialog(Context context, int layoutResId) {
		super(context,R.style.dialog_style);
		this.layoutResId = layoutResId;
		message = "加载中...";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutResId);
		tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
		tips_loading_msg.setText(this.message);
	}
}
