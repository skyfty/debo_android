package com.qcwl.debo.ui.found.joke.video_new;


public class CallBackUtils {
    private static TouchCallBack mCallBack;
    public static void setCallBack(TouchCallBack callBack) {
        mCallBack = callBack;
    }
    
    public static void doCallBackMethod(int i){
         mCallBack.doSomeThing(i);
    }
}
