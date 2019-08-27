package com.qcwl.debo.utils;

import com.qcwl.debo.ui.found.chat.bean.Rand_User_Data;

public class CallBackUtils {
    private static CallBack mCallBack;
    private static Rand_User_Data data;
    public static void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }
   /* public static void setBean(Rand_User_Data bean){
        data = bean;
    }*/
    
    public static Rand_User_Data doCallBackMethod(){
        return mCallBack.doSomeThing();
    }
}
