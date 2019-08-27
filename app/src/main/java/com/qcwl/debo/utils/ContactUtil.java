package com.qcwl.debo.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.cjt2325.cameralibrary.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 通讯录工具类 - 获取姓名,手机号码,并转换为JsonArray
 */
public class ContactUtil {
    //    private String data;
    public static JSONArray readContacts(Context context) {
// 首先，从raw_contacts中读取联系人的id（"contact_id")
// 其次，根据contact_id从data表中查询出相应的电话号码和联系人名称
// 然后，根据mimetype来区分哪个是联系人，哪个是电话号码
// Uri rawContactsUri = Uri
// .parse("content://com.android.contacts/raw_contacts");
        ArrayList<HashMap<String, String>> contacts = new ArrayList<HashMap<String, String>>();
        JSONArray jsonArray = new JSONArray();
        JSONObject tmpObj = new JSONObject();
        // 从raw_contacts中读取联系人的id("contact_id")
        Cursor rawContactsCursor = context.getContentResolver()
                .query(ContactsContract.RawContacts.CONTENT_URI, new String[]{"contact_id", "sort_key"}, null, null, "sort_key");
        //按sort_key排序
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);                // System.out.println(contactId);
                if (contactId != null) {
                    // 曾经有过，已经删除的联系人在raw_contacts表中记录仍在，但contact_id值为null
                    // 根据contact_id从data表中查询出相应的电话号码和联系人名称
                    // Uri dataUri = Uri
                    // .parse("content://com.android.contacts/data");
                    Cursor dataCursor = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            new String[]{"data1", "mimetype"},
                            "contact_id=?", new String[]{contactId}, null);
                    Log.i("ContactUtil","....="+dataCursor.getCount());
                    if (dataCursor != null) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        while (dataCursor.moveToNext()) {
                            String data1 = dataCursor.getString(0);
                            String mimetype = dataCursor.getString(1);
                            if (data1 == null) {
                                data1 = "";
                            }
                            tmpObj = new JSONObject();
                            if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                                map.put("iphone", data1);
                            } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                                // map.put("name", data1);
                                map.put("displayName", data1);
                                // } else if ("vnd.android.cursor.item/group_membership".equals(mimetype)) {
                                //  map.put("tagLabel", data1);
                                // }else{
                                //  LogUtil.e("mimetype ------------- ", mimetype + "   " + data1);
                                //  }
                            }
                            try {
                                tmpObj.put("iphone", map.get("iphone") == null ? "" : map.get("iphone"));
                                tmpObj.put("tagLabel", map.get("tagLabel") == null ? "" : map.get("tagLabel"));
                                tmpObj.put("displayName", map.get("displayName") == null ? "" : map.get("displayName"));
                                jsonArray.put(tmpObj);
                                contacts.add(map);
                            } catch (JSONException e) {
                                LogUtil.e("e ----------------- ", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        dataCursor.close();
                    }
                }
                LogUtil.e("ContactUtil", "rawContactsCursor --- " + contacts);
                LogUtil.e("ContactUtil", "jsonArray --- " + jsonArray.toString());
            }
        }
        return jsonArray;
    }
}


