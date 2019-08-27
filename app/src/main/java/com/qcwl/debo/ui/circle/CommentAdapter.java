package com.qcwl.debo.ui.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.widget.CircleImageView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;


/**
 * Created by Administrator on 2017/7/22.
 */

public class CommentAdapter extends CommonAdapter<CommentBean> {
    private CircleFragment2 fragment;
    private AlbumDetailActivity activity;
    //    private SubCircleFragment subfragment;
    private int circle_type;
    private int outerPos;

    int pageType = 0;
    private String uid = "";

    private Context context;
    private String TAG = "CommentAdapter";
    public static final int FLAG = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    public CommentAdapter(int circle_type, int outerPos, CircleFragment2 fragment, List<CommentBean> datas) {
        super(fragment.getActivity(), R.layout.item_circle_comment, datas);
        this.circle_type = circle_type;
        this.outerPos = outerPos;
        this.fragment = fragment;
        pageType = 1;
        uid = fragment.uid;
        context = fragment.getActivity();
    }

    public CommentAdapter(int circle_type, int outerPos, AlbumDetailActivity activity, List<CommentBean> datas) {
        super(activity, R.layout.item_circle_comment, datas);
        this.circle_type = circle_type;
        this.outerPos = outerPos;
        this.activity = activity;
        pageType = 2;
        uid = activity.uid;
        context = activity;
    }

//    public CommentAdapter(int circle_type, int outerPos, SubCircleFragment subfragment, List<CommentBean> datas) {
//        super(subfragment.getActivity(), R.layout.item_circle_comment, datas);
//        this.circle_type = circle_type;
//        this.outerPos = outerPos;
//        this.subfragment = subfragment;
//        pageType = 3;
//        uid = fragment.uid;
//        context = fragment.getActivity();
//    }

    @Override
    protected void convert(ViewHolder viewHolder, final CommentBean item, final int position) {
        TextView textName = viewHolder.getView(R.id.text_name);
        TextView textDate = viewHolder.getView(R.id.text_date);
        LinearLayout linear = viewHolder.getView(R.id.linear);
       // TextView textContent = viewHolder.getView(R.id.text_content);
        CircleImageView iv_photo = viewHolder.getView(R.id.iv_photo);
//        TextView textAt = viewHolder.getView(R.id.text_at);
//        TextView textReply = viewHolder.getView(R.id.text_reply);//circle_reply
        textName.setText("" + item.getName());
        //textDate.setText("" + item.getComment_time());
        Log.i(TAG,"..............PATH="+position+"     "+item.getAvatar());
        if (item.getAvatar() == null||item.getAvatar().equals("")){
            iv_photo.setImageResource(R.mipmap.debo_logo);
        }else {
            ImgUtil.load2(context, item.getAvatar(), iv_photo);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        if (!"0".equals(item.getUid()) && !item.getUid().equals(item.getReply_uid())) {

            if (!"0".equals(item.getReply_uid())) {
                ssb.append(" 回复");
                ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_name2)), 0, ssb.length(), FLAG);
                String text = " ";
                if (!TextUtils.isEmpty(item.getReply_name())) {
                    text = " " + item.getReply_name() + "";
                }
                ssb.append(text);
                ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_name2)), ssb.length() - text.length(), ssb.length(), FLAG);
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (!TextUtils.isEmpty(item.getReply_name())) {
                           /* Intent intent = new Intent(context, ContactsContentActivity.class);
                            //.putExtra("f_uid", "" + item.getUid())
                            intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
                            intent.putExtra("mobile", item.getReply_mobile());
                            intent.putExtra("type", "" + circle_type);
                            context.startActivity(intent);*/
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(getColor(R.color.circle_name));
                    }
                }, ssb.length() - text.length(), ssb.length(), FLAG);
            }
        }

        ssb.append(" : "+item.getMc_content());
        ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_name2)), ssb.length() - item.getMc_content().length(), ssb.length(), FLAG);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                /*if (!uid.equals(item.getUid())) {
                    if (pageType == 1) {
                        fragment.openEditText(outerPos, item.getUid());
                    } else if (pageType == 2) {
                        activity.openEditText(outerPos, item.getUid());
                    } else if (pageType == 3) {
                        //subfragment.openEditText(outerPos, item.getUid());
                    }
                } else {
                    if (pageType == 1) {
                        fragment.deleteComment(circle_type, 2, outerPos, position);
                    } else if (pageType == 2) {
                        activity.deleteComment(circle_type, 2, outerPos, position);
                    } else if (pageType == 3) {
                        //subfragment.deleteComment(circle_type, 2, outerPos, position);
                    }
                }*/
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getColor(R.color.circle_name2));
            }
        }, ssb.length() - item.getMc_content().length(), ssb.length(), FLAG);
        textDate.setText(ssb);
        textDate.setMovementMethod(LinkMovementMethod.getInstance());
        textDate.setHighlightColor(Color.TRANSPARENT);
       /* textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //my_mobile  mobile
                Intent intent = new Intent(context, ContactsContentActivity.class);
                //.putExtra("f_uid", "" + item.getUid())
                intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
                intent.putExtra("mobile", item.getMobile());
                intent.putExtra("type", "" + circle_type);
                context.startActivity(intent);
            }
        });*/
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"photoClicke");
                Intent intent = new Intent(context, ContactsContentActivity.class);
                //.putExtra("f_uid", "" + item.getUid())
                intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
                intent.putExtra("mobile", item.getMobile());
                intent.putExtra("type", "" + circle_type);
                context.startActivity(intent);
            }
        });
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,".......uid="+uid+"     "+item.getUid());
                if (!uid.equals(item.getUid())) {
                    if (pageType == 1) {
                        Log.i(TAG,".......1");
                        fragment.openEditText(outerPos, item.getUid());
                    } else if (pageType == 2) {
                        activity.openEditText(outerPos, item.getUid());
                    } else if (pageType == 3) {
                        //subfragment.openEditText(outerPos, item.getUid());
                    }
                } else {
                    if (pageType == 1) {
                        fragment.deleteComment(circle_type, 2, outerPos, position);
                    } else if (pageType == 2) {
                        activity.deleteComment(circle_type, 2, outerPos, position);
                    } else if (pageType == 3) {
                        //subfragment.deleteComment(circle_type, 2, outerPos, position);
                    }
                }
            }
        });
    }


    private int getColor(int colorId) {
        return mContext.getResources().getColor(colorId);
    }

//    @Override
//    protected void convert(ViewHolder viewHolder, final CommentBean item, final int position) {
//        TextView textName = viewHolder.getView(R.id.text_name);
//        TextView textDate = viewHolder.getView(R.id.text_date);
//        TextView textContent = viewHolder.getView(R.id.text_content);
//        TextView textAt = viewHolder.getView(R.id.text_at);
//        TextView textReply = viewHolder.getView(R.id.text_reply);
//        textName.setText("" + item.getName());
//        textDate.setText("" + item.getComment_time());
//        if (!"0".equals(item.getUid()) && !item.getUid().equals(item.getReply_uid())) {
//            textAt.setVisibility(View.VISIBLE);
//            textReply.setVisibility(View.VISIBLE);
//            if (!"0".equals(item.getReply_uid())) {
//                textAt.setText("@");
//                if (TextUtils.isEmpty(item.getReply_name())) {
//                    textReply.setText("   ");
//                } else {
//                    textReply.setText(" " + item.getReply_name() + " ");
//                }
//            } else {
//                textAt.setVisibility(View.GONE);
//                textReply.setVisibility(View.GONE);
//            }
//        } else {
//            textAt.setVisibility(View.GONE);
//            textReply.setVisibility(View.GONE);
//        }
//
//        textContent.setText(item.getMc_content());
//        textName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //my_mobile  mobile
//                Intent intent = new Intent(context, ContactsContentActivity.class);
//                //.putExtra("f_uid", "" + item.getUid())
//                intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
//                intent.putExtra("mobile", item.getMobile());
//                intent.putExtra("type", "" + circle_type);
//                context.startActivity(intent);
//            }
//        });
//        textReply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ContactsContentActivity.class);
//                //.putExtra("f_uid", "" + item.getUid())
//                intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
//                intent.putExtra("mobile", item.getReply_mobile());
//                intent.putExtra("type", "" + circle_type);
//                context.startActivity(intent);
//            }
//        });
//        textContent.setText(ssb.toString());
//        textContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!uid.equals(item.getUid())) {
//                    if (pageType == 1) {
//                        fragment.openEditText(outerPos, item.getUid());
//                    } else if (pageType == 2) {
//                        activity.openEditText(outerPos, item.getUid());
//                    }
//                } else {
//                    if (pageType == 1) {
//                        fragment.deleteComment(circle_type, 2, outerPos, position);
//                    } else if (pageType == 2) {
//                        activity.deleteComment(circle_type, 2, outerPos, position);
//                    }
//                }
//            }
//        });
//    }

//    if (!"0".equals(item.getReply_uid())) {
//        sb.append("<font color='#464646'>").append("@").append("</font>")
//                .append("<font color='#8170ca'>").append(" " + item.getReply_name() + " ").append("</font>");
//    }
//        sb.append("<font color='#464646'>").append("" + item.getMc_content()).append("</font>");
//        textContent.setText(Html.fromHtml(sb.toString()));
}
