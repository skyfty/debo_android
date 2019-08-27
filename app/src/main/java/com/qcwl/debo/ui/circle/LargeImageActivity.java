package com.qcwl.debo.ui.circle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.FileUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.tencent.mm.opensdk.utils.Log;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.request.OkHttpRequest;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class LargeImageActivity extends BaseActivity {

    @Bind(R.id.banner)
    ConvenientBanner convenientBanner;

    private List<String> images;
    private int mPosition;
    private String TAG = "LargeImageActivity";
    private final int RESULT_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        ButterKnife.bind(this);
        if (getIntent() == null) {
            return;
        }
        mPosition = getIntent().getIntExtra("position", 0);
        images = (List<String>) getIntent().getSerializableExtra("images");
        Log.i(TAG,".......images="+images.size()+"      ="+mPosition);
        initData();
    }

    private void initData() {
        try {
            convenientBanner.setPages(
                    new CBViewHolderCreator<ImageHolderView>() {
                        @Override
                        public ImageHolderView createHolder() {
                            return new ImageHolderView();
                        }
                    }, images)
                    //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                    .setPageIndicator(new int[]{R.drawable.ic_banner_indicator, R.drawable.ic_banner_indicator_focused})
                    //设置指示器的方向
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
            convenientBanner.setCanLoop(false);
            convenientBanner.setManualPageable(true);
            convenientBanner.setcurrentitem(mPosition);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ImageHolderView implements Holder<String> {
        private PinchImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new PinchImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            mPosition = position;
            Log.i(TAG,".......mPosition="+mPosition);
            Log.i(TAG,".......UpdateUI="+images.size());
            if (images.size() == 0||images.get(0).equals("")) {
                Log.i(TAG,".......UpdateUI="+images.size());
                imageView.setImageResource(R.mipmap.debo_logo);
            } else {
                ImgUtil.load(context, data, imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
            registerForContextMenu(imageView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "保存");
        menu.add(0, 1, 0, "转发");
//        ViewPager.LayoutParams params = new ViewPager.LayoutParams();
//        params.width = ScreenUtils.dp2px(this, 80);
//        params.height = ScreenUtils.dp2px(this, 50);
//        v.setLayoutParams(params);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                downloadImage(images.get(convenientBanner.getCurrentItem()));
                break;
            case 1:
                //转发实现
                startActivityForResult((new Intent(LargeImageActivity.this,MyFrendActivity.class).putExtra("imgPath",images.get(convenientBanner.getCurrentItem()))),RESULT_CODE);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void downloadImage(String imageUrl) {
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/"));

        OkHttpUtils.get().url(imageUrl)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new FileCallBack(FileUtil.getPictureDir(this), filename) {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(final File response, int id) {
                        if (response == null) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(LargeImageActivity.this, "图片已保存至debo/picture/文件夹");
                                remindPhoto(response);
                            }
                        });
                    }
                });
    }

    private void remindPhoto(File file) {
        if (file != null) {
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "title", "description");
                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, " ...........=" + resultCode + "    =" + data);
        if (resultCode == -1) {
            Toast.makeText(LargeImageActivity.this, "转发成功", Toast.LENGTH_SHORT).show();
        }
    }
}
