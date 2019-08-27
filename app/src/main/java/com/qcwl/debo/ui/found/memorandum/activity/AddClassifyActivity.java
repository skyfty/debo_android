package com.qcwl.debo.ui.found.memorandum.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.ColorUtils;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddClassifyActivity extends BaseActivity {

    @Bind(R.id.color_classify)
    RecyclerView mColorClassify;
    @Bind(R.id.classify_edit)
    EditText mClassifyEdit;
    @Bind(R.id.text_test)
    TextView text_test;
    private String mClassifyColor = "";

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classify);
        ButterKnife.bind(this);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("添加分类")
                .setImageLeftRes(R.mipmap.back)
                .setTextRight("确认")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClassifyEdit.getText().toString().equals("")&&mClassifyEdit.getText().toString().length() == 0){
                            ToastUtils.showShort(AddClassifyActivity.this,"请输入分类名");
                            return;
                        }
                        if(mClassifyColor.equals("")&&mClassifyColor.length() == 0){
                            ToastUtils.showShort(AddClassifyActivity.this,"请选择颜色");
                            return;
                        }
//                        ToastUtils.showShort(AddClassifyActivity.this, "请求确认接口");
                        addClassify();
                    }
                })
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initView() {
        ClassifyAdapter adapter = new ClassifyAdapter();
        mColorClassify.setAdapter(adapter);
        mColorClassify.setLayoutManager(new GridLayoutManager(this,6));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                mClassifyColor = position+"";
                Log.i("color_position",position+"/色值："+ColorUtils.classifyColor[position]);
            }
        });
    }

    private class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ClassifyHolder> {
        private OnItemClickListener listener;

        @Override
        public ClassifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ClassifyHolder holder = new ClassifyHolder(View.inflate(AddClassifyActivity.this, R.layout.item_color, null), listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(ClassifyHolder holder, int position) {
            holder.mColor.setBackgroundResource(ColorUtils.classifyColor[position]);
        }

        @Override
        public int getItemCount() {
            return ColorUtils.classifyColor.length;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public class ClassifyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private OnItemClickListener listener;
            private RoundedImageView mColor;

            public ClassifyHolder(View itemView, OnItemClickListener listener) {
                super(itemView);
                this.listener = listener;
                mColor = (RoundedImageView) itemView.findViewById(R.id.color);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnItemClick(v, getAdapterPosition());
                }
            }
        }
    }

    private void addClassify(){
        Api.addMemorandumType(sp.getString("uid"), mClassifyEdit.getText().toString().trim(), mClassifyColor, new ApiResponseHandler(AddClassifyActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.getCode() == 0){
                    ToastUtils.showShort(AddClassifyActivity.this,"添加成功");
                    finish();
                }else{
                    ToastUtils.showShort(AddClassifyActivity.this,"添加失败");
                }
            }
        });
    }
}
