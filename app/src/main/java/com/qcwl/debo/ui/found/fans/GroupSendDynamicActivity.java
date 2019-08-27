package com.qcwl.debo.ui.found.fans;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class GroupSendDynamicActivity extends BaseActivity implements FansContract.View {

    @Bind(R.id.edit_title)
    EditText editTitle;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.img_delete)
    ImageView imgDelete;
    @Bind(R.id.btn_publish)
    Button btnPublish;
    private FansPresenter presenter = null;

    private Context context;

    private List<String> imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_send_dynamic);
        ButterKnife.bind(this);
        context = this;
        initTitleBar();

        initView();

        presenter = new FansPresenter(this);
    }

    private void initView() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPermissions.requestPermissions(GroupSendDynamicActivity.this, CODE_PHOTO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgs != null) {
                    imgs.clear();
                }
                imageView.setImageResource(R.mipmap.btn_add_img);
                imgDelete.setVisibility(View.GONE);
                imageView.setClickable(true);
            }
        });
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    ToastUtils.showShort(context, "动态名称不能为空");
                    return;
                }
                String content = editContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort(context, "动态内容不能为空");
                    return;
                }
                if (imgs == null || imgs.size() == 0) {
                    ToastUtils.showShort(context, "请选择一张图片");
                    return;
                }
                presenter.sendGroupMessage(GroupSendDynamicActivity.this,
                        sp.getString("uid"), 1, title, content, imgs.get(0));
            }
        });
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("群发动态")
                .setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static final int REQUEST_IMAGE = 199;
    public static final int CODE_PHOTO = 200;

    @PermissionGrant(CODE_PHOTO)
    public void requestPermissionSuccess() {
        MultiImageSelector.create()
                .showCamera(true) // show camera or not. true by default
                //.count(canSelectCount) // max select image size, 9 by default. used width #.multi()
                .single() // single mode
                //.multi() // multi mode, default mode;
                //.origin(imgs) // original select data set, used width #.multi()
                .start(GroupSendDynamicActivity.this, REQUEST_IMAGE);
    }

    @PermissionDenied(CODE_PHOTO)
    public void requestPermissionFailed() {
        Toast.makeText(this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE:
                    if (data == null) {
                        return;
                    }
                    imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (imgs == null || imgs.size() == 0) {
                        return;
                    }
                    ImgUtil.load(this, imgs.get(0), imageView);
                    imgDelete.setVisibility(View.VISIBLE);
                    imageView.setClickable(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void doSuccess(int type, Object object) {
        finish();
    }

    @Override
    public void doFailure(int code) {

    }
}
