package com.qcwl.debo.ui.circle;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.contact.CreateGroupActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.widget.sortlistview.SortGroupMemberAdapter;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class PublishCircleActivity extends BaseActivity {//AppCompatActivity


    private static final String TAG = "PublishCircleActivity";

    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.grid_view)
    GridView gridView;
    /*@Bind(R.id.checkbox_friends)
    CheckBox checkboxFriends;
    @Bind(R.id.checkbox_contacts)
    CheckBox checkboxContacts;*/
    @Bind(R.id.layout_top)
    LinearLayout layoutTitle;

    @Bind(R.id.image_thubm)
    ImageView imageThubm;
    @Bind(R.id.layout_video)
    RelativeLayout layoutVideo;

    @Bind(R.id.position)
    TextView tvPosition;
    @Bind(R.id.tv_isvisible)
    TextView tvIsvisible;
    /*@Bind(R.id.tv_call_name)
    TextView tvCallName;*/
    @Bind(R.id.tv_position)
    TextView tv_position;
    @Bind(R.id.relative_who_visible)
    RelativeLayout relativeVisible;
    @Bind(R.id.relative_call)
    RelativeLayout relativeCall;



    private final int REQUEST_IMAGE = 100;
    private final int REQUEST_POSITION = 101;
    private final int REQUEST_WHO_VISIBLE = 102;
    private final int REQUEST_CALL = 103;
    private ArrayList<String> imgs = new ArrayList<>();
    private int canSelectCount = 9;
    private MultiImageAdapter adapter;

    private List<String> items = null;

    private String uid = "";//"34";

    private int type = 0;

    private ProgressDialog progressDialog;

    private int publishType = 0;//0图片  1视频

    private String videoPath, thumbPath;
    private String lat,lng;
    private int moments_type;
    private int type1 = 1;
    private String position;
    private String idCall;
    private String[] s;
    private String[] ids;
    private String[] renMaiIdss;
    private String[] contactIdss;

    private String[] renMaiCall;
    private String[] contactCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_circle2);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        ButterKnife.bind(this);
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
//        StatusBarUtil.setTransparentForImageView(this, null);
        uid = SPUtil.getInstance(this).getString("uid");
        int statusHeight = ScreenUtils.getStatusHeight(this);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        layoutTitle.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        layoutTitle.getBackground().setAlpha(255);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发布中……");
    }

    @OnClick({R.id.image_back, R.id.text_send,R.id.relative_who_visible,R.id.position,R.id.relative_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.text_send:
                publishInfo();
                break;
            case R.id.relative_who_visible:
               startActivityForResult(new Intent(PublishCircleActivity.this,WhereCircleActivity.class),REQUEST_WHO_VISIBLE);
                break;
            case R.id.position:
                Log.i(TAG,"..........进入位置界面");
                startActivityForResult(new Intent(PublishCircleActivity.this,PositionActivity.class),REQUEST_POSITION);
                break;
            case R.id.relative_call:
                Log.i(TAG,"..........@好友");
                startActivityForResult(new Intent(PublishCircleActivity.this,CreateGroupActivity.class).putExtra("type","publishcircle"),REQUEST_CALL);

                break;
        }
    }

    private void publishInfo() {
      /*  if (checkboxFriends.isChecked() && !checkboxContacts.isChecked()) {
            type = 1;
        }
        if (!checkboxFriends.isChecked() && checkboxContacts.isChecked()) {
            type = 2;
        }
        if (checkboxFriends.isChecked() && checkboxContacts.isChecked()) {
            type = 3;
        }
        if (!checkboxFriends.isChecked() && !checkboxContacts.isChecked()) {
            Toast.makeText(this, "朋友圈、人脉圈至少选择一个", Toast.LENGTH_SHORT).show();
            return;
        }*/
      if(type1 == 0){
          Toast.makeText(this, "朋友圈、人脉圈至少选择一个", Toast.LENGTH_SHORT).show();
          return;
      }
        progressDialog.show();
        String content = editContent.getText().toString().trim();
        List<File> files = new ArrayList<>();
        if (publishType == 0) {
            if (items != null && items.size() > 0) {
                for (String img : items) {
                    files.add(Luban.get(this).launch(new File(img)));
                }
            }
        } else if (publishType == 1) {
            files.add(new File(thumbPath));//第一帧图片
            files.add(new File(videoPath));//视频路径
        }
        Log.i(TAG,".........publishCircleInfo="+publishType+"      "+type1+"     "+moments_type+"     "+renMaiCall+"      "+renMaiIdss+"　　　"+contactIdss);
        Api.publishCircleInfo(uid,position,lat,lng, publishType, type1,moments_type, content, files,contactCall,renMaiCall,renMaiIdss,contactIdss, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (apiResponse.getCode() == 0) {
                    KeyBoardUtils.closeKeybord(editContent, PublishCircleActivity.this);
                    Toast.makeText(PublishCircleActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.i(TAG,".......................="+apiResponse.getMessage());
                    Toast.makeText(PublishCircleActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
               /* case REQUEST_POSITION:
                    String position = data.getStringExtra("position");
                    lat = data.getStringExtra("lat");
                    lng = data.getStringExtra("lng");
                    tv_position.setText(""+position);
                    break;*/
                default:
                    break;
            }
        } else if (resultCode == REQUEST_POSITION) {
            position = data.getStringExtra("position");
            lat = data.getStringExtra("lat");
            lng = data.getStringExtra("lng");
            tv_position.setText("" + position);
            Log.i(TAG,".........REQUEST_POSITION="+position+"     "+lat+"     "+lng);
        } else if (resultCode == REQUEST_WHO_VISIBLE) {
            type1 =data.getIntExtra("type",0);
            moments_type = data.getIntExtra("moments_type",0);
            String contactIds = data.getStringExtra("contactIds");
            String renMaiIds = data.getStringExtra("renMaiIds");
            Log.i(TAG,".........REQUEST_WHO_VISIBLE="+type1+"     "+moments_type+"     "+contactIds+"　　　"+renMaiIds);
            if (moments_type == 1){
                tvIsvisible.setText("公开");
            }else if(moments_type == 2){
                tvIsvisible.setText("私密");
            }else if(moments_type == 3){
                tvIsvisible.setText("部分好友可见");
            }else if(moments_type == 4){
                tvIsvisible.setText("不给谁看");
            }
            if (moments_type == 3||moments_type == 4){
                if (type1 == 1){
                    if (contactIds!=null&&contactIds.length() != 0){
                        contactIdss = contactIds.split(",");
                    }
                }else if(type1 == 2){
                    if (renMaiIds!=null&&renMaiIds.length() != 0){
                        renMaiIdss = renMaiIds.split(",");
                    }
                }else if(type1 == 3){
                    if (contactIds!=null&&contactIds.length() != 0){
                        contactIdss = contactIds.split(",");
                    }
                    if (renMaiIds!=null&&renMaiIds.length() != 0){
                        renMaiIdss = renMaiIds.split(",");
                    }
                }
                Log.i(TAG,".........REQUEST_WHO_VISIBLE2="+renMaiIdss+"    "+contactIdss);
            }
        }else if (resultCode == REQUEST_CALL) {

            String contactcall = data.getStringExtra("contactIds");
            String renmaicall = data.getStringExtra("renMaiIds");
            if (contactcall != null&&contactcall.length() != 0){
                contactCall = contactcall.split(",");
            }
            if (renmaicall != null&&renmaicall.length() != 0){
                renMaiCall = renmaicall.split(",");
            }
            //idCall = data.getStringExtra("ids");
            Log.i(TAG,".........REQUEST_CALL="+contactcall+"    "+renmaicall);
            /*if (idCall!=null&&idCall.length() != 0){
                s = idCall.split(",");
                //Log.i(TAG,".........s="+s.toString()+"  "+s[0]+"   "+s[1]);
            }*/
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
