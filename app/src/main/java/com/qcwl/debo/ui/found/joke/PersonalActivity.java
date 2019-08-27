package com.qcwl.debo.ui.found.joke;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.PersonalBean;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.DeviceUtil;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TabLayoutUtils;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PersonalActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.iv_gender)
    ImageView iv_gender;
    @Bind(R.id.coord)
    CoordinatorLayout coord;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbarlayout)
    AppBarLayout appBarLayout;
    @Bind(R.id.linear)
    LinearLayout linear;
    @Bind(R.id.line1)
    LinearLayout line1;
    @Bind(R.id.act_playvideo_back)
    ImageView tv_back;
    @Bind(R.id.act_usericon)
    CircleImageView iv_photo;
    @Bind(R.id.bt_guanzhu)
    TextView bt_guanzhu;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_addr)
    TextView tv_addr;
    @Bind(R.id.tv_number)
    TextView tv_number;
    @Bind(R.id.tv_content)
    TextView tv_content;
    @Bind(R.id.tv_praise)
    TextView tv_praise;
    @Bind(R.id.tv_guanzhu)
    TextView tv_guanzhu;
    @Bind(R.id.tv_fan)
    TextView tv_fans;
    @Bind(R.id.vp_fragment)
    ViewPager viewPager;
    @Bind(R.id.tl_tabs)
    TabLayout tabLayout;
    private String uid;
    private String f_uid;
    private PersonalBean personalBean;
    private String TAG = "PersonalActivity";
    public final static int TAB_STORE_INDEX = 0;//作品fragment
    public final static int TAB_GOODS_INDEX = 1;//喜欢fragment
    private List<Fragment> fragments = new ArrayList<>();
    private String[] tabs = new String[]{"作品", "喜欢"};
    private final int COL_3F51B5 = Color.parseColor("#71cca2");
    private final int RED = Color.red(COL_3F51B5);
    private final int GREEN = Color.green(COL_3F51B5);
    private final int BLUE = Color.blue(COL_3F51B5);
    private String is_follow;
    private String gender = "2";
    private String is_fans;
    private String lovesNum;
    private String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        //initHeadView();
        f_uid = getIntent().getStringExtra("f_udi");
        uid = SPUtil.getInstance(this).getString("uid");
        is_follow = SPUtil.getInstance(this).getString("is_follow");
        getVideo_Info();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void initDate() {
        if (is_fans.equals("1")){
            setGZ("关注成功");
        }else{
            setGZ("");
        }

        ImgUtil.loadHead(this, personalBean.getUser_info().getAvatar(), iv_photo);
        tv_name.setText(personalBean.getUser_info().getUser_nickname());
        tv_addr.setText(personalBean.getUser_info().getCity());
        tv_number.setText("嘚波号：" + personalBean.getUser_info().getMobile());
        String content = personalBean.getUser_info().getSignature();
        if (!content.equals("")) {
            tv_content.setText(personalBean.getUser_info().getSignature());
        }
        tv_fans.setText(personalBean.getFans_count() + "粉丝");
        tv_guanzhu.setText(personalBean.getFollow_count() + "关注");
        tv_praise.setText(personalBean.getUpvote_count() + "获赞");
        if (gender.equals("1")){
            iv_gender.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.gender_man));
        }else if(gender.equals("2")){
            iv_gender.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.gender_women));
        }else{
            iv_gender.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.gender_women));
        }
        Glide.with(this)
                .load(personalBean.getUser_info().getAvatar())
                .bitmapTransform(new BlurTransformation(this, 15)).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                line1.setBackground(resource);
            }
        });
        if (personalBean.getJoke_list()==null){
            num = "作品 0";
        }else{
            num = "作品 "+personalBean.getJoke_count();
        }
        
        Log.i(TAG,"...........="+personalBean.getJoke_like_list());
        if (personalBean.getJoke_like_list() == null){
            lovesNum = "喜欢 0";
        }else{
            lovesNum = "喜欢 "+personalBean.getLike_count();
        }

        tabs[0] = num;
        tabs[1] = lovesNum;
        showTablist(tabs);

        tv_back.setOnClickListener(this);
        bt_guanzhu.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
               // float i=(210+verticalOffset)/210.0f;
               // Log.i("info","--------ver"+verticalOffset+"   "+i);
                //toolbar.setAlpha(i);
                if (verticalOffset >= 0) {
                    Log.i("info","--------verticalOffset >= 0        "+verticalOffset);
                    linear.setBackgroundColor(Color.argb((int) 0, RED, GREEN, BLUE));//AGB由相关工具获得，或者美工提供
                } else if (verticalOffset < 0 && 0-verticalOffset <= EaseTitleBar.dp2px(PersonalActivity.this,70)) {
                    float scale = (float) (0-verticalOffset) / EaseTitleBar.dp2px(PersonalActivity.this,70);
                    float alpha = (255 * scale);
                    Log.i("info","--------else        "+verticalOffset);
                    // 只是layout背景透明(仿知乎滑动效果)
                    linear.setBackgroundColor(Color.argb((int) alpha, RED, GREEN, BLUE));
                } else {
                    Log.i("info","--------else        "+verticalOffset);
                    linear.setBackgroundColor(Color.argb((int) 255, RED, GREEN, BLUE));
                }
            }
        });
    }

    private void getVideo_Info() {
        //   showProgressDialog("正在获取");
        Api.getUserVideo(uid, f_uid, new ApiResponseHandler(PersonalActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("获取视频信息", "发现apiResponse。。。" + apiResponse);
                if (apiResponse.getCode() == 0) {
                    personalBean = JSON.parseObject(apiResponse.getData(), PersonalBean.class);
                    gender = personalBean.getUser_info().getSex();
                    is_fans = personalBean.getIs_fans();
                    Log.i(TAG,"...........="+personalBean);
                    Log.i(TAG,"...........="+personalBean.getJoke_like_list());
                    initDate();
                    Log.i("获取视频信息", "发现apiResponse222222。。。=" + personalBean.getJoke_like_list()+ "      " + personalBean.getJoke_list()+"    "+gender);
                } else {
                    ToastUtils.showShort(PersonalActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    private void showTablist(String[] tabs) {

        for (int i = 0; i < tabs.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
            TabLayoutUtils.reflex(tabLayout);
            switch (i) {
                case TAB_STORE_INDEX:
                    fragments.add(WorksFragment.newInstance(personalBean,is_follow,"0"));
                    break;
                case TAB_GOODS_INDEX:
                    fragments.add(WorksFragment.newInstance(personalBean,is_follow,"1"));
                    break;
                default:
                    fragments.add(WorksFragment.newInstance(personalBean,is_follow,"0"));
                    break;
            }
        }
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(TAB_STORE_INDEX);//要设置到viewpager.setAdapter后才起作用
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVerticalScrollbarPosition(TAB_STORE_INDEX);
        //tlTabs.setupWithViewPager方法内部会remove所有的tabs，这里重新设置一遍tabs的text，否则tabs的text不显示
        for (int i = 0; i < tabs.length; i++) {
            Log.i(TAG, "............" + tabs[i] + "    " + tabLayout + "     " + tabLayout.getTabAt(i));
            tabLayout.getTabAt(i).setText(tabs[i]);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_playvideo_back:
                this.finish();
                break;
            case R.id.bt_guanzhu:
                setGuanzhu();
                break;
            case R.id.found_ll:

                break;
        }
    }

    private void setGuanzhu() {
        Api.focusfans(uid, f_uid, new ApiResponseHandler(PersonalActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("获取视频信息", "发现apiResponse。。。" + apiResponse);
               setGZ(apiResponse.getMessage().toString().trim());
            }
        });
    }
    void setGZ(String guanzhu){
        if ("关注成功".equals(guanzhu)) {
            //mGuanzhu.setImageResource(R.mipmap.icon_invite);
            bt_guanzhu.setText("已关注");
            bt_guanzhu.setBackground(getResources().getDrawable(R.mipmap.guanzhu_gb));
            bt_guanzhu.setCompoundDrawables(null,null,null,null);
            //Toast.makeText(getApplicationContext(), "关注成功", Toast.LENGTH_SHORT).show();
        } else {
            bt_guanzhu.setText("关注");
            Drawable drawable = getResources().getDrawable(R.mipmap.add);
            Log.e("getApplicationContext", "drawable=======" + drawable);
            bt_guanzhu.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            bt_guanzhu.setCompoundDrawablePadding(EaseTitleBar.dp2px(PersonalActivity.this,5));
            bt_guanzhu.setBackgroundResource(R.color.light_green3);
            //Toast.makeText(getApplicationContext(), "取消关注", Toast.LENGTH_SHORT).show();
        }
    }
}
