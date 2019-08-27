package com.qcwl.debo.ui.found.bump.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * Created by admin on 2018/5/8.
 */

public class VibratorHelper {

    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vibrator = (Vibrator) activity
                .getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    public static void Vibrate(final Activity activity, long[] pattern,
                               boolean isRepeat) {
        Vibrator vibrator = (Vibrator) activity
                .getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, isRepeat ? 1 : -1);
    }


}
