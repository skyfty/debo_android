package com.qcwl.debo.ui.found.jinshanyun;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ksyun.media.shortvideo.kit.KSYMergeKit;
import com.ksyun.media.shortvideo.kit.KSYRemuxKit;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.fans.CameraActivity;
import com.qcwl.debo.ui.found.jinshanyun.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 配置窗口示例：
 * 录制参数配置
 * 编辑合成参数配置
 */
public class ConfigActivity extends BaseActivity {
    private final static String TAG = "ConfigActivity";
    private final static int REQUEST_CODE = 6384;
    private final static int PERMISSION_REQUEST_STORAGE = 1;
    private final static int PERMISSION_REQUEST_WRITE = 2;
    private final static String TITLE = "ksy_import_file";
    private final static String EXT_TRANSCODE = "/newTranscode";//转码生成后的文件前缀

    /*******转码参数配置示例******/

    private TextView[] mOutProfileGroup;
    private List<Uri> mTransCodeUris;
    private ShortVideoConfig mTransConfig; //输出视频参数配置
    private Timer mTimer;

    private static final int[] OUTPUT_PROFILE_ID = {R.id.trans_output_config_low_power,
            R.id.trans_output_config_balance, R.id.trans_output_config_high_performance};

    /*******录制参数配置示例******/

    private MergeFilesAlertDialog mTranscodeDialog;

    private LinearLayout ll_config_import;  //从外部导入文件
    private LinearLayout ll_config_record;   //由此进入录制示例窗口

    private KSYMergeKit mKsyMergeKit;
    private ConfigObserver mObserver;
    private static ShortVideoConfig mRecordConfig = new ShortVideoConfig();  //录制参数配置

    private static final int[] ENCODE_PROFILE_TYPE = {VideoEncodeFormat.ENCODE_PROFILE_LOW_POWER,
            VideoEncodeFormat.ENCODE_PROFILE_BALANCE, VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mObserver = new ConfigObserver();
        mOutProfileGroup = new TextView[3];

        ll_config_import = (LinearLayout) findViewById(R.id.ll_config_import);
        ll_config_import.setOnClickListener(mObserver);

        ll_config_record = (LinearLayout) findViewById(R.id.ll_config_record);
        ll_config_record.setOnClickListener(mObserver);

        mKsyMergeKit = new KSYMergeKit(ConfigActivity.this);
        mKsyMergeKit.setOnErrorListener(mOnErrorListener);
        mKsyMergeKit.setOnInfoListener(mOnInfoListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermisson();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTranscodeDialog != null) {
            mTranscodeDialog.dismiss();
            mTranscodeDialog = null;
        }

        if (mKsyMergeKit != null) {
            mKsyMergeKit.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                break;
            }
        }
    }



    private void confirmConfig() {
        mRecordConfig.isLandscape = false;
        mRecordConfig.encodeProfile =VideoEncodeFormat.ENCODE_PROFILE_BALANCE;
        //  mTransConfig.decodeMethod = StreamerConstants.DECODE_METHOD_SOFTWARE;
        mRecordConfig.encodeMethod = StreamerConstants.ENCODE_METHOD_SOFTWARE;
        mRecordConfig.encodeType = AVConst.CODEC_ID_HEVC;
        mRecordConfig.resolution = StreamerConstants.VIDEO_RESOLUTION_1080P;
        mRecordConfig.fps = Integer.parseInt("30");
        mRecordConfig.videoBitrate = Integer.parseInt("4000");
        mRecordConfig.audioBitrate = Integer.parseInt("48");
    }

    /**
     * 读取磁盘权限检查
     */
    private void checkPermisson() {
        int storagePer = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePer = ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE);

        if (storagePer != PackageManager.PERMISSION_GRANTED || writePer != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.e(TAG, "hasPermission: API version < M");

            } else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions,
                        PERMISSION_REQUEST_STORAGE);
            }
        } else {

        }
    }

    public static final int CODE_CAMERA = 200;

    public class ConfigObserver implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_config_import:
                    confirmConfig();
                    //启动本地导入页面
                    onImportClick();
                    break;
                case R.id.ll_config_record:
//                    confirmConfig();
//                    //启动短视频录制
//                    RecordActivity.startActivity(getApplicationContext());
                    startActivityForResult(new Intent(ConfigActivity.this, CameraActivity.class).putExtra("is_cycle", 2),CODE_CAMERA);
                    finish();
                    break;
                case R.id.output_config_low_power:
                    onOutputEncodeProfileClick(0);
                    break;
                case R.id.output_config_balance:
                    onOutputEncodeProfileClick(1);
                    break;
                case R.id.output_config_high_performance:
                    onOutputEncodeProfileClick(2);
                    break;
                default:
                    break;
            }

        }
    }

    private void onImportClick() {
        Intent intent = new Intent(this, MediaImportActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 从本地导入视频文件结果处理
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        List<Uri> uris = new LinkedList<>();
                        ArrayList<String> pathList = data.getStringArrayListExtra("filePath");
                        for (int i = 0; i < pathList.size(); i++) {
                            Uri uri = Uri.parse("file://" + pathList.get(i));
                            uris.add(uri);
                        }
                        if (uris.size() > 1) {
                            //多选后转码和拼接处理
                            mTransCodeUris = uris;
                            onOutputConfirmClick();
                        } else {
                            Uri uri = uris.get(0);
                            Log.i(TAG, "Uri = " + uri.toString());
                            try {
                                // Get the file path from the URI
                                final String path = FileUtils.getPath(this, uri);
                                String mimeType = FileUtils.getMimeType(this, uri);

                                if (!TextUtils.isEmpty(mimeType) && isSupportedMimeType(mimeType)) {

                                    Toast.makeText(ConfigActivity.this,
                                            "File Selected: " + path, Toast.LENGTH_LONG).show();
                                    EditActivity.startActivity(getApplicationContext(), path);
                                } else {
                                    if (path.endsWith("m3u8")) {

                                        final MergeFilesAlertDialog dialog = new MergeFilesAlertDialog
                                                (ConfigActivity.this, R.style.dialog);
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        KSYRemuxKit ksyRemuxKit = new KSYRemuxKit();
                                        ksyRemuxKit.setOnInfoListener(new KSYRemuxKit.OnInfoListener() {
                                            @Override
                                            public void onInfo(KSYRemuxKit ksyRemuxKit, int type, String msg) {
                                                if (type == KSYRemuxKit.INFO_PUBLISHER_STOPPED) {
                                                    ksyRemuxKit.release();
                                                    dialog.dismiss();
                                                    EditActivity.startActivity(ConfigActivity.this, Environment
                                                            .getExternalStorageDirectory() + "/newRemux" +
                                                            ".mp4");
                                                }
                                            }
                                        });
                                        ksyRemuxKit.setOnErrorListener(new KSYRemuxKit.OnErrorListener() {
                                            @Override
                                            public void onError(KSYRemuxKit ksyRemuxKit, int type, long msg) {
                                                ksyRemuxKit.release();
                                                dialog.dismiss();
                                                Toast.makeText(ConfigActivity.this, "Remux m3u8 " +
                                                        "failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        ksyRemuxKit.start(path, Environment
                                                .getExternalStorageDirectory() + "/newRemux" +
                                                ".mp4");
                                    } else {
                                        Toast.makeText(ConfigActivity.this,
                                                "Do not support this file, please select other File ", Toast
                                                        .LENGTH_LONG).show();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "File select error:" + e);
                            }
                        }

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startTranscode(final List<Uri> srcFiles) {
        mTranscodeDialog = new MergeFilesAlertDialog
                (ConfigActivity.this, R.style.dialog);
        mTranscodeDialog.setCancelable(false);
        mTranscodeDialog.show();

/*       mKsyMergeKit.setEncodeMethod(mTransConfig.encodeMethod);
        mKsyMergeKit.setTargetSize(mTransConfig.width, mTransConfig.height);
        mKsyMergeKit.setVideoKBitrate(mTransConfig.videoBitrate);
        mKsyMergeKit.setAudioKBitrate(mTransConfig.audioBitrate);
        mKsyMergeKit.setAudioChannels(mTransConfig.audioChannel);
        mKsyMergeKit.setAudioSampleRate(mTransConfig.audioSampleRate);
        mKsyMergeKit.setVideoFps(mTransConfig.fps);
        mKsyMergeKit.setVideoDecodeMethod(mTransConfig.decodeMethod);*/
        String outputFile = getTranscodeFileFolder() + "/mergedFile" +
                System.currentTimeMillis() + ".mp4";
        mKsyMergeKit.start(mTransCodeUris, outputFile, null, true);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mKsyMergeKit != null && mTranscodeDialog != null) {
                    mTranscodeDialog.updateProgress((int) mKsyMergeKit.getTranscodeProgress(),
                            mKsyMergeKit.getCurrentTransFileId());
                }
            }
        }, 500, 500);
    }

    private KSYMergeKit.OnInfoListener mOnInfoListener = new KSYMergeKit.OnInfoListener() {
        @Override
        public void onInfo(int type, String msg) {
            switch (type) {
                case KSYMergeKit.INFO_MERGE_FINISH:
                    if (mTranscodeDialog != null) {
                        mTranscodeDialog.dismiss();
                        mTranscodeDialog = null;
                    }
                    EditActivity.startActivity(ConfigActivity.this, msg);
                    break;
                case KSYMergeKit.INFO_TRANSCODE_UNSUPPORT:
                    Log.d(TAG, "onInfo: " + msg);
                    break;
                case KSYMergeKit.INFO_TRANSCODE_STOPBYUSERS:
                    if (mTranscodeDialog != null) {
                        mTranscodeDialog.dismiss();
                        mTranscodeDialog = null;
                    }
                    Log.d(TAG, "onInfo: stopped by user");
                default:
                    break;
            }
        }
    };

    private KSYMergeKit.OnErrorListener mOnErrorListener = new KSYMergeKit.OnErrorListener() {
        @Override
        public void onError(int type, int reason, long msg) {
            Toast.makeText(ConfigActivity.this, "Transcode " +
                    "failed: " + type + "， reason:" + reason, Toast.LENGTH_SHORT).show();

            switch (type) {
                case KSYMergeKit.ERROR_MERGE_EMPTY:
                case KSYMergeKit.ERROR_MERGE_FAILED:
                    if (mTranscodeDialog != null) {
                        mTranscodeDialog.dismiss();
                        mTranscodeDialog = null;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private String getTranscodeFileFolder() {
        String fileFolder = "/sdcard/ksy_sv_transcode_test";
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return fileFolder;
    }

    /**
     * 判断是否是所支持的MIME类型
     */
    private boolean isSupportedMimeType(String mimeType) {
        for (int i = 0; i < SUPPORT_FILE_MIME_TYPE.length; i++) {
            if (mimeType.equals(SUPPORT_FILE_MIME_TYPE[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 支持的MIME类型数组
     */
    private String[] SUPPORT_FILE_MIME_TYPE = new String[]{
            "video/mp4",  //.mp4
            "video/ext-mp4",  //.mp4
            "video/3gpp",   //.3gp
            "video/quicktime" //.mov
    };

    public static ShortVideoConfig getRecordConfig() {
        return mRecordConfig;
    }

    private class MergeFilesAlertDialog extends AlertDialog {
        private TextView mProgress;
        private AlertDialog mConfimDialog;

        protected MergeFilesAlertDialog(Context context, int themID) {
            super(context, themID);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(R.layout.merge_record_files_layout);
            mProgress = (TextView) findViewById(R.id.progress_text);
        }

        public void updateProgress(final int progress, final int index) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgress.getVisibility() != View.VISIBLE) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(String.valueOf(index));
                    builder.append(":");
                    builder.append(String.valueOf(progress));
                    builder.append("%");
                    mProgress.setText(String.valueOf(builder.toString()));
                }
            });
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    mConfimDialog = new Builder(ConfigActivity.this).setCancelable
                            (true)
                            .setTitle("中止导入?")
                            .setNegativeButton("取消", new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    mConfimDialog = null;
                                }
                            })
                            .setPositiveButton("确定", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (mTranscodeDialog != null) {
                                        mTranscodeDialog.dismiss();
                                        mTranscodeDialog = null;
                                    }
                                    mConfimDialog = null;
                                    mKsyMergeKit.stop();
                                }
                            }).show();

                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private void onOutputConfirmClick() {
        //   confirmTransConfig();
        startTranscode(mTransCodeUris);
    }

    private void confirmTransConfig() {
     /*   mTransConfig.encodeProfile = VideoEncodeFormat.ENCODE_PROFILE_BALANCE;
        mTransConfig.decodeMethod = StreamerConstants.DECODE_METHOD_SOFTWARE;
        mTransConfig.encodeMethod = StreamerConstants.ENCODE_METHOD_SOFTWARE;
        mTransConfig.encodeType = AVConst.CODEC_ID_AVC;
        mTransConfig.resolution = StreamerConstants.VIDEO_RESOLUTION_540P;
        mTransConfig.fps = Integer.parseInt("20");
        mTransConfig.videoBitrate = Integer.parseInt("2000");
        mTransConfig.audioBitrate = Integer.parseInt("64");
        mTransConfig.videoCRF = Integer.parseInt("24");
        mTransConfig.width = Integer.parseInt("480");
        mTransConfig.height = Integer.parseInt("480");*/
    }

    private void onOutputEncodeProfileClick(int index) {
        mOutProfileGroup[index].setActivated(true);
        for (int i = 0; i < mOutProfileGroup.length; i++) {
            if (i != index) {
                mOutProfileGroup[i].setActivated(false);
            }
        }
    }
}
