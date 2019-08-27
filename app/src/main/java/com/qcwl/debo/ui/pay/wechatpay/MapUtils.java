package com.qcwl.debo.ui.pay.wechatpay;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by NEU on 2016/11/17.
 */

public class MapUtils {
    /**object的空处理*/
    public static  String getObject (Object object) {
        return null==object?"":object.toString();
    }

    public static boolean isMapHaveValue (Map<String,Object> mapInfo,String key) {
        if (mapInfo.containsKey(key) && TextUtils.isEmpty(MapUtils.getObject(mapInfo.get(key)))) {
            return true;
        } else {
            return false;
        }
    }
}
