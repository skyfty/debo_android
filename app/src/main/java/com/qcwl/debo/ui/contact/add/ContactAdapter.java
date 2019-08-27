package com.qcwl.debo.ui.contact.add;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.MobileUserBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;

import java.util.List;
import java.util.Map;

import static com.umeng.socialize.utils.DeviceConfig.context;

class ContactAdapter extends BaseAdapter implements SectionIndexer {
    private List<MobileUserBean> list;
    private PhoneContactActivity phoneContactActivity;
    private int type;
    private SortGroupMemberAdapter.ChangeState c;
    private Map<String,String> map;
    private String uid = "";

    public ContactAdapter(PhoneContactActivity phoneContactActivity, List<MobileUserBean> list, Map<String, String> map, String uid) {
        this.list = list;
        this.phoneContactActivity = phoneContactActivity;
        this.map = map;
        this.uid = uid;
    }

    public void setListener(SortGroupMemberAdapter.ChangeState c) {
        this.c = c;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<MobileUserBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(phoneContactActivity).inflate(R.layout.item_phone_contact, null);
            vh.image = (RoundedImageView) convertView.findViewById(R.id.imageView);
            vh.tv_name = (TextView) convertView.findViewById(R.id.textName);
            vh.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            vh.tv_add = (TextView) convertView.findViewById(R.id.tv_add);
            vh.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            vh.tvLetter.setVisibility(View.VISIBLE);
            vh.tvLetter.setText(list.get(position).getSortLetters());
        } else {
            vh.tvLetter.setVisibility(View.GONE);
        }

        ImgUtil.setGlideHead(phoneContactActivity, list.get(position).getAvatar(), vh.image);
        vh.tv_name.setText(list.get(position).getUser_nickname());
        String mobile = list.get(position).getMobile();
        String name = map.get(mobile);
        vh.tv_number.setText(name+": "+mobile);
        //1 已经是好友，2非好友
        if (list.get(position).getIs_friends()==1){
            vh.tv_add.setBackgroundResource(R.drawable.text_gray);
            vh.tv_add.setText("已是好友");
            vh.tv_add.setFocusable(false);
        }else if(list.get(position).getIs_friends()==2){
            ViewHolder finalVh = vh;
            vh.tv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ContactAdapter","....onClick="+uid+"     ="+mobile);
                    Api.addFriends(uid, mobile, new ApiResponseHandler(context) {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {
                            finalVh.tv_add.setBackgroundResource(R.drawable.text_gray);
                            finalVh.tv_add.setFocusable(false);
                            list.get(position).setIs_friends(1);
                            ToastUtils.showShort(phoneContactActivity, apiResponse.getMessage());
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            super.onFailure(errMessage);
                        }
                    });
                }
            });
        }

        return convertView;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    public List<MobileUserBean> getList() {
        return list;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    private class ViewHolder {
        private TextView tv_name, tv_number, tv_add, tvLetter;
        private RoundedImageView image;
    }

}
