package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONObject;

public class EaseChatCard extends EaseChatRow {
    private ImageView image;
    private TextView name,phone;

    public EaseChatCard(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }


    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_card : R.layout.ease_row_sent_card, this);
    }

    @Override
    protected void onFindViewById() {
        image = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.phone);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
        try {
            String json = message.getStringAttribute("ext");
            JSONObject object = new JSONObject(json);
            // 设置内容
            if(object.has("gift")&&object.getString("gift")!=null){
                findViewById(R.id.bubble).setBackgroundColor(000);
                findViewById(R.id.liner_bubble).setVisibility(GONE);
                findViewById(R.id.gift_bubble).setVisibility(VISIBLE);
            }else {
//                findViewById(R.id.liner_bubble).setVisibility(VISIBLE);
//                findViewById(R.id.gift_bubble).setVisibility(GONE);
                name.setText(object.getString("nameStr"));
                phone.setText("嘚啵号: " + object.getString("phone"));
                if (TextUtils.isEmpty(object.getString("headStr"))) {
                    if (Util.isOnMainThread()) {
                        Glide.with(context).load(R.drawable.head).into(image);
                    }
                } else {
                    if (Util.isOnMainThread()) {
                        Glide.with(context).load(object.getString("headStr")).into(image);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handleTextMessage();
    }

    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            if (!message.isAcked() && message.getChatType() == ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {

    }

}
