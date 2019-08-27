package com.qcwl.debo.ui.contact.add;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.MobileUserBean;
import com.qcwl.debo.utils.PinYinUtils;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.StatusBarUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.view.AutoSwipRefreshLayout;
import com.qcwl.debo.widget.SearchEditText;
import com.qcwl.debo.widget.sortlistview.PinyinComparator2;
import com.qcwl.debo.widget.sortlistview.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qcwl.debo.R.id.listView;

public class PhoneContactActivity extends Activity {
    private ListView sortListView;
    private LinearLayout group;
    private LinearLayout friends;
    private LinearLayout chatRoom;
    private TextView tip;
    private AutoSwipRefreshLayout refresh_layout;
    private SideBar sideBar;
    private List<MobileUserBean> list = new ArrayList<MobileUserBean>();
    private List<MobileUserBean> list_search = new ArrayList<MobileUserBean>() {
    };
    private String[] strings;
    private Map<String, String> map = new HashMap<String, String>();
    private ContactAdapter adapter;
    private List<MobileUserBean> list2;
    private PinyinComparator2 pinyinComparator = new PinyinComparator2();
    private String sortString;
    private SearchEditText query;
    private ImageButton clearSearch;
    private RelativeLayout relativelayout;
    private RelativeLayout relative;
    private TextView tv_setting;
    private HashMap<String, String> _map;
    private String uid;
    private SPUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏设置为绿色:#71cca2
        StatusBarUtil.setStatusBarColor(this, R.color.light_green);
        setContentView(R.layout.activity_phone_contact);
        sp = SPUtil.getInstance(this);
        uid = sp.getString("uid");
        initTitleBar();
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            //判断是否已经赋予权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Log.i("PhoneContactActivity", "..........checkSelfPermission");
                relativelayout.setVisibility(View.GONE);
                getContact();
            } else {
                Log.i("PhoneContactActivity", "..........checkSelfPermission0");
                String[] mPermissionList = new String[]{Manifest.permission.READ_CONTACTS};
                ActivityCompat.requestPermissions(this, mPermissionList, 1);
            }
        }else{
            //relative.setVisibility(View.GONE);
            relativelayout.setVisibility(View.GONE);
            getContact();
            Log.i("PhoneContactActivity", "..........checkSelfPermission00");

        }
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("手机联系人").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        relativelayout = (RelativeLayout) findViewById(R.id.relativelayout);
        relative = (RelativeLayout) findViewById(R.id.relative);

        sortListView = (ListView) findViewById(listView);
        group = (LinearLayout) findViewById(R.id.contact_header_group);
        friends = (LinearLayout) findViewById(R.id.contact_header_friends);
        chatRoom = (LinearLayout) findViewById(R.id.contact_header_chat_room);
        tip = (TextView) findViewById(R.id.tip);
        query = (SearchEditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        adapter = new ContactAdapter(this, list,map,uid);
        sortListView.setAdapter(adapter);
        refresh_layout = (AutoSwipRefreshLayout) findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.holo_list_light);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (strings.length>0) {
                    getDatas();
                }
            }
        });
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        TextView dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
                if (s != null)
                    filterContactList(s.toString());


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
                //hideSoftKeyboard();
            }
        });
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("PhoneContactActivity", "..........tv_setting");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);

            }
        });
    }


    private void getDatas() {
        Log.i("PhoneContactActivity", ".....jsonArray=" + "     =" + sp.getString("uid") + "        " + strings);
        Api.getMobileUser(uid, strings, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                refresh_layout.setRefreshing(false);
                list2 = com.alibaba.fastjson.JSONArray.parseArray(apiResponse.getData(), MobileUserBean.class);
                list2 = filledData(list2);
                if (list2 != null && list2.size() != 0) {
                    list.clear();
                }
                Collections.sort(list2, pinyinComparator);
                list.addAll(list2);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                refresh_layout.setRefreshing(false);
            }
        });
    }

    private void getContact() {
        _map = new HashMap<String,String>();
        map.clear();
        int i = 0;
        ContentResolver contentResolver = getContentResolver();
        Log.i("PhoneContactActivity", ".....contentResolver="+contentResolver);
        // 获得所有的联系人
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null,
                null, null);
        Log.i("PhoneContactActivity", ".....cursor=" + cursor);
        int count = cursor.getCount();
        Log.i("PhoneContactActivity", ".....count=" + count);
        strings = new String[count];
        // 循环遍历
        if (count==0){
            cursor.close();
            return;
        }
        if (cursor.moveToFirst()) {
            Log.i("PhoneContactActivity", ".....moveToFirst");
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            Log.i("PhoneContactActivity", ".....idColumn="+idColumn);
            do {
                // 获得联系人的ID号
                String contactId = cursor.getString(idColumn);
                // 获得联系人姓名
                String disPlayName = cursor.getString(displayNameColumn);
                // 查看该联系人有多少个电话号码。如果没有这返回值为0
                int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                //在联系人数量不为空的情况下执行
                if (phoneCount > 0) {
                    // 获得联系人的电话号码列表
                    Cursor phonesCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = " + contactId, null,
                            null);
                    //如果联系人有多个手机号的话 只取第一个
                    if (phonesCursor.moveToFirst()) {
                        do {
                            // 遍历所有的电话号码
                            String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNumber = phoneNumber.replaceAll(" ", "");
                            String rasPhont = RAS.getPublicKeyStrRAS(phoneNumber.getBytes());
                            strings[i] = rasPhont;
                            _map.put(phoneNumber, disPlayName);
                            i += 1;
                            break;
                            //Toast.makeText(this, "联系人姓名：" + disPlayName + "\n联系人电话：" + phoneNumber, Toast.LENGTH_LONG).show();
                        } while (phonesCursor.moveToNext());
                    }
                }

            } while (cursor.moveToNext());

            map.putAll(_map);
            if (strings.length>0) {
                getDatas();
            }
        }
        cursor.close();
    }

    private void filterContactList(String filterStr) {
        String name = "";
        String mobile = "";
        List<MobileUserBean> filterDateList = new ArrayList<MobileUserBean>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list2;
        } else {
            filterDateList.clear();
            for (MobileUserBean sortModel : list2) {
                if (TextUtils.isEmpty(sortModel.getUser_nickname())) {
                    name = sortModel.getMobile();
                } else {
                    name = sortModel.getUser_nickname();
                }
                mobile = sortModel.getMobile();
                if (name.indexOf(filterStr.toString()) != -1 || PinYinUtils.getPinYinFirstLetter(name).startsWith(filterStr.toString())
                        || mobile.indexOf(filterStr.toString()) != -1 || PinYinUtils.getPinYinFirstLetter(mobile).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        adapter.updateListView(filterDateList);
    }

    /**
     * 为ListView填充数据
     *
     * @param list
     * @return
     */
    private List<MobileUserBean> filledData(List<MobileUserBean> list) {
        List<MobileUserBean> mSortList = new ArrayList<MobileUserBean>();

        for (int i = 0; i < list.size(); i++) {
            MobileUserBean sortModel = new MobileUserBean();
            if (TextUtils.isEmpty(list.get(i).getUser_nickname())) {
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getMobile()).toUpperCase();
            } else {
                sortModel.setUser_nickname(list.get(i).getUser_nickname());
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getUser_nickname()).toUpperCase();
            }
            sortModel.setMobile(list.get(i).getMobile());
            sortModel.setId(list.get(i).getId());
            sortModel.setAvatar(list.get(i).getAvatar());
            sortModel.setIs_friends(list.get(i).getIs_friends());
            sortModel.setUser_nickname(list.get(i).getUser_nickname());
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("PhoneContactActivity", "..........requestCode=" + requestCode + "    " + grantResults[0]);

        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != 0) {
                    Log.i("PhoneContactActivity", "..........requestCode11");
                    return;
                }
            }
            relativelayout.setVisibility(View.GONE);
            getContact();
            Log.i("PhoneContactActivity", "..........requestCode22");

        }
    }

}
