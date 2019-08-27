package com.qcwl.debo.ui.found.fans.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.fans.FansDynamicCommentListActivity;
import com.qcwl.debo.ui.found.fans.bean.FansDynamicCommentBean;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/3.
 */

public class FansDynamicCommentAdapter extends CommonAdapter<FansDynamicCommentBean> {

    public static final int FLAG = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    public FansDynamicCommentAdapter(Context context, List<FansDynamicCommentBean> datas) {
        super(context, R.layout.item_fans_dynamic_comment, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final FansDynamicCommentBean item, final int outerPos) {
        try {
            CommentViewHolder holder = new CommentViewHolder(viewHolder.getConvertView());
            ImgUtil.loadHead(mContext, item.getAvatar(), holder.imageView);
            holder.textName.setText(item.getName());
            holder.textTime.setText(item.getComment_time());
            holder.textContent.setText(item.getMc_content());
            holder.checkboxPraise.setText(item.getUpvote_num());

            if (item.getIs_upvote() == 1) {
                holder.checkboxPraise.setChecked(true);
            } else {
                holder.checkboxPraise.setChecked(false);
            }
            if (item.getUid().equals(SPUtil.getInstance(mContext).getString("uid"))) {
                holder.textDelete.setVisibility(View.VISIBLE);
                holder.imgComment.setVisibility(View.INVISIBLE);
                holder.imgComment.setClickable(false);
            } else {
                holder.textDelete.setVisibility(View.GONE);
                holder.imgComment.setVisibility(View.VISIBLE);
                holder.imgComment.setClickable(true);
            }

            List<FansDynamicCommentBean> list = item.getReplay_list();
            if (list == null || list.size() == 0) {
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                holder.recyclerView.setVisibility(View.VISIBLE);
                loadCommentData(holder, outerPos, item.getMc_id(), item.getUid(), list);
            }
            holder.imgComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof FansDynamicCommentListActivity) {
                        ((FansDynamicCommentListActivity) mContext).showSoftInput(outerPos, item.getMc_id(), item.getUid());
                    }
                }
            });
            holder.checkboxPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof FansDynamicCommentListActivity) {
                        ((FansDynamicCommentListActivity) mContext).praiseComment(outerPos, item.getMc_id(), item.getIs_upvote());
                    }
                }
            });
            holder.textDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof FansDynamicCommentListActivity) {
                        ((FansDynamicCommentListActivity) mContext).deleteFansDynamicComment(outerPos, -1);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadCommentData(CommentViewHolder holder, final int outerPos, final String mc_id, final String uid, List<FansDynamicCommentBean> list) {
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.recyclerView.setAdapter(new CommonAdapter<FansDynamicCommentBean>(mContext, R.layout.item_textview, list) {
            @Override
            protected void convert(ViewHolder vh, final FansDynamicCommentBean bean, final int innerPos) {
                try {
                    TextView textView = vh.getView(R.id.text_view);
                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    ssb.append(bean.getName());
                    ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_reply)), 0, ssb.length(), FLAG);
                    ssb.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
//                            Intent intent=new Intent(mContext, ContactsContentActivity.class);
//                            intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
//                            intent.putExtra("mobile",bean.getMobile());
//                            mContext.startActivity(intent);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(getColor(R.color.circle_reply));
                        }
                    }, 0, ssb.length(), FLAG);
                    if (!TextUtils.isEmpty(bean.getUid()) && !bean.getUid().equals(uid)) {
                        ssb.append(" @ ");
                        ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_name)), ssb.length() - 3, ssb.length(), FLAG);
                        ssb.append(bean.getReply_name());
                        ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_reply)), ssb.length() - bean.getReply_name().length(), ssb.length(), FLAG);
                        ssb.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
//                                Intent intent=new Intent(mContext, ContactsContentActivity.class);
//                                intent.putExtra("my_mobile", SPUtil.getInstance(mContext).getString("phone"));
//                                intent.putExtra("mobile",bean.getReply_mobile());
//                                mContext.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setColor(getColor(R.color.circle_reply));
                            }
                        }, ssb.length() - bean.getReply_name().length(), ssb.length(), FLAG);
                    }
                    ssb.append("：");
                    ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_reply)), ssb.length() - 1, ssb.length(), FLAG);
                    ssb.append(bean.getMc_content());
                    ssb.setSpan(new ForegroundColorSpan(getColor(R.color.circle_name)), ssb.length() - bean.getMc_content().length(), ssb.length(), FLAG);
                    ssb.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mContext instanceof FansDynamicCommentListActivity) {
                                if (bean.getUid().equals(SPUtil.getInstance(mContext).getString("uid"))) {
                                    ((FansDynamicCommentListActivity) mContext).deleteFansDynamicComment(outerPos, innerPos);
                                } else {
                                    ((FansDynamicCommentListActivity) mContext).showSoftInput(outerPos, mc_id, bean.getUid());
                                }
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(getColor(R.color.circle_name));
                        }
                    }, ssb.length() - bean.getMc_content().length(), ssb.length(), FLAG);
                    textView.setText(ssb);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setHighlightColor(Color.TRANSPARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int getColor(int colorId) {
        return mContext.getResources().getColor(colorId);
    }

    static class CommentViewHolder {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.checkbox_praise)
        CheckBox checkboxPraise;
        @Bind(R.id.img_comment)
        ImageView imgComment;
        @Bind(R.id.text_time)
        TextView textTime;
        @Bind(R.id.text_delete)
        TextView textDelete;
        @Bind(R.id.text_content)
        TextView textContent;
        @Bind(R.id.recycler_view)
        RecyclerView recyclerView;

        CommentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
