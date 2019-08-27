package com.qcwl.debo.ui.found.fans;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.circle.Luban;
import com.qcwl.debo.ui.circle.MultiImageAdapter;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class PublishFansDynamicActivity extends BaseActivity {

    private static final String TAG = "PublishFansDynamicActiv";

    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.grid_view)
    GridView gridView;
    @Bind(R.id.checkbox_friends)
    CheckBox checkboxFriends;
    @Bind(R.id.checkbox_contacts)
    CheckBox checkboxContacts;

    private final int REQUEST_IMAGE = 100;
    @Bind(R.id.image_thubm)
    ImageView imageThubm;
    @Bind(R.id.layout_video)
    RelativeLayout layoutVideo;
    private ArrayList<String> imgs = new ArrayList<>();
    private int canSelectCount = 9;
    private MultiImageAdapter adapter;

    private List<String> items = null;

    private String uid = "";//"34";

    private int type = 0;

    private ProgressDialog progressDialog;

    private int publishType = 0;//0图片  1视频

    private String videoPath, thumbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_fans_dynamic);
        ButterKnife.bind(this);
        initTitleBar();

        if (getIntent() == null) {
            return;
        }
        publishType = getIntent().getIntExtra("publishType", 0);
        switch (publishType) {
            case 0:
                gridView.setVisibility(View.VISIBLE);
                layoutVideo.setVisibility(View.GONE);

                items = new ArrayList<>();
                items.add("");
                adapter = new MultiImageAdapter(this, items);
                gridView.setAdapter(adapter);
                break;
            case 1:
                gridView.setVisibility(View.GONE);
                layoutVideo.setVisibility(View.VISIBLE);
                videoPath = getIntent().getStringExtra("videoPath");
                thumbPath = getIntent().getStringExtra("thumbPath");
                ImgUtil.load(this, thumbPath, imageThubm);
                Log.e(TAG, "onCreate: videoPath=" + videoPath);
                Log.e(TAG, "onCreate: thumbPath=" + thumbPath);
                break;
            default:
                break;
        }

        uid = SPUtil.getInstance(this).getString("uid");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发布中……");
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("发布动态").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        })
                .setTextRight("发布")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        publishInfo();
                    }
                });
    }

    private void publishInfo() {
        if (!checkboxFriends.isChecked() && !checkboxContacts.isChecked()) {
            type = 4;
        }
        if (checkboxFriends.isChecked() && !checkboxContacts.isChecked()) {
            type = 5;
        }
        if (!checkboxFriends.isChecked() && checkboxContacts.isChecked()) {
            type = 6;
        }
        if (checkboxFriends.isChecked() && checkboxContacts.isChecked()) {
            type = 7;
        }
        progressDialog.show();
        String content = editContent.getText().toString().trim();
        List<File> files = new ArrayList<>();
        if (publishType == 0) {
            if (imgs != null && imgs.size() > 0) {
                for (String img : imgs) {
                    files.add(Luban.get(this).launch(new File(img)));
                }
            }
        } else if (publishType == 1) {
            files.add(new File(thumbPath));//第一帧图片
            files.add(new File(videoPath));//视频路径
        }
        Api.publishFansDynamicInfo(uid, publishType, type, content, files, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (apiResponse.getCode() == 0) {
                    KeyBoardUtils.closeKeybord(editContent, PublishFansDynamicActivity.this);
                    Toast.makeText(PublishFansDynamicActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(PublishFansDynamicActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void selectPictrue() {
        canSelectCount = 9 - items.size() + 1;
        MPermissions.requestPermissions(this, CODE_PHOTO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    if ("".equals(items.get(items.size() - 1))) {
                        items.remove(items.size() - 1);
                    }
                    items.addAll(imgs);
                    if (items.size() < 9) {
                        items.add("");
                    }
                    adapter.notifyDataSetChanged();
                    setGridViewHeight(items.size());
                default:
                    break;
            }
        }
    }

    int h = 72;

    public void setGridViewHeight(int size) {
        int height = 0;
        if (size <= 4) {
            height = h;
        } else if (size <= 8) {
            height = h * 2 + 10;
        } else {
            height = h * 3 + 10 * 2;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp2px(height));
        params.leftMargin = dp2px(20);
        params.rightMargin = dp2px(20);
        params.topMargin = dp2px(20);
        params.bottomMargin = dp2px(30);
        gridView.setLayoutParams(params);
    }

    private int dp2px(int dpval) {
        return ScreenUtils.dp2px(this, dpval);
    }

    public static final int CODE_PHOTO = 200;

    @PermissionGrant(CODE_PHOTO)
    public void requestPermissionSuccess() {
        MultiImageSelector.create()
                .showCamera(true) // show camera or not. true by default
                .count(canSelectCount) // max select image size, 9 by default. used width #.multi()
                //.single() // single mode
                .multi() // multi mode, default mode;
//                .origin(imgs) // original select data set, used width #.multi()
                .start(this, REQUEST_IMAGE);
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
}
