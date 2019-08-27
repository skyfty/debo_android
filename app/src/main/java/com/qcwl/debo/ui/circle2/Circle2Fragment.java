//package com.qcwl.debo.ui.circle2;
//
//
//import android.Manifest;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.aspsine.swipetoloadlayout.OnRefreshListener;
//import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
//import com.baidu.mobstat.StatService;
//import com.qcwl.debo.R;
//import com.qcwl.debo.http.Api;
//import com.qcwl.debo.http.ApiResponse;
//import com.qcwl.debo.http.ApiResponseHandler;
//import com.qcwl.debo.ui.circle.Luban;
//import com.qcwl.debo.ui.circle.PublishCircleActivity;
//import com.qcwl.debo.ui.circle.ScreenUtils;
//import com.qcwl.debo.ui.contact.ContactsContentActivity;
//import com.qcwl.debo.ui.found.fans.CameraActivity;
//import com.qcwl.debo.utils.SPUtil;
//import com.zhy.m.permission.MPermissions;
//import com.zhy.m.permission.PermissionDenied;
//import com.zhy.m.permission.PermissionGrant;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import me.nereo.multi_image_selector.MultiImageSelector;
//import me.nereo.multi_image_selector.MultiImageSelectorActivity;
//
//import static android.app.Activity.RESULT_OK;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class Circle2Fragment extends Fragment {
//
//    @Bind(R.id.swipe_target)
//    RecyclerView recyclerView;
//    @Bind(R.id.swipeToLoadLayout)
//    SwipeToLoadLayout swipeToLoadLayout;
//    @Bind(R.id.status_bar)
//    View statusBar;
//    @Bind(R.id.image_back)
//    ImageView imageBack;
//    @Bind(R.id.tabLayout)
//    TabLayout tabLayout;
//    @Bind(R.id.text_publish)
//    ImageView imageCamera;
//    @Bind(R.id.layout_title)
//    LinearLayout layoutTitle;
//
//    @Bind(R.id.layout_comment)
//    LinearLayout layoutComment;
//    @Bind(R.id.edit_content)
//    EditText editContent;
//    @Bind(R.id.text_send)
//    TextView textSend;
//
//    public String uid = "";
//    private String mobile = "";
//
//    private Context instance = null;
//    private Circle2Fragment circleFragment;
//    private int type = 1;
//
//    private Circle2Adapter adapter;
//    private CircleResultBean result;
//
//    private List<Fragment> fs;
//    private FragmentManager fm;
//    private FragmentTransaction ft;
//
//    public Circle2Fragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_circle2, container, false);
//        ButterKnife.bind(this, view);
//
//        instance = getActivity();
//        circleFragment = this;
//        //spacing = ScreenUtils.getStatusHeight(instance) + ScreenUtils.dp2px(getActivity(), 50);
//        uid = SPUtil.getInstance(instance).getString("uid");
//        // 页面埋点
//        StatService.onPageStart(getActivity(), "启动圈子界面");
//        getCircleTopImg();
//        return view;
//    }
//
//    private void initView() {
//        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.getStatusHeight(instance)));
//        tabLayout.addTab(tabLayout.newTab().setText("朋友圈"));
//        tabLayout.addTab(tabLayout.newTab().setText("人脉圈"));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                layoutComment.setVisibility(View.GONE);
//                ft = fm.beginTransaction();
//                switch (tab.getPosition()) {
//                    case 0:
//                        ft.show(fs.get(0)).hide(fs.get(1));
//                        break;
//                    case 1:
//                        ft.show(fs.get(1)).hide(fs.get(0));
//                        break;
//                    default:
//                        break;
//                }
//                ft.commit();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        //tabLayout.setSelectedTabIndicatorHeight(0);
//        tabLayout.setTabTextColors(getResources().getColor(android.R.color.white), getResources().getColor(android.R.color.white));
//        swipeToLoadLayout.setRefreshEnabled(true);
//        swipeToLoadLayout.setLoadMoreEnabled(true);
//        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                SubCircleFragment subFragment = (SubCircleFragment) (fs.get(tabLayout.getSelectedTabPosition()));
//                if (subFragment.isRequestData()) {
//                    subFragment.requestCircleList(false);
//                }
//                swipeToLoadLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (swipeToLoadLayout == null) {
//                            return;
//                        }
//                        swipeToLoadLayout.setRefreshing(false);
//                    }
//                }, 1500);
//            }
//        });
//        swipeToLoadLayout.setOnLoadMoreListener(new com.aspsine.swipetoloadlayout.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                SubCircleFragment subFragment = (SubCircleFragment) (fs.get(tabLayout.getSelectedTabPosition()));
//                if (subFragment.isRequestData()) {
//                    subFragment.requestCircleList(true);
//                }
//                swipeToLoadLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (swipeToLoadLayout == null) {
//                            return;
//                        }
//                        swipeToLoadLayout.setLoadingMore(false);
//                    }
//                }, 1500);
//            }
//        });
//    }
//
//    private void initData() {
//        fm = getFragmentManager();
//        fs = new ArrayList<>();
//        SubCircleFragment subCircleFragment = null;
//        Bundle bundle = null;
//        ft = fm.beginTransaction();
//        for (int i = 1; i <= 2; i++) {
//            subCircleFragment = new SubCircleFragment();
//            bundle = new Bundle();
//            bundle.putInt("type", i);
//            subCircleFragment.setArguments(bundle);
//            fs.add(subCircleFragment);
//            ft.add(R.id.frame_layout, subCircleFragment);
//        }
//        ft.show(fs.get(0)).hide(fs.get(1)).commit();
//
//    }
//
//    public void onCirlceHeaderClick() {
//        MPermissions.requestPermissions(circleFragment, CODE_PHOTO,
//                Manifest.permission.CAMERA,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//    }
//
//    public void onCircleHeaderUserPhotoClick() {
//        startActivity(new Intent(getActivity(), ContactsContentActivity.class)
//                .putExtra("my_mobile", mobile)
//                .putExtra("mobile", mobile)
//                .putExtra("type", "" + type));
//    }
//
//    @OnClick({R.id.image_back, R.id.text_publish})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.image_back:
//                getActivity().finish();
//                break;
//            case R.id.text_publish:
//                dialog();
//                break;
//        }
//    }
//
//    public void getCircleTopImg() {
//        Api.getCircleTopImg(uid, new ApiResponseHandler(instance) {
//            @Override
//            public void onSuccess(ApiResponse apiResponse) {
//                if (apiResponse.getCode() == 0) {
//                    try {
//                        JSONObject object = new JSONObject(apiResponse.getData());
//                        String bgImg = object.getString("moments_background_img");
//                        String avatar = object.getString("avatar");
//                        String nickname = object.getString("user_nickname");
//                        mobile = object.getString("mobile");
//                        result = new CircleResultBean();
//                        result.setBgImg(bgImg);
//                        result.setAvatar(avatar);
//                        result.setNickname(nickname);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(-1,-1));
//                        recyclerView.setAdapter(new Circle2Adapter(Circle2Fragment.this,result));
//                        recyclerView.smoothScrollToPosition(0);
//                        initData();
//                        initView();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void modifyTopImg(File file) {
//        if (file == null) {
//            Toast.makeText(instance, "图片为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Api.modifyCircleTopImg(uid, file, new ApiResponseHandler(instance) {
//            @Override
//            public void onSuccess(ApiResponse apiResponse) {
//                if (apiResponse.getCode() == 0) {
//                    try {
//                        JSONObject object = new JSONObject(apiResponse.getData());
//                        String imgUrl = object.getString("moments_background_img");
//                        // ImgUtil.load(instance, imgUrl, headerViewBg);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(instance, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case CODE_PHOTO:
//                    if (data == null) {
//                        return;
//                    }
//                    List<String> imgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                    if (imgs == null || imgs.size() == 0) {
//                        return;
//                    }
//                    modifyTopImg(Luban.get(getActivity()).launch(new File(imgs.get(0))));
//                    break;
//                case CODE_PUBLISH:
//                case CODE_CAMERA:
//                    SubCircleFragment subFragment = (SubCircleFragment) (fs.get(tabLayout.getSelectedTabPosition()));
//                    if (subFragment.isRequestData()) {
//                        subFragment.requestCircleList(false);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    public static final int CODE_PHOTO = 199;
//    public static final int CODE_PUBLISH = 299;
//
//    @PermissionGrant(CODE_PHOTO)
//    public void requestPermissionSuccess() {
//        new AlertDialog.Builder(instance)
//                .setItems(new String[]{"更换相册封面"}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        MultiImageSelector.create()
//                                .showCamera(true) // show camera or not. true by default
//                                //.count(canSelectCount) // max select image size, 9 by default. used width #.multi()
//                                .single() // single mode
//                                //.multi() // multi mode, default mode;
//                                //.origin(imgs) // original select data set, used width #.multi()
//                                .start(circleFragment, CODE_PHOTO);
//                    }
//                }).create().show();
//    }
//
//    @PermissionDenied(CODE_PHOTO)
//    public void requestPermissionFailed() {
//        Toast.makeText(instance, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    private void dialog() {
//        new AlertDialog.Builder(getActivity())
//                .setItems(new String[]{"视频", "图片"}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                MPermissions.requestPermissions(circleFragment, CODE_CAMERA,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                        Manifest.permission.RECORD_AUDIO,
//                                        Manifest.permission.CAMERA);
//                                StatService.onEvent(getActivity(), "圈子", "发布视频动态");
//                                break;
//                            case 1:
//                                startActivityForResult(new Intent(getActivity(), PublishCircleActivity.class), CODE_PUBLISH);
//                                StatService.onEvent(getActivity(), "圈子", "发布图片动态");
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                }).create().show();
//    }
//
//    public static final int CODE_CAMERA = 200;
//
//    @PermissionGrant(CODE_CAMERA)
//    public void requestPermissionSuccess2() {
//        startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("is_cycle", 1), CODE_CAMERA);
//    }
//
//    @PermissionDenied(CODE_CAMERA)
//    public void requestPermissionFailed2() {
//        Toast.makeText(instance, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        StatService.onPause(this);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        StatService.onResume(this);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//        StatService.onPageEnd(getActivity(), "结束圈子界面");
//    }
//
//}
