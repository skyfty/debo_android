package com.qcwl.debo.ui.found.joke.ruidong_video;

import android.content.DialogInterface;

/**
 * 配置对话框listener
 */
public interface ConfigDialogListener extends DialogInterface.OnDismissListener {
	/**
	 * 响应保存配置数据
	 * 
	 * @param configData
	 */
	void onSaveConfigData(ConfigData configData);
}
