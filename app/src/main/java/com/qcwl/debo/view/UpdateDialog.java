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
import com.tencent.bugly.beta.Beta;


/**
 * Created by Administrator on 2016/10/28.
 */
public class UpdateDialog extends Dialog {
    private Context context;
    private TextView title, content, confirm, cancel;
    private ProgressButton progress_button;
    private ClickListenerInterface clickListenerInterface;

    public UpdateDialog(Context context) {
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
        View view = inflater.inflate(R.layout.upgrade_dialog, null);
        setContentView(view);
        title = (TextView) view.findViewById(R.id.title);
        content = (TextView) view.findViewById(R.id.content);
        confirm = (TextView) view.findViewById(R.id.confirm);
        progress_button = (ProgressButton) view.findViewById(R.id.progress_button);
        cancel = (TextView) view.findViewById(R.id.cancel);
        title.setText(Beta.getUpgradeInfo().title);
        content.setText(Beta.getUpgradeInfo().newFeature);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerInterface.doConfirm();
            }
        });
        progress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerInterface.doConfirm();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.cancelDownload();
                dismiss();
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕的0.9
        lp.height = d.heightPixels * 1;
        dialogWindow.setAttributes(lp);
    }

    public interface ClickListenerInterface {
        public void doConfirm();
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public void setTask(String task) {
        confirm.setText(task);
        progress_button.setText(task);
    }

    public void setProgress(int progress) {
        progress_button.setProgress(progress);
    }

}