package com.qcwl.debo.ui.my.partner;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Printer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.event.MessageEvent;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.partner.record.CoinsTradingRecordActivity;
import com.qcwl.debo.ui.my.partner.sale.CoinsSaleActivity;
import com.qcwl.debo.ui.my.partner.shop.CoinsShopActivity;
import com.qcwl.debo.ui.my.partner.withdraw.WithdrawCoinsActivity;
import com.qcwl.debo.ui.my.wallet.storage.TotalIncomeActivity;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.widget.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AlMn on 2017/11/16.
 */

public class OpenedFragment extends Fragment {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.text_right)
    TextView textRight;
    @Bind(R.id.text_limit)
    TextView textLimit;
    @Bind(R.id.text_total)
    TextView textTotal;
    @Bind(R.id.text_income)
    TextView textIncome;

    private String uid, mobile, limitCoins, currentCoins;
    private String TAG = "OpenedFragment";
    private boolean isGroup = false;
    private boolean isGroup1 = false;
    private ViewHolder holder;
    private int index = 0;
    private int index2 = 0;
    private JSONObject object;
    private RadioButton radiobutton2 = null;
    private int position = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_opened, container, false);
        ButterKnife.bind(this, view);
        getRote();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textTitle.setText("合伙人");
        textRight.setText("交易记录");
        textRight.setVisibility(View.VISIBLE);
        uid = SPUtil.getInstance(getActivity()).getString("uid");
        mobile = SPUtil.getInstance(getActivity()).getString("phone");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"................onResume");
        getTotalLimitCoins();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        getTotalLimitCoins();
    }
    private void getTotalLimitCoins() {
        Api.getTotalLimitCoins(uid, mobile, new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                try {
                    if (apiResponse.getCode() == 0) {
                        Log.i(TAG,"................getTotalLimitCoins="+apiResponse.getData());
                        JSONObject object = new JSONObject(apiResponse.getData());
                        limitCoins = object.optString("purchase_limit");
                        currentCoins = object.optString("debo_coins");
                        textLimit.setText("" + limitCoins);
                        textTotal.setText("" + currentCoins);
                        textIncome.setText("" + object.optString("income"));
                    } else {
                        Toast.makeText(getActivity(), "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getRote() {
        Api.getRote(new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                try {
                    if (apiResponse.getCode() == 0) {
                        object = new JSONObject(apiResponse.getData());
                    } else {
                        Toast.makeText(getActivity(), "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    Dialog bottomDialog = null;
    View contentView = null;

    private void initBuyDialog() {
        bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_partner_buy, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels; //- ScreenUtils.dp2px(this, 16f);
        params.bottomMargin = 0;//ScreenUtils.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        listener(contentView);
    }

    private void listener(View contentView) {
        holder = new ViewHolder(contentView);
        /*try {
            holder.radio_btn.setText(object.getString("three_rate"));
            holder.radio_btn1.setText(object.getString("six_rate"));
            holder.radio_btn2.setText(object.getString("nine_rate"));
            holder.radio_btn3.setText(object.getString("twelve_rate"));
            holder.radio_btn4.setText(object.getString("freedom"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        String[] s = new String[5];
        try {
            s[0] = object.getString("three_rate");
            s[1] = object.getString("six_rate");
            s[2] = object.getString("nine_rate");
            s[3] = object.getString("twelve_rate");
            s[4] = object.getString("freedom");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //String[] s = new String[]{"asdfasdf","asdfasdf","asdf","asdfasdfasdfasdfadsf","asdfa","asdf","asdfas","asdfasdfasdf"};
        //初始化单选
        holder.flowLayoutSingleView.removeAllViews();
        for (int i = 0; i < s.length; i++) {
            final TextView child = new TextView(getActivity());
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            if (i == s.length - 1) {
                params.setMargins(10, 12, 5, 15);
            } else {
                params.setMargins(10, 12, 5, 5);
            }
            child.setLayoutParams(params);
            child.setTag(false);
            child.setBackgroundResource(R.drawable.partner_checkbg);
            child.setText(s[i]);
            child.setTextColor(Color.BLACK);
            child.setTextSize(9);
            child.setPadding(12, 12, 12, 12);
            child.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    for(int a=0;a<holder.flowLayoutSingleView.getChildCount();a++){
                        holder.flowLayoutSingleView.getChildAt(a).setTag(false);
                        TextView view = (TextView)holder.flowLayoutSingleView.getChildAt(a);
                        view.setTextColor(Color.BLACK);
                        holder.flowLayoutSingleView.getChildAt(a).setBackgroundResource(R.drawable.partner_checkbg);
                    }

                    child.setTag(true);
                    child.setTextColor(getResources().getColor(R.color.color_red2));
                    child.setBackgroundResource(R.drawable.partner_checkbg2);
                }
            });
            holder.flowLayoutSingleView.addView(child);
            holder.flowLayoutSingleView.requestLayout();
        }

        holder.textCurrentNum.setText("现有" + currentCoins + "个嘚啵币");
        holder.textMaxNum.setText("最高可购买" + limitCoins + "个嘚啵币");
        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = holder.editNum.getText().toString().trim();
                if (TextUtils.isEmpty(num) || Integer.parseInt(num) == 0) {
                    Toast.makeText(getActivity(), "请输入购买数量", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i=0;i<holder.flowLayoutSingleView.getChildCount();i++){
                    if((Boolean) holder.flowLayoutSingleView.getChildAt(i).getTag()){
                        position = i;
                        if (position == 4){
                            position = 0;
                        }else{
                            position+=1;
                        }
                        break;
                    }
                }
                if (position == -1){
                    Toast.makeText(getActivity(), "请选择购买时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"......position="+position);
                PayDialog.createDialog(getActivity(), "购买嘚啵币", 18, "", "" + Integer.parseInt(num),""+position).show();
                bottomDialog.dismiss();
            }
        });

        /*holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "..........radioGroup1=" + checkedId);
                if (checkedId != -1) {
                    RadioButton radiobutton = (RadioButton) group.findViewById(checkedId);
                    if (radiobutton.isChecked()) {
                        holder.radioGroup2.clearCheck();
                    }
                    setTextValue(radiobutton, radiobutton.getText().toString());
                    position = group.indexOfChild(group.findViewById(checkedId))+1;
                    Log.i(TAG, "..........position=" + position);
                }
            }
        });
        holder.radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "..........ffradioGroup2=" + checkedId);
                if (checkedId != -1) {
                    RadioButton radiobutton = (RadioButton) group.findViewById(checkedId);
                    if (radiobutton.isChecked()) {
                        holder.radioGroup.clearCheck();
                    }
                    setTextValue(radiobutton, radiobutton.getText().toString());
                    position = group.indexOfChild(group.findViewById(checkedId));
                    if(position == 1){
                        position = 0;
                    }else{
                        position = 4;
                    }
                    Log.i(TAG, "..........position2=" + position);
                }
            }
        });*/
        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = holder.editNum.getText().toString();
                Log.i(TAG, "..........iv_add=" + num + "　　　　" + Integer.parseInt(num));
                Integer integer = Integer.parseInt(num);
                integer += 100;
                holder.editNum.setText(integer.toString());
                holder.editNum.setSelection(integer.toString().length());

            }
        });
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = holder.editNum.getText().toString();
                Log.i(TAG, "..........iv_delete=" + num);
                Integer integer = Integer.parseInt(num);
                if (integer >= 100) {
                    integer -= 100;
                    holder.editNum.setText(integer.toString());
                    holder.editNum.setSelection(integer.toString().length());
                }
            }
        });
    }

    private void initFlowlayout(JSONObject object) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setTextValue(RadioButton radioButton, String value) {
        //int end = value.indexOf("|");
        int end = value.length();
        Log.i(TAG, "................end=" + end + "     " + value.length());
        if (end == -1) {
            end = value.length();
        }
        ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_red2));
        SpannableStringBuilder builder = new SpannableStringBuilder(value);
        //为不同位置字符串设置不同颜色
        //四个参数分别为，颜色值，起始位置，结束位置，最后的为类型。
        builder.setSpan(redSpan, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        radioButton.setText(builder);

        if (radiobutton2 != null) {
            String value2 = radiobutton2.getText().toString();
            int end2 = value2.indexOf("|");
            Log.i(TAG, "................end2=" + end2 + "     " + value2.length()+"   "+value2);
            if (end2 == -1) {
                end2 = value2.length();
            }
            Log.i(TAG, "................radiobutton2=" + value2 + "     " + value2.length());
            ForegroundColorSpan redSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.circle_reply2));
            SpannableStringBuilder builder2 = new SpannableStringBuilder(value2);
            //为不同位置字符串设置不同颜色
            //四个参数分别为，颜色值，起始位置，结束位置，最后的为类型。
            builder2.setSpan(redSpan2, 0, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            radiobutton2.setText(builder2);
        }
        radiobutton2 = radioButton;
    }

    @OnClick({R.id.left_image, R.id.text_right, R.id.btn_buy, R.id.layout_shop, R.id.layout_sale, R.id.layout_withdrawal, R.id.layout_income})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                getActivity().finish();
                break;
            case R.id.text_right:
                startActivity(new Intent(getActivity(), CoinsTradingRecordActivity.class));
                break;
            case R.id.btn_buy:
                if (bottomDialog == null) {
                    initBuyDialog();
                }
                bottomDialog.show();
                break;
            case R.id.layout_income:
                startActivity(new Intent(getActivity(), TotalIncomeActivity.class)
                        .putExtra("type", 2));
                break;
            case R.id.layout_shop:
                startActivity(new Intent(getActivity(), CoinsShopActivity.class));
                break;
            case R.id.layout_sale:
                startActivity(new Intent(getActivity(), CoinsSaleActivity.class).putExtra("status", 1));
                break;
            case R.id.layout_withdrawal:
                startActivity(new Intent(getActivity(), WithdrawCoinsActivity.class).putExtra("total_coins", currentCoins));
                break;
        }
    }

    static class ViewHolder {
        @Bind(R.id.image_view)
        ImageView imageView;
        @Bind(R.id.text_current_num)
        TextView textCurrentNum;
        @Bind(R.id.text_max_num)
        TextView textMaxNum;
        @Bind(R.id.edit_num)
        EditText editNum;
        @Bind(R.id.btn_confirm)
        Button btnConfirm;
        /*@Bind(R.id.radio_group)
        RadioGroup radioGroup;
        @Bind(R.id.radio_group2)
        RadioGroup radioGroup2;*/

        @Bind(R.id.iv_add)
        ImageView iv_add;
        @Bind(R.id.iv_delete)
        ImageView iv_delete;

        /*@Bind(R.id.radio_btn)
        RadioButton radio_btn;
        @Bind(R.id.radio_btn1)
        RadioButton radio_btn1;
        @Bind(R.id.radio_btn2)
        RadioButton radio_btn2;
        @Bind(R.id.radio_btn3)
        RadioButton radio_btn3;
        @Bind(R.id.radio_btn4)
        RadioButton radio_btn4;*/

        @Bind(R.id.flowLayoutSingleView)
        FlowLayout flowLayoutSingleView;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
