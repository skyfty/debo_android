package com.qcwl.debo.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/4/19.
 */

public class InputUtils {

    public static void showSoftInput(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    public static void hideSoftInput(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setText("");
//        editText.setFocusable(false);
//        editText.setFocusableInTouchMode(false);
//        editText.clearFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
