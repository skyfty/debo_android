package com.qcwl.debo.ui.found.jinshanyun;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.jinshanyun.util.HttpRequestTask;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * 引导窗口页
 */

public class ShortVideoActivity extends Activity {
    private static String TAG = "ShortVideoActivity";
    public static String AUTH_SERVER_URI = "http://ksvs-demo.ks-live.com:8321/Auth";//the uri of your appServer
    public static final String COLOPHON_URL = "https://ks3-cn-beijing.ksyun.com/ksplayer/svod_change_log/dist/Android.html";
    //view
    private TextView mStart;
    private Handler mMainHandler;

    //config params
    //auth
    private HttpRequestTask mAuthTask;  //SDK鉴权异步任务
    private HttpRequestTask.HttpResponseListener mAuthResponse;
    private static int MAX_RETRY_COUNT = 3;  //若AKSK请求失败尝试3次
    private int mRetryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.short_video_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mStart = (TextView) findViewById(R.id.tv_start_short_video);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShortVideoActivity.this, ConfigActivity.class);
                startActivity(intent);
            }
        });
        mMainHandler = new Handler();
        checkAuth();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }

        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            mAuthTask.release();
            mAuthTask = null;
        }

        AuthInfoManager.getInstance().removeAuthResultListener(mCheckAuthResultListener);
    }

    /**
     * SDK鉴权
     */
    private void checkAuth() {
        String token = null;
        try {
            InputStream in = getResources().getAssets().open("AuthForTest.pkg");

            int length = in.available();

            byte[] buffer = new byte[length];

            in.read(buffer);

            token = EncodingUtils.getString(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(token)) {
            AuthInfoManager.getInstance().setAuthInfo(token);
            AuthInfoManager.getInstance().checkAuth();
            if (AuthInfoManager.getInstance().getAuthState()) {
                Toast.makeText(ShortVideoActivity.this, "Auth Success", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(ShortVideoActivity.this, "Auth Failed", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            AuthInfoManager.getInstance().addAuthResultListener(new AuthInfoManager.CheckAuthResultListener() {
                @Override
                public void onAuthResult(int i) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            AuthInfoManager.getInstance().removeAuthResultListener(mCheckAuthResultListener);
                            if (AuthInfoManager.getInstance().getAuthState()) {
                                Toast.makeText(ShortVideoActivity.this, "Auth Success", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(ShortVideoActivity.this, "Auth Failed", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            });
            if (mAuthResponse == null) {
                mAuthResponse = new HttpRequestTask.HttpResponseListener() {
                    @Override
                    public void onHttpResponse(int responseCode, String response) {
                        //params response
                        boolean authResult = false;
                        if (responseCode == 200) {
                            try {
                                JSONObject temp = new JSONObject(response);
                                JSONObject data = temp.getJSONObject("Data");
                                int result = data.getInt("RetCode");
                                if (result == 0) {
                                    String authInfo = data.getString("Authorization");
                                    String date = data.getString("x-amz-date");
                                    //初始化鉴权信息
                                    AuthInfoManager.getInstance().setAuthInfo(authInfo, date);
                                    //添加鉴权结果回调接口(不是必须)
                                    AuthInfoManager.getInstance().addAuthResultListener(mCheckAuthResultListener);
                                    //开始向KSServer申请鉴权
                                    AuthInfoManager.getInstance().checkAuth();
                                    authResult = true;
                                } else {
                                    Log.e(TAG, "get auth failed from app server RetCode:" + result);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "get auth failed from app server json parse failed");
                            }
                        } else {
                            Log.e(TAG, "get auth failed from app server responseCode:" + responseCode);
                        }

                        final boolean finalAuthResult = authResult;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!finalAuthResult) {
                                    Toast.makeText(ShortVideoActivity.this, "get auth failed from app " +
                                            "server", Toast.LENGTH_SHORT)
                                            .show();
                                    //鉴权失败，尝试3次
                                    if (mRetryCount < MAX_RETRY_COUNT) {
                                        mRetryCount++;
                                        checkAuth();
                                    }
                                }
                            }
                        });
                    }
                };
            }

            if (mAuthTask != null && mAuthTask.getStatus() != AsyncTask.Status.FINISHED) {
                mAuthTask.cancel(true);
                mAuthTask = null;
            }
            //开启异步任务，向AppServer请求鉴权信息
            mAuthTask = new HttpRequestTask(mAuthResponse);
          //  String url = AUTH_SERVER_URI + "?Pkg=" + getApplicationContext().getPackageName();
            String url = AUTH_SERVER_URI + "?Pkg=" + "com.qcwl.debo";
            Log.d(TAG, "request auth:" + url);
            mAuthTask.execute(url);
        }
    }

    private AuthInfoManager.CheckAuthResultListener mCheckAuthResultListener = new AuthInfoManager
            .CheckAuthResultListener() {
        @Override
        public void onAuthResult(int result) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    AuthInfoManager.getInstance().removeAuthResultListener(mCheckAuthResultListener);
                    if (AuthInfoManager.getInstance().getAuthState()) {
                        Toast.makeText(ShortVideoActivity.this, "Auth Success", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(ShortVideoActivity.this, "Auth Failed", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    };

}
