package com.qcwl.debo.ui.found.star;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShipActivity extends BaseActivity {

    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.image_view)
    CircleImageView imageView;
    @Bind(R.id.text_name)
    TextView textName;
    @Bind(R.id.text_sign)
    TextView textSign;
    @Bind(R.id.text_content)
    TextView textContent;

    private RedPacketBean packetBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.getStatusHeight(this)));
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        packetBean = (RedPacketBean) getIntent().getSerializableExtra("red_packet");
        ImgUtil.loadHead(this, packetBean.getAvatar(), imageView);
        textName.setText("" + packetBean.getUser_nickname());
        textSign.setText("" + packetBean.getSignature());
        textContent.setText("" + packetBean.getWishing_content());
    }

    @OnClick({R.id.image_back, R.id.btn_cancel, R.id.btn_reply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_cancel:
                putbackWishingStar();
                break;
            case R.id.btn_reply:
                startActivity(new Intent(this, ChatActivity.class).putExtra("userId", packetBean.getMobile()).putExtra("nickname", packetBean.getUser_nickname()));
                break;
        }
    }

    private void putbackWishingStar() {
        Api.putbackWishingStar("" + sp.getString("uid"), "" + packetBean.getStar_id(), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(ShipActivity.this, "放回成功");
                    finish();
                } else {
                    ToastUtils.showShort(ShipActivity.this, apiResponse.getMessage() + "");
                }
            }
        });
    }
}
