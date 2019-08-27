package com.qcwl.debo.ui.my.partner;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;

import org.json.JSONObject;

public class PartnerActivity extends BaseActivity {

    private FragmentTransaction ft = null;
    private UnOpenedFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);
        FragmentManager fm = this.getFragmentManager();
        ft = fm.beginTransaction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        verify();
        StatService.onResume(this);
    }


    private void setVerifyState(int verify_state,String coins_limit) {
        fragment = new UnOpenedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("verify_state", verify_state);
        bundle.putString("coins_limit", coins_limit);
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动开通合伙人页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束开通合伙人页面");
    }

    private void verify() {
        Api.verifyIdentity(sp.getString("uid"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                try {
                    if (apiResponse.getCode() == 0) {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        int verify_state = object.optInt("verify_state");
                        String coins_limit=object.optString("purchase_limit");
                        //1、认证中；2、认证通过；3、认证失败
                        switch (verify_state) {
                            case 1:
                                if (fragment == null) {
                                    setVerifyState(verify_state,coins_limit);
                                } else {
                                    fragment.btnOpen.setText("开通审核中");
                                    fragment.btnOpen.setBackgroundResource(R.drawable.btn_partner_gray);
                                    fragment.btnOpen.setClickable(false);
                                }
                                break;
                            case 2:
                                ft.replace(R.id.fragment, new OpenedFragment());
                                break;
                            case 3:
                                setVerifyState(verify_state,coins_limit);
                                break;
                            default:
                                break;
                        }
                        ft.commit();
                    } else {
                        Toast.makeText(PartnerActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
