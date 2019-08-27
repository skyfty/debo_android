package com.qcwl.debo.ui.found.near;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.near.adapter.TrumpetAdapter;
import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;
import com.qcwl.debo.ui.found.near.contact.NearContact;
import com.qcwl.debo.ui.found.near.presenter.NearPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PurchaseTrumpetActivity extends BaseActivity implements NearContact.View {

    @Bind(R.id.list_view)
    ListView listView;

    private NearPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_trumpet);
        ButterKnife.bind(this);
        initTitleBar();
        presenter = new NearPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.getTrumpetList(this, sp.getString("uid"));
        }
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("小喇叭")
                .setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void doTrumpetListSuccess(List<TrumpetBean> items) {
        if (items != null && items.size() > 0) {
            imageSetting(items);
            listView.setAdapter(new TrumpetAdapter(PurchaseTrumpetActivity.this, items));
        }
    }

    private void imageSetting(List<TrumpetBean> items) {
        for (int i = 0; i < items.size(); i++) {
            switch (i % 5) {
                case 0:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_yellow);
                    break;
                case 1:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_orange);
                    break;
                case 2:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_red);
                    break;
                case 3:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_blue);
                    break;
                case 4:
                    items.get(i).setImgId(R.mipmap.ic_trumpet_purple);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void doNearListSuccess(List<NearBean> list) {

    }

    @Override
    public void doTrumpetShout() {

    }

    @Override
    public void doFailure(int code) {

    }
}
