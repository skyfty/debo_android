package com.qcwl.debo.widget;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 */

public class InviteMessgeDao {
    static final String TABLE_NAME = "new_friends_msgs";
    static final String COLUMN_NAME_ID = "id";
    static final String COLUMN_NAME_FROM = "username";
    static final String COLUMN_NAME_GROUP_ID = "groupid";
    static final String COLUMN_NAME_GROUP_Name = "groupname";

    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    static final String COLUMN_NAME_GROUPINVITER = "groupinviter";

    static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";


    public InviteMessgeDao(Context context){
    }

    /**
     * save message
     * @param message
     * @return  return cursor of the message
     */
    public Integer saveMessage(InviteMessage message){
        return DemoDBManager.getInstance().saveMessage(message);
    }

    /**
     * update message
     * @param msgId
     * @param values
     */
    public void updateMessage(int msgId,ContentValues values){
        DemoDBManager.getInstance().updateMessage(msgId, values);
    }

    /**
     * get messges
     * @return
     */
    public List<InviteMessage> getMessagesList(){
        return DemoDBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from){
        DemoDBManager.getInstance().deleteMessage(from);
    }

    public int getUnreadMessagesCount(){
        return DemoDBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count){
        DemoDBManager.getInstance().setUnreadNotifyCount(count);
    }
}
