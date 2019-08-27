package com.hyphenate.easeui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.util.Util;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.TypeStateBean;
import com.hyphenate.easeui.sqlite.ConversationSqlite;
import com.hyphenate.easeui.sqlite.StrangerBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImgUtil;
import com.hyphenate.easeui.widget.EaseConversationList.EaseConversationListHelper;
import com.hyphenate.util.TimeInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

import static com.hyphenate.util.DateUtils.getTodayStartAndEndTime;
import static com.hyphenate.util.DateUtils.getYesterdayStartAndEndTime;

/**
 * conversation list adapter
 */
public class EaseConversationAdapter extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    //private final List<TypeStateBean> list;
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;
    //List<TypeStateBean> date;
    Map<String, String> date;

    public EaseConversationAdapter(Context context, int resource, List<EMConversation> objects,/*List<TypeStateBean>*/Map<String, String> date) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
        this.date = date;
        //list = JSON.parseArray(data, TypeStateBean.class);
        //Log.i("EaseConversationAdapter","......date="+date.size());
        //Log.i("EaseConversationAdapter","......LIST2="+list.size()+"      "+data);
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Log.i(TAG, " getView=" + date);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
        holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
        holder.message = (TextView) convertView.findViewById(R.id.message);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
        holder.msgState = convertView.findViewById(R.id.msg_state);
        holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
        holder.motioned = (TextView) convertView.findViewById(R.id.mentioned);
        holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);

        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String username = conversation.conversationId();
        String exid = conversation.getExtField();

        if (conversation.getType() == EMConversationType.GroupChat) {
            Log.i(TAG, " GroupChat ");
            String groupId = conversation.conversationId();
            if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
                holder.motioned.setVisibility(View.VISIBLE);
            } else {
                holder.motioned.setVisibility(View.GONE);
            }
            // group message, show group avatar
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            holder.name.setText(group != null ? group.getGroupName() : username);
        } else if (conversation.getType() == EMConversationType.ChatRoom) {
            Log.i(TAG, " ChatRoom");
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            holder.motioned.setVisibility(View.GONE);
        } else {
            Log.i(TAG, " else");
            //username.replace(getMobile(username), "").trim()
            EaseUserUtils.setUserNick(getContext(), username, holder.name, holder.avatar);
            holder.motioned.setVisibility(View.GONE);
        }
        Log.i(TAG, " .....defaule_name="+username);
        if (conversation.getUnreadMsgCount() > 0) {
            // show unread message count
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMsgCount() != 0) {
            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();
            String content = null;
            if (cvsListHelper != null) {
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))), BufferType.SPANNABLE);
            if (content != null) {
                holder.message.setText(content);
            }
            holder.time.setText(getTimestampString(new Date(lastMessage.getMsgTime())));
//            holder.time.setText(formatTimes(lastMessage.getMsgTime()));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }
        if (primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if (timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        if (date == null || date.size() == 0) {

        } else {

            String type = date.get(username);
            Log.i(TAG, "........................=" + username + "         " + type);
            //if (type != null) {
                if(type == null||type.equals("")||type.equals("0")){
                    holder.tv_type.setText("");
                    holder.tv_type.setBackground(null);
                }else if (type.equals("1") == true) {
                    holder.tv_type.setText("好友");
                    holder.tv_type.setBackground(getContext().getResources().getDrawable(R.mipmap.icon_type_yellow));
                } else if (type.equals("2") == true) {
                    holder.tv_type.setText("人脉");
                    holder.tv_type.setBackground(getContext().getResources().getDrawable(R.mipmap.icon_type_green));
                } /*else {
                    holder.tv_type.setText("");
                    holder.tv_type.setBackground(getContext().getResources().getDrawable(R.mipmap.icon_type_green));
                    //holder.tv_type.setVisibility(View.GONE);
                }*/
            //}
        }

        return convertView;
    }

    private String getMobile(String username) {
        if (TextUtils.isEmpty(username)) {
            return "";
        }
        int start = 0;
        for (int i = 0; i < username.length(); i++) {
            if (Character.isDigit(username.charAt(i))) {
                start = i;
                break;
            }
        }
        return username.substring(start);
    }

    public String formatTimes(long s) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        dateFormat.applyPattern("yyyy-MM-dd HH:mm");
        Timestamp date = new Timestamp(s);
        return dateFormat.format(date);
    }

    public String getTimestampString(Date var0) {
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = var0.getTime();
        if (isSameDay(var4)) {
            if (var3) {
                var1 = "aa HH:mm";
            } else {
                var1 = "HH:mm aa";
            }
        } else if (isYesterday(var4)) {
            if (!var3) {
                return "Yesterday " + (new SimpleDateFormat("HH:mm aa", Locale.ENGLISH)).format(var0);
            }

            var1 = "昨天aa HH:mm";
        } else if (var3) {
            var1 = "M月d日aa HH:mm";
        } else {
            var1 = "MMM dd HH:mm aa";
        }

        return var3 ? (new SimpleDateFormat(var1, Locale.CHINESE)).format(var0) : (new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    private boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }

    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        /*for (int i = 0;i<conversationList.size();i++){
            //Log.i(TAG,".......conversationList="+i+"      ="+conversationList.get(i).getMessage());
        }*/

        return conversationFilter;
    }


    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    EMConversation value = mOriginalValues.get(i);
                    String username = value.conversationId();
                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    } else {
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
                        if (room != null){
                            username = room.getName();
                        }else{
                            username = dbName(username);
                        }

                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        // TODO: not support Nick anymore
//                        if(user != null && user.getNick() != null)
//                            username = user.getNick();
                    }
                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        private String dbName(String username){
            ConversationSqlite sqlite = ConversationSqlite.getInstance(getContext());
            SQLiteDatabase db = sqlite.getReadableDatabase();
            Cursor cursor;
            String user_name = "";
            if (username.equals(EMClient.getInstance().getCurrentUser())) {
                cursor = db.rawQuery("select * from person where phone='"
                        + username + "'", null);
            } else {
                cursor = db.rawQuery("select * from conversation where phone='"
                        + username + "'", null);
            }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    user_name = cursor.getString(2);
                }
            }else{
                cursor = db.rawQuery("select * from stranger where phone='"
                        + username + "'", null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        user_name = cursor.getString(2);
                    }
                }
            }
            sqlite.close();
            db.close();
            cursor.close();
            return user_name;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EMConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {
        /**
         * who you chat with
         */
        TextView name;
        /**
         * unread message count
         */
        TextView unreadLabel;
        /**
         * content of last message
         */
        TextView message;
        /**
         * time of last message
         */
        TextView time;
        /**
         * avatar
         */
        ImageView avatar;
        /**
         * status of last message
         */
        View msgState;
        /**
         * layout
         */
        RelativeLayout list_itease_layout;
        TextView motioned;
        TextView tv_type;
    }
}

