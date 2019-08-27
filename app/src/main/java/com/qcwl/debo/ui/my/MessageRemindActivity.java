package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.view.View;

import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.widget.DemoHelper;
import com.qcwl.debo.widget.DemoModel;


/**
 * Created by Administrator on 2017/1/11.
 */

public class MessageRemindActivity extends BaseActivity implements View.OnClickListener {
    private EaseSwitchButton message_ring, message_vibration;
    private DemoModel settingsModel;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.message_remind);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("消息提醒").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        message_ring = (EaseSwitchButton) findViewById(R.id.message_ring);
        message_vibration = (EaseSwitchButton) findViewById(R.id.message_vibration);
        settingsModel = DemoHelper.getInstance().getModel();

        //声音震动的状态
        if (settingsModel.getSettingMsgSound()) {
            message_ring.openSwitch();
        } else {
            message_ring.closeSwitch();
        }

        if (settingsModel.getSettingMsgVibrate()) {
            message_vibration.openSwitch();
        } else {
            message_vibration.closeSwitch();
        }
    }

    private void monitor() {
        message_ring.setOnClickListener(this);
        message_vibration.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_ring:
                if (message_ring.isSwitchOpen()) {
                    message_ring.closeSwitch();
                    settingsModel.setSettingMsgSound(false);
                    ToastUtils.showShort(this, "已关闭信息响铃");
                } else {
                    message_ring.openSwitch();
                    settingsModel.setSettingMsgSound(true);
                    ToastUtils.showShort(this, "已打开信息响铃");
                }
                break;
            case R.id.message_vibration:
                if (message_vibration.isSwitchOpen()) {
                    message_vibration.closeSwitch();
                    settingsModel.setSettingMsgVibrate(false);
                    ToastUtils.showShort(this, "已关闭信息震动");
                } else {
                    message_vibration.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                    ToastUtils.showShort(this, "已打开信息震动");
                }
                break;
        }
    }
}
