package com.qcwl.debo.ui.my.wallet.trading;

import android.content.Context;
import android.widget.RadioButton;

import com.qcwl.debo.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7.
 */

public class RadioAdapter extends CommonAdapter<RadioBean> {
    public RadioAdapter(Context context, List<RadioBean> datas) {
        super(context, R.layout.item_trading_type, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, RadioBean item, int position) {
        try {
            RadioButton radioButton = viewHolder.getView(R.id.radio_button);
            radioButton.setText(item.getText());
            radioButton.setChecked(item.isChecked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
