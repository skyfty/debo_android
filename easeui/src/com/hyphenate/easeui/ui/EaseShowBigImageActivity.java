/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseLoadLocalBigImgTask;
import com.hyphenate.easeui.view.PinchImageView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import okhttp3.Call;

/**
 * download and show original image
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {
    private static final String TAG = "ShowBigImage";
    private ProgressDialog pd;
    private PinchImageView image;
    private int default_res = R.drawable.ease_default_image;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private String msgId = null;
    Uri uri;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ease_activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (PinchImageView) findViewById(R.id.image);
        ProgressBar loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_avatar);
        uri = getIntent().getParcelableExtra("uri");
        localFilePath = getIntent().getExtras().getString("localUrl");
        msgId = getIntent().getExtras().getString("messageId");
//        Glide.with(this).load(uri).into(image);
        registerForContextMenu(image);
//        EMLog.d(TAG, "show big msgId:" + msgId);
//
//        //show the image if it exist in local path
        if (uri != null && new File(uri.getPath()).exists()) {
            EMLog.d(TAG, "showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = EaseImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (msgId != null) {
            downloadImage(msgId);
        } else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "保存");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                FileInputStream fis = null;
                FileOutputStream fos = null;
                File dstFile=null;
                try {
                    File path = new File(getPictureDir(this));
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                      dstFile = new File(path, System.currentTimeMillis() + ".jpg");
                    if (!dstFile.exists()) {
                        path.createNewFile();
                    }
                    if (uri == null) {
                        fis = new FileInputStream(new File(localFilePath));
                    } else {
                        fis = new FileInputStream(new File(uri.getPath()));
                    }
                    fos = new FileOutputStream(dstFile);

                    byte[] data = new byte[1024];
                    int len = 0;
                    while ((len = fis.read(data)) != -1) {
                        fos.write(data, 0, len);
                    }
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                        fos.close();
                        Toast.makeText(EaseShowBigImageActivity.this, "图片已保存至debo/picture/文件夹", Toast.LENGTH_SHORT).show();
                        remindPhoto(dstFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //downloadPic(localFilePath);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    // 根目录名称
    private static final String mainDir = "debo";

    /**
     * 文件根目录
     */
    public static final String getMainDir() {
        return Environment.getExternalStorageDirectory() + "/" + mainDir;
    }

    /**
     * 图片的目录
     */
    public static final String getPictureDir(Context context) {
        String dir = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            dir = getMainDir() + "/picture";
        } else {
            // /data/data/pkg/cache
            dir = context.getCacheDir().toString();
        }
        return dir;
    }

  /*  private void downloadPic(String imageUrl) {
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/"));
        OkHttpUtils.get().url(imageUrl)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new FileCallBack(getPictureDir(this), filename) {
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
                                Toast.makeText(EaseShowBigImageActivity.this, "图片已保存至debo/picture/文件夹", Toast.LENGTH_SHORT).show();
                                remindPhoto(response);
                            }
                        });
                    }
                });
    }*/

    private void remindPhoto(File file) {
        if (file != null) {
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "title", "description");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * download image
     */
    @SuppressLint("NewApi")
    private void downloadImage(final String msgId) {
        EMLog.e(TAG, "download with messageId: " + msgId);
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        File temp = new File(localFilePath);
        final String tempPath = temp.getParent() + "/temp_" + temp.getName();
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {
                EMLog.e(TAG, "onSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new File(tempPath).renameTo(new File(localFilePath));

                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                        if (bitmap == null) {
                            image.setImageResource(default_res);
                        } else {
                            image.setImageBitmap(bitmap);
                            EaseImageCache.getInstance().put(localFilePath, bitmap);
                            isDownloaded = true;
                        }
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                EMLog.e(TAG, "offline file transfer error:" + msg);
                File file = new File(tempPath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        image.setImageResource(default_res);
                        pd.dismiss();
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                EMLog.d(TAG, "Progress: " + progress);
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };

        EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
        msg.setMessageStatusCallback(callback);

        EMLog.e(TAG, "downloadAttachement");
        EMClient.getInstance().chatManager().downloadAttachment(msg);
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
