//package com.qcwl.debo.ui.circle2;
//
//
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AlertDialog;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.qcwl.debo.R;
//import com.qcwl.debo.http.Api;
//import com.qcwl.debo.http.ApiResponse;
//import com.qcwl.debo.http.ApiResponseHandler;
//import com.qcwl.debo.ui.circle.CircleAdapter;
//import com.qcwl.debo.ui.circle.CircleBean;
//import com.qcwl.debo.ui.circle.KeyBoardUtils;
//import com.qcwl.debo.utils.SPUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class SubCircleFragment extends Fragment {
//
//    @Bind(R.id.listView)
//    ListView listView;
//
//    @Bind(R.id.layout_comment)
//    LinearLayout layoutComment;
//    @Bind(R.id.edit_content)
//    EditText editContent;
//    @Bind(R.id.text_send)
//    TextView textSend;
//
//    private List<CircleBean> items;
//    private CircleAdapter adapter;
//
//    public String uid = "";
//    private int page = 1;
//    private int type = 1;
//
//    private boolean isInitData = false;
//
//    public SubCircleFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_sub_circle, container, false);
//        ButterKnife.bind(this, view);
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        uid = SPUtil.getInstance(getActivity()).getString("uid");
//        if (getArguments() != null) {
//            type = getArguments().getInt("type", 1);
//        }
//        items = new ArrayList<>();
//        adapter = new CircleAdapter(items, this);
//        listView.setAdapter(adapter);
//        listView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
//        getCircleList();
//    }
//
//    public boolean isRequestData() {
//        return getUserVisibleHint() && !isInitData;
//    }
//
//    public void requestCircleList(boolean isLoadMore) {
//        if (isLoadMore) {
//            page++;
//        } else {
//            page = 1;
//        }
//        getCircleList();
//    }
//
//    private void getCircleList() {
//        Api.requestCircleList(type, uid, page, new ApiResponseHandler(getActivity()) {
//            @Override
//            public void onSuccess(ApiResponse apiResponse) {
//                if (apiResponse.getCode() == 0) {
//                    List<CircleBean> list = JSON.parseArray(apiResponse.getData(), CircleBean.class);
//                    if (page == 1) {
//                        items.clear();
//                    }
//                    if (list != null && list.size() > 0) {
//                        items.addAll(list);
//                    }
//                    adapter.notifyDataSetChanged();
//                    if (items.size() > 0) {
//                        isInitData = true;
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    public void deleteComment(final int cir_type, final int com_type, final int outerPos, final int innerPos) {
//        new AlertDialog.Builder(getActivity())
//                .setTitle("提示")
//                .setMessage("您确定要删除吗？")
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        delete(cir_type, com_type, outerPos, innerPos);
//                        dialog.dismiss();
//                    }
//                })
//                .create().show();
//    }
//
//    private void delete(int cir_type, final int com_type, final int outerPos, final int innerPos) {
//        CircleBean circleBean = items.get(outerPos);
//        try {
//            String mc_id = "";
//            if (com_type == 1) {
//                mc_id = "0";
//            } else if (com_type == 2) {
//                mc_id = circleBean.getComment_list().get(innerPos).getMc_id();
//            }
//            Api.deleteCircleInfo(uid, cir_type, com_type, circleBean.getMoments_id(), mc_id, new ApiResponseHandler(getActivity()) {
//                @Override
//                public void onSuccess(ApiResponse apiResponse) {
//                    if (apiResponse.getCode() == 0) {
//                        if (com_type == 1) {
//                            items.remove(outerPos);
//                        } else if (com_type == 2) {
//                            items.get(outerPos).getComment_list().remove(innerPos);
//                        }
//                        adapter.notifyDataSetChanged();
//                        Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private int mPosition = 0;
//
//    public void openEditText(int position, String reply_uid) {
//        this.mPosition = position;
//        this.moment_mobile = items.get(position).getMobile();
//        this.reply_uid = reply_uid;
//        this.moments_id = items.get(position).getMoments_id();
//        layoutComment.setVisibility(View.VISIBLE);
//        editContent.setFocusable(true);
//        editContent.setFocusableInTouchMode(true);
//        editContent.requestFocus();
//        KeyBoardUtils.openKeybord(editContent, getActivity());
//
//    }
//
//    String reply_uid, moments_id, moment_mobile;
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }
//}
