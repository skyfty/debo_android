package com.qcwl.debo.ui.my.wallet.trading;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.ui.my.wallet.TradingRecordActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/6.
 */

public class TradingAdapter extends CommonAdapter<TradingBean> {

    public TradingAdapter(Context context, List<TradingBean> datas) {
        super(context, R.layout.item_trading_record, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TradingBean item, int position) {
        try {
            TradingViewHolder holder = new TradingViewHolder(viewHolder.getConvertView());
            if (item.getType() == 2) {
                holder.layoutTotal.setVisibility(View.VISIBLE);
                holder.layoutDetail.setVisibility(View.GONE);
                holder.textInfoDate.setText(item.getYear() + "年" + item.getMonth() + "月");
                holder.textInfo.setText(item.getIncome_defray());
                holder.imageFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TradingRecordActivity) mContext).showTimePicker();
                    }
                });
            } else if (item.getType() == 1) {
                holder.layoutTotal.setVisibility(View.GONE);
                holder.layoutDetail.setVisibility(View.VISIBLE);
                holder.textDetailDate.setText(item.getCreate_time());
                typeSetting(item.getIndent());
                holder.imageType.setImageResource(imgResId);
                item.setImgResId(imgResId);
                holder.textDetailType.setText(detailInfo);
                holder.textDetailMoney.setText(symbol + item.getOrder_price());
                holder.textDetailMoney.setTextColor(mContext.getResources().getColor(moneyTextColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String symbol = "";
    private int moneyTextColor = 0;
    private int imgResId = 0;
    private String detailInfo = "";

    private void typeSetting(int indent) {
        //1、充值 ；2、购买合约人；3、发红包；4、领取红包；5、退款；6、购买小喇叭；7、三方客转入；8、三方客转出；9、提现；10、积分兑换
        switch (indent) {
            case 1:
                imgResId = R.mipmap.ic_trading_recharge;
                detailInfo = "充值";
                symbol = "+";
                moneyTextColor = R.color.bump_red;
                break;
            case 2:
                imgResId = R.mipmap.ic_trading_contacts;
                detailInfo = "购买合约人";
                symbol = "-";
                moneyTextColor = R.color.circle_name;
                break;
            case 3:
                imgResId = R.mipmap.ic_trading_red_packet;
                detailInfo = "发红包";
                symbol = "-";
                moneyTextColor = R.color.circle_name;
                break;
            case 4:
                imgResId = R.mipmap.ic_trading_red_packet;
                detailInfo = "领取红包";
                symbol = "+";
                moneyTextColor = R.color.bump_red;
                break;
            case 5:
                imgResId = R.mipmap.ic_trading_refund;
                detailInfo = "退款";
                symbol = "+";
                moneyTextColor = R.color.bump_red;
                break;
            case 6:
                imgResId = R.mipmap.ic_trading_trumpet;
                detailInfo = "购买小喇叭";
                symbol = "-";
                moneyTextColor = R.color.circle_name;
                break;
            case 7:
                imgResId = R.mipmap.ic_trading_guarantee;
                detailInfo = "三方客转入";
                symbol = "+";
                moneyTextColor = R.color.bump_red;
                break;
            case 8:
                imgResId = R.mipmap.ic_trading_guarantee;
                detailInfo = "三方客转出";
                symbol = "-";
                moneyTextColor = R.color.circle_name;
                break;
            case 9:
                imgResId = R.mipmap.ic_trading_withdrawal;
                detailInfo = "提现";
                symbol = "";
                moneyTextColor = R.color.circle_name;
                break;
            case 10:
                imgResId = R.mipmap.ic_trading_point;
                detailInfo = "积分兑换";
                symbol = "+";
                moneyTextColor = R.color.bump_red;
                break;
            case 13:
                imgResId = R.mipmap.ic_transfer;
                detailInfo = "转出";
                symbol = "-";
                moneyTextColor = R.color.bump_red;
                break;
            case 14:
                imgResId = R.mipmap.ic_transfer;
                detailInfo = "转入";
                symbol = "+";
                moneyTextColor = R.color.bump_red;
                break;
            case 15:
                imgResId = R.mipmap.ic_transfer_in;
                detailInfo = "存入（储蓄罐）";
                symbol = "";
                moneyTextColor = R.color.color_green;
                break;
            case 16:
                imgResId = R.mipmap.ic_transfer_out;
                detailInfo = "转出（储蓄罐）";
                symbol = "";
                moneyTextColor = R.color.color_green;
                break;
            case 17:
                imgResId = R.mipmap.ic_transfer_out;
                detailInfo = "转出（合伙人）";
                symbol = "";
                moneyTextColor = R.color.color_green;
                break;
            case 18:
                imgResId = R.mipmap.ic_coins_purchase;
                detailInfo = "购买（嘚啵币）";
                symbol = "";
                moneyTextColor = R.color.color_green;
                break;
            case 20:
                imgResId = R.mipmap.icon_gift_trading;
                detailInfo = "发送礼物";
                symbol = "-";
                moneyTextColor = R.color.bump_red;
                break;
            case 21:
                imgResId = R.mipmap.icon_gift_trading;
                detailInfo = "接收礼物";
                symbol = "+";
                moneyTextColor = R.color.circle_name;
                break;
            default:
                break;
        }
    }
    //15、存入（储蓄罐）、16、转出（储蓄罐）、17、转出（合伙人）、18、购买（嘚啵币）

    static class TradingViewHolder {
        @Bind(R.id.image_filter)
        ImageView imageFilter;
        @Bind(R.id.text_info_date)
        TextView textInfoDate;
        @Bind(R.id.text_info)
        TextView textInfo;
        @Bind(R.id.layout_total)
        RelativeLayout layoutTotal;
        @Bind(R.id.image_type)
        ImageView imageType;
        @Bind(R.id.text_detail_type)
        TextView textDetailType;
        @Bind(R.id.text_detail_money)
        TextView textDetailMoney;
        @Bind(R.id.text_detail_date)
        TextView textDetailDate;
        @Bind(R.id.layout_detail)
        LinearLayout layoutDetail;

        TradingViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
