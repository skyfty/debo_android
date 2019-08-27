package com.qcwl.debo.ui.found.joke;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.joke.video_new.PlayVideoActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/12.
 */

public class JokeAdapter extends CommonAdapter<VideoBean> implements TouchDataCallBack{
    private Context mContext;
    private int STORAGE_REQUEST_CODE = 100;
    private int position2;
    private List<VideoBean> mlist;
    public JokeAdapter(Context context, List<VideoBean> mListVideo) {
        super(context, R.layout.item_joke, mListVideo);
        this.mContext = context;
        this.mlist = mListVideo;
    }
    public void setDataCallback() {
        DataCallBackUtils.setCallBack(this);
    }

    @Override
    protected void convert(ViewHolder viewHolder, VideoBean item, int position) {
        try {
            Log.e("getbean——video", item.toString());
            JokeViewHolder holder = new JokeViewHolder(viewHolder.getConvertView());
            holder.imageView.setBackgroundResource(R.color.default_image_bg);
            ImgUtil.load(mContext, ApiHttpClient.VIDEO_URL + item.getImg_url(), holder.imageView);
            ImgUtil.setGlideHead(mContext, item.getAvatar(), holder.head_iv);

           /* Glide.with(mContext)
                    .load(item.getAvatar()).bitmapTransform(new CropCircleTransformation(mContext))//SPUtil.getInstance(mContext).getString("headsmall")
                    .placeholder(R.mipmap.head)
                    .error(R.mipmap.head)
                    .into(holder.head_iv);*/
            //   holder.textDesc.setText(item.getTitle());
            holder.checkBox.setText(item.getUpvote_num() + "");
            //   holder.tv_name.setText(item.getUser_nickname());
            int h = 0;
            if (position % 2 == 0) {
                if (position == 0) {
                    h = ScreenUtils.dp2px(mContext, 260);
                } else {
                    h = ScreenUtils.dp2px(mContext, 300);
                }
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, h);
                params.rightMargin = ScreenUtils.dp2px(mContext, 1);
                params.bottomMargin = ScreenUtils.dp2px(mContext, 2);
                holder.imageView.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, ScreenUtils.dp2px(mContext, 300));
                params.leftMargin = ScreenUtils.dp2px(mContext, 1);
                params.bottomMargin = ScreenUtils.dp2px(mContext, 2);
                holder.imageView.setLayoutParams(params);
            }
            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击播放的口
                    int storagePermissionRet = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (storagePermissionRet != PackageManager.PERMISSION_GRANTED) {
                        requestPermission();
                        return;
                    }
                    setDataCallback();
                    position2 = position;
                    Intent intent = new Intent();
                    intent.setClass(mContext, PlayVideoActivity.class);
                    intent.putExtra("data",(Serializable)mlist);
                    intent.putExtra("position", position);
                    //intent.putExtra("itme_date",(Serializable) item);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", item.getId());
                    bundle.putString("img_url", item.getImg_url());
                    bundle.putString("video_url", item.getVideo_url());
                    bundle.putString("uid", item.getUid());
                    bundle.putString("nickname", item.getUser_nickname());
                    bundle.putString("image_photo", item.getAvatar());
                    bundle.putString("is_follow", item.getIs_follow());
                    bundle.putString("type", "0");
                    bundle.putString("comment_num", item.getComment_num());
                    intent.putExtras(bundle);
                   /* intent.putExtra("vid", item.getId());
                    intent.putExtra("follow_uid", item.getUid());
                    if (TextUtils.isEmpty(item.getImg_url())){
                        intent.putExtra("fengmian", "");
                    }else {
                        intent.putExtra("fengmian", item.getImg_url());
                    }
                    if (TextUtils.isEmpty(item.getVideo_url())){
                        intent.putExtra("video_url", "");
                    }else {
                        intent.putExtra("video_url", item.getVideo_url());
                    }
                    if (TextUtils.isEmpty(item.getAvatar())){
                        intent.putExtra("avatar", "");
                    }else {
                        intent.putExtra("avatar", item.getAvatar());
                    }
                    if (TextUtils.isEmpty(item.getUser_nickname())){
                        intent.putExtra("nickname","");
                    }else {
                        intent.putExtra("nickname", item.getUser_nickname());
                    }
                    if(TextUtils.isEmpty(item.getUpvote_num())){
                        intent.putExtra("upvote","") ;
                    }else{
                        intent.putExtra("upvote",item.getUpvote_num()) ;
                    }
                    if(TextUtils.isEmpty(item.getIs_follow())){
                        intent.putExtra("is_follow","") ;
                    }else{
                        intent.putExtra("is_follow",item.getIs_follow()) ;
                    }
                    if(TextUtils.isEmpty(item.getIs_upvote())){
                        intent.putExtra("is_upvote","") ;
                    }else{
                        intent.putExtra("is_upvote",item.getIs_upvote()) ;
                    }
                    if(TextUtils.isEmpty(item.getComment_num())){
                        intent.putExtra("comment_num","") ;
                    }else{
                        intent.putExtra("comment_num",item.getComment_num()) ;
                    }*/
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class JokeViewHolder {
        @Bind(R.id.image_view)
        ImageView imageView;
        @Bind(R.id.text_desc)
        TextView textDesc;
        @Bind(R.id.checkbox_praise)
        CheckBox checkBox;
        @Bind(R.id.head_iv)
        ImageView head_iv;
   /*     @Bind(R.id.tv_name)
        TextView tv_name;*/

        JokeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 向用户解释为什么需要这个权限
            new AlertDialog.Builder(mContext)
                    .setMessage("下载视频，边播边存以及播放本地视频，需要SD卡权限。")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //申请权限
                            ActivityCompat.requestPermissions((JokeActivity) mContext,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                        }
                    })
                    .show();
        }
    }
    @Override
    public JSONObject doSomeThing(int i) {
        Log.i("JokeAdapter","..............="+(position2+i)+"       "+mlist.size()+"　　　　"+position2);
        position2 = position2+i;
        if(position2>=mlist.size()||position2<0){
            position2 = position2-i;
            return null;
        }
        VideoBean videoBean = mlist.get(position2);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", videoBean.getId());
            jsonObject.put("img_url", videoBean.getImg_url());
            jsonObject.put("video_url", videoBean.getVideo_url());
            jsonObject.put("uid", videoBean.getUid());
            jsonObject.put("nickname", videoBean.getUser_nickname());
            jsonObject.put("image_photo", videoBean.getAvatar());
            jsonObject.put("is_follow", videoBean.getIs_follow());
            jsonObject.put("type", "0");
            jsonObject.put("comment_num", videoBean.getComment_num());
            Log.i("JokeAdapter","......doSomeThing="+i);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
