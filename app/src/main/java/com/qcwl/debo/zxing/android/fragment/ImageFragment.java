package com.qcwl.debo.zxing.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.SPUtil;

/**
 * 二维码展示界面
 */
public class ImageFragment extends Fragment {

    private ImageView imageView;

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.image_code);
        StatService.onPageStart(getActivity(),"开始瞅一瞅二维码页面");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StatService.onPageEnd(getActivity(),"结束瞅一瞅二维码页面");
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
        params.leftMargin = ScreenUtils.dp2px(getActivity(), 50);
        params.rightMargin = ScreenUtils.dp2px(getActivity(), 50);
        params.topMargin=ScreenUtils.dp2px(getActivity(),50);
        imageView.setLayoutParams(params);
        ImgUtil.load(getActivity(), SPUtil.getInstance(getActivity()).getString("qr_code"), imageView);
    }
}
