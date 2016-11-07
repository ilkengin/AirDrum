package com.ilkengin.airdrum;

/* authors :
 * Ahmet Özkul - ahmet.ozkul@itu.edu.tr
 * Rabia Yorgancı - rabia.yorganci@itu.edu.tr
 * 
 * This project is developed a part of study for IROS 2014
 * */

import java.util.ArrayList;
import java.util.HashMap;

import com.ilkengin.airdrum.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;


public class GamePage extends Activity {
	// Sound Pool
	public static final int leftCrashCymbalNumber = R.raw.crash_cymbal_left;
	public static final int rightCrashCymbalNumber = R.raw.crash_cymbal_right;
	public static final int leftSnareNumber = R.raw.snare_drum_left;
	public static final int rightSnareNumber = R.raw.snare_drum_right;
	public static final int leftHighTomNumber = R.raw.high_tom_left;
	public static final int rightHighTomNumber = R.raw.high_tom_right;
	private static SoundPool soundPool;
	private static HashMap<Integer, Integer> soundPoolMap;
	private static boolean bIsTeaching = true;
	
	// To log the data
	public int strAction = 0;
	public long lTimer = 0;

	// exit
	LayoutInflater layoutInflater;
	View popupView;
	PopupWindow popupWindow;

	//
	// pop up start
	LayoutInflater layoutInflaterstart;
	View popupViewstart;
	PopupWindow popupWindowstart;
	//

	// pop up CountDown
	LayoutInflater layoutInflaterCount;
	View popupViewCount;
	PopupWindow popupWindowCount;
	//

	private final long startTime = 30 * 1000;
	private final long interval = 1 * 1000;
	private CountDownTimer countDownTimer;
	
	private Boolean mIsReceiving;
    private ArrayList<ByteArray> mTransferedDataList = new ArrayList<ByteArray>();
    private final static String TAG = "MainActivity";
    private final static boolean DEBUG = false;
    MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_page);
		IntentFilter filter = new IntentFilter();
        filter.addAction(ArduinoCommunicatorService.DATA_RECEIVED_INTENT);
        filter.addAction(ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT);
        registerReceiver(mReceiver, filter);
		// exit
		layoutInflater = (LayoutInflater) getBaseContext().getSystemService(
				LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popup, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		//
		// start
		layoutInflaterstart = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupViewstart = layoutInflaterstart
				.inflate(R.layout.popup_start, null);
		popupWindowstart = new PopupWindow(popupViewstart,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//

		// countDown
		layoutInflaterCount = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupViewCount = layoutInflaterCount.inflate(R.layout.popup_countdown,
				null);
		popupWindowCount = new PopupWindow(popupViewCount,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//

		initSounds(this);

		lTimer = SystemClock.elapsedRealtime();
		countDownTimer = new MyCountDownTimer(startTime, interval);
	}
	
	// SoundPool fonksiyonları
	public static void initSounds(Context context) {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>(8);
		soundPoolMap.put(leftCrashCymbalNumber,
				soundPool.load(context, R.raw.crash_cymbal_left, 1));
		soundPoolMap.put(rightCrashCymbalNumber,
				soundPool.load(context, R.raw.crash_cymbal_right, 2));
		soundPoolMap.put(leftSnareNumber,
				soundPool.load(context, R.raw.snare_drum_left, 3));
		soundPoolMap.put(rightSnareNumber,
				soundPool.load(context, R.raw.snare_drum_right, 4));
		soundPoolMap.put(leftHighTomNumber,
				soundPool.load(context, R.raw.high_tom_left, 5));
		soundPoolMap.put(rightHighTomNumber,
				soundPool.load(context, R.raw.high_tom_right, 6));
	}

	public void playSound(int soundID, boolean bIsLeft) {
		if (soundPool == null || soundPoolMap == null) {
			initSounds(this);
		}
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;

		if (bIsLeft) {
			soundPool.play((Integer) soundPoolMap.get(soundID), volume,
					volume / 10, 1, 0, 1f);
		} else {
			soundPool.play((Integer) soundPoolMap.get(soundID), volume / 10,
					volume, 1, 0, 1f);
		}

	}

	@Override
	public void onWindowFocusChanged(boolean bFocus) {

		super.onWindowFocusChanged(bFocus);

		if (bFocus & bIsTeaching) {
			countDownTimer.start();
			if (!popupWindowCount.isShowing()) {
				Button skipIntro = (Button) popupViewCount
						.findViewById(R.id.skip);

				skipIntro.setOnClickListener(new ImageButton.OnClickListener() {
					@Override
					public void onClick(View v) {
						FileOperations.writeToFile(
								"Bu adımdan sonraki vuruslar test vurusu",
								"Basla",
								(SystemClock.elapsedRealtime() - lTimer));
						lTimer = SystemClock.elapsedRealtime();
						countDownTimer.cancel();
						bIsTeaching = false;
						popupWindowCount.dismiss();

					}
				});

				popupWindowCount.showAtLocation(popupViewCount, Gravity.RIGHT
						| Gravity.BOTTOM, 0, 0);
			}
		}
	}

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			bIsTeaching = false;
			if (!popupWindowstart.isShowing()) {
				ImageButton playTogether = (ImageButton) popupViewstart
						.findViewById(R.id.playTogether);

				playTogether
						.setOnClickListener(new ImageButton.OnClickListener() {
							@Override
							public void onClick(View v) {
								FileOperations
										.writeToFile(
												"Bu adımdan sonraki vuruslar test vurusu",
												"Basla",
												(SystemClock.elapsedRealtime() - lTimer));
								lTimer = SystemClock.elapsedRealtime();
								popupWindowstart.dismiss();
								popupWindowCount.dismiss();
							}
						});

				popupWindowstart.showAtLocation(popupViewstart, Gravity.CENTER,
						0, 0);
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if ((millisUntilFinished / 1000) % 5 == 0) {
				Toast.makeText(getApplicationContext(),
						(millisUntilFinished / 1000) + "", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public void exit() {
		if (!popupWindow.isShowing()) {
			ImageButton yesBtn = (ImageButton) popupView.findViewById(R.id.yes);
			ImageButton noBtn = (ImageButton) popupView.findViewById(R.id.no);
			yesBtn.setImageResource(R.drawable.karakter_1_uzgun);
			noBtn.setImageResource(R.drawable.karakter_1);
			yesBtn.setOnClickListener(new ImageButton.OnClickListener() {
				@Override
				public void onClick(View v) {
					bIsTeaching = true;
					finish();
					Intent myIntent = new Intent(GamePage.this, MainActivity.class);
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

	@Override
	public void onBackPressed() {
		exit();
	}





    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        private void handleTransferedData(Intent intent, boolean receiving) {
            if (mIsReceiving == null || mIsReceiving != receiving) {
                mIsReceiving = receiving;
                mTransferedDataList.add(new ByteArray());
            }
            final ImageView left_cymbal = (ImageView) findViewById(R.id.left_crash_cymbal);
            final ImageView left_snare = (ImageView) findViewById(R.id.left_snare_drum);
            final ImageView left_high = (ImageView) findViewById(R.id.left_high_tom);
            final ImageView right_cymbal = (ImageView) findViewById(R.id.right_crash_cymbal);
            final ImageView right_snare = (ImageView) findViewById(R.id.right_snare_drum);
            final ImageView right_high = (ImageView) findViewById(R.id.right_high_tom);    
            final byte[] newTransferedData = intent.getByteArrayExtra(ArduinoCommunicatorService.DATA_EXTRA);
            final Vibrator vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            String a = new String(newTransferedData);
            if(a.startsWith("RL"))
            {
            	playSound(rightSnareNumber, false);
                long[] pattern = {0,300, 200, 300};
				strAction++;
				FileOperations.writeToFile("Vurus - " + strAction,
						"rightSnareNumber",
						(SystemClock.elapsedRealtime() - lTimer));
				lTimer = SystemClock.elapsedRealtime();
				vibr.vibrate(pattern, -1);
				right_snare.setImageResource(R.drawable.snare_right_pressed);
				try {
		            Thread.sleep(300);
					right_snare.setImageResource(R.drawable.snare_right);
		     }
		    catch (InterruptedException e)
		     {
		              e.printStackTrace();
		     }
            }
            if(a.startsWith("RF"))
            {
            	playSound(rightHighTomNumber, false);
                long[] pattern = {0, 300};
				strAction++;
				FileOperations.writeToFile("Vurus - " + strAction,
						"rightHighTomNumber",
						(SystemClock.elapsedRealtime() - lTimer));
				lTimer = SystemClock.elapsedRealtime();
				vibr.vibrate(pattern, -1);
            }
            if(a.startsWith("RR"))
            {
            	playSound(rightCrashCymbalNumber, false);
                long[] pattern = {0,200, 200, 400};
				strAction++;
				FileOperations.writeToFile("Vurus - " + strAction,
						"rightCrashCymbalNumber",
						(SystemClock.elapsedRealtime() - lTimer));
				lTimer = SystemClock.elapsedRealtime();
				vibr.vibrate(pattern, -1);
            }
            if(a.startsWith("LL"))
            {
            	playSound(leftCrashCymbalNumber, true);
                long[] pattern = {0,400, 200, 200};
				strAction++;
				FileOperations.writeToFile("Vurus - " + strAction,
						"leftCrashCymbalNumber",
						(SystemClock.elapsedRealtime() - lTimer));
				lTimer = SystemClock.elapsedRealtime();
				vibr.vibrate(pattern, -1);
            }
            if(a.startsWith("LF"))
            {
            	playSound(leftHighTomNumber, true);
                long[] pattern = {0,200, 300, 200};
				strAction++;
				FileOperations.writeToFile("Vurus - " + strAction,
						"leftHighTomNumber",
						(SystemClock.elapsedRealtime() - lTimer));
				lTimer = SystemClock.elapsedRealtime();
				vibr.vibrate(pattern, -1);
            }
            if(a.startsWith("LR"))
            {
            	playSound(leftSnareNumber, true);
                long[] pattern = {0,450, 300, 150};
				strAction++;
				FileOperations.writeToFile("Vurus - " + strAction,
						"leftSnareNumber",
						(SystemClock.elapsedRealtime() - lTimer));
				lTimer = SystemClock.elapsedRealtime();
				vibr.vibrate(pattern, -1);
            }
            
            if (DEBUG) Log.i(TAG, "data: " + newTransferedData.length + " \"" + a + "\"");
            }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (DEBUG) Log.d(TAG, "onReceive() " + action);

            if (ArduinoCommunicatorService.DATA_RECEIVED_INTENT.equals(action)) {
                handleTransferedData(intent, true);
            } else if (ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT.equals(action)) {
                handleTransferedData(intent, false);
            }
        }
    };
}
