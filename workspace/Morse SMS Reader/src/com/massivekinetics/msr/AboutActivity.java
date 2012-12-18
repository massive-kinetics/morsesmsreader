package com.massivekinetics.msr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.massivekinetics.msr.converter.MorseCodeConverter;
import com.mopub.mobileads.MoPubView;

public class AboutActivity extends Activity {
	SharedPreferences prefs;
	EditText etSeparator;
	Button btnTry, btnSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		prefs = getSharedPreferences("msrprefs", 0);
		init();
	}

	void init() {
		etSeparator = (EditText) findViewById(R.id.etSeparator);
		etSeparator.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSeparator.setOnEditorActionListener(donelistener);
		
		String separator = prefs.getString("separator", "");
		if (!separator.equals(""))
			etSeparator.setHint(etSeparator.getHint().toString()
					+ "; Current value = '" + separator + "'");
		btnSet = (Button) findViewById(R.id.btnSet);
		btnTry = (Button) findViewById(R.id.btnTry);
		
		MoPubView mpv = (MoPubView) findViewById(R.id.adview);
		mpv.setAdUnitId("27b3536a3ca111e2bf1612313d143c11");
		mpv.loadAd();
	}

	public void click(View clicked) {
		if (clicked.getId() == R.id.btnTry) {
			tryMorseCode();
		} else {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("separator", etSeparator.getText().toString());
			editor.commit();
		}
	}

	private void tryMorseCode() {
		long[] pattern = MorseCodeConverter.pattern(etSeparator.getText()
				.toString());
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, -1);
	}

	OnEditorActionListener donelistener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				tryMorseCode();
				return true;
			}
			return false;
		}
	};

}
