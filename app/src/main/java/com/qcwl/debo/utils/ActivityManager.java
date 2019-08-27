package com.qcwl.debo.utils;

import android.app.Activity;

import com.qcwl.debo.ui.login.LoginActivity;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;

/**
 * Created by hwj on 2016/5/30.
 * Activity管理类
 */
public class ActivityManager {

    private static LinkedList<Activity> activities = new LinkedList<Activity>();

    private static ActivityManager manager;

    private ActivityManager() {
    }

    public static ActivityManager getActivityManager() {
        if (manager == null) {
            manager = new ActivityManager();
        }
        return manager;
    }

    public synchronized void addActivity(Activity activity) {
        if (activities != null) {
            activities.add(activity);
        }
    }

    public synchronized void removeActivity(Activity activity) {
        if (activities != null) {
            activities.remove(activity);
        }
    }

    public synchronized void finishAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!AppUtils.isEmptyList(activities))
                    try {
                        for (Activity activity : activities) {
                            if (!(activity instanceof LoginActivity) && !activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    } catch (ConcurrentModificationException ex) {
                        activities.clear();
                    }

            }
        }).start();
    }

}
