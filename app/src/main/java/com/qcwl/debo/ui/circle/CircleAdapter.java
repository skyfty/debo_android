package com.qcwl.debo.ui.circle;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.view.MyListView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/7/21.
 */

public class CircleAdapter extends BaseAdapter {

    private int w = 93;
    private int h = 93;
    private int width = 0;
    private int height = 0;
    private int spacing = 5;

    List<CircleBean> items;
    Context context;
    CircleFragment2 fragment;
    AlbumDetailActivity activity;
//    SubCircleFragment subfragment;

    private int position = 0;

    int pageType = 0;

    private String uid = "";

    public CircleAdapter(List<CircleBean> items, CircleFragment2 fragment) {
        pageType = 1;
        this.items = items;
        this.fragment = fragment;
        this.context = fragment.getActivity();
        uid = fragment.uid;
    }

    public CircleAdapter(List<CircleBean> items, AlbumDetailActivity activity) {
        pageType = 2;
        this.items = items;
        this.activity = activity;
        this.context = activity;
        uid = activity.uid;
    }

//    public CircleAdapter(List<CircleBean> items, SubCircleFragment subfragment) {
//        pageType = 3;
//        this.items = items;
//        this.subfragment = subfragment;
//        this.context = subfragment.getActivity();
//        uid = subfragment.uid;
//    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items != null ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolderOne holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_circlr_normal, parent, false);
            holder = new ViewHolderOne(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderOne) convertView.getTag();
        }

        loadNormalCircleData(holder, index);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    fragment.closeEditText();
                }
            }
        });
        return convertView;
    }

    private void loadNormalCircleData(final ViewHolderOne holder, final int index) {
        position = index;
        final CircleBean circle = items.get(index);
        if (uid.equals(circle.getMoments_uid())) {
            holder.textDelete.setVisibility(View.VISIBLE);
            holder.textDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = index;
                    if (pageType == 1) {
                        fragment.deleteComment(circle.getCircle_type(), 1, position, -1);
                    } else if (pageType == 2) {
                        activity.deleteComment(circle.getCircle_type(), 1, position, -1);
                    }
                }
            });
        } else {
            holder.textDelete.setVisibility(View.GONE);
        }
        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = index;
                Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
            }
        });
        holder.imgPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgPraise.setEnabled(false);
                position = index;
                int isPraise = 0;
                if (items.get(index).getIs_upvote().equals("0")) {
                    isPraise = 1;
                } else {
                    isPraise = 0;
                }
                if (pageType == 1) {
                    fragment.praiseCircle(position, isPraise, holder.imgPraise);
                } else if (pageType == 2) {
                    activity.praiseCircle(position, isPraise, holder.imgPraise);
                }
            }
        });
        holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = index;
                if (pageType == 1) {
                    fragment.openEditText(position, "0");
                } else if (pageType == 2) {
                    activity.openEditText(position, "0");
                }
            }
        });
        holder.imageView.setImageResource(R.mipmap.head);
        ImgUtil.loadHead(context, circle.getAvatar(), holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ContactsContentActivity.class)
                        .putExtra("my_mobile", SPUtil.getInstance(context).getString("phone"))
                        .putExtra("mobile", circle.getMobile())
                        .putExtra("type", "" + circle.getCircle_type()));
            }
        });
        holder.textName.setText(circle.getUser_nickname());
        if (!TextUtils.isEmpty(circle.getMoments_content())) {
            holder.textContent.setText(circle.getMoments_content());
            holder.textContent.setVisibility(View.VISIBLE);
        } else {
            holder.textContent.setVisibility(View.GONE);
        }
        holder.textTime.setText("" + circle.getCreate_time());
        if (circle.getType() == 1) {
//            holder.videoPlayer.setVisibility(View.GONE);
            holder.layoutPlayVideo.setVisibility(View.GONE);
            loadPicture(holder, circle.getImages());
        } else if (circle.getType() == 2) {
            holder.gridView.setVisibility(View.GONE);
            loadVideo(holder, circle.getVideo_path(), circle.getVideo_img(), circle.getMoments_content(),
                    circle.getVideo_img_width(), circle.getVideo_img_height());
        }

        if (circle.getComment_list() != null && circle.getComment_list().size() > 0) {
            //holder.imgCommentFlag.setVisibility(View.VISIBLE);
            holder.listView.setVisibility(View.VISIBLE);
            if (pageType == 1) {
                holder.listView.setAdapter(new CommentAdapter(circle.getCircle_type(), index, fragment, circle.getComment_list()));
            } else if (pageType == 2) {
                holder.listView.setAdapter(new CommentAdapter(circle.getCircle_type(), index, activity, circle.getComment_list()));
            } else if (pageType == 3) {
                //holder.listView.setAdapter(new CommentAdapter(circle.getCircle_type(), index, subfragment, circle.getComment_list()));
            }
        } else {
           // holder.imgCommentFlag.setVisibility(View.GONE);
            holder.listView.setVisibility(View.GONE);
        }
        if ("1".equals(circle.getIs_upvote())) {
            holder.imgPraise.setImageResource(R.mipmap.ic_circle_praise_green_big);
        } else {
            holder.imgPraise.setImageResource(R.mipmap.ic_circle_praise_normal);
        }
        holder.flexboxLayout.removeAllViews();
        holder.flexboxLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        if (circle.getUpvote_list() == null || circle.getUpvote_list().size() == 0) {
            //holder.imgPraiseFlag.setVisibility(View.GONE);
        } else {
            //holder.imgPraiseFlag.setVisibility(View.VISIBLE);
            for (int i = 0; i < circle.getUpvote_list().size(); i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.layout_circle_imageview, null);
                CircleImageView imageView = (CircleImageView) view.findViewById(R.id.image_view);//new CircleImageView(context);
                ImgUtil.load(context, circle.getUpvote_list().get(i).getSmall_avatar(), imageView);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px(40), dp2px(40));
                view.setLayoutParams(layoutParams);
                holder.flexboxLayout.addView(view);
            }
        }
    }

    private void loadVideo(ViewHolderOne holder, final String video_path, final String video_img, final String content, int w, int h) {
        if (TextUtils.isEmpty(video_path)) {
//            holder.videoPlayer.setVisibility(View.GONE);
            holder.layoutPlayVideo.setVisibility(View.GONE);
        } else {
//            holder.videoPlayer.setUp(video_path, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");//SCREEN_LAYOUT_NORMAL
//            ImgUtil.load(context, video_img, holder.videoPlayer.thumbImageView);
//            holder.videoPlayer.setLayoutParams(new LinearLayout.LayoutParams(-1, dp2px(200)));
//            holder.videoPlayer.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(video_img)) {
                setLayoutSize(holder, w, h);
                holder.imageVideo.setBackgroundResource(R.color.default_image_bg);
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
                    if (TextUtils.isEmpty(content)) {
                        data.putExtra("video_desc", "");
                    } else {
                        data.putExtra("video_desc", content);
                    }
                    context.startActivity(data);
                }
            });
        }
    }

    private void setLayoutSize(ViewHolderOne holder, int width, int height) {
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
        holder.layoutPlayVideo.setLayoutParams(new LinearLayout.LayoutParams(dp2px(w), dp2px(h)));
    }

    private void loadPicture(ViewHolderOne holder, List<String> images) {
        if (images == null || images.size() == 0) {
            holder.gridView.setVisibility(View.GONE);
        } else {
            final List<String> list = new ArrayList<>();
            if (images.size() <= 9) {
                list.addAll(images);
            } else {
                list.addAll(images.subList(0, 9));
            }
            final int size = list.size();
            if (size == 1) {
                holder.gridView.setNumColumns(1);
                width = 230;
                height = 180;
                holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(width), dp2px(height)));
            } else if (size == 2 || size == 4) {
                holder.gridView.setNumColumns(2);
                height = (size == 2) ? h : h * 2;
                width = h * 2 + spacing;
                holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(width), dp2px(height)));
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
                holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(width), dp2px(height)));
            }

            holder.gridView.setAdapter(new CommonAdapter<String>(context, R.layout.item_circle_image, list) {
                @Override
                protected void convert(ViewHolder viewHolder, String item, int position) {
                    final ImageView imageView = viewHolder.getView(R.id.image_view);

                    if (size == 1) {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(230), dp2px(180)));
                    } else {
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(dp2px(w), dp2px(h)));
                    }
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setBackgroundResource(R.color.default_image_bg);
                    ImgUtil.load(context, item, imageView);
                    viewHolder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
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
        }
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(context, dpVal);
    }

    static class ViewHolderOne {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_content)
        TextView textContent;
        @Bind(R.id.grid_view)
        MyGridView gridView;
        @Bind(R.id.text_time)
        TextView textTime;
        @Bind(R.id.text_delete)
        TextView textDelete;
        @Bind(R.id.img_share)
        ImageView imgShare;
        @Bind(R.id.img_praise)
        ImageView imgPraise;
        @Bind(R.id.img_comment)
        ImageView imgComment;
        /*@Bind(R.id.img_praise_flag)
        ImageView imgPraiseFlag;*/
        @Bind(R.id.layout_praise_photo)
        LinearLayout layoutPraisePhoto;
        /*@Bind(R.id.img_comment_flag)
        ImageView imgCommentFlag;*/
        @Bind(R.id.list_view)
        MyListView listView;
//        @Bind(R.id.video_player)
//        JCVideoPlayerStandard videoPlayer;

        @Bind(R.id.layout_play_video)
        View layoutPlayVideo;
        @Bind(R.id.image_video)
        ImageView imageVideo;

        @Bind(R.id.flex_box_layout)
        FlexboxLayout flexboxLayout;

        ViewHolderOne(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
