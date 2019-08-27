package com.qcwl.debo.chat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.qcwl.debo.R;
import com.qcwl.debo.widget.Constant;
import com.qcwl.debo.widget.DemoHelper;
import com.qcwl.debo.widget.sortlistview.CharacterParser;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import rx.Observer;

/**
 * Created by Administrator on 2016/9/23.
 */

public class PickContactNoCheckboxActivity extends BaseActivity {

    protected EaseContactAdapter contactAdapter;
    private List<EaseUser> contactList;
    private ConversationSqlite sqlite;
    private SQLiteDatabase db;
    private Cursor cursor;
    private List<EaseUser> contactLists;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private String sortString;
    private String pinyin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_pick_contact_no_checkbox);
        ListView listView = (ListView) findViewById(R.id.list);
        EaseSidebar sidebar = (EaseSidebar) findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        contactList = new ArrayList<EaseUser>();
        contactLists = new ArrayList<>();
        // get contactlist
        getContactList();
        // set adapter
        contactAdapter = new EaseContactAdapter(this, R.layout.ease_row_contact, filledData(contactLists));
        listView.setAdapter(contactAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

    }

    protected void onListItemClick(int position) {
        setResult(RESULT_OK, new Intent().putExtra("username", contactAdapter.getItem(position)
                .getUsername()));
        finish();
    }

    public void back(View view) {
        finish();
    }

    private void getContactList() {
        contactList.clear();
        Map<String, EaseUser> users = DemoHelper.getInstance().getContactList();
        for (Entry<String, EaseUser> entry : users.entrySet()) {
            if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME) && !entry.getKey().equals(Constant.CHAT_ROOM) && !entry.getKey().equals(Constant.CHAT_ROBOT))
                contactList.add(entry.getValue());
        }

        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) {
                            sqlite = ConversationSqlite.getInstance(PickContactNoCheckboxActivity.this);
                            db = sqlite.getReadableDatabase();
                            contactLists.clear();
                            for(EaseUser eu : contactList){
                                cursor = db.rawQuery("select * from conversation where phone='"
                                        + eu.getUsername() + "'", null);
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        eu.setNick(cursor.getString(2));
                                        eu.setAvatar(cursor.getString(3));
                                    }
                                }
                                contactLists.add(eu);
                                cursor.close();
                            }
                            db.close();
                        } else {
                            Toast.makeText(PickContactNoCheckboxActivity.this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // sort
        Collections.sort(contactLists, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNick().compareTo(rhs.getNick());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }

    /**
     * 为ListView填充数据
     *
     * @param list
     * @return
     */
    private List<EaseUser> filledData(List<EaseUser> list) {
        List<EaseUser> mSortList = new ArrayList<EaseUser>();

        for (int i = 0; i < list.size(); i++) {
            EaseUser sortModel = new EaseUser();
            if (TextUtils.isEmpty(list.get(i).getNick())) {
                sortModel.setUsername(list.get(i).getUsername());
                // 汉字转换成拼音
                pinyin = characterParser.getSelling(list.get(i).getUsername());
            } else {
                sortModel.setNick(list.get(i).getNick());
                // 汉字转换成拼音
                pinyin = characterParser.getSelling(list.get(i).getNick());
                sortModel.setUsername(list.get(i).getUsername());
            }

            sortModel.setAvatar(list.get(i).getAvatar());

            sortString = pinyin.substring(0, 1).toUpperCase();

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

}
