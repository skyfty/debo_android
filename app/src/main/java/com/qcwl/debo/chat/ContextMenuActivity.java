package com.qcwl.debo.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.qcwl.debo.R;
import com.qcwl.debo.widget.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/23.
 */

public class ContextMenuActivity extends BaseActivity {

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_RECALL = 4;
    private String TAG = "ContextMenuActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EMMessage message = getIntent().getParcelableExtra("message");
        boolean isChatroom = getIntent().getBooleanExtra("ischatroom", false);

        int type = message.getType().ordinal();
        if (type == EMMessage.Type.TXT.ordinal()) {
            JSONObject object = null;
            try {
                object = new JSONObject(message.getStringAttribute("ext"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                    || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                //red packet code : 屏蔽红包消息的转发功�?
                    ){
                //end of red packet code
                setContentView(R.layout.em_context_menu_for_location);
            }else if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                Log.i(TAG,"         ="+"MESSAGE_ATTR_IS_BIG_EXPRESSION");
                setContentView(R.layout.em_context_menu_for_image);
            }else try {
                if (object != null) {
                    if (object.getString("is_card").equals("true")) {
                        setContentView(R.layout.em_context_menu_for_location);
                    }
                }else{
                    setContentView(R.layout.em_context_menu_for_text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type == EMMessage.Type.LOCATION.ordinal()) {
            setContentView(R.layout.em_context_menu_for_location);
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            Log.i(TAG,"         ="+"IMAGE");
            setContentView(R.layout.em_context_menu_for_image);
        } else if (type == EMMessage.Type.VOICE.ordinal()) {
            setContentView(R.layout.em_context_menu_for_voice);
        } else if (type == EMMessage.Type.VIDEO.ordinal()) {
            setContentView(R.layout.em_context_menu_for_video);
        } else if (type == EMMessage.Type.FILE.ordinal()) {
            setContentView(R.layout.em_context_menu_for_location);
        }
        if (isChatroom
            //red packet code : 屏蔽红包消息的撤回功�?
                ) {
            //end of red packet code
            View v = findViewById(R.id.tv_forward);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void copy(View view){
        setResult(RESULT_CODE_COPY);
        finish();
    }
    public void delete(View view){
        setResult(RESULT_CODE_DELETE);
        finish();
    }
    public void forward(View view){
        setResult(RESULT_CODE_FORWARD);
        finish();
    }
    public void recall(View view){
        setResult(RESULT_CODE_RECALL);
        finish();
    }

}
