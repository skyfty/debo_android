package com.qcwl.debo.ui.found.fans.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.LargeImageActivity;
import com.qcwl.debo.ui.circle.MyGridView;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.circle.VideoActivity;
import com.qcwl.debo.ui.found.fans.FansDynamicActivity;
import com.qcwl.debo.ui.found.fans.FansDynamicCommentListActivity;
import com.qcwl.debo.ui.found.fans.FansDynamicDetailActivity;
import com.qcwl.debo.ui.found.fans.MyFansActivity;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicBean;
import com.qcwl.debo.utils.SPUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansDynamicAdapter extends CommonAdapter<FansDynamicBean> {

    private int w = 90;
    private int h = 90;
    private int width = 0;
    private int height = 0;
    private int spacing = 5;

    private Context context;
    private int mPosition;

    public FansDynamicAdapter(Context context, List<FansDynamicBean> datas) {
        super(context, R.layout.item_fans_dynamic, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder viewHolder, final FansDynamicBean item, final int position) {
        try {
            DynamicViewHolder holder = new DynamicViewHolder(viewHolder.getConvertView());
            ImgUtil.loadHead(mContext, item.getAvatar(), holder.imageView);
            holder.textName.setText(item.getUser_nickname());
            if (!TextUtils.isEmpty(item.getMoments_content())) {
                holder.textContent.setText(item.getMoments_content());
                holder.textContent.setVisibility(View.VISIBLE);
            } else {
                holder.textContent.setText("");
                holder.textContent.setVisibility(View.GONE);
            }
            holder.textTime.setText(item.getCreate_time());
            holder.textShare.setText("0");
            holder.textComment.setText(item.getComment_num());
            holder.textPraise.setText(item.getUpvote_num());
            if (item.getMoments_uid().equals(SPUtil.getInstance(mContext).getString("uid"))) {
                holder.textDelete.setVisibility(View.VISIBLE);
            } else {
                holder.textDelete.setVisibility(View.GONE);
            }
            if (item.getIs_upvote() == 1) {//已点赞
                holder.textPraise.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_circle_praise_green_big, 0, 0, 0);
                //holder.textPraise.setImageResource(R.mipmap.ic_circle_praise_green_big);
                holder.textPraise.setTextColor(mContext.getResources().getColor(R.color.color_green));
            } else {
                holder.textPraise.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_circle_praise_normal, 0, 0, 0);
//                holder.textPraise.setImageResource(R.mipmap.ic_circle_praise_normal);
                holder.textPraise.setTextColor(mContext.getResources().getColor(R.color.fans_gray));
            }
            if (item.getType() == 1) {
//                holder.videoPlayer.setVisibility(View.GONE);
                holder.layoutPlayVideo.setVisibility(View.GONE);
                loadImages(holder, item);
            } else if (item.getType() == 2) {
                holder.gridView.setVisibility(View.GONE);
                loadVideo(holder, item.getVideo_path(), item.getVideo_img(), item.getMoments_content(),
                        item.getVideo_img_width(), item.getVideo_img_height());
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, MyFansActivity.class).putExtra("uid", item.getMoments_uid()));
                }
            });

            holder.textShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.textComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, FansDynamicCommentListActivity.class).putExtra("moments_id", item.getMoments_id()));
                }
            });
            holder.textPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = position;
                    int isPraise = 0;
                    if (item.getIs_upvote() == 1) {
                        isPraise = 0;
                    } else {
                        isPraise = 1;
                    }
                    if (mContext instanceof FansDynamicActivity) {
                        ((FansDynamicActivity) mContext).praiseDynamic(mPosition, item.getMoments_id(), isPraise);
                    } else if (mContext instanceof FansDynamicDetailActivity) {
                        ((FansDynamicDetailActivity) mContext).praiseDynamic(mPosition, item.getMoments_id(), isPraise);
                    }
                }
            });

            holder.textDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof FansDynamicActivity) {
                        ((FansDynamicActivity) mContext).deleteFansDynamic(mPosition);
                    } else if (mContext instanceof FansDynamicDetailActivity) {
                        ((FansDynamicDetailActivity) mContext).deleteFansDynamic(mPosition);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVideo(DynamicViewHolder holder, final String video_path, final String video_img, final String content, int w, int h) {
        if (TextUtils.isEmpty(video_path)) {
//            holder.videoPlayer.setVisibility(View.GONE);
            holder.layoutPlayVideo.setVisibility(View.GONE);
        } else {
//            holder.videoPlayer.setUp(video_path, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
//            ImgUtil.load(context, video_img, holder.videoPlayer.thumbImageView);
//            holder.videoPlayer.setLayoutParams(new LinearLayout.LayoutParams(-1, dp2px(200)));
//            holder.videoPlayer.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(video_img)) {
                setLayoutSize(holder, w, h);
                ImgUtil.load(context, video_img, holder.imageVideo);
                holder.layoutPlayVideo.setVisibility(View.VISIBLE);
            } else {
                holder.layoutPlayVideo.setVisibility(View.GONE);
            }
            holder.layoutPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent(context, VideoActivity.class);
                    data.putExtra("video_path", video_path);
                    data.putExtra("video_img", video_img);
                    data.putExtra("video_desc", "" + content);
                    context.startActivity(data);
                }
            });
        }
    }

    private void setLayoutSize(DynamicViewHolder holder, int width, int height) {
        int w = 0;
        int h = 0;
        double scale = 1.0 * height / width;
        if (height == 0 || width == 0) {
            w = 100;
            h = 150;
        } else {
            if (width >= height) {
                w = 150;
            } else {
                w = 100;
            }
            h = (int) (w * scale);
            Log.d("===", "scale=" + scale + ",w=" + w + ",h=" + h);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(w), dp2px(h));
        params.leftMargin = dp2px(50);
        holder.layoutPlayVideo.setLayoutParams(params);
    }

    private void loadImages(final DynamicViewHolder holder, FansDynamicBean item) {
        List<String> imgs = item.getImages();
        if (imgs != null && imgs.size() > 0) {
            final List<String> list = new ArrayList<>();
            if (imgs.size() <= 9) {
                list.addAll(imgs);
            } else {
                list.addAll(imgs.subList(0, 9));
            }
            final int size = list.size();
            if (size == 1) {
                holder.gridView.setNumColumns(1);
                String url = list.get(0);
//                width = 230;
//                height = 180;
//                holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(width), dp2px(height)));
                layoutSetting(holder.gridView, url);
            } else if (size == 2 || size == 4) {
                holder.gridView.setNumColumns(2);
                height = (size == 2) ? h : h * 2;
                width = h * 2 + spacing;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(width), dp2px(height));
                params.leftMargin = dp2px(50);
                holder.gridView.setLayoutParams(params);
            } else {
                holder.gridView.setNumColumns(3);
                //height = (size == 2) ? h : h * 2;
                if (size == 3) {
                    height = h;
                } else if (size <= 6) {
                    height = h * 2 + spacing;
                } else {
                    height = h * 3 + spacing * 2;
                }
                width = h * 3 + spacing * 2;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(width), dp2px(height));
                params.leftMargin = dp2px(50);
                holder.gridView.setLayoutParams(params);
            }

            holder.gridView.setAdapter(new com.zhy.adapter.abslistview.CommonAdapter<String>(context, R.layout.item_circle_image, list) {
                @Override
                protected void convert(com.zhy.adapter.abslistview.ViewHolder viewHolder, String item, int position) {
                    final ImageView imageView = viewHolder.getView(R.id.image_view);

                    if (size == 1) {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(s_width), dp2px(s_height)));
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setBackgroundResource(R.color.transparent);
                    } else if (size == 2 || size == 4) {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(w), dp2px(h)));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setBackgroundResource(R.color.default_image_bg);
                    } else {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(w), dp2px(h)));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setBackgroundResource(R.color.default_image_bg);
                    }
                    ImgUtil.load(context, item, imageView);
                    //viewHolder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                }
            });
            holder.gridView.setVisibility(View.VISIBLE);
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, LargeImageActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("images", (Serializable) list);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.gridView.setVisibility(View.GONE);
        }
    }

    private int s_width = 0, s_height = 0;

    private void layoutSetting(final MyGridView gridView, String url) {
        if (null != mContext)
            Glide.with(mContext)
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            int w = resource.getWidth();
                            int h = resource.getHeight();
                            if (w >= h) {
                                width = 200;
                                height = (int) (1.0f * width * h / w);
                            } else {
                                height = 150;
                                width = (int) (1.0f * height * w / h);
                            }
                            s_width = width;
                            s_height = height;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(s_width), dp2px(s_height));
                            params.leftMargin = dp2px(50);
                            gridView.setLayoutParams(params);
                        }
                    });
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(context, dpVal);
    }

    static class DynamicViewHolder {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_time)
        TextView textTime;
        @Bind(R.id.text_content)
        TextView textContent;
        @Bind(R.id.grid_view)
        MyGridView gridView;
        @Bind(R.id.text_delete)
        TextView textDelete;
        @Bind(R.id.text_share)
        TextView textShare;
        @Bind(R.id.text_comment)
        TextView textComment;
        @Bind(R.id.text_praise)
        TextView textPraise;
//        @Bind(R.id.video_player)
//        JCVideoPlayerStandard videoPlayer;

        @Bind(R.id.layout_play_video)
        View layoutPlayVideo;
        @Bind(R.id.image_video)
        ImageView imageVideo;

        DynamicViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
