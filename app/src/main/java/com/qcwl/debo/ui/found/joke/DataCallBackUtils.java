package com.qcwl.debo.ui.found.joke;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataCallBackUtils {
    private static TouchDataCallBack mCallBack;
    private static List<TouchDataCallBack> list = new ArrayList<>();
    private static List<TouchDataCallBack> list2 = new ArrayList<>();
    public static void setCallBack(TouchDataCallBack callBack) {
        Log.i("DataCallBackUtils","......list.size="+list.size());
        for (int i = 0;i<list.size();i++){
            list2.clear();
            Log.i("DataCallBackUtils","......for="+i);
            if (list.get(i) == callBack){
                if (i == list.size()-1){
                    list2.add(callBack);
                }
                Log.i("DataCallBackUtils","......一样");
            }else{
                if (i == list.size()-1){
                    list2.add(callBack);
                }

                Log.i("DataCallBackUtils","......不一样");
            }
        }
        if (list.size() == 0){
            Log.i("DataCallBackUtils","......00000");
            list2.add(callBack);
        }

        list.clear();
        list.addAll(list2);
        Log.i("DataCallBackUtils","......list2="+list2.size()+"　　　　"+list.size());

    }
    public static JSONObject doCallBackMethod(int jsonObject){
        Log.i("DataCallBackUtils","......doCallBackMethod="+jsonObject+"　　　　"+list.size());
        if (list.size()>0){
            mCallBack = list.get(list.size()-1);
            if (mCallBack == null){
                Log.i("DataCallBackUtils","......doCallBackMethod == null");
                return null;
            }else{
                Log.i("DataCallBackUtils","......doCallBackMethod != null");
                return mCallBack.doSomeThing(jsonObject);
            }
        }else{
            return null;
        }
    }
    public boolean isCallback(){
        if (mCallBack == null){
            return false;
        }else{
            return true;
        }
    }
    public static void removeCallBack() {
        Log.i("DataCallBackUtils","......list.size="+list.size());
        if (list.size()>0){
            list.remove(list.size()-1);
        }
    }
    /*public static void setCallBack(TouchDataCallBack callBack) {
        mCallBack = callBack;
    }
    
    public static JSONObject doCallBackMethod(int jsonObject){
        Log.i("DataCallBackUtils","......doSomeThing="+jsonObject);
        if (mCallBack == null){
            return null;
        }
         return mCallBack.doSomeThing(jsonObject);
    }
    public boolean isCallback(){
        if (mCallBack == null){
            return false;
        }else{
            return true;
        }
    }*/
}
