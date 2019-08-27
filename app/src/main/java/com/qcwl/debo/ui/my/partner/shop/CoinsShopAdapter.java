package com.qcwl.debo.ui.my.partner.shop;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlMn on 2017/11/20.
 */

public class CoinsShopAdapter extends CommonAdapter<CoinsShopBean> {

    private int screenWidth = 0;

    public CoinsShopAdapter(Context context, List<CoinsShopBean> datas) {
        super(context, R.layout.item_coins_shop, datas);
        screenWidth = ScreenUtils.getScreenWidth(context);
    }

    @Override
    protected void convert(ViewHolder viewHolder, CoinsShopBean item, int position) {
        try {
            ShopViewHolder holder = new ShopViewHolder(viewHolder.getConvertView());
            holder.textPrice.setText(item.getMoney() + "元");
            holder.textNumber.setText(item.getDebo_coins());
            int imgBgWidth = screenWidth / -dp2px(20);//图片背景宽度，图片实际比例（230dp*137dp）
            int imgBgHeight = (int) (imgBgWidth * 137 / 230.0f);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(imgBgWidth, imgBgHeight);
            params.topMargin=dp2px(20);
            params.bottomMargin=dp2px(10);
            holder.layout.setLayoutParams(params);
            holder.layout.setPadding(dp2px(10), 0, dp2px(10), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(mContext, dpVal);
    }

    static class ShopViewHolder {
        @Bind(R.id.text_price)
        TextView textPrice;
        //        @Bind(R.id.image_view)
//        ImageView imageView;
        @Bind(R.id.text_number)
        TextView textNumber;
        @Bind(R.id.layout)
        LinearLayout layout;

        ShopViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
