package com.qcwl.debo.ui.found.fans.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.fans.ArticleDetailActivity;
import com.qcwl.debo.ui.found.fans.FansDynamicDetailActivity;
import com.qcwl.debo.ui.found.fans.bean.FansHomeBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/3.
 */

public class FansHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<FansHomeBean> list;
    private View view = null;

    public FansHomeAdapter(Context context, List<FansHomeBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {//群发动态
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.item_fans_home_article, null);
                view.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
                holder = new ArticleViewHolder(view);
                break;
            case 2:// 群发消息
                view = LayoutInflater.from(context).inflate(R.layout.item_fans_home_text, null);
                view.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
                holder = new MessageViewHolder(view);
                break;
            case 3://动态（图文、视频）
            case 4:
                view = LayoutInflater.from(context).inflate(R.layout.item_fans_home_dynamic, null);
                view.setLayoutParams(new ViewGroup.LayoutParams(-1, ScreenUtils.dp2px(context, 80)));
                holder = new DynamicViewHolder(view);
                break;
//            case 5://动态纯文字
//
//                break;
            default:
                break;
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (list == null || list.size() == 0) {
            return;
        }
        FansHomeBean bean = list.get(position);
        if (bean == null) {
            return;
        }
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 1://群发动态
                loadArticleData((ArticleViewHolder) holder, bean);
                break;
            case 2:// 群发消息
                loadTextData((MessageViewHolder) holder, bean);
                break;
            case 3://动态（图文、视频）
            case 4:
                loadDynamicData((DynamicViewHolder) holder, bean);
                break;
//            case 5://动态纯文字
//
//                break;
            default:
                break;
        }
    }

    private void loadDynamicData(final DynamicViewHolder holder, final FansHomeBean bean) {
        if (bean.getType() == 3) {//图片
            if (bean.getImg() == null || bean.getImg().size() == 0) {
                holder.imageView.setVisibility(View.GONE);
                holder.imageIcon.setVisibility(View.GONE);
            } else {
                ImgUtil.load(context, bean.getImg().get(0), holder.imageView);
                holder.imageIcon.setImageResource(R.mipmap.ic_flag_img);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageIcon.setVisibility(View.VISIBLE);
            }
        } else if (bean.getType() == 4) {//视频
            if (TextUtils.isEmpty(bean.getVideo_img())) {
                holder.imageView.setVisibility(View.GONE);
                holder.imageIcon.setVisibility(View.GONE);
            } else {
                ImgUtil.load(context, bean.getVideo_img(), holder.imageView);
                holder.imageIcon.setImageResource(R.mipmap.ic_flag_video);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageIcon.setVisibility(View.VISIBLE);
            }
        }
        holder.textContent.setText(bean.getContent());
        holder.textTime.setText(bean.getAdd_time());
        holder.layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FansDynamicDetailActivity.class).putExtra("bean", bean));
            }
        });
    }

    private void loadTextData(MessageViewHolder holder, FansHomeBean bean) {
        holder.textContent.setText(bean.getContent());
        ImgUtil.loadHead(context, bean.getAvatar(), holder.imageView);
    }

    private void loadArticleData(ArticleViewHolder holder, final FansHomeBean bean) {
        holder.textTitle.setText(bean.getTitle());
        holder.textTime.setText(bean.getAdd_time());
        ImgUtil.load(context, bean.getImg().get(0), holder.imageView);
        holder.layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ArticleDetailActivity.class).putExtra("m_id", bean.getM_id()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_title)
        TextView textTitle;
        @Bind(R.id.text_time)
        TextView textTime;
        @Bind(R.id.image_view)
        ImageView imageView;
        @Bind(R.id.layout_content)
        View layoutContent;

        ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_content)
        TextView textContent;
        @Bind(R.id.image_view)
        CircleImageView imageView;

        MessageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class DynamicViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_view)
        ImageView imageView;
        @Bind(R.id.image_icon)
        ImageView imageIcon;
        @Bind(R.id.text_content)
        TextView textContent;
        @Bind(R.id.text_time)
        TextView textTime;
        @Bind(R.id.layout_content)
        View layoutContent;

        DynamicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
