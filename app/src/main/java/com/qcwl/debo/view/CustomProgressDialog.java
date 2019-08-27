package com.qcwl.debo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.qcwl.debo.R;


public class CustomProgressDialog extends Dialog {
	
	private TextView mMessageView;
	
	public CustomProgressDialog(Context context){
		this(context, "");
	}
	
	public CustomProgressDialog(Context context, String strMessage) {  
        this(context, R.style.loading_dialog, strMessage);
    }  
	
	public CustomProgressDialog(Context context, int theme, String strMessage) {  
        super(context, theme);  
        this.setContentView(R.layout.custom_progress_dialog);  
        this.getWindow().getAttributes().gravity = Gravity.CENTER;  
        mMessageView = (TextView) this.findViewById(R.id.tipTextView);  
        if ( mMessageView != null) {  
        	 mMessageView.setText(strMessage);  
        }

		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && true) {
					return false;
				}
				return false;
			}
		});
    }  
  
	public void setMessage(String message){
		mMessageView.setText(message);
	}

}
