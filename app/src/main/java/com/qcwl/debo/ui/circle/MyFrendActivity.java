package com.qcwl.debo.ui.circle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.ui.found.jinshanyun.ReleasePassageActivity;
import com.qcwl.debo.utils.FileUtil;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.widget.sortlistview.SideBar;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2018/9/11.
 */

public class MyFrendActivity extends BaseActivity implements ContactListPresenterInf {
    private View view, headerview;
    private ListView sortListView;
    private AutoSwipRefreshLayout refresh_layout;
    private ContactListPresenter contactListPresenter;
    private SortGroupMemberAdapter adapter;
    private List<ContactsBean> SourceDateList = new ArrayList<ContactsBean>();
    private LinearLayout group, friends, chatRoom;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private String name;
    private SPUtil sp;
    private String[] mStringItems = {"删除", "查看详情"};//"设置备注",
    private SideBar sideBar;
    private TextView dialog, tip;
    private TextView title;
    private int i = 0;
    private ContactsBean cb;
    private ProgressDialog progressDialog;
    private String imgPath;
    private ContactsBean c;
    private final int RESULT_CODE = 100;
    private String filePath;
    private String type, nickname, userId,nameStr;
    private String sex, user_nickname, area, avatar, mobile,uidStr;
    private String phone;
    private String headStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_frend);
        imgPath = getIntent().getStringExtra("imgPath");
        type = getIntent().getStringExtra("type");
        headStr = getIntent().getStringExtra("headStr");
        nameStr = getIntent().getStringExtra("nameStr");
        nickname = getIntent().getStringExtra("nickname");
        mobile = getIntent().getStringExtra("mobile");
        phone = getIntent().getStringExtra("phone");
        uidStr = getIntent().getStringExtra("uidStr");
        userId = getIntent().getStringExtra("userId");
        sex = getIntent().getStringExtra("sex");
        user_nickname = getIntent().getStringExtra("user_nickname");
        area = getIntent().getStringExtra("area");
        avatar = getIntent().getStringExtra("avatar");
        initView();
        monitor();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("转发中……");
    }

    private void getDatas() {
        contactListPresenter.getContactList(this, sp.getString("uid"), SourceDateList);
    }

    private void initView() {
        contactListPresenter = new ContactListPresenter(this);
        sp = SPUtil.getInstance(this);
        sortListView = (ListView) findViewById(R.id.listView);
        adapter = new SortGroupMemberAdapter(this, SourceDateList,"");
        sortListView.setAdapter(adapter);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        title = (TextView) findViewById(R.id.title);
        title.setText("好友");
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });
    }

    private void monitor() {
/*
        friends.setOnClickListener(this);
        group.setOnClickListener(this);
        chatRoom.setOnClickListener(this);*/

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type != null && type.equals("sharecard") == true) {
                    //资料设置界面进行
                    ListView listView = (ListView)parent;
                    ContactsBean contactsBean = (ContactsBean) listView.getItemAtPosition(position);
                    String mobile = contactsBean.getMobile();
                    String nickname  =  contactsBean.getUser_nickname();
                    Log.i("MyFrendActivity", "......setOnItemClickListener=" + mobile+"    "+nickname);
                    Intent card = new Intent(MyFrendActivity.this, ChatActivity.class);
                    card.putExtra("type", "sharecard");
                    card.putExtra("headStr", headStr);//
                    card.putExtra("nameStr", nameStr);//
                    card.putExtra("phone", phone);//
                    card.putExtra("sex", sex);
                    card.putExtra("uidStr", uidStr);//
                    card.putExtra("user_area", area);
                    card.putExtra("nickname", nickname);
                    card.putExtra("userId", mobile);
                    startActivity(card);
                    finish();
                } else {
                    c = adapter.getList().get(position);
                    Log.i("MyFrendActivity", "......CCCCCC=" + c.getId());
                   /* Intent intent = new Intent(MyFrendActivity.this, ContactsContentActivity.class);
                    intent.putExtra("mobile", c.getMobile());
                    intent.putExtra("my_mobile", sp.getString("phone"));
                    intent.putExtra("f_uid", c.getId());
                    intent.putExtra("type", "1");
                    startActivity(intent);*/
                    progressDialog.show();
                    downloadImage(imgPath);

                }


            }
        });

    }

    private void downloadImage(String imageUrl) {
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/"));

        OkHttpUtils.get().url(imageUrl)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new FileCallBack(FileUtil.getPictureDir(this), filename) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtils.showShort(MyFrendActivity.this, "转发失败");
                            }
                        });
                    }

                    @Override
                    public void onResponse(final File response, int id) {
                        if (response == null) {
                            return;
                        }
                        filePath = FileUtil.getPictureDir(MyFrendActivity.this) + filename;
                        Log.i("MyFrendActivity", "......filePath=" + filePath);
                        remindPhoto(response);
                        contactListPresenter.getUserInformation(MyFrendActivity.this, sp.getString("phone"), c.getMobile());
                    }
                });
    }

    private void remindPhoto(File file) {
        if (file != null) {
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "title", "description");
                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(sp.getString("uid")))
            getDatas();
    }

    @Override
    public void getResult(int code, String message, Object o) {
        Log.i("MyFrendActivity", "......getResult00000=" + code);
        if (i == 0) {
            if (code == 0) {
                Log.i("MyFrendActivity", "......00000");
                adapter.notifyDataSetChanged();
            } else {
                SourceDateList.clear();
                adapter.notifyDataSetChanged();
            }
            i += 1;
        } else {
            cb = (ContactsBean) o;
            EMMessage emMessage = EMMessage.createImageSendMessage(filePath, true, cb.getMobile());
            emMessage.setAttribute("type", "1");
            EMClient.getInstance().chatManager().sendMessage(emMessage);
            emMessage.setMessageStatusCallback(new EMCallBack() {
                @Override
                public void onSuccess() {
                    /*Log.i("MyFrendActivity", "......onSuccess");
                    boolean b = FileUtil.delete(filePath);
                    // 上传完成后删除下载的图片
                    if (b == true) {
                        MyFrendActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
                    }*/
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    MyFrendActivity.this.finish();
                }

                @Override
                public void onError(int i, String s) {
                    Log.i("MyFrendActivity", "......onError=" + s);
                    progressDialog.dismiss();
                    Toast.makeText(MyFrendActivity.this, "转发失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }

    public void filterData(String filter) {
        if (filter != null && contactListPresenter != null)
            contactListPresenter.filterContactList(filter);
    }
}
