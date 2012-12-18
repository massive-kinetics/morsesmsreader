package com.massivekinetics.msr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.massivekinetics.msr.converter.MorseCodeConverter;
import com.massivekinetics.msr.data.SmsAdapter;
import com.massivekinetics.msr.data.SmsEntity;
import com.massivekinetics.msr.receiver.SmsReceiver;
import com.massivekinetics.msr.receiver.SmsReceiver.SmsListener;

public class MainActivity extends Activity implements SmsListener {
	SmsAdapter adapter;
	static List<SmsEntity> items = new ArrayList<SmsEntity>();

	static boolean isVibrating = false;
	Handler vibratorHandler = new Handler();

	SharedPreferences prefs;
	File counterFile;

	AudioManager audioManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		prefs = getSharedPreferences("msrprefs", 0);
		audioManager = (AudioManager) getBaseContext().getSystemService(
				Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

		File sdcard = Environment.getExternalStorageDirectory();
		counterFile = new File(sdcard, "androidversion.dat");
		try {
			counterFile.createNewFile();
		} catch (IOException e) {

		}

		SmsReceiver.setListener(this);
		updateListView();

	}

	private void updateListView() {
		List<SmsEntity> newList = new ArrayList<SmsEntity>(items);
		Collections.copy(newList, items);
		Collections.reverse(newList);
		SmsAdapter adapter = new SmsAdapter(getApplicationContext(), newList);
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onSmsReceived(SmsEntity sms) {
		items.add(sms);
		vibratorHandler.post(new VibratorRunnable(sms.body));
		updateListView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		if (menuId == R.id.menu_exit) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			moveTaskToBack(true);
			finish();

			// Kill process and force app to shut down!
			try {
				System.runFinalizersOnExit(true);
				System.exit(0);
			}
			// Force app to stop if above fails.
			catch (Exception e) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		} else if (menuId == R.id.menu_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}

	}

	private void updateWorkCounter() {
		SharedPreferences.Editor editor = prefs.edit();
		int count = prefs.getInt("count", 0);
		editor.putInt("count", ++count);
		editor.commit();

		try {
			FileWriter writer = new FileWriter(counterFile, true);
			BufferedWriter bWriter = new BufferedWriter(writer);
			bWriter.write("+");
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
		}
	}

	private boolean checkAllowedWork() {
		long size = -1;
		try {
			size = new BufferedReader(new FileReader(counterFile)).readLine()
					.length();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (counterFile == null || size >= 5)
			return false;

		int count = prefs.getInt("count", 0);
		return (count >= 5) ? false : true;
	}

	private void notifyLimited() {
		Toast.makeText(this, "Functionality is limited", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onBackPressed() {
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		super.onBackPressed();
	}

	class VibratorRunnable implements Runnable {
		String mMessage;

		public VibratorRunnable(String message) {
			mMessage = message;
		}

		@Override
		public void run() {
			if (isVibrating)
				vibratorHandler.postDelayed(new VibratorRunnable(mMessage),
						5000);
			else {
				isVibrating = true;
				try {
					vibrate();
				} finally {
					isVibrating = false;
				}
			}
		}

		private void vibrate() {
			String separator = prefs.getString("separator", "");

			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			if (!separator.equals("")) {
				long[] pattern = MorseCodeConverter.pattern(separator);
				vibrator.vibrate(pattern, -1);
			}
			long[] pattern = MorseCodeConverter.pattern(mMessage);
			vibrator.vibrate(pattern, -1);
		}

	}
}
