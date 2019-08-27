/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qcwl.debo.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatRoomListener;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.chatroom.ChatRoomAdapter;
import com.qcwl.debo.chat.chatroom.ChatRoomBean;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.AreaBean;
import com.qcwl.debo.utils.GetJsonDataUtil;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class PublicChatRoomsActivity extends BaseActivity  {
    private ProgressBar pb;
    private ListView listView;

    private LinearLayout footLoadingLayout;
    private EditText etSearch;
    private ImageButton ibClean;
    private ChatRoomChangeListener chatRoomChangeListener;
    private Button mBtnCreateGroup;

    private OptionsPickerView pvOptions;
    private ArrayList<AreaBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String province;
    private String city;
    private String area;

    private SwipeRefreshLayout swipeRefreshLayout;
    private String uid = "";//"34";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_public_groups);

        handler = new Handler();
        etSearch = (EditText) findViewById(R.id.query);
        ibClean = (ImageButton) findViewById(R.id.search_clear);
        etSearch.setHint(R.string.search);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.list);
        TextView title = (TextView) findViewById(R.id.tv_title);
        mBtnCreateGroup = (Button) findViewById(R.id.btn_create_group);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_list_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                getChatRoomList();
            }
        });
        title.setText(getResources().getString(R.string.chat_room));

        View footView = getLayoutInflater().inflate(R.layout.em_listview_footer_view, listView, false);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        listView.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);

        items = new ArrayList<>();
        adapter = new ChatRoomAdapter(this, 1, items);
        listView.setAdapter(adapter);
        getChatRoomList();

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
                if (s.length() > 0) {
                    ibClean.setVisibility(View.VISIBLE);
                } else {
                    ibClean.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ibClean.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.getText().clear();
                hideSoftKeyboard();
                //loadAndShowData();
            }
        });
        mBtnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据地区创建
                chooseArea();
            }
        });

        chatRoomChangeListener = new ChatRoomChangeListener();
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);

        //loadAndShowData();


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    // create chat room
//                    startActivity(new Intent(PublicChatRoomsActivity.this, NewChatRoomActivity.class));
//                } else {
//                    final EMChatRoom room = adapter.getItem(position - 1);
//                    startActivity(new Intent(PublicChatRoomsActivity.this, ChatActivity.class).putExtra("chatType", 3).
//                            putExtra("userId", room.getId()));
//                }
                ChatRoomBean room = adapter.getItem(position);
                startActivity(new Intent(PublicChatRoomsActivity.this, ChatActivity.class)
                        .putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM)
                        .putExtra("type", "1")
                        .putExtra("userId", room.getChatroom_id())
                        .putExtra("is_show_name", "0")
                );

            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initJsonData();
            }
        }, 0);
    }

    private class ChatRoomChangeListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            if (adapter != null) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (adapter != null) {
                            //loadAndShowData();
                            getChatRoomList();
                        }
                    }

                });
            }
        }

       /* @Override
        public void onRemovedFromChatRoom(int i, String s, String s1, String s2) {

        }*/

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

    }

    public void back(View view) {
        hideSoftKeyboard();
        finish();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomChangeListener);
        super.onDestroy();
    }

    private int page = 1;
    private List<ChatRoomBean> items = null;
    private ChatRoomAdapter adapter;

    private void getChatRoomList() {
        pb.setVisibility(View.VISIBLE);
        Api.getChatRoomList(sp.getString("uid"), page, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    List<ChatRoomBean> list = JSON.parseArray("" + apiResponse.getData(), ChatRoomBean.class);
                    if (list != null) {
                        items.addAll(list);
                    }
                    adapter.firstNotifyDataSetChanged();
                    pb.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    ToastUtils.showShort(PublicChatRoomsActivity.this, "" + apiResponse.getMessage());
                }
            }
        });
    }

    private void chooseArea() {
        //条件选择器
        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {

                //返回的分别是三个级别的选中位置
                province = options1Items.get(options1).getPickerViewText();
                city = options2Items.get(options1).get(option2);
                area = options3Items.get(options1).get(option2).get(options3);
                //调用上传创建聊天室的方法
//                if (province.equals(city)) {
////                        area_tv.setText(city + " " + area);
//                } else {
////                        area_tv.setText(province + " " + city + " " + area);
//                }
                submit(city,area);
            }
        }).setLayoutRes(R.layout.pick_area_option, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvOptions.returnData();
                        pvOptions.dismiss();
                    }
                });

                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvOptions.dismiss();
                    }
                });
            }
        }).setDividerColor(getResources().getColor(R.color.font_select))
                .setTextColorCenter(getResources().getColor(R.color.font_select)).setContentTextSize(18)
                .build();
        if(options1Items.size()>0&&options2Items.size()>0&&options3Items.size()>0){
            pvOptions.setPicker(options1Items, options2Items, options3Items);
        }

        pvOptions.show();
    }

    private void submit(String city, String area){

        uid = SPUtil.getInstance(this).getString("uid");

        Api.addChatRoom(uid, city, area, "", new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    ToastUtils.showShort(PublicChatRoomsActivity.this,apiResponse.getMessage());
                }else{
                    ChatRoomBean chatRoomBean = JSON.parseObject(apiResponse.getData(), ChatRoomBean.class);
                    startActivity(new Intent(PublicChatRoomsActivity.this, ChatActivity.class)
                            .putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM)
                            .putExtra("type", "1")
                            .putExtra("userId", chatRoomBean.getChatroom_id())
                            .putExtra("is_show_name", "0")
                    );
                }
            }
        });

    }

    //解析数据
    private void initJsonData() {

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<AreaBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

    }

    public ArrayList<AreaBean> parseData(String result) {//Gson 解析
        ArrayList<AreaBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                AreaBean entity = gson.fromJson(data.optJSONObject(i).toString(), AreaBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }



}
