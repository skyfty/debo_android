package com.qcwl.debo.ui.my.sheet;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class SheetAdapter extends CommonAdapter<SheetBean> {

    public static final int FLAG = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    public SheetAdapter(Context context, List<SheetBean> datas) {
        super(context, R.layout.item_sheet, datas);
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(mContext, dpVal);
    }

    @Override
    protected void convert(ViewHolder viewHolder, SheetBean item, int position) {
        SheetViewHolder holder = new SheetViewHolder(viewHolder.getConvertView());
        try {
            ImgUtil.loadHead(mContext, item.getAvatar(), holder.imageView);
            holder.textName.setText(item.getUser_nickname());
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append("购买价格：")
                    .setSpan(new ForegroundColorSpan(getColor(R.color.fans_gray)), 0, ssb.length(), FLAG);
            ssb.append("￥" + item.getPur_price())
                    .setSpan(new ForegroundColorSpan(getColor(R.color.color_green)), 5, ssb.length(), FLAG);
            holder.textPrice.setText(ssb);
            //--------------------------------
            holder.textStartDate.setText("购买时间：" + item.getPur_time());
            //--------------------------------
            ssb = new SpannableStringBuilder();
            ssb.append("到期时间：")
                    .setSpan(new ForegroundColorSpan(getColor(R.color.fans_gray)), 0, ssb.length(), FLAG);
            ssb.append(item.getPur_contract_time())
                    .setSpan(new ForegroundColorSpan(getColor(R.color.bump_red)), 5, ssb.length(), FLAG);
            holder.textEndDate.setText(ssb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getColor(int colorId) {
        return mContext.getResources().getColor(colorId);
    }

    static class SheetViewHolder {
        @Bind(R.id.image_view)
        CircleImageView imageView;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_price)
        TextView textPrice;
        @Bind(R.id.text_start_date)
        TextView textStartDate;
        @Bind(R.id.text_end_date)
        TextView textEndDate;

        SheetViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
