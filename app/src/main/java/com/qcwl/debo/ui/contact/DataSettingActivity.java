package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.ui.circle.MyFrendActivity;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DataSettingActivity extends BaseActivity{

    @Bind(R.id.remarks)
    LinearLayout remarks;
    @Bind(R.id.layout_share)
    LinearLayout layout_share;
    @Bind(R.id.message_ring)
    EaseSwitchButton message_ring;
    @Bind(R.id.message_vibration)
    EaseSwitchButton message_vibration;
    @Bind(R.id.message_ring2)
    EaseSwitchButton message_ring2;
    @Bind(R.id.complaints)
    LinearLayout complaints;
    @Bind(R.id.bt_delete)
    Button bt_delete;
    private String remark, mobile,id;
    private String type;
    private String type1, blacklist, circle_state1, circle_state2,sex,user_nickname,area,avatar;
    private String api_type;
    private String user_state = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_data_setting);
        ButterKnife.bind(this);
        initTitleBar();
        initView();
        setBlacklist();
        Log.i("DataSettingActivity",".....blacklist="+blacklist+"　　　　"+circle_state2);
    }

    private void initView() {
        remark = getIntent().getStringExtra("remark");
        mobile = getIntent().getStringExtra("mobile");//好友手机号
        id = getIntent().getStringExtra("id");
        blacklist = getIntent().getStringExtra("blacklist");
        circle_state1 = getIntent().getStringExtra("circle_state1");
        circle_state2 = getIntent().getStringExtra("circle_state2");

        sex = getIntent().getStringExtra("sex");
        user_nickname = getIntent().getStringExtra("user_nickname");
        area = getIntent().getStringExtra("area");
        avatar = getIntent().getStringExtra("avatar");

        if (blacklist == null){
            message_ring2.closeSwitch();
            message_ring.closeSwitch();
            message_vibration.closeSwitch();
        }else {

            if (blacklist.equals("1") == true) {
                message_ring2.openSwitch();
            } else {
                message_ring2.closeSwitch();
            }
            if (circle_state1.equals("1") == true) {
                message_ring.closeSwitch();
            } else {
                message_ring.openSwitch();
            }
            if (circle_state2.equals("1") == true) {
                message_vibration.closeSwitch();
            } else {
                message_vibration.openSwitch();
            }
        }
    }
    @OnClick({R.id.remarks, R.id.layout_share, R.id.message_ring, R.id.message_vibration, R.id.message_ring2, R.id.complaints, R.id.bt_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.remarks://修改备注
                Intent intent = new Intent(this, ModifyRemarkActivity.class);
                intent.putExtra("remark", remark);
                intent.putExtra("mobile", mobile);
                intent.putExtra("type", "1");
                intent.putExtra("id", id);
                startActivityForResult(intent, 1);
                break;
            case R.id.layout_share://推荐给朋友
                /*Intent card = new Intent(this, ContactCardActivity.class);
                startActivity(card);*/

                Log.i("DataSettingActivity","...........share="+user_nickname+"　　　　"+avatar+"    "+mobile+"    "+sex+"    "+area+"    "+id);
                Intent card = new Intent(this,  MyFrendActivity.class);
                card.putExtra("type","sharecard");
                card.putExtra("headStr", avatar);
                card.putExtra("nameStr", user_nickname);
                card.putExtra("phone", mobile);
                card.putExtra("sex", sex);
                card.putExtra("uidStr", id);
                card.putExtra("user_area", area);
                card.putExtra("nickname", user_nickname);
                card.putExtra("userId", mobile);
                startActivity(card);

                break;
            case R.id.message_ring://不让他看我的朋友圈
                if (message_ring.isSwitchOpen() == true) {
                    Log.i("DataSettingActivity","....if");
                    circle_state1 = "1";
                } else {
                    Log.i("DataSettingActivity","....else");
                    circle_state1 = "2";
                }
                setMomentsAuthority((EaseSwitchButton)view,"circle_state1",circle_state1);

                //getButotnType((EaseSwitchButton) view);
                break;
            case R.id.message_vibration://不看他的朋友圈
                if (message_vibration.isSwitchOpen() == true) {
                    circle_state2 = "1";
                } else {
                    circle_state2 = "2";
                }
                setMomentsAuthority((EaseSwitchButton)view,"circle_state2",circle_state2);
                //getButotnType((EaseSwitchButton) view);
                break;
            case R.id.message_ring2://加入黑名单
                if (message_ring2.isSwitchOpen() == true) {
                    blacklist = "0";
                } else {
                    blacklist = "1";
                }
                setMomentsAuthority((EaseSwitchButton)view,"blacklist",blacklist);
                break;
            case R.id.complaints://投诉
                ToastUtils.showShort(this, "投诉成功");
                break;
            case R.id.bt_delete:
                //contactListPresenter.delContact(this, sp.getString("uid"), c, adapter.getList());
                Api.delContact(sp.getString("uid"), mobile, new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode()==0) {
                            Log.i("DataSettingActivity","...................删除="+apiResponse.getMessage());
                            String message = apiResponse.getMessage();
                            ToastUtils.showCustom(DataSettingActivity.this,message,2);
                            DataSettingActivity.this.finish();
                            setResult(RESULT_OK);
                        }
                    }
                });
                break;
        }
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("资料设置").setAlpha(1).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setMomentsAuthority(EaseSwitchButton view,String circle_state1,String circle_state2) {
        Log.i("DataSettingActivity","....setMomentsAuthority="+circle_state1+"     "+circle_state2+"     "+sp.getString("uid")+"      "+id+"     "+user_state);
        Api.setMomentsAuthority(sp.getString("uid"), id, circle_state1, circle_state2,user_state,
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            if (view.isSwitchOpen()) {
                                view.closeSwitch();
                            } else {
                                view.openSwitch();
                            }
                        } else {
                            Log.i("DataSettingActivity","....else="+apiResponse.getMessage());
                            Toast.makeText(DataSettingActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        super.onFailure(errMessage);
                        Log.i("DataSettingActivity",".....onFailure="+errMessage);
                    }
                });
    }

    private void setBlacklist() {
        Log.i("DataSettingActivity","....setBlacklist="+blacklist+"    "+mobile);
        Api.getUserState(sp.getString("uid"), mobile,
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            try {
                                JSONArray jsonArray = new JSONArray(apiResponse.getData());
                                if(jsonArray.length()>0){
                                    user_state = jsonArray.getJSONObject(0).getString("user_state");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                user_state = "";
                            }

                        } else {
                            Toast.makeText(DataSettingActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
