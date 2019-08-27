package com.qcwl.debo.widget.sortlistview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcwl.debo.R;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.ui.found.SelectActivity;

import java.util.List;


public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List<ContactsBean> list = null;
    private Context mContext;
    private int type;
    private ChangeState c;
    private ChangeStateWhere c1;
    private String typeString;
    private String addr = "";
    public SortGroupMemberAdapter(Context mContext, List<ContactsBean> list, String type) {
        this.mContext = mContext;
        this.list = list;
        this.typeString = type;
    }

    public void setListener(ChangeState c) {
        this.c = c;
    }
    public void setListener2(ChangeStateWhere c1) {
        this.c1 = c1;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ContactsBean> list) {
        Log.i("SortGroupMemberAdapter","..............updateListView="+list);
        if (list!=null) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final ContactsBean mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.social_item, null);

            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.touxiang = (ImageView) view.findViewById(R.id.touxiang);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.desc = (TextView) view.findViewById(R.id.desc);
            viewHolder.line = (View) view.findViewById(R.id.line);
            viewHolder.line1 = (View) view.findViewById(R.id.line1);

            viewHolder.layout = view.findViewById(R.id.social_item_ll);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvLetter.setOnClickListener(null);
        if (type == 1) {
            viewHolder.checkbox.setVisibility(View.VISIBLE);
            viewHolder.layout.setClickable(true);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = list.get(position).isCheck();
                    checkSetting(position, !isChecked);
                }
            });
        } else {
            viewHolder.layout.setClickable(false);
        }
        if (type == 2) {
            viewHolder.time.setVisibility(View.VISIBLE);
            viewHolder.desc.setVisibility(View.VISIBLE);
            viewHolder.time.setText(list.get(position).getPur_time());
            viewHolder.desc.setText("为你带来了" + list.get(position).getCon_fans_num() + "位粉丝");
        }
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
            viewHolder.line.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.line.setVisibility(View.GONE);
        }
        if (position == list.size() - 1) {
            viewHolder.line.setVisibility(View.GONE);
            viewHolder.line1.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(mContent.getUser_nickname())) {
            viewHolder.tvTitle.setText(this.list.get(position).getMobile());
        } else {
            viewHolder.tvTitle.setText(this.list.get(position).getUser_nickname());
        }
        if (TextUtils.isEmpty(list.get(position).getAvatar())) {
            viewHolder.touxiang.setImageResource(R.mipmap.head);
        } else {
            ImageLoader.getInstance().displayImage(list.get(position).getAvatar(), viewHolder.touxiang);
        }

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkSetting(position, isChecked);
            }
        });
        viewHolder.checkbox.setChecked(mContent.isCheck());

        return view;

    }

    private void checkSetting(int position, boolean isChecked) {
        int count = 0;
        String str = "";
        list.get(position).setCheck(isChecked);

        for (ContactsBean b : list) {
            if (b.isCheck()) {
                count++;
                if (TextUtils.isEmpty(str)) {
                    if (typeString!=null&&typeString.equals("publishcircle")||typeString.equals("wherecircle")) {
                        //发布朋友圈
                        str += b.getId();

                    } else {
                        str += b.getMobile();
                    }
                } else {
                    if (typeString!=null&&typeString.equals("publishcircle")||typeString.equals("wherecircle")) {
                        //发布朋友圈
                        str += "," + b.getId();
                    } else {
                        str += "," + b.getMobile();
                    }
                }
            }
        }
            if (typeString!=null&&typeString.equals("publishcircle")||typeString.equals("wherecircle")){
                    c1.getWhereCounts(addr,count);
                    c1.getWhereMember(addr,str);
            }else {
                c.getCounts(count);
                c.getMember(str);
            }
        notifyDataSetChanged();
        if (mContext instanceof SelectActivity) {
            Intent intent = new Intent();
            intent.putExtra("avatar", list.get(position).getAvatar());
            intent.putExtra("nickname", list.get(position).getUser_nickname());
            intent.putExtra("uid", list.get(position).getId());
            ((SelectActivity) mContext).setResult(Activity.RESULT_OK, intent);
            ((SelectActivity) mContext).finish();
        }
    }


    private class ViewHolder {
        private TextView tvLetter, time, desc;
        private TextView tvTitle;
        private ImageView touxiang;
        private View line;
        private View line1;
        private CheckBox checkbox;
        private View layout;
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

    public List<ContactsBean> getList() {
        return list;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public interface ChangeState {

        void getCounts(int count);

        void getMember(String member);

    }

    public interface ChangeStateWhere {

        void getWhereCounts(String key,int count);

        void getWhereMember(String key,String member);

    }

}