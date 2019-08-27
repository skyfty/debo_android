package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.RenMaiBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.ui.found.bump.RedPacketDetailActivity;
import com.qcwl.debo.ui.my.MessageRemindActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.TipDialog;

import java.util.List;

/**
 * Created by qcwl02 on 2017/9/13.
 */

public class SingleChatSettingActivity extends BaseActivity implements View.OnClickListener,ContactListPresenterInf{

    private LinearLayout message_remind, clear_all_history, complaints;
    private Button termination;
    private ContactListPresenter contactListPresenter;
    private TipDialog tipDialog;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.single_chat_setting);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("聊天设置")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setImageRightRes(R.drawable.single_set).setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleChatSettingActivity.this,ContactsContentActivity.class).putExtra("mobile",getIntent().getStringExtra("mobile")).putExtra("type","1").putExtra("my_mobile", SPUtil.getInstance(SingleChatSettingActivity.this).getString("phone")).putExtra("module",getIntent().getStringExtra("module")));
            }
        });
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        message_remind = (LinearLayout) findViewById(R.id.message_remind);
        clear_all_history = (LinearLayout) findViewById(R.id.clear_all_history);
        complaints = (LinearLayout) findViewById(R.id.complaints);
        termination = (Button) findViewById(R.id.termination);

        contactListPresenter.isBuyRenMai(this,sp.getString("phone"),getIntent().getStringExtra("mobile"));
    }

    private void monitor(){
        message_remind.setOnClickListener(this);
        clear_all_history.setOnClickListener(this);
        complaints.setOnClickListener(this);
        termination.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.message_remind:
                startActivity(new Intent(this, MessageRemindActivity.class));
                break;
            case R.id.clear_all_history:
                tipDialog = new TipDialog(this);
                tipDialog.show();
                tipDialog.setTip("清除提示");
                tipDialog.setContent("是否清除和此好友聊天记录");
                tipDialog.setButtonText("取消","清除");
                tipDialog.setClicklistener(new TipDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        tipDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction("com.debo.is_show_name");
                        intent.putExtra("flag", "clear_history");
                        sendBroadcast(intent);
                    }
                    @Override
                    public void doCancel() {
                        tipDialog.dismiss();
                    }
                });
                break;
            case R.id.complaints:
                ToastUtils.showShort(this,"投诉成功");
                break;
            case R.id.termination:
                contactListPresenter.terminationRenMai(this,sp.getString("phone"),getIntent().getStringExtra("mobile"));
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if(code==0){
            if(o instanceof RenMaiBean){
                RenMaiBean renMaiBean = (RenMaiBean)o;
                if("1".equals(renMaiBean.getIs_my_pur())){
                    termination.setVisibility(View.VISIBLE);
                }
            }else {
                finish();
                ToastUtils.showShort(this,message);
            }
        }else {
            ToastUtils.showShort(this,message);
        }
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }
}
