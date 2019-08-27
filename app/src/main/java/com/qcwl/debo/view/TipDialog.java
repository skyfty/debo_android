package com.qcwl.debo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qcwl.debo.R;


/**
 * Created by Administrator on 2016/10/28.
 */
public class TipDialog extends Dialog {
    private Context context;
    private TextView cancel, commit, tip, content;
    private ClickListenerInterface clickListenerInterface;

    public TipDialog(Context context) {
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
        View view = inflater.inflate(R.layout.dialog_tip, null);
        setContentView(view);
        cancel = (TextView) view.findViewById(R.id.cancel);
        commit = (TextView) view.findViewById(R.id.commit);
        tip = (TextView) view.findViewById(R.id.tip);
        content = (TextView) view.findViewById(R.id.content);
        cancel.setOnClickListener(new clickListener());
        commit.setOnClickListener(new clickListener());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public interface ClickListenerInterface {
        void doConfirm();

        void doCancel();
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.cancel:
                    clickListenerInterface.doCancel();
                    break;
                case R.id.commit:
                    clickListenerInterface.doConfirm();
                    break;
            }
        }
    }

    ;

    public void setTip(String tittle) {
        tip.setText(tittle);
    }

    public void setContent(String contents) {
        content.setText(contents);
    }

    public void setButtonText(String leftTip, String rightTip) {
        cancel.setText(leftTip);
        commit.setText(rightTip);
    }

}