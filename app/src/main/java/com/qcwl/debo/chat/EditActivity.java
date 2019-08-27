package com.qcwl.debo.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.utils.TitleBarBuilder;


/**
 * Created by Administrator on 2016/9/23.
 */

public class EditActivity extends BaseActivity {
    private EditText editText;
    private TextView tip;
    private String title, data;
    private ImageButton clear;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_edit);
        title = getIntent().getStringExtra("title");
        data = getIntent().getStringExtra("data");

        initTitleBar();

        editText = (EditText) findViewById(R.id.edittext);
        tip = (TextView) findViewById(R.id.tip);
        clear = (ImageButton) findViewById(R.id.clear);

        if (data != null)
            editText.setText(data);
        tip.setText(title+" :");
        editText.setSelection(editText.length());
        if (editText.length() > 0)
            clear.setVisibility(View.VISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    clear.setVisibility(View.INVISIBLE);
                } else {
                    clear.setVisibility(View.VISIBLE);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle(title).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setTextRight("保存").setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("data", editText.getText().toString().trim()));finish();
            }
        });
    }

}
