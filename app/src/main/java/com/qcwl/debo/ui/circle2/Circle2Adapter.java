//package com.qcwl.debo.ui.circle2;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.qcwl.debo.R;
//import com.qcwl.debo.ui.circle.ImgUtil;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import de.hdodenhof.circleimageview.CircleImageView;
//
///**
// * Created by AlMn on 2018/3/9.
// */
//
//public class Circle2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private Context mContext = null;
//    private CircleResultBean result;
//
//    private Circle2Fragment fragment;
//
//    public Circle2Adapter(Circle2Fragment fragment, CircleResultBean resultBean) {
//        this.result = resultBean;
//        this.fragment = fragment;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        mContext = parent.getContext();
//        RecyclerView.ViewHolder viewHolder = null;
//        View view = null;
//        switch (viewType) {
//            case 0:
//                view = inflate(R.layout.layout_circle_header);
//                viewHolder = new HeaderViewHolder(view);
//                break;
//            case 1:
//                view = inflate(R.layout.layout_circle_list);
//                viewHolder = new FrameViewHolder(view);
//                break;
//        }
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        int viewType = getItemViewType(position);
//        switch (viewType) {
//            case 0:
//                initHeaderView((HeaderViewHolder) holder);
//                break;
//            case 1:
//                initHeaderView((FrameViewHolder) holder);
//                break;
//        }
//    }
//
//    private void initHeaderView(HeaderViewHolder holder) {
//        if (TextUtils.isEmpty(result.getBgImg())) {
//            holder.headerViewBg.setImageResource(R.mipmap.debo_logo);
//        } else {
//            ImgUtil.load(mContext, result.getBgImg(), holder.headerViewBg);
//        }
//        if (TextUtils.isEmpty(result.getAvatar())) {
//            holder.headerPhoto.setImageResource(R.mipmap.head);
//        } else {
//            ImgUtil.loadHead(mContext, result.getAvatar(), holder.headerPhoto);
//        }
//        holder.headerName.setText(result.getNickname());
//        holder.headerViewBg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fragment.onCirlceHeaderClick();
//            }
//        });
//        holder.headerPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fragment.onCircleHeaderUserPhotoClick();
//            }
//        });
//    }
//
//    private void initHeaderView(FrameViewHolder holder) {
//        holder.frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1,-1));
//    }
//
//    @Override
//    public int getItemCount() {
//        return 2;
//    }
//
//    private View inflate(int layoutId) {
//        return LayoutInflater.from(mContext).inflate(layoutId, null);
//    }
//
//    static class HeaderViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.img_circle_bg)
//        ImageView headerViewBg;
//        @Bind(R.id.img_photo)
//        CircleImageView headerPhoto;
//        @Bind(R.id.text_name)
//        TextView headerName;
//        @Bind(R.id.comment_iv)
//        ImageView commentIv;
//        @Bind(R.id.comment_tv)
//        TextView commentTv;
//        @Bind(R.id.comment_linear)
//        LinearLayout commentLinear;
//
//        HeaderViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//    static class FrameViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.frame_layout)
//        FrameLayout frameLayout;
//
//        FrameViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//}
