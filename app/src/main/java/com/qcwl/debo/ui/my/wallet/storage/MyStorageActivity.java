package com.qcwl.debo.ui.my.wallet.storage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.my.AboutActivity;
import com.qcwl.debo.utils.MD5;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomScrollView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyStorageActivity extends BaseActivity {

    @Bind(R.id.text_total)
    TextView textTotal;
    @Bind(R.id.text_total_income)
    TextView textTotalIncome;
    @Bind(R.id.text_yesterday_income)
    TextView textYesterdayIncome;
    @Bind(R.id.scroll_view)
    CustomScrollView scrollView;
    @Bind(R.id.view_status_bar)
    View viewStatusBar;
    @Bind(R.id.left_image)
    ImageView leftImage;
    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.layout_title)
    RelativeLayout layoutTitle;
    @Bind(R.id.text_right)
    TextView textRight;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_storage);
        ButterKnife.bind(this);
        money = getIntent().getStringExtra("money");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private int alpha = 0;

    private void initView() {
        scrollView.setOnScrollListener(new CustomScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                int distance = ScreenUtils.dp2px(MyStorageActivity.this, 70);
                if (scrollY > 0 && scrollY < distance) {
                    alpha = (int) (1.0 * scrollY / distance * 255);
                    viewStatusBar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layoutTitle.setBackgroundColor(getResources().getColor(R.color.white));
                    leftImage.setImageResource(R.mipmap.goback);
                    textTitle.setTextColor(getResources().getColor(R.color.circle_name));
                    textRight.setTextColor(getResources().getColor(R.color.circle_name));
                } else if (scrollY >= distance) {
                    alpha = 255;
                    viewStatusBar.setBackgroundColor(getResources().getColor(R.color.status_bar_bg));
                    layoutTitle.setBackgroundColor(getResources().getColor(R.color.white));
                    leftImage.setImageResource(R.mipmap.goback);
                    textTitle.setTextColor(getResources().getColor(R.color.circle_name));
                    textRight.setTextColor(getResources().getColor(R.color.circle_name));
                } else {
                    alpha = 0;
                    viewStatusBar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layoutTitle.setBackgroundColor(getResources().getColor(R.color.transparent));
                    leftImage.setImageResource(R.mipmap.back);
                    textTitle.setTextColor(getResources().getColor(R.color.white));
                    textRight.setTextColor(getResources().getColor(R.color.white));
                }
                layoutTitle.getBackground().setAlpha(alpha);
            }
        });
    }

    private String total_money;

    private void loadData() {

        Api.getMyStorage(sp.getString("uid"),
                sp.getString("phone"),
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            try {
                                JSONObject object = new JSONObject(apiResponse.getData());
                                total_money = object.optString("total_money");
                                String total_income = object.optString("total_income");
                                String income = object.optString("income");
                                textTotal.setText("" + total_money);
                                textTotalIncome.setText("" + total_income);
                                textYesterdayIncome.setText("" + income);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.showShort(MyStorageActivity.this, apiResponse.getMessage());
                        }
                    }
                });
    }

    @OnClick({R.id.btn_roll_out, R.id.btn_roll_in, R.id.left_image, R.id.text_right, R.id.layout_total_income})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_roll_out:
                startActivity(new Intent(this, TransferAccountsActivity.class)
                        .putExtra("type", 0)
                        .putExtra("money", money)
                        .putExtra("total_money", total_money));
                break;
            case R.id.btn_roll_in:
                startActivity(new Intent(this, TransferAccountsActivity.class)
                        .putExtra("type", 1)
                        .putExtra("money", money)
                        .putExtra("total_money", total_money));
                break;
            case R.id.left_image:
                finish();
                break;
            case R.id.text_right:
                startActivity(new Intent(this, AboutActivity.class).putExtra("type", 8));
                break;
            case R.id.layout_total_income:
                startActivity(new Intent(this, TotalIncomeActivity.class)
                        .putExtra("type", 1));
                break;
        }
    }
}
