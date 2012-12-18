package com.massivekinetics.msr.receiver;

import com.massivekinetics.msr.data.SmsEntity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	public interface SmsListener {
		void onSmsReceived(SmsEntity sms);
	}

	private static SmsListener mListener;

	public static void setListener(SmsListener listener) {
		mListener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras(); // ---get the SMS message passed
												// in---
			SmsMessage[] msgs = null;
			String msg_from;
			if (bundle != null) {
				// ---retrieve the SMS message received---
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						// msg_from = msgs[i].getOriginatingAddress();

						if (mListener != null)
							mListener.onSmsReceived(new SmsEntity(msgs[i]
									.getTimestampMillis(), msgs[i]
									.getDisplayMessageBody(), msgs[i]
									.getOriginatingAddress()));

					}
					 
				} catch (Exception e) {
					// Log.d("Exception caught",e.getMessage());
				}

			}
		}

	}

}
