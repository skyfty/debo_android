package com.qcwl.debo.ui.found.memorandum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.utils.ColorUtils;

/**
 * Created by admin on 2018/6/21.
 */

public class TextColorAdapter extends RecyclerView.Adapter<TextColorAdapter.TextColorHolder> {

    private OnItemClickListener listener;
    private Context mContext;

    public TextColorAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public TextColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextColorHolder holder = new TextColorHolder(View.inflate(mContext, R.layout.item_text_color, null),listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(TextColorHolder holder, int position) {
        holder.mColor.setBackgroundResource(ColorUtils.textColor[position]);
    }

    @Override
    public int getItemCount() {
        return ColorUtils.textColor.length;
    }

    public class TextColorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RoundedImageView mColor;
        private OnItemClickListener listener;
        public TextColorHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            mColor = (RoundedImageView) itemView.findViewById(R.id.color);
            itemView.setOnClickListener( this);
        }
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.OnItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
}
