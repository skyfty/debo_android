package com.qcwl.debo.ui.found.joke;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qcwl.debo.R;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.model.Joke_like_list;
import com.qcwl.debo.model.Joke_list;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.joke.video_new.PlayVideoActivity;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class PersonalAdapter extends BaseQuickAdapter<Joke_like_list, BaseViewHolder> implements TouchDataCallBack{
    FragmentActivity fragmentActivity;
    List<Joke_like_list> joke_like_lists;
    int height;
    int resId;
    private String TAG = "PersonalAdapter";
    String avatar, nickname, is_follow;
    public int position2;
    public PersonalAdapter(FragmentActivity fragmentActivity, int layoutResId, @Nullable List<Joke_like_list> data, String avatar, String nickname, String is_follow, int height) {
        super(layoutResId, data);
        this.fragmentActivity = fragmentActivity;
        this.joke_like_lists = data;
        this.height = height;
        this.resId = layoutResId;
        this.avatar = avatar;
        this.nickname = nickname;
        this.is_follow = is_follow;
    }
    public void setDataCallback() {
        DataCallBackUtils.setCallBack(this);
    }
    @Override
    protected void convert(BaseViewHolder helper, Joke_like_list item) {
        ImgUtil.loadHead(fragmentActivity, ApiHttpClient.VIDEO_URL + item.getImg_url(), helper.getView(R.id.imageView));
        ViewGroup.LayoutParams layoutParams = helper.getView(R.id.imageView).getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = height;
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,".......position="+helper.getPosition());
                setDataCallback();
                position2 = helper.getPosition();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",(Serializable) joke_like_lists);
                bundle.putString("id", item.getId());
                bundle.putString("video_url", item.getVideo_url());
                bundle.putString("uid", item.getUid());
                bundle.putString("nickname", nickname);
                bundle.putString("image_photo", avatar);
                bundle.putString("is_follow", is_follow);
                bundle.putInt("position", position2);
                bundle.putString("type", "2");
                Intent intent = new Intent();
                intent.setClass(fragmentActivity, PlayVideoActivity.class);
                intent.putExtras(bundle);
                fragmentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public JSONObject doSomeThing(int i) {
        Log.i("JokeAdapter","..............="+(position2+i)+"       "+joke_like_lists.size()+"　　　　"+position2);
        position2 = position2+i;
        if(position2>=joke_like_lists.size()||position2<0){
            position2 = position2-i;
            return null;
        }
        Joke_like_list videoBean = joke_like_lists.get(position2);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", videoBean.getId());
            jsonObject.put("img_url", videoBean.getImg_url());
            jsonObject.put("video_url", videoBean.getVideo_url());
            jsonObject.put("uid", videoBean.getUid());
            jsonObject.put("nickname", nickname);
            jsonObject.put("image_photo", avatar);
            jsonObject.put("is_follow", is_follow);
            jsonObject.put("type", "1");
            Log.i("JokeAdapter","......doSomeThing="+i);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
