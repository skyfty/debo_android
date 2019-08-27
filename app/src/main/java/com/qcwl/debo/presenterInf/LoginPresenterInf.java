package com.qcwl.debo.presenterInf;

import android.content.Context;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface LoginPresenterInf extends PresenterInf{


    interface HandleData{
        //登录
        void login(Context context,String user,String pwd);
        void login(Context context,Map<String,String> map);
    }

}
