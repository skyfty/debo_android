package com.qcwl.debo.ui.found.fans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.fans.bean.ArticleDetailBean;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends BaseActivity implements FansContract.View {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.text_date)
    TextView textDate;
    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.text_content)
    TextView textContent;
    @Bind(R.id.text_read)
    TextView textRead;
    @Bind(R.id.checkbox_praise)
    CheckBox checkboxPraise;
    @Bind(R.id.text_comment)
    TextView textComment;
    private String m_id = "";
    private FansPresenter presenter;
    private ArticleDetailBean bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        m_id = getIntent().getStringExtra("m_id");
        presenter = new FansPresenter(this);
        presenter.getArticleDetail(this, sp.getString("uid"), m_id);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("详情")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void doSuccess(int type, Object object) {
        if (type == FansPresenter.TYPE_FANS_ARTICLE_DETAIL) {
            bean = (ArticleDetailBean) object;
            if (bean != null) {
                if (!TextUtils.isEmpty(bean.getTitle())) {
                    textTitle.setText(bean.getTitle());
                    textTitle.setVisibility(View.VISIBLE);
                } else {
                    textTitle.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(bean.getContent())) {
                    textContent.setText(bean.getContent());
                    textContent.setVisibility(View.VISIBLE);
                } else {
                    textContent.setVisibility(View.GONE);
                }
                textDate.setText(bean.getAdd_time());
                textRead.setText("" + bean.getRead_num());
                textComment.setText("" + bean.getComment_num());
                checkboxPraise.setText("" + bean.getZan_num());
                if (bean.getImg() == null || bean.getImg().size() == 0) {
                    imageView.setVisibility(View.GONE);
                } else {
                    if (TextUtils.isEmpty(bean.getImg().get(0))) {
                        imageView.setVisibility(View.GONE);
                    } else {
                        ImgUtil.load(ArticleDetailActivity.this, bean.getImg().get(0), imageView);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void doFailure(int code) {

    }
}
