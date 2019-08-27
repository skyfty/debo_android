package com.qcwl.debo.ui.contact;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.my.MyAlbumActivity;
import com.qcwl.debo.utils.AppUtils;
import com.qcwl.debo.utils.DpUtils;
import com.qcwl.debo.utils.NetWorkUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.widget.InviteMessage;
import com.qcwl.debo.widget.InviteMessgeDao;

import java.util.List;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by Administrator on 2017/7/21.
 */

public class NewFriendMsgContentActivity extends BaseActivity implements View.OnClickListener, ContactListPresenterInf {
    private RoundedImageView image;
    private TextView nickname, number, area;
    private LinearLayout pic, image_ll;
    private Button send, add, refuse;
    private ContactListPresenter contactListPresenter;
    private ContactsBean cb;
    private String f_uid = "";
    private InviteMessage msg;
    private InviteMessgeDao messgeDao;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.new_msg_content);
        initView();
        monitor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTitleBar();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("信息详情").setAlpha(1).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        messgeDao = new InviteMessgeDao(this);
        msg = (InviteMessage) getIntent().getSerializableExtra("msg");
        contactListPresenter = new ContactListPresenter(this);
        image = (RoundedImageView) findViewById(R.id.image);
        nickname = (TextView) findViewById(R.id.nickname);
        number = (TextView) findViewById(R.id.number);
        area = (TextView) findViewById(R.id.area);
        image_ll = (LinearLayout) findViewById(R.id.image_ll);
        pic = (LinearLayout) findViewById(R.id.pic);
        send = (Button) findViewById(R.id.send);
        add = (Button) findViewById(R.id.add);
        refuse = (Button) findViewById(R.id.refuse);

        if (getIntent() == null) {
            return;
        }
        if (msg.getStatus() != InviteMessage.InviteMesageStatus.AGREED && msg.getStatus() != InviteMessage.InviteMesageStatus.REFUSED) {
            add.setVisibility(View.VISIBLE);
            refuse.setVisibility(View.VISIBLE);
        }
        contactListPresenter.getUserInformation(this, getIntent().getStringExtra("my_mobile"), getIntent().getStringExtra("mobile"));
    }

    private void monitor() {
        pic.setOnClickListener(this);
        send.setOnClickListener(this);
        add.setOnClickListener(this);
        refuse.setOnClickListener(this);
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
                    if (sp.getString("phone").equals(cb.getMobile())) {
                        ToastUtils.showShort(this, "不能和自己聊天");
                        return;
                    }
                    if (ChatActivity.activityInstance != null)
                        ChatActivity.activityInstance.finish();
                    startActivity(new Intent(NewFriendMsgContentActivity.this, ChatActivity.class).putExtra("userId", cb.getMobile()).putExtra("nickname", cb.getUser_nickname()).putExtra("type", "1"));
                    finish();
                }
                break;
            case R.id.add:
                acceptInvitation(add, refuse, msg);
                if (NetWorkUtils.isConnected(this)) {
                    Api.addFriends(sp.getString("uid"), getIntent().getStringExtra("mobile"), new ApiResponseHandler(context) {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {
                            ToastUtils.showShort(NewFriendMsgContentActivity.this, apiResponse.getMessage());
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            super.onFailure(errMessage);
                        }
                    });

                } else {
                    ToastUtils.showShort(context, "网络异常");
                }
                break;
            case R.id.refuse:
                refuseInvitation(add, refuse, msg);
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            cb = (ContactsBean) o;
            f_uid = cb.getId();
            if (TextUtils.isEmpty(cb.getAvatar())) {
                if (Util.isOnMainThread()) {
                    // Glide.with(this).load(R.mipmap.head).into(image);
                    ImgUtil.setGlideHead(this, R.mipmap.head, image);
                }
            } else {
                if (Util.isOnMainThread()) {
                    ImgUtil.setGlideHead(this, cb.getAvatar(), image);
                    // Glide.with(this).load(cb.getAvatar()).into(image);
                }
            }
            nickname.setText("昵称: " + cb.getUser_nickname());
            number.setText("嘚啵号: " + cb.getMobile());
            area.setText(cb.getProvince() + cb.getCity() + cb.getArea());
            for (String url : cb.getMoments_images()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DpUtils.dp2px(this, 60), DpUtils.dp2px(this, 60));
                params.setMargins(0, 0, DpUtils.dp2px(this, 10), 0);
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImgUtil.setGlideHead(this, url, image);
                //Glide.with(this).load(url).into(imageView);
                image_ll.addView(imageView);
            }
        } else {
            ToastUtils.showShort(this, message);
        }

    }


    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }

    /**
     * accept invitation
     */
    private void acceptInvitation(final Button buttonAgree,
                                  final Button buttonRefuse, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(this);
        String str1 = getResources().getString(R.string.Are_agree_with);
        final String str2 = getResources().getString(
                R.string.Has_agreed_to);
        final String str3 = getResources().getString(
                R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // call api
                try {
                    if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {// accept
                        // be
                        // friends
                        EMClient.getInstance().contactManager()
                                .acceptInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) { // accept
                        // application
                        // to
                        // join
                        // group
                        EMClient.getInstance()
                                .groupManager()
                                .acceptApplication(msg.getFrom(),
                                        msg.getGroupId());
                    } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                        EMClient.getInstance()
                                .groupManager()
                                .acceptInvitation(msg.getGroupId(),
                                        msg.getGroupInviter());
                    }
                    msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                            .getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonAgree.setVisibility(View.GONE);
                            buttonRefuse.setVisibility(View.GONE);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }

    /**
     * decline invitation
     */
    private void refuseInvitation(final Button buttonAgree,
                                  final Button buttonRefuse, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(this);
        String str1 = getResources()
                .getString(R.string.Are_refuse_with);
        final String str2 = getResources().getString(
                R.string.Has_refused_to);
        final String str3 = getResources().getString(
                R.string.Refuse_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // call api
                try {
                    if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {// decline
                        // the
                        // invitation
                        EMClient.getInstance().contactManager()
                                .declineInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) { // decline
                        // application
                        // to
                        // join
                        // group
                        EMClient.getInstance()
                                .groupManager()
                                .declineApplication(msg.getFrom(),
                                        msg.getGroupId(), "");
                    } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                        EMClient.getInstance()
                                .groupManager()
                                .declineInvitation(msg.getGroupId(),
                                        msg.getGroupInviter(), "");
                    }
                    msg.setStatus(InviteMessage.InviteMesageStatus.REFUSED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                            .getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonRefuse.setVisibility(View.GONE);
                            buttonAgree.setVisibility(View.GONE);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

}
