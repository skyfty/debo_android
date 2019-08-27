package com.qcwl.debo.chat;

import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/12/12.
 */

public class HXMessages {
    public static final  String TAG = "HXMessages";
    /**
     * 发送环信消息
     *
     * @param mes
     * @param toChatUsername 为对方用户或者群聊的id
     * @param isGroup 如果是群聊，设置chattype，默认是单聊
     */
    public static void sendMessages(String mes, String toChatUsername,boolean isGroup, Map map) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        Log.i(TAG, "sendMessages mes:" + mes + " toChatUsername:" + toChatUsername);
        if(mes == null || mes.equals("")){
            Log.i(TAG, "消息内容不能为空！！！！");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(mes, toChatUsername);
        if(map != null ){
            setAttribute(message,map);
        }
        //如果是群聊，设置chattype，默认是单聊
        if (isGroup) {
            //MyLog.i(TAG, "sendMessages messages:" + message + " " + EMMessage.ChatType.GroupChat);
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 发送语音消息
     *
     * @param toChatUsername
     * @param filePath  录音文件路径
     * @param length 录音时长
     * @param toChatUsername 为对方用户或者群聊的id
     * @param isGroup 如果是群聊，设置chattype，默认是单聊
     *
     */
    public static void sendVoiceMessages(String filePath,int length, String toChatUsername,boolean isGroup, Map map) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        Log.i(TAG, "sendMessages filePath:" + filePath + " toChatUsername:" + toChatUsername);
        //filePath为语音文件路径，length为录音时间(秒)
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        if(map != null ){
            setAttribute(message,map);
        }
        //如果是群聊，设置chattype，默认是单聊
        if (isGroup) {
            //MyLog.i(TAG, "sendMessages messages:" + message + " " + EMMessage.ChatType.GroupChat);
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);
    }


    /**
     * 发送短视频
     * @param videoPath 为视频本地路径
     * @param thumbPath 为视频预览图路径
     * @param videoLength 为视频时间长度
     * @param toChatUsername 为对方用户或者群聊的id
     * @param isGroup 如果是群聊，设置chattype，默认是单聊
     */
    public static void sendVideoMessages(String videoPath,String thumbPath,int videoLength, String toChatUsername,boolean isGroup, Map map) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        Log.i(TAG, "sendMessages videoPath:" + videoPath + " thumbPath:" + thumbPath + " toChatUsername:" + toChatUsername);
        //videoPath为视频本地路径，thumbPath为视频预览图路径，videoLength为视频时间长度
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        if(map != null ){
            setAttribute(message,map);
        }
        //如果是群聊，设置chattype，默认是单聊
        if (isGroup) {
            //MyLog.i(TAG, "sendMessages messages:" + message + " " + EMMessage.ChatType.GroupChat);
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);
    }


    public static void setAttribute(EMMessage message , Map map){
        Log.i(TAG, "setAttribute message:" + message );
        Set set = map.keySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()){
            Object obj = iter.next();
            Log.i(TAG, "setAttribute map key:" + obj + " value:" + map.get(obj));
            message.setAttribute(obj.toString(),map.get(obj).toString());
        }
    }

}
