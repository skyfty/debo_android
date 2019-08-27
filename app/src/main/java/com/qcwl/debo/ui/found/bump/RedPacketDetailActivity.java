package com.qcwl.debo.ui.found.bump;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.LargeImageActivity;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.ui.found.near.NearActivity;
import com.qcwl.debo.utils.SPUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
/**红包详情*/
public class RedPacketDetailActivity extends BaseActivity {

    @Bind(R.id.image_view)
    CircleImageView imagePhoto;
    @Bind(R.id.text_name)
    TextView textName;
    @Bind(R.id.text_money)
    TextView textMoney;
    @Bind(R.id.text_unit)
    TextView textUnit;
    @Bind(R.id.text_ad_title)
    TextView textAdTitle;
    @Bind(R.id.text_ad_link)
    TextView textAdLink;
    @Bind(R.id.text_ad_content)
    TextView textAdContent;
    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.image_back)
    ImageView imageBack;
    @Bind(R.id.layout_title)
    LinearLayout layoutTitle;
    @Bind(R.id.layout_image)
    LinearLayout layoutImage;
    private Context instance = null;

    private RedPacketBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packet_detail);
        ButterKnife.bind(this);
        instance = this;
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.getStatusHeight(instance)));
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent() == null) {
            return;
        }

        bean = (RedPacketBean) getIntent().getSerializableExtra("bean");
        loadData();
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RedPacketDetailActivity.this, ContactsContentActivity.class)
                        .putExtra("my_mobile", SPUtil.getInstance(RedPacketDetailActivity.this).getString("phone"))
                        .putExtra("mobile", ""+bean.getMobile())
                        .putExtra("type", "1"));
            }
        });
    }

    private void loadData() {
        ImgUtil.loadHead(this, bean.getAvatar(), imagePhoto);
        textName.setText(bean.getUser_nickname());
        textMoney.setText(bean.getMoney());
        textUnit.setText("元");
        if (TextUtils.isEmpty(bean.getTitle())) {
            textAdTitle.setVisibility(View.GONE);
        } else {
            textAdTitle.setText(bean.getTitle());
            textAdTitle.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(bean.getAd_content())) {
            textAdContent.setVisibility(View.GONE);
        } else {
            textAdContent.setText(bean.getAd_content());
            textAdContent.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(bean.getAd_link())) {
            textAdLink.setVisibility(View.GONE);
        } else {
            textAdLink.setText(bean.getAd_link());
//            textAdLink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
            textAdLink.setVisibility(View.VISIBLE);
        }
        List<String> list = bean.getAd_images();
        if (list == null || list.size() == 0) {
            layoutImage.setVisibility(View.GONE);
            return;
        }
        layoutImage.setVisibility(View.VISIBLE);
        ImageView imageView = null;
        LinearLayout.LayoutParams params = null;
        final List<String> imgs = new ArrayList<>();
        if (list.size() <= 3) {
            imgs.addAll(list);
        } else {
            imgs.addAll(list.subList(0, 3));
        }
        for (int i = 0; i < imgs.size(); i++) {
            final int j = i;
            imageView = new ImageView(this);
            params = new LinearLayout.LayoutParams(0, dp2px(100));
            params.weight = 1;
            params.leftMargin = dp2px(10);
            params.topMargin = dp2px(10);
            params.bottomMargin = dp2px(10);
            if (i == 2) {
                params.rightMargin = dp2px(10);
            }
            imageView.setLayoutParams(params);
            ImgUtil.load(this, imgs.get(i), imageView);
            layoutImage.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RedPacketDetailActivity.this,
                            LargeImageActivity.class)
                            .putExtra("position", j)
                            .putExtra("images", (Serializable) imgs));
                }
            });
        }
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }
}
