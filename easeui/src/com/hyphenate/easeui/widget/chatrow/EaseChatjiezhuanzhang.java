package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseSmileUtils;

import org.json.JSONObject;

public class EaseChatjiezhuanzhang extends EaseChatRow {
        private TextView tv_text;
        public EaseChatjiezhuanzhang(Context context, EMMessage message, int position, BaseAdapter adapter) {
            super(context, message, position, adapter);
        }


        @Override
        protected void onInflateView() {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.ease_row_recerved_jiehongbao1 : R.layout.ease_row_send_jiehongbao1, this);
        }

        @Override
        protected void onFindViewById() {
            tv_text = (TextView) findViewById(R.id.tv_text);
        }

        @Override
        public void onSetUpView() {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            try {
                String json = message.getStringAttribute("ext");
                JSONObject object = new JSONObject(json);
                // 设置内容
                if (object.getString("offer_user_nickname")!=null){
                    tv_text.setText(object.getString("first_get_name")+"已确认收账");
                }else {
                   // tv_text.setText(object.getString("myUserName")+"领取了"+object.getString("user_nickname")+"的红包");
                    tv_text.setText(object.getString("first_get_name")+"已确认收账");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
         //   handleTextMessage();
        }

     /*   protected void handleTextMessage() {
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
        }*/

        @Override
        protected void onUpdateView() {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onBubbleClick() {

        }

    }
