package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.NewFriendsPresenter;
import com.qcwl.debo.presenterInf.NewFriendsPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.bump.RedPacketDetailActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CommonDialog;
import com.qcwl.debo.widget.DemoHelper;

/**
 * Created by Administrator on 2017/7/24.
 */

public class AddFriendsActivity extends BaseActivity implements NewFriendsPresenterInf {
    private EditText query;
    private RelativeLayout relative,relative1;
    private LinearLayout search_linear, tip_linear;
    private RoundedImageView image;
    private TextView nickname, search_content, add, finish;
    private ImageButton clearSearch;
    private NewFriendsPresenter friendsPresenter;
    private SPUtil sp;
    private CommonDialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.add_friends);
        initTitleBar();
        initView();
        monitor();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("添加好友").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        sp = SPUtil.getInstance(this);
        query = (EditText) findViewById(R.id.query);
        search_content = (TextView) findViewById(R.id.search_content);
        search_linear = (LinearLayout) findViewById(R.id.search_linear);
        tip_linear = (LinearLayout) findViewById(R.id.tip_linear);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        relative = (RelativeLayout) findViewById(R.id.relative);
        relative1 = (RelativeLayout) findViewById(R.id.relative1);
        image = (RoundedImageView) findViewById(R.id.image);
        nickname = (TextView) findViewById(R.id.nickname);
        add = (TextView) findViewById(R.id.add);
        finish = (TextView) findViewById(R.id.finish);

        if (!TextUtils.isEmpty(getIntent().getStringExtra("content"))) {
            query.setText(getIntent().getStringExtra("content"));
            search_content.setText(getIntent().getStringExtra("content"));
            query.setSelection(getIntent().getStringExtra("content").length());
            clearSearch.setVisibility(View.VISIBLE);
            search_linear.setVisibility(View.GONE);
        }
        friendsPresenter = new NewFriendsPresenter(this);

        search_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AddFriendsAcitivity",".................search_linear");
                friendsPresenter.searchById(AddFriendsActivity.this, sp.getString("uid"), query.getText().toString().trim());
            }
        });

        relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AddFriendsAcitivity",".................relative");
                startActivity(new Intent(AddFriendsActivity.this, ContactsContentActivity.class)
                        .putExtra("my_mobile", SPUtil.getInstance(AddFriendsActivity.this).getString("phone"))
                        .putExtra("mobile", "" + mobile)
                        .putExtra("type", "1"));
            }
        });
    }

    private void monitor() {
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tip_linear.setVisibility(View.GONE);
                relative1.setVisibility(View.GONE);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    search_linear.setVisibility(View.VISIBLE);
                    search_content.setText(s);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                    search_linear.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
    }

    private String mobile = "";

    @Override
    public void getResult(int code, String message, Object o) {
        //nickname.setText("1654651651");
        if (code == 0) {
            Log.i("AddFriendsAcitivity","..............if");
            relative1.setVisibility(View.VISIBLE);
            search_linear.setVisibility(View.GONE);
            final ContactsBean cb = (ContactsBean) o;
            mobile = cb.getMobile();
            if (TextUtils.isEmpty(cb.getAvatar())) {
                if (Util.isOnMainThread()) {
                    Log.i("AddFriendsAcitivity","..............if");
                    //ImgUtil.setGlideHead(this, R.mipmap.head, image);
                    Glide.with(this).load(R.mipmap.head).into(image);
                }

            } else {
                if (Util.isOnMainThread())
                    Log.i("AddFriendsAcitivity","..............else");
                    //ImgUtil.setGlideHead(this,cb.getAvatar(), image);
                    Glide.with(this).load(cb.getAvatar()).into(image);
            }
            if (TextUtils.isEmpty(cb.getUser_nickname())) {
                nickname.setText(cb.getMobile());
            } else {
                nickname.setText(cb.getUser_nickname());
            }
            if ("1".equals(cb.getIs_conn()) || "1".equals(cb.getIs_friend())) {
                add.setVisibility(View.GONE);
                finish.setVisibility(View.VISIBLE);
            } else {
                add.setVisibility(View.VISIBLE);
                finish.setVisibility(View.GONE);
            }

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (cb.getMobile().equals(sp.getString("phone"))) {
                        ToastUtils.showShort(AddFriendsActivity.this, "您不能添加自己为好友");
                        return;
                    }

                    if (DemoHelper.getInstance().getContactList().containsKey(cb.getMobile())) {
                        //let the user know the contact already in your contact list
                        if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(cb.getMobile())) {
                            ToastUtils.showShort(AddFriendsActivity.this, "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可");
                            return;
                        }
                        return;
                    }

                    dialog = new CommonDialog(AddFriendsActivity.this);
                    dialog.show();
                    dialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                        @Override
                        public void doConfirm(final String reason) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().contactManager().addContact(cb.getMobile(), reason);
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
            });
        } else {
            Log.i("AddFriendsAcitivity","..............else2");
            search_linear.setVisibility(View.GONE);
            tip_linear.setVisibility(View.VISIBLE);
            relative.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getIntent().getStringExtra("content"))) {
            friendsPresenter.searchById(AddFriendsActivity.this, sp.getString("uid"), query.getText().toString().trim());
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            ToastUtils.showShort(AddFriendsActivity.this, "添加好友请求发送成功，请等待~");
        }
    };

}
