package com.qcwl.debo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qcwl.debo.R;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.SearchRenMaiBean;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.presenter.ContactListPresenter;
import com.qcwl.debo.presenterInf.ContactListPresenterInf;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import java.util.List;


public class SearchRenMaiAdapter extends BaseAdapter implements ContactListPresenterInf{
    private List<SearchRenMaiBean> list = null;
    private Context mContext;
    private int type;

    private ContactListPresenter contactListPresenter;
    public SearchRenMaiAdapter(Context mContext, List<SearchRenMaiBean> list) {
        this.mContext = mContext;
        this.list = list;
        contactListPresenter = new ContactListPresenter(this);
    }


    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SearchRenMaiBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SearchRenMaiBean> getList() {
        return list;
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
        final SearchRenMaiBean bean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.search_renmai_item, null);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.touxiang = (ImageView) view.findViewById(R.id.touxiang);
            viewHolder.desc = (TextView) view.findViewById(R.id.desc);
            viewHolder.invite_linear = (LinearLayout) view.findViewById(R.id.invite_linear);
            viewHolder.price_linear = (LinearLayout) view.findViewById(R.id.price_linear);
            viewHolder.price = (TextView) view.findViewById(R.id.price);
            viewHolder.iv_invite = (ImageView) view.findViewById(R.id.iv_invite);
            viewHolder.tv_tip = (TextView) view.findViewById(R.id.tv_tip);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (TextUtils.isEmpty(bean.getAvatar())) {
            ImgUtil.setGlideHead(mContext,R.mipmap.head,viewHolder.touxiang);
            //Glide.with(mContext).load(R.mipmap.head).into(viewHolder.touxiang);
        } else {
            ImgUtil.setGlideHead(mContext,bean.getAvatar(),viewHolder.touxiang);
          //  Glide.with(mContext).load(bean.getAvatar()).into(viewHolder.touxiang);
        }
        if (TextUtils.isEmpty(bean.getUser_nickname())) {
            viewHolder.name.setText(bean.getMobile());
        } else {
            viewHolder.name.setText(bean.getUser_nickname());
        }

        viewHolder.desc.setText("个性签名: " + bean.getSignature());

        if (type == 1) {
            viewHolder.invite_linear.setVisibility(View.VISIBLE);
            if("0".equals(bean.getIs_friend())){
                viewHolder.tv_tip.setTextColor(mContext.getResources().getColor(R.color.font_select));
                viewHolder.iv_invite.setImageResource(R.mipmap.icon_invite);
            }else {
                viewHolder.tv_tip.setTextColor(mContext.getResources().getColor(R.color.font_normal));
                viewHolder.iv_invite.setImageResource(R.mipmap.icon_not_invite);
            }
        } else if (type == 2) {
            viewHolder.price_linear.setVisibility(View.VISIBLE);
            viewHolder.price.setText("¥ " + bean.getPur_price());
            viewHolder.price_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayDialog.createDialog(mContext, "贵族人脉", 2, bean.getId(), bean.getPur_price(),"").show();
                }
            });
        }
        viewHolder.invite_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(mContext, "已发送邀请");
                if(bean.getMobile().equals(SPUtil.getInstance(mContext))){
                    ToastUtils.showShort(mContext,"不能邀请自己");
                    return;
                }
                if("1".equals(bean.getIs_friend())){
                    return;
                }
                contactListPresenter.sendInvitation(mContext, SPUtil.getInstance(mContext).getString("phone"),bean.getMobile());
            }
        });

        return view;

    }

    @Override
    public void updateDataByFilter(List<ContactsBean> filterDateList) {

    }

    @Override
    public void getResult(int code, String message, Object o) {
        ToastUtils.showShort(mContext,message);
    }


    private class ViewHolder {
        private LinearLayout invite_linear, price_linear;
        private TextView name,desc,price,tv_tip;
        private ImageView touxiang,iv_invite;
    }

}