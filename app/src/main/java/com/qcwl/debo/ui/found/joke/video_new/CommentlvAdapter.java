package com.qcwl.debo.ui.found.joke.video_new;

import java.util.List;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.joke.CommentBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommentlvAdapter extends MyBaseAdapter {

	private Context context;
	private List<CommentBean> datas;
	private LayoutInflater inflater;

	public CommentlvAdapter(Context context, List<CommentBean> datas) {
		super();
		this.context = context;
		this.datas = datas;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			v = inflater.inflate(R.layout.item_comment, null);
			vh = new ViewHolder();
			vh.tvnickname = (TextView) v.findViewById(R.id.item_comment_nickname);
			vh.tvtime = (TextView) v.findViewById(R.id.item_comment_time);
			vh.tvcontent = (TextView) v.findViewById(R.id.item_comment_content);
			vh.usericon = (RoundedImageView) v.findViewById(R.id.item_comment_icon);
			v.setTag(vh);
		}else {
			vh = (ViewHolder) v.getTag();
		}

		//显示评论的内容
		vh.tvcontent.setText(datas.get(position).getContent());
		//显示评论者昵称
		vh.tvnickname.setText(datas.get(position).getUser_nickname());
		//显示评论时间
		vh.tvtime.setText(datas.get(position).getTime());
		//显示评论者的头像
		Glide.with(context)
				.load(datas.get(position).getAvatar())
				.into(vh.usericon);

		return v;
	}

	class ViewHolder{
		TextView tvnickname,tvcontent,tvtime;
		RoundedImageView usericon;
	}
}
