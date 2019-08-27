package com.qcwl.debo.chat.chatroom;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.PublicChatRoomsActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlMn on 2018/1/18.
 */

public class ChatRoomAdapter extends ArrayAdapter<ChatRoomBean> {

    private LayoutInflater inflater;
    private RoomFilter filter;
    private List<ChatRoomBean> rooms;
    List<ChatRoomBean> rooms2 = new ArrayList<>();

    public ChatRoomAdapter(Context context, int res, List<ChatRoomBean> rooms) {
        super(context, res, rooms);
        this.inflater = LayoutInflater.from(context);
        this.rooms = rooms;
    }

    public void firstNotifyDataSetChanged() {
        this.rooms2.clear();
        this.rooms2.addAll(rooms);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//            if (position == 0) {
//                if (convertView == null) {
//                    convertView = inflater.inflate(R.layout.em_row_add_group, parent, false);
//                }
//                ((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.em_create_group);
//                final String newChatRoom = "创建新的聊天室";//"Create new Chat Room";
//                ((TextView) convertView.findViewById(R.id.name)).setText(newChatRoom);
//            } else {
//                if (convertView == null) {
//                    convertView = inflater.inflate(R.layout.em_row_group, parent, false);
//                }
//                ((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.em_group_icon);
//                ((TextView) convertView.findViewById(R.id.name)).setText(getItem(position - 1).getName());
//            }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.em_row_group, parent, false);
        }
        //((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.em_group_icon);
        //((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getChatroom_name());
        TextView textView = (TextView) convertView.findViewById(R.id.name);
        RoundedImageView imageView = (RoundedImageView) convertView.findViewById(R.id.avatar);
        imageView.setCornerRadius((float) ScreenUtils.dp2px(getContext(), 5));
        textView.setText(getItem(position).getChatroom_name());
        ImgUtil.load(getContext(), getItem(position).getRoom_avatar(), imageView);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new RoomFilter();
        }
        return filter;
    }

    private class RoomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                results.values = rooms2;
                results.count = rooms2.size();
            } else {
                List<ChatRoomBean> list = new ArrayList<>();
                for (ChatRoomBean chatRoom : rooms2) {
                    if (chatRoom.getChatroom_name().contains(constraint)) {
                        list.add(chatRoom);
                    }
                }
                results.values = list;
                results.count = list.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<ChatRoomBean> list = (List<ChatRoomBean>) results.values;
            if (list == null) {
                list = new ArrayList<>();
            }
            if (!TextUtils.isEmpty(constraint)) {
                rooms.clear();
                rooms.addAll(list);
            } else {
                rooms.clear();
                rooms.addAll(rooms2);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return super.getCount();
    }//super.getCount() + 1
}
