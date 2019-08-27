package com.qcwl.debo.ui.contact.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.near.NearActivity;
import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;
import com.qcwl.debo.ui.found.near.contact.NearContact;
import com.qcwl.debo.ui.found.near.presenter.NearPresenter;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class NearListActivity extends BaseActivity {

    private ListView listView;
    private List<NearBean> items = null;
    private NearAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_list);
        initTitleBar();
        listView = (ListView) findViewById(R.id.listView);
        items = new ArrayList<>();
        adapter = new NearAdapter(this, items);
        listView.setAdapter(adapter);
        requestLocationPermissions();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(NearListActivity.this, ContactsContentActivity.class)
                        .putExtra("my_mobile", SPUtil.getInstance(NearListActivity.this).getString("phone"))
                        .putExtra("mobile", items.get(position).getMobile())
                        .putExtra("type", "1"));
            }
        });
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("邻近的人").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void locationSuccessResult(BDLocation location) {
        super.locationSuccessResult(location);
        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        locationClient.stop();
        getNearList(this, sp.getString("uid"), latitude, longitude, 0);
    }

    public void getNearList(final Context context, String uid, double lat, double lng, int distance) {
        Api.getNearList(uid, "" + lat, "" + lng, distance, new ApiResponseHandler(context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<NearBean> list = JSON.parseArray(apiResponse.getData(), NearBean.class);
                    if (list != null) {
                        items.addAll(list);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    ToastUtils.showShort(context, "" + apiResponse.getMessage());
                }
            }
        });
    }


}
