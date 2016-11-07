package com.ilkengin.airdrum;

import com.ilkengin.airdrum.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.VideoView;

public class VideoTeach extends Activity {
	VideoView myVideoView;
	Button skipVideo;
	public String strVideoPath = "android.resource://";
	// exit
	LayoutInflater layoutInflater;
	View popupView;
	PopupWindow popupWindow;

	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_teach);
		// exit
		layoutInflater = (LayoutInflater) getBaseContext().getSystemService(
				LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popup, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		//

		myVideoView = (VideoView) findViewById(R.id.videoTeach);
		myVideoView.setVideoURI(Uri.parse(strVideoPath + getPackageName() + "/"
				+ R.raw.v3_trainingli));
		myVideoView.requestFocus();
		myVideoView.start();

		myVideoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						Intent myIntent = new Intent(VideoTeach.this,
								GamePage.class);
						startActivityForResult(myIntent, 0);
					}
				});

		skipVideo = (Button) findViewById(R.id.skip);
		skipVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(VideoTeach.this, GamePage.class);
				startActivityForResult(myIntent, 0);
			}

		});
	}

	@Override
	public void onBackPressed() {
		if (!popupWindow.isShowing()) {
			ImageButton yesBtn = (ImageButton) popupView.findViewById(R.id.yes);
			ImageButton noBtn = (ImageButton) popupView.findViewById(R.id.no);
			yesBtn.setImageResource(R.drawable.karakter_1_uzgun);
			noBtn.setImageResource(R.drawable.karakter_1);
			yesBtn.setOnClickListener(new ImageButton.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					Intent myIntent = new Intent(VideoTeach.this,
							MainActivity.class);
					startActivityForResult(myIntent, 0);
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(startMain);
				}
			});
			noBtn.setOnClickListener(new ImageButton.OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
				}

			});
			popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
		}
	}
}
