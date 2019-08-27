package com.qcwl.debo.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.utils.PinYinUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observer;

/**
 * Created by Administrator on 2016/9/23.
 */

public class GroupPickContactsActivity extends BaseActivity {
    private PickContactAdapter contactAdapter;
    /**
     * members already in the group
     */
    private List<String> existMembers;
    private ConversationSqlite sqlite;
    private SQLiteDatabase db;
    private Cursor cursor;
    private List<EaseUser> contactLists;
    private String sortString;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_group_pick_contacts);
        contactLists = new ArrayList<>();
        initTitleBar();
        existMembers = getIntent().getStringArrayListExtra("memberList");

        // 联系人列表

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
                            sqlite = ConversationSqlite.getInstance( GroupPickContactsActivity.this);
                            db = sqlite.getReadableDatabase();
                            contactLists.clear();
                            EaseUser e;

                            if (type == 0) {
                                cursor = db.rawQuery("select * from conversation", null);
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        e = new EaseUser();
                                        e.setUsername(cursor.getString(4));
                                        e.setNick(cursor.getString(2));
                                        e.setAvatar(cursor.getString(3));
                                        contactLists.add(e);
                                    }
                                }
                            } else if (type == 2) {

                                //人脉列表
                                cursor = db.rawQuery("select * from renmai", null);
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        e = new EaseUser();
                                        e.setUsername(cursor.getString(4));
                                        e.setNick(cursor.getString(2));
                                        e.setAvatar(cursor.getString(3));
                                        contactLists.add(e);
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(GroupPickContactsActivity.this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        ListView listView = (ListView) findViewById(R.id.list);
        List<EaseUser> easeUsers = filledData(contactLists);
        // sort the list
        Collections.sort(easeUsers, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
        contactAdapter = new PickContactAdapter(this, R.layout.em_row_contact_with_checkbox, easeUsers);
        listView.setAdapter(contactAdapter);
        ((EaseSidebar) findViewById(R.id.sidebar)).setListView(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    private void initTitleBar() {

        new TitleBarBuilder(this).setTitle(getIntent().getStringExtra("title")).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setTextRight("保存").setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> var = getToBeAddMembers();
                setResult(RESULT_OK, new Intent().putExtra("newmembers", var.toArray(new String[var.size()])));
                finish();
            }
        });
    }

    /**
     * get selected members
     *
     * @return
     */
    private List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();
        int length = contactAdapter.isCheckedArray.length;
        for (int i = 0; i < length; i++) {
            String username = contactAdapter.getItem(i).getUsername();
            if (contactAdapter.isCheckedArray[i] && !existMembers.contains(username)) {
                members.add(username);
            }
        }

        return members;
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
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getUsername()).toUpperCase();
            } else {
                sortModel.setNick(list.get(i).getNick());
                // 汉字转换成拼音
                sortString = PinYinUtils.getPinYinFirstLetter(list.get(i).getNick()).toUpperCase();
                sortModel.setUsername(list.get(i).getUsername());
            }

            sortModel.setAvatar(list.get(i).getAvatar());

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setInitialLetter(sortString.toUpperCase());
            } else {
                //sortModel.setSortLetters("#");
                sortModel.setInitialLetter("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * adapter
     */
    private class PickContactAdapter extends EaseContactAdapter {

        private boolean[] isCheckedArray;

        public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            final String username = getItem(position).getUsername();
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
            TextView nameView = (TextView) view.findViewById(R.id.name);
            if (checkBox != null) {
                if (existMembers != null && existMembers.contains(username)) {
                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_gray_selector);
                } else {
                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_selector);
                }

                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // check the exist members
                        if (existMembers.contains(username)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                        }
                        isCheckedArray[position] = isChecked;

                    }
                });
                // keep exist members checked
                if (existMembers.contains(username)) {
                    checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    checkBox.setChecked(isCheckedArray[position]);
                }
            }

            return view;
        }
    }

}
