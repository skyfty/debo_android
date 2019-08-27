package com.qcwl.debo.chat;

import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.qcwl.debo.R;

/**
 * Created by Administrator on 2016/9/23.
 */

public class ForwardMessageActivity extends PickContactNoCheckboxActivity {
    private EaseUser selectUser;
    private String forward_msg_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forward_msg_id = getIntent().getStringExtra("forward_msg_id");
    }

    @Override
    protected void onListItemClick(int position) {
        selectUser = contactAdapter.getItem(position);
        new EaseAlertDialog(this, null, getString(R.string.confirm_forward_to, selectUser.getNick()), null, new AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (selectUser == null)
                        return;
                    try {
                        ChatActivity.activityInstance.finish();
                    } catch (Exception e) {
                    }
                    Intent intent = new Intent(ForwardMessageActivity.this, ChatActivity.class);
                    // it is single chat
                    intent.putExtra("userId", selectUser.getUsername());
                    intent.putExtra("nickname", selectUser.getNick());
                    intent.putExtra("forward_msg_id", forward_msg_id);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    intent.putExtra("is_show_name",getIntent().getStringExtra("is_show_name"));
                    startActivity(intent);
                    finish();
                }
            }
        }, true).show();
    }

}
