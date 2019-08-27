package com.qcwl.debo.ui.found.jinshanyun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt2325.cameralibrary.util.FileUtil;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.jinshanyun.adapter.MediaGalleryAdapter;
import com.qcwl.debo.ui.found.jinshanyun.adapter.MediaSelectedAdapter;
import com.qcwl.debo.ui.found.jinshanyun.media.MediaInfo;
import com.qcwl.debo.ui.found.jinshanyun.media.MediaStorage;
import com.qcwl.debo.ui.found.jinshanyun.media.ThumbnailGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MediaImportActivity extends BaseActivity {

    private static final int RESULT_CODE = -1;


    private ImageButton mBack;
    private TextView mTitle;
    private RecyclerView mGalleryView;
    private TextView mTotalDuration;
    private Button mNextStep;
    private RecyclerView mSelectedView;

    private ButtonObserver mObserver;
    private MediaStorage mMediaStorage;
    private ThumbnailGenerator mThumbGenerator;
    private MediaGalleryAdapter mMediaGalleryAdapter;
    private MediaSelectedAdapter mSelectedAdapter;
    private String mThumbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_import);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mObserver = new ButtonObserver();
        mBack = (ImageButton) findViewById(R.id.gallery_backBtn);
        mBack.setOnClickListener(mObserver);
        mTitle = (TextView) findViewById(R.id.gallery_title);
        mTotalDuration = (TextView) findViewById(R.id.tv_duration_value);
        mNextStep = (Button) findViewById(R.id.btn_next_step);
        mNextStep.setOnClickListener(mObserver);
        mGalleryView = (RecyclerView) findViewById(R.id.gallery_media);
        mSelectedView = (RecyclerView) findViewById(R.id.rv_selected_video);
        mMediaStorage = new MediaStorage(this);
        mThumbGenerator = new ThumbnailGenerator(this);
        initMediaGalleryView();
        initMediaSelectedView();
    }

    @Override
    public void statusBarSetting() {
        setDefaultStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaStorage.cancelTask();
        mThumbGenerator.cancelAllTask();
    }

    private void initMediaGalleryView() {
        mGalleryView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mMediaGalleryAdapter = new MediaGalleryAdapter(mMediaStorage.getMediaList(), mThumbGenerator);
        mMediaGalleryAdapter.setOnItemClickListener(new MediaGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MediaInfo info) {
                mSelectedAdapter.addData(info);
            }
        });
        mMediaStorage.setOnMediaDataUpdateListener(new MediaStorage.OnMediaDataUpdateListener() {
            @Override
            public void onDateUpdate(List<MediaInfo> data) {
                int count = mMediaGalleryAdapter.getItemCount();
                int size = data.size();
                int insert = count - size;
                mMediaGalleryAdapter.notifyItemRangeInserted(insert, size);
            }
        });
        mGalleryView.setAdapter(mMediaGalleryAdapter);
        mMediaStorage.startCaptureMedias(MediaStorage.MEDIA_TYPE_VIDEO);
    }

    private void initMediaSelectedView() {
        mSelectedView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSelectedAdapter = new MediaSelectedAdapter(mThumbGenerator);
        mSelectedAdapter.setItemCallback(new MediaSelectedAdapter.ItemCallback() {
            @Override
            public void onDurationUpdate(long duration) {
                int sec = Math.round(((float) duration) / 1000);
                int hour = sec / 3600;
                int min = (sec % 3600) / 60;
                sec = (sec % 60);
                mTotalDuration.setText(String.format("%1$02d:%2$02d:%3$02d", hour, min, sec));
            }
        });
        mSelectedView.setAdapter(mSelectedAdapter);
    }

    private void onSelectedDataConfirmed() {
        List<MediaInfo> list = mSelectedAdapter.getSelectedList();
        ArrayList<String> path = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            path.add(list.get(i).filePath);
        }
        if (path.size() == 1) {
            Bitmap bitmap = getVideoThumbnail(path.get(0));
            mThumbPath = FileUtil.saveBitmap("JCamera", bitmap);
            Intent intent = new Intent(MediaImportActivity.this,ReleasePassageActivity.class);
            intent.putExtra("publishType",1);
            intent.putExtra("thumbPath", mThumbPath);
            intent.putExtra("videoPath",path);
            setResult(RESULT_CODE, intent);
            finish();
        } else {
            Toast.makeText(this, "只可选择一个视频", Toast.LENGTH_SHORT).show();
        }
    }

    private class ButtonObserver implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.gallery_backBtn:
                    finish();
                    break;
                case R.id.btn_next_step:
//                    onSelectedDataConfirmed();
            }
        }
    }

    public Bitmap getVideoThumbnail(String url) {
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //（）根据文件路径获取缩略图
            //retriever.setDataSource(filePath);
            retriever.setDataSource(url, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        Log.v("bitmap", "bitmap="+bitmap);
        return bitmap;
    }

}
