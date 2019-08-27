/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qcwl.debo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.chat.bean.Rand_User_Data;
import com.qcwl.debo.utils.CallBackUtils;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatOverDialog extends Dialog {
	
	public interface AlertDialogUser {
		void onResult(int confirmed, Bundle bundle);
	}

	private String title;
	private String msg;
	private AlertDialogUser user;
	private Bundle bundle;
	private boolean showCancel = false;
	private Context context;
	public ChatOverDialog(Context context, String msg, AlertDialogUser alertDialogUser) {
		super(context,R.style.chat_over_dialog);
		this.msg = msg;
		this.context = context;
		user = alertDialogUser;
		this.setCanceledOnTouchOutside(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_over);
		Rand_User_Data data = CallBackUtils.doCallBackMethod();
		TextView tv_continue = (TextView)findViewById(R.id.tv_continue);
		TextView tv_return = (TextView)findViewById(R.id.tv_return);
		TextView tv_add = (TextView) findViewById(R.id.tv_add);
		
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.tv_continue) {
					onNext(view);
				} else if (view.getId() == R.id.tv_return) {
					onCancel(view);
				}else if (view.getId() == R.id.tv_add) {
					onAdd(view);
				}
			}
		};
		tv_add.setOnClickListener(listener);
		tv_return.setOnClickListener(listener);
		tv_continue.setOnClickListener(listener);

		CircleImageView iv_user = (CircleImageView)findViewById(com.qcwl.debo.R.id.iv_user);
		CircleImageView iv_pruser = (CircleImageView) findViewById(com.qcwl.debo.R.id.iv_pruser);
		TextView tv_user_name = (TextView) findViewById(com.qcwl.debo.R.id.tv_user_name);
		TextView tv_name = (TextView) findViewById(com.qcwl.debo.R.id.tv_name);
		FrameLayout framelayout = (FrameLayout) findViewById(com.qcwl.debo.R.id.framelayout);


		SpringSystem springSystem = SpringSystem.create();
		Spring spring = springSystem.createSpring();
		spring.setCurrentValue(1.0f);
		spring.setSpringConfig(new SpringConfig(90, 3));
		spring.addListener(new SimpleSpringListener() {
			@Override
			public void onSpringUpdate(Spring spring) {
				super.onSpringUpdate(spring);
				float currentValue = (float) spring.getCurrentValue();
				framelayout.setScaleX(currentValue);
				framelayout.setScaleY(currentValue);

			}
		});
		spring.setEndValue(1.05);

		ImgUtil.load(context, data.getUser().getAvatar(), iv_user);
		ImgUtil.load(context, data.getRand_user().getAvatar(), iv_pruser);
		tv_user_name.setText(data.getUser().getUser_nickname());
		tv_name.setText(data.getRand_user().getUser_nickname());

	}
	
	public void onNext(View view){
		this.dismiss();
		if (this.user != null) {
			this.user.onResult(0, this.bundle);
		}
	}
	
	public void onCancel(View view) {
		this.dismiss();
		if (this.user != null) {
			this.user.onResult(1, this.bundle);
		}
	}public void onAdd(View view) {
		this.dismiss();
		if (this.user != null) {
			this.user.onResult(2, this.bundle);
		}
	}
}
