package com.qcwl.debo.ui.circle;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.JsonObject;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.my.remind.RemindActivity;
import com.qcwl.debo.utils.DeviceUtil;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.view.ExpandableTextView;
import com.qcwl.debo.view.MyListView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/9/9.
 */

public class Circle2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private String uid = "";
    CircleFragment1 fragment;
    List<CircleBean> items;
    private int position;
    int pageType = 0;
    AlbumDetailActivity activity;
    private int w = 0;
    private int h;
    private int width = 0;
    private int height = 0;
    private int spacing = 5;
    private String TAG = "Circle2Adapter";
    private Map<String, String> object;
    private MyHeadViewHolder myHeadViewHolder;
    private float mPosX, mPosY, mCurPosX, mCurPosY;
    private String avatar;
    private ArrayList<String> list = new ArrayList<String>();
    private String bgImg;

    public Circle2Adapter(List<CircleBean> items, CircleFragment1 fragment, Map<String, String> object) {
        Log.i(TAG, ".............CIRCLE111111111111111");
        pageType = 1;
        this.items = items;
        this.fragment = fragment;
        this.context = fragment.getActivity();
        uid = fragment.uid;
        this.object = object;
        int[] i = DeviceUtil.getDp(context);

       // w = i[0] - photoMagin - marginleft - marginPhoto;
        Log.i(TAG,"......I="+i[0]);
        Log.i(TAG,"......I="+h);
    }

    public Circle2Adapter(List<CircleBean> items, AlbumDetailActivity activity) {
        pageType = 2;
        this.items = items;
        this.activity = activity;
        this.context = activity;
        uid = activity.uid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, ".............CIRCLE2222222222=" + (int) context.getResources().getDimension(R.dimen.circle_head_height));
        if (viewType == 0) {
            View headView = LayoutInflater.from(context).inflate(R.layout.layout_circle_header, parent, false);
            if (TextUtils.isEmpty(SPUtil.getInstance(context).getString("comment"))) {
                //headView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(330)));
                //headView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(290)));
            }
            else {
                //headView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(360)));
            }
            headView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(325)));
            myHeadViewHolder = new MyHeadViewHolder(headView);
            return myHeadViewHolder;
        } else {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_circlr_normal, parent, false);
            Log.i(TAG, "    convertView=" + convertView);
            return new MyViewHolder(convertView);
            //convertView.setTag(holder);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHeadViewHolder) {
            /*((ImageView) ((MyHeadViewHolder) holder).img_circle_bg).setImageResource(R.mipmap.logo);
            ((MyHeadViewHolder) holder).img_photo.setImageResource(R.mipmap.logo);
            ((MyHeadViewHolder) holder).text_name.setText("我叫MT");*/
            Log.i(TAG, ".....object=" + object);
            if (object == null) {
                ((ImageView) ((MyHeadViewHolder) holder).img_circle_bg).setImageResource(R.mipmap.debo_logo);
                ((MyHeadViewHolder) holder).img_photo.setImageResource(R.mipmap.head);
            } else {
                bgImg = object.get("moments_background_img");
                avatar = object.get("avatar");
                String nickname = object.get("user_nickname");
                String mobile = object.get("mobile");
                if (TextUtils.isEmpty(bgImg)) {
                    ((ImageView) ((MyHeadViewHolder) holder).img_circle_bg).setImageResource(R.mipmap.debo_logo);
                } else {

                    ImgUtil.load(context, bgImg, ((ImageView) ((MyHeadViewHolder) holder).img_circle_bg));
                }
                if (TextUtils.isEmpty(avatar)) {
                    ((MyHeadViewHolder) holder).img_photo.setImageResource(R.mipmap.head);
                } else {
                    ImgUtil.loadHead(context, avatar, ((MyHeadViewHolder) holder).img_photo);
                }
                ((MyHeadViewHolder) holder).text_name.setText("" + nickname);

            }
            if (TextUtils.isEmpty(SPUtil.getInstance(fragment.getActivity()).getString("comment"))) {
               // headerView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(290)));
                ((MyHeadViewHolder) holder).comment_linear.setVisibility(View.GONE);
            }
            else {
                ((MyHeadViewHolder) holder).comment_linear.setVisibility(View.VISIBLE);
                //headerView.setLayoutParams(new AbsListView.LayoutParams(-1, dp2px(360)));
                EaseUserUtils.setUserNick(fragment.getActivity(), SPUtil.getInstance(fragment.getActivity()).getString("comment").split(",")[SPUtil.getInstance(fragment.getActivity()).getString("comment").split(",").length - 1], null, ((MyHeadViewHolder) holder).comment_iv);
                ((MyHeadViewHolder) holder).comment_tv.setText("有" + SPUtil.getInstance(fragment.getActivity()).getString("comment").split(",").length + "条消息");
                ((MyHeadViewHolder) holder).comment_linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtil.getInstance(fragment.getActivity()).setString("comment", "");
                        ((MyHeadViewHolder) holder).comment_linear.setVisibility(View.GONE);
                        fragment.getActivity().startActivity(new Intent(fragment.getActivity(), RemindActivity.class));
                    }
                });
            }
            ((MyHeadViewHolder) holder).img_circle_bg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(TAG, ".......LongClick");
                    if (!bgImg.equals("")) {
                        list.clear();
                        list.add(bgImg);
                    }
                    Intent intent = new Intent(context, LargeImageActivity.class);
                    intent.putExtra("position", 0);
                    intent.putExtra("images", (Serializable) list);
                    context.startActivity(intent);
                    return false;
                }
            });
            ((MyHeadViewHolder) holder).img_circle_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, ".......Click");
                    fragment.callHeadBg();
                }
            });


            ((MyHeadViewHolder) holder).reletive_head.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i(TAG, "...........onTouch=" + event.getAction());
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mPosX = event.getX();
                            mPosY = event.getY();
                            Log.i(TAG, "...........mPosY=" + mPosX + "     " + mCurPosY);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mCurPosX = event.getX();
                            mCurPosY = event.getY();
                            // fragment.callBack();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (mCurPosY - mPosY > 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                                //向下滑動
                                Log.i(TAG, "...........向下滑動");
                            } else if (mCurPosY - mPosY < 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                                //向上滑动
                                Log.i(TAG, "...........向上滑动");
                                //collapse();
                            }

                            break;
                        case MotionEvent.ACTION_CANCEL:
                            Log.i(TAG, "...........ACTION_CANCEL");
                            break;
                    }
                    return false;
                }
            });
        } else if (holder instanceof MyViewHolder) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            loadNormalCircleData(viewHolder, position-1);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment != null) {
                        fragment.closeEditText();
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return position;
        }
    }

    public void updateImage(JSONObject jsonObject) {
        try {
            String bgImg = jsonObject.getString("moments_background_img");
            String avatar = jsonObject.getString("avatar");
            String nickname = jsonObject.getString("user_nickname");
            String mobile = jsonObject.getString("mobile");
            if (TextUtils.isEmpty(bgImg)) {
                Log.i(TAG, "....myHeadViewHolder=" + myHeadViewHolder.img_circle_bg);
                myHeadViewHolder.img_circle_bg.setImageResource(R.mipmap.debo_logo);
            } else {
                ImgUtil.load(context, bgImg, myHeadViewHolder.img_circle_bg);
            }
            if (TextUtils.isEmpty(avatar)) {
                myHeadViewHolder.img_photo.setImageResource(R.mipmap.head);
            } else {
                ImgUtil.loadHead(context, avatar, myHeadViewHolder.img_photo);
            }
            myHeadViewHolder.text_name.setText("" + nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class MyHeadViewHolder extends MyViewHolder {
        ImageView img_circle_bg;
        CircleImageView img_photo;
        TextView text_name;
        RelativeLayout reletive_head;
        LinearLayout comment_linear;
        TextView comment_tv;
        ImageView comment_iv;
        public MyHeadViewHolder(View view) {
            super(view);
            img_photo = (CircleImageView) view.findViewById(R.id.img_photo);
            //img_circle_bg = (ImageView) view.findViewById(R.id.img_circle_bg);
            text_name = (TextView) view.findViewById(R.id.text_name);
            //reletive_head = (RelativeLayout) view.findViewById(R.id.relative_head);
            comment_linear = (LinearLayout) view.findViewById(R.id.comment_linear);
            comment_tv = (TextView) view.findViewById(R.id.comment_tv);
            comment_iv = (ImageView) view.findViewById(R.id.comment_iv);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image_arrow;
        CircleImageView imageView;

        TextView textName;
        ExpandableTextView textContent;
        TextView textTime;
        TextView textDelete;
        MyGridView gridView;
        ImageView imgShare;
        ImageView imgPraise;
        ImageView imgComment;
        //ImageView imgPraiseFlag;
        LinearLayout layoutPraisePhoto;
        //ImageView imgCommentFlag;
        MyListView listView;
        //        @Bind(R.id.video_player)
        //        JCVideoPlayerStandard videoPlayer;

        View layoutPlayVideo;
        ImageView imageVideo;

        FlexboxLayout flexboxLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            flexboxLayout = (FlexboxLayout) itemView.findViewById(R.id.flex_box_layout);
            imageView = (CircleImageView) itemView.findViewById(R.id.image_view);

            layoutPlayVideo = (View) itemView.findViewById(R.id.layout_play_video);
            imageVideo = (ImageView) itemView.findViewById(R.id.image_video);
            layoutPraisePhoto = (LinearLayout) itemView.findViewById(R.id.layout_praise_photo);

            textName = (TextView) itemView.findViewById(R.id.text_name);
            textContent = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textDelete = (TextView) itemView.findViewById(R.id.text_delete);

            imgShare = (ImageView) itemView.findViewById(R.id.img_share);
            imgPraise = (ImageView) itemView.findViewById(R.id.img_praise);
            image_arrow = (ImageView) itemView.findViewById(R.id.image_arrow);
            imgComment = (ImageView) itemView.findViewById(R.id.img_comment);
            // imgPraiseFlag = (ImageView) itemView.findViewById(R.id.img_praise_flag);
            gridView = (MyGridView) itemView.findViewById(R.id.grid_view);
            // imgCommentFlag = (ImageView) itemView.findViewById(R.id.img_comment_flag);

            listView = (MyListView) itemView.findViewById(R.id.list_view);


        }
    }

    private void loadNormalCircleData(MyViewHolder holder, int index) {
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
            Log.i(TAG, "        =" + holder.textDelete);
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
            // holder.imgCommentFlag.setVisibility(View.VISIBLE);
            holder.listView.setVisibility(View.VISIBLE);
            //holder.listView.setPadding(0,10,0,10);
            if (pageType == 1) {
                //holder.listView.setAdapter(new CommentAdapter(circle.getCircle_type(), index, fragment, circle.getComment_list()));
            } else if (pageType == 2) {
                holder.listView.setAdapter(new CommentAdapter(circle.getCircle_type(), index, activity, circle.getComment_list()));
            } else if (pageType == 3) {
                //holder.listView.setAdapter(new CommentAdapter(circle.getCircle_type(), index, subfragment, circle.getComment_list()));
            }
        } else {
            //holder.imgCommentFlag.setVisibility(View.GONE);
            holder.listView.setVisibility(View.GONE);
        }
        if ("1".equals(circle.getIs_upvote())) {
            holder.imgPraise.setImageResource(R.mipmap.ic_circle_praise_green_big);
            holder.image_arrow.setVisibility(View.VISIBLE);
        } else {
            holder.imgPraise.setImageResource(R.mipmap.ic_circle_praise_normal);
            holder.image_arrow.setVisibility(View.GONE);
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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px(28), dp2px(28));
                view.setLayoutParams(layoutParams);
                holder.flexboxLayout.addView(view);
            }
        }
    }

    private void setLayoutSize(MyViewHolder holder, int width, int height) {
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

    private void loadVideo(MyViewHolder holder, final String video_path, final String video_img, final String content, int w, int h) {
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

    private void loadPicture(MyViewHolder holder, List<String> images) {
        h = (int) context.getResources().getDimension(R.dimen.circle_message_image);
        w = h;
        Log.i(TAG,"..........I="+h);
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

    public interface CircleCallback {
        /**
         * Called when a view has been clicked.
         */
        void callBack();

        void callHeadBg();
    }
}
