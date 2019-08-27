package com.hyphenate.easeui.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.util.EMLog;

import java.io.File;

/**
 * show the video
 * 
 */
public class EaseShowVideoActivity extends EaseBaseActivity{
	private static final String TAG = "ShowVideoActivity";
	
	private RelativeLayout loadingLayout;
	private ProgressBar progressBar;
	private String localFilePath;
	private VideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ease_showvideo_activity);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		videoView = (VideoView)findViewById(R.id.videoView);
		final EMMessage message = getIntent().getParcelableExtra("msg");
		if (!(message.getBody() instanceof EMVideoMessageBody)) {
			Toast.makeText(EaseShowVideoActivity.this, "Unsupported message body", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		EMVideoMessageBody messageBody = (EMVideoMessageBody)message.getBody();

		localFilePath = messageBody.getLocalUrl();

		if (localFilePath != null && new File(localFilePath).exists()) {
			/*Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(localFilePath)),
					"video/mp4");
			startActivity(intent);
			finish();*/
			progressBar.setVisibility(View.GONE);
			playVideo(Uri.parse(localFilePath));
		} else {
			Log.i(TAG, "download remote video file");
			//playVideo(Uri.parse(messageBody.getRemoteUrl()+".mp4"));
			downloadVideo(message);
		}

	}
	//视频播放
	private void playVideo(Uri uri) {
		videoView.setMediaController(new MediaController(this));
		videoView.setVideoURI(uri);
		videoView.start();
	}

	/**
	 * show local video
	 * @param localPath -- local path of the video file
	 */
	private void showLocalVideo(String localPath){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(localPath)),
				"video/mp4");
		startActivity(intent);
		finish();
	}

	/**
	 * download video file
	 */
	private void downloadVideo(EMMessage message) {
		message.setMessageStatusCallback(new EMCallBack() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressBar.setVisibility(View.GONE);
						//showLocalVideo(localFilePath);
						playVideo(Uri.parse(localFilePath));
					}
				});
			}

			@Override
			public void onProgress(final int progress,String status) {
				Log.d("ease", "video progress:" + progress);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

					}
				});

			}

			@Override
			public void onError(int error, String msg) {
				Log.e("###", "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists()) {
					file.delete();
				}
			}
		});
		EMClient.getInstance().chatManager().downloadAttachment(message);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
 

}
