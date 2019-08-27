package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.chat.VideoCallActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.LargeImageActivity;
import com.qcwl.debo.ui.found.chat.TouhChatActivity;
import com.qcwl.debo.ui.my.MyAlbumActivity;
import com.qcwl.debo.ui.my.ShowLocationActivity;
import com.qcwl.debo.utils.AppUtils;
import com.qcwl.debo.utils.DpUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CommonDialog;
import com.qcwl.debo.widget.DemoHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ContactsContentActivity extends BaseActivity implements View.OnClickListener, ContactListPresenterInf {
    private RoundedImageView image;
    private TextView nickname, number, name, remark, area;
    private LinearLayout pic, image_ll, remark_ll, layout_location;
    private Button send, add;
    private ContactListPresenter contactListPresenter;
    private ContactsBean cb;
    private String type;
    private String f_uid = "";
    private CommonDialog dialog;
    private Button sendVideo;
    private String TAG = "ContactsContentActivity";
    private String my_mobile;
    private String mobile;
    private String module;
    private TitleBarBuilder titleBarBuilder;
    private String api_type;//1为好友，2为人脉

//    public LocationClient locationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
//    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.contact_content);
        initView();
        monitor();

//        initLocationClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTitleBar();
        //EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    private void initTitleBar() {
        titleBarBuilder = new TitleBarBuilder(this).setTitle("个人信息").setAlpha(1).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!"3".equals(type)&&!my_mobile.equals(mobile)&&module==null) {
            titleBarBuilder.setAlpha(2).setImageRightRes(R.mipmap.icon_data_setting).setRightListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"................f_uid="+f_uid);
                    startActivityForResult(new Intent(ContactsContentActivity.this, DataSettingActivity.class)
                            .putExtra("id", "" + f_uid).putExtra("mobile", "" + cb.getMobile())
                            .putExtra("remark", cb.getRemark()).putExtra("circle_state1", cb.getCircle_state1())
                            .putExtra("circle_state2", cb.getCircle_state2()).putExtra("blacklist", cb.getBlacklist())
                            .putExtra("sex",cb.getSex()).putExtra("user_nickname",cb.getUser_nickname()).putExtra("area",cb.getArea())
                            .putExtra("blacklist",cb.getBlacklist())
                            .putExtra("avatar",cb.getAvatar()).putExtra("api_type",api_type),100);
                }
            });
        }
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        image = (RoundedImageView) findViewById(R.id.image);
        nickname = (TextView) findViewById(R.id.nickname);
        number = (TextView) findViewById(R.id.number);
        name = (TextView) findViewById(R.id.name);
        remark_ll = (LinearLayout) findViewById(R.id.remark_ll);
        remark = (TextView) findViewById(R.id.remark);
        area = (TextView) findViewById(R.id.area);
        image_ll = (LinearLayout) findViewById(R.id.image_ll);
        pic = (LinearLayout) findViewById(R.id.pic);
        send = (Button) findViewById(R.id.send);
        sendVideo = (Button) findViewById(R.id.send_video);
        add = (Button) findViewById(R.id.add);

        layout_location = (LinearLayout) findViewById(R.id.layout_location);
        if (getIntent() == null) {
            return;
        }
        type = getIntent().getStringExtra("type");
        my_mobile = getIntent().getStringExtra("my_mobile");
        mobile = getIntent().getStringExtra("mobile");
        module = getIntent().getStringExtra("module");
        api_type = getIntent().getStringExtra("api_type");
        Log.i(TAG,"............module="+module);
        if (api_type == null){
            api_type = "";
        }
        if (module!=null){
            sendVideo.setVisibility(View.GONE);
        }
        Log.i("ContactsContentActivity", ".........type" + type+"    "+my_mobile+"     "+mobile);
        if ("1".equals(type)) {
            contactListPresenter.getUserInformation(this, my_mobile, mobile);
        } else if ("2".equals(type)) {
            contactListPresenter.getRenMaiInformation(this, my_mobile, mobile);
        } else if ("3".equals(type)) {
            //获取用户个人信息
            contactListPresenter.getUserInformation1(this, mobile);
        }else{
            contactListPresenter.getUserInformation(this, my_mobile, mobile);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> images = new ArrayList<>();
                images.add("" + cb.getAvatar());
                startActivity(new Intent(ContactsContentActivity.this, LargeImageActivity.class)
                        .putExtra("position", 0)
                        .putExtra("images", (Serializable) images));
            }
        });
    }

    private void monitor() {
        pic.setOnClickListener(this);
        remark_ll.setOnClickListener(this);
        send.setOnClickListener(this);
        sendVideo.setOnClickListener(this);
        add.setOnClickListener(this);
        layout_location.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pic:
                //startActivity(new Intent(this, MyPhotoActivity.class).putExtra("f_uid", f_uid));
                startActivity(new Intent(this, MyAlbumActivity.class).putExtra("f_uid", f_uid));
                break;
            case R.id.send:
                if (cb != null) {
                    if (sp.getString("phone").equals("" + cb.getMobile())) {
                        ToastUtils.showShort(this, "不能和自己聊天");
                        return;
                    }
                    if (ChatActivity.activityInstance != null)
                        ChatActivity.activityInstance.finish();
                    if (TextUtils.isEmpty(cb.getRemark())) {
                        startActivity(new Intent(ContactsContentActivity.this, ChatActivity.class).putExtra("userId", "" + cb.getMobile()).putExtra("nickname", cb.getUser_nickname()).putExtra("type", type));
                    } else {
                        //Remark  好友备注
                        startActivity(new Intent(ContactsContentActivity.this, ChatActivity.class).putExtra("userId", "" + cb.getMobile()).putExtra("nickname", cb.getRemark()).putExtra("type", type));
                    }
                    finish();
                }
                break;
            case R.id.send_video:
                startActivityForResult(new Intent(ContactsContentActivity.this, VideoCallActivity.class).putExtra("username", cb.getMobile())
                        .putExtra("isComingCall", false).putExtra("nickname", cb.getUser_nickname()), 100);
                break;
            case R.id.remark_ll:
                Intent intent = new Intent(this, ModifyRemarkActivity.class);
                if (!TextUtils.isEmpty(cb.getRemark()) || !TextUtils.isEmpty(cb.getMobile())) {
                    intent.putExtra("remark", cb.getRemark());
                    intent.putExtra("mobile", "" + cb.getMobile());
                    intent.putExtra("type", type);
                    intent.putExtra("id", "" + f_uid);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.add:
                if (!TextUtils.isEmpty(cb.getMobile())) {
                    if (cb.getMobile().equals(sp.getString("phone"))) {
                        ToastUtils.showShort(ContactsContentActivity.this, "您不能添加自己为好友");
                        return;
                    }

                    if (DemoHelper.getInstance().getContactList().containsKey("" + cb.getMobile())) {
                        //let the user know the contact already in your contact list
                        if (EMClient.getInstance().contactManager().getBlackListUsernames().contains("" + cb.getMobile())) {
                            ToastUtils.showShort(ContactsContentActivity.this, "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可");
                            return;
                        }
                        return;
                    }
                    dialog = new CommonDialog(ContactsContentActivity.this);
                    dialog.show();
                    dialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                        @Override
                        public void doConfirm(final String reason) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().contactManager().addContact("" + cb.getMobile(), reason);
                                        Message msg = new Message();
                                        msg.obj = cb;
                                        handler.sendMessage(msg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Message msg = new Message();
                                        handler.sendMessage(msg);
                                    }
                                }
                            }.start();
                        }
                    });
                }
                break;
            case R.id.layout_location:
                //接口请求成功添加监听，获取消息成功后取消监听
                count = 10;
                layout_location.setClickable(false);
                mHandler.postDelayed(runnable, 1000);
                Toast.makeText(this, "请求已发送……", Toast.LENGTH_SHORT).show();
                sendLocationRequest();
//                countDown();
                break;
        }
    }

    private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            count--;
            if (count > 0) {
                mHandler.postDelayed(runnable, 1000);
                layout_location.setClickable(false);
            } else {
                layout_location.setClickable(true);
            }
        }
    };

    private int count = 10;

    private void sendLocationRequest() {
        if (!TextUtils.isEmpty(sp.getString("phone"))) {
            Api.sendLocationRequest(sp.getString("phone"), "" + cb.getMobile(),
                    new ApiResponseHandler(this) {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {
                            if (apiResponse.getCode() == 0) {
                            } else {
                                Toast.makeText(ContactsContentActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            Log.i(TAG,".......o="+o);
            cb = (ContactsBean) o;
            f_uid = cb.getId();
            ImgUtil.setGlideHead(this, cb.getAvatar(), image);
            //  Glide.with(this).load("" + cb.getAvatar()).error(R.mipmap.head).into(image);

            if (TextUtils.isEmpty(cb.getRemark())) {
                nickname.setText(cb.getUser_nickname());
            } else {
                nickname.setText(cb.getRemark());
                name.setText("昵称: " + cb.getUser_nickname());
            }
            number.setText("嘚啵号: " + cb.getDebo_code());
            remark.setText(cb.getRemark());
            area.setText(cb.getProvince() + cb.getCity() + cb.getArea());
            for (String url : cb.getMoments_images()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DpUtils.dp2px(this, 60), DpUtils.dp2px(this, 60));
                params.setMargins(0, 0, DpUtils.dp2px(this, 10), 0);
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //ImgUtil.setGlideHead(this, url, image);
                Glide.with(this).load(url).into(imageView);
                image_ll.addView(imageView);
            }
            if ("0".equals(cb.getIs_connection())) {
                if ("0".equals(cb.getIs_friend())) {
                    add.setVisibility(View.VISIBLE);
                    titleBarBuilder.setImageRightGone();
                }
            }

            if (type.equals("3")){
                sendVideo.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
            }
        } else {
            ToastUtils.showShort(this, message);
            ContactsContentActivity.this.finish();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            ToastUtils.showShort(ContactsContentActivity.this, "添加好友请求发送成功，请等待~");
        }
    };

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"...................onActivityResult="+requestCode+"     "+resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String s = data.getStringExtra("remark");
                cb.setRemark(s);
                if (TextUtils.isEmpty(s)) {
                    nickname.setText(cb.getUser_nickname());
                    name.setText("");
                } else {
                    nickname.setText(s);
                    name.setText("昵称: " + cb.getUser_nickname());
                }
                remark.setText(s);
            }
        }else if(requestCode == 100 && resultCode ==RESULT_OK){
            ContactsContentActivity.this.finish();
        }
    }

    @Override
    public void receiveLocationResult(EMMessage message) {
        super.receiveLocationResult(message);
        //lat,lng,mobile、user_nickname、avatar
        Intent intent = new Intent(ContactsContentActivity.this, ShowLocationActivity.class);
        intent.putExtra("lat", message.getStringAttribute("lat", ""));
        intent.putExtra("lng", message.getStringAttribute("lng", ""));
        intent.putExtra("mobile", message.getStringAttribute("mobile", ""));
        intent.putExtra("user_nickname", message.getStringAttribute("user_nickname", ""));
        intent.putExtra("avatar", message.getStringAttribute("avatar", ""));
        startActivity(intent);
    }


}
