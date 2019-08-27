package com.qcwl.debo.ui.my;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.hyphenate.easeui.view.RoundedImageView;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.AreaBean;
import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.GetJsonDataUtil;
import com.qcwl.debo.utils.PicUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.qcwl.debo.utils.PicUtil.CROP_IMAGE;
import static com.qcwl.debo.utils.PicUtil.REQUEST_IMAGE;

/**
 * Created by Administrator on 2017/7/17.
 */

public class MyInfoActivity extends BaseActivity implements View.OnClickListener, MyInfoPresenterInf {
    private LinearLayout head_ll, sex_ll, area_ll, nickname_ll, qr_linear, address_ll, sign_ll;
    private TextView sex_tv, area_tv, nickname, number, address, sign;
    private ImageView qr_iv;
    private RoundedImageView head_iv;
    private String[] permissions;
    private final int PESSION_CODE = 1;
    private List<String> imgs = null;
    private String newFileName;
    private File file;
    private ArrayList<AreaBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    ArrayList<String> seArrayLists = new ArrayList<>();
    private int flag;
    private Handler handler;
    private OptionsPickerView pvOptions;
    private MyInfoPresenter myInfoPresenter;
    private String province;
    private String city;
    private String area;

    private EaseSwitchButton switchButton;
    private TextView number2;
    private LinearLayout debo_ll;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.my_info);
        initTitleBar();
        initView();
        monitor();
        locationInfoIsOpen();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("个人信息")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void submit(int index) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", sp.getString("uid"));
        if (index == 1) {
            String sex = "0";
            if ("保密".equals(sex_tv.getText().toString())) {
                sex = "3";
            } else if ("男".equals(sex_tv.getText().toString())) {
                sex = "1";
            } else if ("女".equals(sex_tv.getText().toString())) {
                sex = "2";
            }
            params.put("sex", sex);
        } else if (index == 2) {
            params.put("province", province);
            params.put("city", city);
            params.put("area", area);
        }
        myInfoPresenter.editUserInformation(this, params, null);

        //myInfoPresenter.editUserInformation(MyInfoActivity.this, sp.getString("uid"), nickname.getText().toString(), address.getText().toString(), sex, province, city, area, sign.getText().toString(), file);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this, "启动个人信息页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, "结束个人信息页面");
    }

    private void initView() {
        handler = new Handler();
        myInfoPresenter = new MyInfoPresenter(this);
        seArrayLists.add("保密");
        seArrayLists.add("男");
        seArrayLists.add("女");
        head_ll = (LinearLayout) findViewById(R.id.head_ll);
        nickname = (TextView) findViewById(R.id.nickname);
        nickname_ll = (LinearLayout) findViewById(R.id.nickname_ll);
        address_ll = (LinearLayout) findViewById(R.id.address_ll);
        sign_ll = (LinearLayout) findViewById(R.id.sign_ll);
        number = (TextView) findViewById(R.id.number);
        number2 = (TextView) findViewById(R.id.number2);
        qr_iv = (ImageView) findViewById(R.id.qr_iv);
        head_iv = (RoundedImageView) findViewById(R.id.head_iv);
        sex_ll = (LinearLayout) findViewById(R.id.sex_ll);
        debo_ll = (LinearLayout) findViewById(R.id.debo_ll);
        qr_linear = (LinearLayout) findViewById(R.id.qr_linear);
        sex_tv = (TextView) findViewById(R.id.sex_tv);
        area_ll = (LinearLayout) findViewById(R.id.area_ll);
        area_tv = (TextView) findViewById(R.id.area_tv);
        address = (TextView) findViewById(R.id.address);
        sign = (TextView) findViewById(R.id.sign);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_button);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initJsonData();
            }
        }, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MyInfoActivity"," ========="+sp.getString("phone"));
        myInfoPresenter.getUserInformation1(this, sp.getString("phone"));
        StatService.onResume(this);
    }

    private void monitor() {
        head_ll.setOnClickListener(this);
        nickname_ll.setOnClickListener(this);
        address_ll.setOnClickListener(this);
        sign_ll.setOnClickListener(this);
        sex_ll.setOnClickListener(this);
        area_ll.setOnClickListener(this);
        qr_linear.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        debo_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_ll:
                requestPermission();
                break;
            case R.id.nickname_ll:
                Intent modify_nick = new Intent(this, EditNickNameActivity.class);
                modify_nick.putExtra("nick", nickname.getText().toString()).putExtra("title", "修改昵称").putExtra("type", 1);
//                startActivityForResult(modify_nick, 1001);
                startActivity(modify_nick);
                break;
            case R.id.address_ll:
                Intent modify_address = new Intent(this, EditNickNameActivity.class);
                modify_address.putExtra("address", address.getText().toString()).putExtra("title", "修改地址").putExtra("type", 2);
//                startActivityForResult(modify_address, 1002);
                startActivity(modify_address);
                break;
            case R.id.sign_ll:
                Intent modify_sign = new Intent(this, EditNickNameActivity.class);
                modify_sign.putExtra("sign", sign.getText().toString()).putExtra("title", "修改签名").putExtra("type", 3);
//                startActivityForResult(modify_sign, 1003);
                startActivity(modify_sign);
                break;
            case R.id.debo_ll:
                Intent modify_debo = new Intent(this, EditNickNameActivity.class);
                modify_debo.putExtra("debo", number2.getText().toString()).putExtra("title", "修改嘚啵号").putExtra("type", 4);
//                startActivityForResult(modify_sign, 1003);
                startActivity(modify_debo);
                break;
            case R.id.qr_linear:
                startActivity(new Intent(this, MyQrActivity.class));
                break;
            case R.id.sex_ll:
                flag = 1;
                chooseArea();
                break;
            case R.id.area_ll:
                flag = 2;
                chooseArea();
                break;
            case R.id.switch_button://开启位置信息
                openLocationInfo();
                break;
        }
    }

    private void openLocationInfo() {
        Api.setLocationInfoIsOpen(sp.getString("uid"), sp.getString("phone"),
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            if (switchButton.isSwitchOpen()) {
                                switchButton.closeSwitch();
                            } else {
                                switchButton.openSwitch();
                            }
                        } else {
                            Toast.makeText(MyInfoActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void locationInfoIsOpen() {
        Api.locationInfoIsOpen(sp.getString("uid"), sp.getString("phone"),
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        try {
                            if (apiResponse.getCode() == 0) {
                                //location_is_open 0 关闭；1 开启
                                JSONObject object = new JSONObject(apiResponse.getData());
                                int is_open = object.optInt("location_is_open");
                                if (is_open == 0) {
                                    switchButton.closeSwitch();
                                } else if (is_open == 1) {
                                    switchButton.openSwitch();
                                }
                            } else {
                                Toast.makeText(MyInfoActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void requestPermission() {
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        MPermissions.requestPermissions(this, PESSION_CODE, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        MultiImageSelector.create()
                .showCamera(true) // show camera or not. true by default
//                .count(1) // max select image size, 9 by default. used width #.multi()
                .single() // single mode
                //.multi() // multi mode, default mode;
                //.origin(imgs) // original select data set, used width #.multi()
                .start(this, REQUEST_IMAGE);
    }

    @PermissionDenied(PESSION_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(this, "权限被禁止，请您去设置界面开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE:

                if (data == null) {
                    return;
                }
                imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (imgs == null || imgs.size() == 0) {
                    return;
                }
                Uri uri = PicUtil.getImageContentUri(this, imgs.get(0));
                PicUtil.startPhotoZoom(uri, MyInfoActivity.this);
                break;
            case CROP_IMAGE:
                // 保存剪切之后的图像
//                    showPhoto(cropImage);
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        // 用户点了取消
                        if (photo == null || extras.getBoolean("is_pressed_cancel")) {
                            finish();
                        } else {
                            String imagePath = PicUtil.cacheBitmapToFile(this, photo, System.currentTimeMillis() + ".jpg");
                            showPhoto(imagePath);
                            photo.recycle();
                        }
                    } else if (data.getData().getPath() != null) {
                        showPhoto(data.getData().getPath());
                    }
                }
                break;
//            case 1001:
//                if (resultCode == RESULT_OK && data != null)
//                    nickname.setText(data.getStringExtra("edit"));
//                break;
//            case 1002:
//                if (resultCode == RESULT_OK && data != null)
//                    address.setText(data.getStringExtra("edit"));
//                break;
//            case 1003:
//                if (resultCode == RESULT_OK && data != null)
//                    sign.setText(data.getStringExtra("edit"));
//                break;
        }
    }

    private void showPhoto(String imgpath) {
        try {
            int degree = PicUtil.readPictureDegree(imgpath);
            if (degree <= 0) {
                newFileName = imgpath;
            } else {
                // 创建操作图片是用的matrix对象
                Matrix matrix = new Matrix();
                // 旋转图片动作
                matrix.postRotate(degree);
                // 创建新图片
                Bitmap bmp = BitmapFactory.decodeFile(imgpath);
                Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0,
                        bmp.getWidth(), bmp.getHeight(), matrix, true);
                String path = PicUtil.getPictureDir(this);
                String fileName = System.currentTimeMillis() + ".jpg";
                PicUtil.saveBitmap(resizedBitmap, path, fileName);
                newFileName = path + "/" + fileName;
            }
            file = new File(newFileName);
            // Glide.with(MyInfoActivity.this).load(file).into(head_iv);
            ImgUtil.setGlideHead(MyInfoActivity.this, file, head_iv);


            Map<String, String> params = new HashMap<>();
            params.put("uid", sp.getString("uid"));
            myInfoPresenter.editUserInformation(this, params, file);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(this, "上传失败");
        }
    }

    private void chooseArea() {
        //条件选择器
        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (flag == 1) {
                    String sex = seArrayLists.get(options1);
                    sex_tv.setText(sex);
                } else if (flag == 2) {
                    //返回的分别是三个级别的选中位置
                    province = options1Items.get(options1).getPickerViewText();
                    city = options2Items.get(options1).get(option2);
                    area = options3Items.get(options1).get(option2).get(options3);
                    if (province.equals(city)) {
                        area_tv.setText(city + " " + area);
                    } else {
                        area_tv.setText(province + " " + city + " " + area);
                    }
                }
                submit(flag);
            }
        }).setLayoutRes(R.layout.pick_area_option, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvOptions.returnData();
                        pvOptions.dismiss();
                    }
                });

                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvOptions.dismiss();
                    }
                });
            }
        }).setDividerColor(getResources().getColor(R.color.font_select))
                .setTextColorCenter(getResources().getColor(R.color.font_select)).setContentTextSize(18)
                .build();
        if (flag == 1) {
            pvOptions.setPicker(seArrayLists);
        } else if (flag == 2) {
            pvOptions.setPicker(options1Items, options2Items, options3Items);
        }
        pvOptions.show();
    }


    //解析数据
    private void initJsonData() {

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<AreaBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

    }

    public ArrayList<AreaBean> parseData(String result) {//Gson 解析
        ArrayList<AreaBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                AreaBean entity = gson.fromJson(data.optJSONObject(i).toString(), AreaBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            if (o != null) {
                ContactsBean cb = (ContactsBean) o;
                if (TextUtils.isEmpty(cb.getAvatar())) {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(this, R.mipmap.head, head_iv);
                        //Glide.with(this).load(R.mipmap.head).into(head_iv);
                    }
                } else {
                    if (Util.isOnMainThread()) {
                        ImgUtil.setGlideHead(this, cb.getAvatar(), head_iv);
                        // Glide.with(this).load(cb.getAvatar()).into(head_iv);
                    }
                }
                sp.setString("qr_code", cb.getQrcode());
                nickname.setText(cb.getUser_nickname());
                number.setText(cb.getMobile());
                number2.setText(cb.getDebo_number());
                ImgUtil.setGlideHead(MyInfoActivity.this, cb.getQrcode(), qr_iv);
                //Glide.with(this).load(cb.getQrcode()).into(qr_iv);
                if ("0".equals(cb.getSex()) || TextUtils.isEmpty(cb.getSex())) {
                    sex_tv.setText("保密");
                } else if ("1".equals(cb.getSex())) {
                    sex_tv.setText("男");
                } else if ("2".equals(cb.getSex())) {
                    sex_tv.setText("女");
                }
                if (cb.getProvince().equals(cb.getCity())) {
                    area_tv.setText(cb.getCity() + " " + cb.getArea());
                } else {
                    area_tv.setText(cb.getProvince() + " " + cb.getCity() + " " + cb.getArea());
                }
                address.setText(cb.getAddress());
                sign.setText(cb.getSignature());
            } else {
                ToastUtils.showShort(this, message);
                finish();
            }
        } else {
            ToastUtils.showShort(this, message);
        }
    }


}
