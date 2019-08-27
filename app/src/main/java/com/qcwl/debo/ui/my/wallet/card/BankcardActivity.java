package com.qcwl.debo.ui.my.wallet.card;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.ListUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BankcardActivity extends BaseActivity {

    @Bind(R.id.list_view)
    ListView listView;

    private List<BankcardBean> items = null;
    private BankcardAdapter adapter = null;

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard);
        ButterKnife.bind(this);
        initTitleBar();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBankcardList();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("银行卡")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initData() {
        LinearLayout emptyView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_emptyview_bankcard, null);
        ImageView imageAdd = (ImageView) emptyView.findViewById(R.id.image_add);
        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BankcardActivity.this, AddBankcardActivity.class), 100);
            }
        });
        ListUtils.setEmptyView(this, listView, emptyView);
        View view = new View(this);
        view.setLayoutParams(new AbsListView.LayoutParams(-1, ScreenUtils.dp2px(this, 0)));
        listView.addHeaderView(view);
        items = new ArrayList<>();
        adapter = new BankcardAdapter(this, items);
        listView.setAdapter(adapter);
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 0);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (type == 1) {
                        Intent data = new Intent();
                        data.putExtra("bean", items.get(position - 1));
                        Log.i("BankcardActivity","............="+items.get(position-1).getBank_account());
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        showUnbindDialog(position - 1);
                    }
                }
            });
        }
    }

    private void showUnbindDialog(final int position) {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"解绑"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unbindBankcard(position);
                    }
                }).create().show();
    }

    private void unbindBankcard(final int position) {
        Api.unbindBankcard(sp.getString("uid"), items.get(position).getBank_account(), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    items.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(BankcardActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    private void getBankcardList() {
        Api.getBankcardList(sp.getString("uid"), sp.getString("phone"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        List<BankcardBean> list = JSON.parseArray(apiResponse.getData(), BankcardBean.class);
                        if (list != null && list.size() > 0) {
                            items.clear();
                            items.addAll(list);
                            items.add(null);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(BankcardActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            BankcardBean bean = (BankcardBean) data.getSerializableExtra("bean");
            if (bean != null) {
                items.add(bean);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
