package com.qcwl.debo.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.Utils;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by xxm on 2018/8/27.
 */

public class AppUtils {
    AppUtils mAppUtils;

    private Context mContext;

    public AppUtils() {
        super();

    }

    public AppUtils AppUtils(Context context) {
        mContext = context;
        if (null == mAppUtils) {
            mAppUtils = this;
        }
        return mAppUtils;
    }


    public static boolean isNullTxt(String txt) {
        return null == txt || txt.length() == 0;
    }

    public static boolean isEmptyList(List list) {
        return null == list || list.size() == 0;
    }

    public static boolean isEmptyMap(Map map) {
        return null == map || map.size() == 0;
    }
}
