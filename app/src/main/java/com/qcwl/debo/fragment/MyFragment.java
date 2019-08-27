package com.qcwl.debo.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gigamole.library.ShadowLayout;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.guide.Guide;
import com.qcwl.debo.guide.GuideBuilder;
import com.qcwl.debo.guide.MyGuideComponent;
import com.qcwl.debo.model.RenMaiZongLanBean;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.bump.BumpActivity;
import com.qcwl.debo.ui.my.BumpAndTouchActivity;
import com.qcwl.debo.ui.my.MyAlbumActivity;
import com.qcwl.debo.ui.my.MyInfoActivity;
import com.qcwl.debo.ui.my.MyQrActivity;
import com.qcwl.debo.ui.my.MyWalletActivity;
import com.qcwl.debo.ui.my.RenMaiSettingActivity;
import com.qcwl.debo.ui.my.SettingActivity;
import com.qcwl.debo.ui.my.ad.AdvertisementListActivity;
import com.qcwl.debo.ui.my.partner.PartnerActivity;
import com.qcwl.debo.ui.my.sheet.FinancialSheetActivity;
import com.qcwl.debo.ui.my.wallet.TradingRecordActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.zxing.android.CaptureActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;

import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * Created by Administrator on 2017/7/6.
 */

public class MyFragment extends Fragment implements View.OnClickListener, MyInfoPresenterInf {
    private View view;
    private SPUtil sp;
    private LinearLayout setting, iv_qr, my_info, wallet, my_photo, star_ll, pengyipeng_ll, zhuangyizhuang_ll, layoutSheet, layoutPartner, tv_set;
    private RoundedImageView touxiang;
    private ImageView imageBg;
    private TextView nick, number, tv_advertisement, total, yesterday, direct;
    private ImageView status;
    private MyInfoPresenter myInfoPresenter;
    private Guide guide;
    private ShadowLayout sl, s2, s3;
    private final int PESSION_CODE = 1;
    private String[] permissions;
    private int flag;
    private String title;
    private String tip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my, null);
        initView();
        monitor();
        return view;
    }

    private void initView() {
        myInfoPresenter = new MyInfoPresenter(this);
        sp = SPUtil.getInstance(getActivity());
        setting = (LinearLayout) view.findViewById(R.id.setting);
        iv_qr = (LinearLayout) view.findViewById(R.id.iv_qr);
        my_info = (LinearLayout) view.findViewById(R.id.my_info);
        wallet = (LinearLayout) view.findViewById(R.id.wallet);
        touxiang = (RoundedImageView) view.findViewById(R.id.touxiang);
        imageBg = (ImageView) view.findViewById(R.id.image_bg);
        nick = (TextView) view.findViewById(R.id.nick);
        number = (TextView) view.findViewById(R.id.number);
        my_photo = (LinearLayout) view.findViewById(R.id.my_photo);
        star_ll = (LinearLayout) view.findViewById(R.id.star_ll);
        pengyipeng_ll = (LinearLayout) view.findViewById(R.id.pengyipeng_ll);
        zhuangyizhuang_ll = (LinearLayout) view.findViewById(R.id.zhuangyizhuang_ll);
        tv_advertisement = (TextView) view.findViewById(R.id.tv_advertisement);
        total = (TextView) view.findViewById(R.id.total);
        yesterday = (TextView) view.findViewById(R.id.yesterday);
        direct = (TextView) view.findViewById(R.id.direct);
        status = (ImageView) view.findViewById(R.id.status);
        layoutSheet = (LinearLayout) view.findViewById(R.id.layout_sheet);
        layoutPartner = (LinearLayout) view.findViewById(R.id.layout_partner);
        tv_set = (LinearLayout) view.findViewById(R.id.tv_set);
        sl = (ShadowLayout) view.findViewById(R.id.sl);
        s2 = (ShadowLayout) view.findViewById(R.id.s2);
        s3 = (ShadowLayout) view.findViewById(R.id.s3);
        myInfoPresenter.getRenMaiInfo(getActivity(), sp.getString("uid"));
        if ("0".equals(sp.getString("is_con_set_right"))) {
            tv_set.setVisibility(View.GONE);
        }

        sl.setIsShadowed(true);
        sl.setShadowAngle(45);//阴影角度
        sl.setShadowRadius(2);//阴影半径
        sl.setShadowDistance(3);//阴影距离
        sl.setShadowColor(R.color.shadowcolor);//阴影颜色

        s2.setIsShadowed(true);
        s2.setShadowAngle(45);//阴影角度
        s2.setShadowRadius(2);//阴影半径
        s2.setShadowDistance(3);//阴影距离
        s2.setShadowColor(R.color.shadowcolor);//阴影颜色

        s3.setIsShadowed(true);
        s3.setShadowAngle(45);//阴影角度
        s3.setShadowRadius(2);//阴影半径
        s3.setShadowDistance(3);//阴影距离
        s3.setShadowColor(R.color.shadowcolor);//阴影颜色
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(sp.getString("headsmall"))) {
            ImgUtil.setGlideHead(getActivity(), R.mipmap.head, touxiang);
            // Glide.with(getActivity()).load(R.mipmap.head).into(touxiang);
            ImgUtil.setGlideTransformHead(getActivity(), R.mipmap.debo_logo, 15, imageBg);
            //Glide.with(getActivity()).load(R.mipmap.debo_logo).bitmapTransform(new BlurTransformation(getActivity(), 15)).into(imageBg);

        } else {
            ImgUtil.setGlideHead(getActivity(), sp.getString("headsmall"), touxiang);
            //Glide.with(getActivity()).load(sp.getString("headsmall")).into(touxiang);
            //radius的取值范围是1-25，radius越大，模糊度越高。
            ImgUtil.setGlideTransformHead(getActivity(), sp.getString("headsmall"), 15, imageBg);
            //Glide.with(getActivity()).load(sp.getString("headsmall")).bitmapTransform(new BlurTransformation(getActivity(), 15)).into(imageBg);
        }

        nick.setText(sp.getString("name"));
        number.setText("帐号 : " + sp.getString("phone"));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!TextUtils.isEmpty(SPUtil.getInstance(getActivity()).getString("my_guide"))) {
                return;
            }
            tv_advertisement.post(new Runnable() {
                @Override
                public void run() {
                    SPUtil.getInstance(getActivity()).setString("my_guide", "1");
                    showGuideView(tv_advertisement);
                }
            });
        }
    }

    private void showGuideView(final TextView view) {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(view)
                .setAlpha(180)
                .setHighTargetCorner(20)
                .setHighTargetPadding(5)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {

            }
        });

        builder.addComponent(new MyGuideComponent());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(getActivity());
    }

    private void monitor() {
        setting.setOnClickListener(this);
        iv_qr.setOnClickListener(this);
        my_info.setOnClickListener(this);
        wallet.setOnClickListener(this);
        my_photo.setOnClickListener(this);
        star_ll.setOnClickListener(this);
        pengyipeng_ll.setOnClickListener(this);
        zhuangyizhuang_ll.setOnClickListener(this);
        layoutSheet.setOnClickListener(this);
        layoutPartner.setOnClickListener(this);
        tv_set.setOnClickListener(this);
        view.findViewById(R.id.text_advertisement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdvertisementListActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                StatService.onEvent(getActivity(), "设置", "进入设置页面");
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.iv_qr:
                StatService.onEvent(getActivity(), "二维码", "进入二维码页面");
                startActivity(new Intent(getActivity(), MyQrActivity.class));
                break;
            case R.id.my_info:
                StatService.onEvent(getActivity(), "个人信息", "修改个人信息");
                Intent intent1 = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.wallet:
                StatService.onEvent(getActivity(), "钱包", "钱包页面");
                startActivity(new Intent(getActivity(), MyWalletActivity.class));
                break;
            case R.id.my_photo:
                StatService.onEvent(getActivity(), "我得相册", "我得相册");
                //startActivity(new Intent(getActivity(), MyPhotoActivity.class).putExtra("f_uid", sp.getString("uid")));
                startActivity(new Intent(getActivity(), MyAlbumActivity.class).putExtra("f_uid", sp.getString("uid")));
                break;
            case R.id.star_ll:
//                Intent intent2 = new Intent(getActivity(), WishingStarActivity.class);
//                startActivity(intent2);
                StatService.onEvent(getActivity(), "许愿星", "发布许愿星");
                startActivityForResult(new Intent(getActivity(), BumpAndTouchActivity.class).putExtra("type", 3),1);
                break;
            case R.id.pengyipeng_ll:
               /* flag = 2;
                title = "碰一碰";
                tip = "touch";
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                MPermissions.requestPermissions(MyFragment.this, PESSION_CODE, permissions);*/
                StatService.onEvent(getActivity(), "碰一碰", "发布碰一碰");
                startActivity(new Intent(getActivity(), BumpAndTouchActivity.class).putExtra("type", 1));
                break;
            case R.id.zhuangyizhuang_ll:
                //startActivity(new Intent(getActivity(), BumpActivity.class));
                StatService.onEvent(getActivity(), "撞一幢", "发布撞一幢");
                startActivity(new Intent(getActivity(), BumpAndTouchActivity.class).putExtra("type", 2));
                break;
            case R.id.layout_sheet:
                //StatService.onEvent(getActivity(), "财务报表", "查看财务报表");
                //startActivity(new Intent(getActivity(), FinancialSheetActivity.class));
                startActivity(new Intent(getActivity(), TradingRecordActivity.class));
                break;
            case R.id.layout_partner:
                StatService.onEvent(getActivity(), "合伙人", "开通合伙人");
                startActivity(new Intent(getActivity(), PartnerActivity.class));
                break;
            case R.id.tv_set:
                StatService.onEvent(getActivity(), "人脉设置", "点击了人脉设置");
                startActivity(new Intent(getActivity(), RenMaiSettingActivity.class));
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            RenMaiZongLanBean rb = (RenMaiZongLanBean) o;
            total.setText(rb.getTotal_con() + "人");
            yesterday.setText(rb.getYesterday_con() + "人");
            direct.setText(rb.getDirect_con() + "人");
            if ("-1".equals(rb.getStatus())) {
                status.setVisibility(View.VISIBLE);
                status.setImageResource(R.mipmap.falling);
            } else if ("0".equals(rb.getStatus())) {
                status.setVisibility(View.GONE);
            } else if ("1".equals(rb.getStatus())) {
                status.setVisibility(View.VISIBLE);
                status.setImageResource(R.mipmap.rise);
            }
        }
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        startActivity(new Intent(getActivity(), CaptureActivity.class).putExtra("flag", flag).putExtra("tip", tip).putExtra("title", title));
    }

}
