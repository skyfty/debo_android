package com.qcwl.debo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.qcwl.debo.R;


/**
 * Created by Administrator on 2016/10/28.
 */
public class CommonDialog extends Dialog {

    private Context context;
    private TextView cancel, commit, tv_tittle;
    private EditText content;
    private ClickListenerInterface clickListenerInterface;

    public CommonDialog(Context context) {
        super(context, R.style.mydialogstyle);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_add_friend, null);
        setContentView(view);
        cancel = (TextView) view.findViewById(R.id.cancel);
        commit = (TextView) view.findViewById(R.id.commit);
        tv_tittle = (TextView) view.findViewById(R.id.tv_tittle);
        content = (EditText) view.findViewById(R.id.content);
        cancel.setOnClickListener(new clickListener());
        commit.setOnClickListener(new clickListener());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public interface ClickListenerInterface {
        public void doConfirm(String reason);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            hideSoftInput();
            int id = v.getId();
            switch (id) {
                case R.id.cancel:
                    dismiss();
                    break;
                case R.id.commit:
                    clickListenerInterface.doConfirm(content.getText().toString().trim());
                    break;
            }
        }
    }

    public void setTittle(String tittle) {
        tv_tittle.setText(tittle);
    }

    public void setContent(String content) {
        tv_tittle.setText(content);
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

}