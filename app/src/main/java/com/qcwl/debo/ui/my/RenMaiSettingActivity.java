package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.model.SettingBean;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

/**
 * Created by Administrator on 2017/8/21.
 */

public class RenMaiSettingActivity extends BaseActivity implements View.OnClickListener, MyInfoPresenterInf {

    private EaseSwitchButton open_contacts, noble_contacts, near_person;
    private LinearLayout linear, open_linear, noble_linear;
    private EditText price, day;
    private MyInfoPresenter myInfoPresenter;
    private String type;
    private String isOpen;
    private SettingBean settingBean;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.renmai_setting);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("人脉设置")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTextRight("保存")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("2".equals(type)) {
                            myInfoPresenter.editRenMaiSetting(RenMaiSettingActivity.this, sp.getString("uid"), type, isOpen, price.getText().toString().trim(), day.getText().toString().trim());
                        }else if ("1".equals(type)){
                            myInfoPresenter.editRenMaiSetting(RenMaiSettingActivity.this, sp.getString("uid"), type, isOpen, "", "");
                    }
                        else finish();
                    }
                });
    }

    private void initView() {
        myInfoPresenter = new MyInfoPresenter(this);
        open_contacts = (EaseSwitchButton) findViewById(R.id.open_contacts);
        open_linear = (LinearLayout) findViewById(R.id.open_linear);
        noble_contacts = (EaseSwitchButton) findViewById(R.id.noble_contacts);
        noble_linear = (LinearLayout) findViewById(R.id.noble_linear);
        near_person = (EaseSwitchButton) findViewById(R.id.near_person);
        linear = (LinearLayout) findViewById(R.id.linear);
        price = (EditText) findViewById(R.id.price);
        day = (EditText) findViewById(R.id.day);
        myInfoPresenter.getRenMaiSetting(this, sp.getString("uid"));
    }

    private void monitor() {
        open_contacts.setOnClickListener(this);
        noble_contacts.setOnClickListener(this);
        near_person.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动人脉设置页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束人脉设置页面");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_contacts:
                type = "1";
                if (open_contacts.isSwitchOpen()) {
                    open_contacts.closeSwitch();
                    isOpen = "2";
                } else {
                    isOpen = "1";
                    open_contacts.openSwitch();
                    noble_contacts.closeSwitch();
                    linear.setVisibility(View.GONE);
                }
                break;
            case R.id.noble_contacts:
                type = "2";
                if (noble_contacts.isSwitchOpen()) {
                    noble_contacts.closeSwitch();
                    isOpen = "2";
                    linear.setVisibility(View.GONE);
                } else {
                    isOpen = "1";
                    noble_contacts.openSwitch();
                    linear.setVisibility(View.VISIBLE);
                    open_contacts.closeSwitch();
                }
                break;
            case R.id.near_person:
                if (near_person.isSwitchOpen()) {
                    near_person.closeSwitch();
                } else {
                    near_person.openSwitch();
                }
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            if (o != null) {
                settingBean = (SettingBean) o;
                //是否禁用此功能，1、禁用；0、不禁用
                if ("1".equals(settingBean.getIs_forbid())) {
                 /*   open_linear.setBackgroundResource(R.drawable.rect_gray);
                    noble_linear.setBackgroundResource(R.drawable.rect_gray);*/
                    ToastUtils.showShort(RenMaiSettingActivity.this, "没有此权限");
                    open_contacts.setClickable(false);
                    noble_contacts.setClickable(false);
                } else {
                    price.setText(settingBean.getPur_price());
                    day.setText(settingBean.getDay_num());
                    if ("1".equals(settingBean.getConn_type())) {
                        if ("1".equals(settingBean.getOperator())) {
                            open_contacts.openSwitch();
                            isOpen = "1";
                        } else {
                            open_contacts.closeSwitch();
                            isOpen = "2";
                        }
                        type = "1";
                    } else if ("2".equals(settingBean.getConn_type())) {
                        if ("1".equals(settingBean.getOperator())) {
                            noble_contacts.openSwitch();
                            linear.setVisibility(View.VISIBLE);
                            isOpen = "1";
                        } else {
                            noble_contacts.closeSwitch();
                            isOpen = "2";
                        }
                        type = "2";
                    } else {
                        type = "0";
                        open_contacts.closeSwitch();
                        noble_contacts.closeSwitch();
                    }
                }
            } else {
                finish();
            }
        } else {
            ToastUtils.showShort(this, message);
        }
    }
}
