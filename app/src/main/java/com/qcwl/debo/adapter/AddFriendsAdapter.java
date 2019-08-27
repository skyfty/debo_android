package com.qcwl.debo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.view.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcwl.debo.R;
import com.qcwl.debo.model.AddFriendsBean;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CommonDialog;
import com.qcwl.debo.widget.DemoHelper;

import java.util.List;

@SuppressLint("InflateParams")
public class AddFriendsAdapter extends BaseAdapter {

	private Context context;
	private List<AddFriendsBean> list;
	private AddFriendsBean af;
	private SPUtil sp;
	private EditText yanzheng_reason;
	private Button yanzheng_fasong;
	private CommonDialog dialog;

	public AddFriendsAdapter(Context context, List<AddFriendsBean> list) {
		this.context = context;
		this.list = list;
		sp = SPUtil.getInstance(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		af = list.get(position);
		ViewHolder vh = null;
		if (convertView == null) {

			vh = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(
					R.layout.add_friends_item, null);
			vh.friends_item_image = (RoundedImageView) convertView
					.findViewById(R.id.friends_item_image);
			vh.friends_item_nickname = (TextView) convertView
					.findViewById(R.id.friends_item_nickname);
			vh.friends_item_isfriend = (TextView) convertView
					.findViewById(R.id.friends_item_isfriend);
			vh.friends_item_tianjia = (Button) convertView
					.findViewById(R.id.friends_item_tianjia);
			vh.friends_item_finish = (TextView) convertView
					.findViewById(R.id.friends_item_finish);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		if (TextUtils.isEmpty(af.getUser_nicename())) {
			vh.friends_item_nickname.setText("无");
		} else {
			vh.friends_item_nickname.setText(af.getUser_nicename());
		}

		if (TextUtils.isEmpty(af.getMobile())) {
			vh.friends_item_isfriend.setText("无");
		} else {
			vh.friends_item_isfriend.setText(af.getMobile());
		}

		if(DemoHelper.getInstance().getContactList().containsKey(list.get(position).getMobile())){
			vh.friends_item_tianjia.setClickable(false);
			vh.friends_item_tianjia.setEnabled(false);
			vh.friends_item_tianjia.setText("已添加");
		}else {
			vh.friends_item_tianjia.setClickable(true);
			vh.friends_item_tianjia.setEnabled(true);
			vh.friends_item_tianjia.setText("添加");
		}

//		if(EaseUserUtils.checkIsFriend(context,list.get(position).getMobile())>0){
//			vh.friends_item_tianjia.setClickable(false);
//			vh.friends_item_tianjia.setEnabled(false);
//			vh.friends_item_tianjia.setText("已添加");
//		}

		if(!TextUtils.isEmpty(af.getHeadsmall())){
			ImageLoader.getInstance().displayImage(af.getHeadsmall(), vh.friends_item_image);
		}else {
			vh.friends_item_image.setImageResource(R.mipmap.head);
		}

		vh.friends_item_tianjia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (list.get(position).getMobile().equals(sp.getString("phone"))) {
					ToastUtils.showShort(context, "您不能添加自己为好友");
					return;
				}
				
				if(DemoHelper.getInstance().getContactList().containsKey(list.get(position).getMobile())){
				    //let the user know the contact already in your contact list
				    if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(list.get(position).getMobile())){
				        ToastUtils.showShort(context, "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可");
				        return;
				    }
					return;
				}

				dialog = new CommonDialog(context);
				dialog.show();
				dialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
					@Override
					public void doConfirm(final String reason) {
						new Thread() {
							@Override
							public void run() {
								try {
									EMClient.getInstance().contactManager().addContact(list.get(position).getMobile(),reason);
									Message msg = new Message();
									msg.obj = list.get(position);
									handler.sendMessage(msg);
								} catch (Exception e) {
									e.printStackTrace();
									Message msg = new Message();
									handler.sendMessage(msg);
								}
							}
						}.start();
					}
				});
			}
		});

		return convertView;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			ToastUtils.showShort(context, "添加好友请求发送成功，请等待~");
		}
	};

	private class ViewHolder {
		private RoundedImageView friends_item_image;
		private TextView friends_item_nickname, friends_item_isfriend,
				friends_item_finish;
		private Button friends_item_tianjia;
	}

}
