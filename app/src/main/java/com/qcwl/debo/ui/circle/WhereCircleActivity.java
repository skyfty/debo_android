package com.qcwl.debo.ui.circle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.contact.CreateGroupActivity;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/9/13.
 */

public class WhereCircleActivity extends BaseActivity {

    @Bind(R.id.text_send)
    TextView tv_send;
    @Bind(R.id.image_back)
    ImageView iv_back;

    @Bind(R.id.cb_people)
    CheckBox cbPeople;
    @Bind(R.id.cb_friend)
    CheckBox cbFriend;
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;
    private int type = 1;
    private int moments_type;
    private String TAG = "WhereCircleActivity";
    private final int REQUEST_CALL = 103;
    private String id;
    private String contactIds;
    private String renMaiIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_circle);
        ButterKnife.bind(this);
        if (getIntent() == null) {
            return;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb3:
                       Log.i(TAG,"......rb3");
                        startActivityForResult(new Intent(WhereCircleActivity.this,CreateGroupActivity.class).putExtra("type","wherecircle"),REQUEST_CALL);
                        break;
                    case R.id.rb4:
                        Log.i(TAG,"......rb4");
                        startActivityForResult(new Intent(WhereCircleActivity.this,CreateGroupActivity.class).putExtra("type","wherecircle"),REQUEST_CALL);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.image_back, R.id.text_send,R.id.cb_people,R.id.cb_friend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.text_send:
                if (cbPeople.isChecked() == false&&cbFriend.isChecked() == false){
                    Toast.makeText(this,"请选择发布地址",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    int index = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
                    if (index >= 0){
                        index+=1;
                    }

                    Intent intent = new Intent();
                    if (cbFriend.isChecked() && !cbPeople.isChecked()) {
                        type = 1;
                        intent.putExtra("contactIds",contactIds);
                    }
                    if (!cbFriend.isChecked() && cbPeople.isChecked()) {
                        type = 2;
                        intent.putExtra("renMaiIds",renMaiIds);
                    }
                    if (cbFriend.isChecked() && cbPeople.isChecked()) {
                        type = 3;
                        intent.putExtra("contactIds",contactIds);
                        intent.putExtra("renMaiIds",renMaiIds);
                    }
                    intent.putExtra("type",type);
                    intent.putExtra("moments_type",index);
                    Log.i(TAG,".....="+index+"    "+type+"     "+cbFriend.isChecked()+"     "+cbPeople.isChecked());
                    this.setResult(102,intent);
                    finish();
               }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == REQUEST_CALL) {
            contactIds = data.getStringExtra("contactIds");
            renMaiIds = data.getStringExtra("renMaiIds");
            Log.i(TAG, ".........REQUEST_CALL=" + contactIds+"    "+renMaiIds);
        }
    }
}
