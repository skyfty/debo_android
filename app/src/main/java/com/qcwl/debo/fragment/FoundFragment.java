package com.qcwl.debo.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qcwl.debo.R;
import com.qcwl.debo.guide.FoundGuideComponent;
import com.qcwl.debo.guide.Guide;
import com.qcwl.debo.guide.GuideBuilder;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.CircleActivity;
import com.qcwl.debo.ui.found.bump.BumpActivity;
import com.qcwl.debo.ui.found.chat.TouhChatActivity;
import com.qcwl.debo.ui.found.fans.MyFansActivity;
import com.qcwl.debo.ui.found.guarantee.GuaranteeActivity;
import com.qcwl.debo.ui.found.joke.JokeActivity;
import com.qcwl.debo.ui.found.memorandum.MemorandumActivity;
import com.qcwl.debo.ui.found.near.NearActivity;
import com.qcwl.debo.ui.my.AboutActivity;
import com.qcwl.debo.ui.my.WishingStarActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.SystemUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.zxing.android.CaptureActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/6.
 */

public class FoundFragment extends Fragment implements View.OnClickListener {

    private View view;
    private LinearLayout chouyichou, pengyipeng, zhaungyizhuang,memorandum, star_ll, layoutCircle, layoutJoke, layoutGuarantee, layoutNear, layoutFans,layout_yule;
    private ImageView iv_chouyichou, iv_pengyipeng, iv_zhuangyizhuang, iv_star, iv_guarantee;
    private String[] permissions;
    private TextView tv_comment;
    private final int PESSION_CODE = 1;
    private int flag;
    private String title;
    private String tip;
    private Guide guide;
    private List<ImageView> list;
    private int i;
    private LinearLayout layout_chat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.found, null);
        initTitleBar();
        initView();
        String phoneType = SystemUtils.getDeviceBrand();
        Log.i("FoundFragment","..........phoneType="+phoneType);
        //if (phoneType.equals("Huawei")||phoneType.equals("HUAWEI")){
            initData();
        //}

        monitor();
        StatService.onPageStart(getActivity(),"启动探索页面");
        return view;
    }

    private void initData() {
        Api.android_states(new ApiResponseHandler(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                int code = apiResponse.getCode();
                if (code == 0) {
                    String data = apiResponse.getData();
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String is_show = jsonObject.getString("is_show");
                        if (is_show.equals("1")){
                            layout_yule.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void initTitleBar() {
        new TitleBarBuilder(view).setTitle("探索");
    }

    private void initView() {
        list = new ArrayList<>();
        chouyichou = (LinearLayout) view.findViewById(R.id.chouyichou);
        iv_chouyichou = (ImageView) view.findViewById(R.id.iv_chouyichou);
        pengyipeng = (LinearLayout) view.findViewById(R.id.pengyipeng);
        iv_pengyipeng = (ImageView) view.findViewById(R.id.iv_pengyipeng);
        zhaungyizhuang = (LinearLayout) view.findViewById(R.id.zhaungyizhuang);
        iv_zhuangyizhuang = (ImageView) view.findViewById(R.id.iv_zhuangyizhuang);
        star_ll = (LinearLayout) view.findViewById(R.id.star_ll);
        iv_star = (ImageView) view.findViewById(R.id.iv_star);
        layoutCircle = (LinearLayout) view.findViewById(R.id.layout_circle);
        layoutJoke = (LinearLayout) view.findViewById(R.id.layout_joke);
        layoutGuarantee = (LinearLayout) view.findViewById(R.id.layout_guarantee);
//        layoutGrasp = (LinearLayout) view.findViewById(R.id.layout_grasp);
        iv_guarantee = (ImageView) view.findViewById(R.id.iv_guarantee);
        layoutNear = (LinearLayout) view.findViewById(R.id.layout_near);
        layoutFans = (LinearLayout) view.findViewById(R.id.layout_fans);
        tv_comment = (TextView) view.findViewById(R.id.tv_comment);
        layout_yule = (LinearLayout) view.findViewById(R.id.layout_yule);
        layout_chat = (LinearLayout) view.findViewById(R.id.layout_chat);
        memorandum = (LinearLayout) view.findViewById(R.id.memorandum);
    }
    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StatService.onPageEnd(getActivity(),"结束探索页面");
        Log.i("FoundFr","...........FoundFragment_onDestroyView");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!TextUtils.isEmpty(SPUtil.getInstance(getActivity()).getString("found_guide"))) {
                return;
            }
            list.clear();
            i = 0;
            list.add(iv_chouyichou);
            list.add(iv_pengyipeng);
            list.add(iv_zhuangyizhuang);
            list.add(iv_star);
            //list.add(iv_guarantee);
            list.get(i).post(new Runnable() {
                @Override
                public void run() {
                    SPUtil.getInstance(getActivity()).setString("found_guide", "1");
                    showGuideView(list.get(i));
                }
            });
        }
    }

    private void showGuideView(final ImageView view) {
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
                i++;
                if (i < 4)
                    showGuideView(list.get(i));
            }
        });

        builder.addComponent(new FoundGuideComponent(i+1));
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(getActivity());
    }

    private void monitor() {
        chouyichou.setOnClickListener(this);
        pengyipeng.setOnClickListener(this);
        zhaungyizhuang.setOnClickListener(this);
        star_ll.setOnClickListener(this);
        layoutCircle.setOnClickListener(this);
        layoutJoke.setOnClickListener(this);
        layoutGuarantee.setOnClickListener(this);
        layoutNear.setOnClickListener(this);
        layoutFans.setOnClickListener(this);
//        layoutGrasp.setOnClickListener(this);
        layout_yule.setOnClickListener(this);
        layout_chat.setOnClickListener(this);
        memorandum.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new TitleBarBuilder(getActivity()).setAlpha(1);
        StatService.onResume(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chouyichou:
                flag = 1;
                title = "瞅一瞅";
                tip = "addFriend";
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                MPermissions.requestPermissions(FoundFragment.this, PESSION_CODE, permissions);
                break;
            case R.id.pengyipeng:
                flag = 2;
                title = "碰一碰";
                tip = "touch";
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                MPermissions.requestPermissions(FoundFragment.this, PESSION_CODE, permissions);
                break;
            case R.id.zhaungyizhuang:
//                flag = 2;
//                title = "撞一撞";
                startActivity(new Intent(getActivity(), BumpActivity.class));
                break;
//            case R.id.layout_grasp://抓一抓
//                startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("type",4));
//                break;
            case R.id.star_ll:
                startActivity(new Intent(getActivity(), WishingStarActivity.class));
                break;
            case R.id.layout_circle://圈子
                startActivity(new Intent(getActivity(), CircleActivity.class));
                //SPUtil.getInstance(getActivity()).setString("comment","");
                break;
            case R.id.layout_joke://段子
                //弹出对话框
                startActivity(new Intent(getActivity(), JokeActivity.class));//DemoActivity
                break;
            case R.id.layout_guarantee://三方客
                startActivity(new Intent(getActivity(), GuaranteeActivity.class));
                break;
            case R.id.layout_near://附近的人
                startActivity(new Intent(getActivity(), NearActivity.class));
                break;
            case R.id.layout_fans://我的粉丝
                startActivity(new Intent(getActivity(), MyFansActivity.class).putExtra("uid", SPUtil.getInstance(getActivity()).getString("uid")));
                break;
            case R.id.layout_yule://娱乐中心
                startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("type",10));
                break;
            case R.id.layout_chat://碰聊
                startActivity(new Intent(getActivity(), TouhChatActivity.class));
                break;
            case R.id.memorandum://备忘录
                startActivity(new Intent(getActivity(),MemorandumActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        startActivity(new Intent(getActivity(), CaptureActivity.class).putExtra("flag", flag).putExtra("tip", tip).putExtra("title", title));
    }

    @PermissionDenied(PESSION_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(getActivity(), "权限被禁止，请您去设置界面开启！", Toast.LENGTH_SHORT).show();
    }

    public void refresh(int comment) {
        if (comment > 0) {
            tv_comment.setVisibility(View.VISIBLE);
            tv_comment.setText(comment + "");
        } else {
            tv_comment.setVisibility(View.GONE);
        }

    }
}
