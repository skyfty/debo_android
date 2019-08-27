package com.qcwl.debo.ui.my.partner;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.AboutActivity;
import com.qcwl.debo.utils.SPUtil;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AlMn on 2017/11/16.
 */

public class UnOpenedFragment extends Fragment {

    @Bind(R.id.text_limit)
    TextView textLimit;
    @Bind(R.id.btn_open)
    Button btnOpen;

    private String uid, mobile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_unopened, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            int verify_state = getArguments().getInt("verify_state");
            String coins_limit = getArguments().getString("coins_limit");
            textLimit.setText(coins_limit);
            //1、认证中；2、认证通过；3、认证失败
            if (verify_state == 3) {
                btnOpen.setText("开通合伙人");
                btnOpen.setBackgroundResource(R.drawable.btn_partner_red);
                btnOpen.setClickable(true);
            } else if (verify_state == 1) {
                btnOpen.setText("开通审核中");
                btnOpen.setBackgroundResource(R.drawable.btn_partner_gray);
                btnOpen.setClickable(false);
            }
        }

        uid = SPUtil.getInstance(getActivity()).getString("uid");
        mobile = SPUtil.getInstance(getActivity()).getString("phone");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_open, R.id.left_image, R.id.text_right, R.id.text_arguement, R.id.text_arguement2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                getActivity().finish();
                break;
            case R.id.text_right:
                startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("type",7));
                break;
            case R.id.btn_open:
                startActivity(new Intent(getActivity(), CertificationActivity.class));
                break;
            case R.id.text_arguement:
                startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("type",6));
                break;
            case R.id.text_arguement2:
                startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("type",5));
                break;
        }
    }
}
