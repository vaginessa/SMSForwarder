package com.spacecowboy.smsforward;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Reads incoming SMS and forwards them.
 * 
 * @author Space Cowboy
 *
 */
public class SMSreceiver extends BroadcastReceiver {

	private boolean asSMS = false;
	private boolean asMail = false;
	private String recipientNumber = "";
	private String recipientMail = "";
	// Preferences names
	public static final String AS_SMS = "asSMS";
	public static final String AS_MAIL = "asMail";
	public static final String RECIPIENT_NUMBER = "recipientNumber";
	public static final String RECIPIENT_MAIL = "recipientMail";
	public static final String PREF_NAME = "spacecowboy.forwarder.prefs";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get user preferences
		SharedPreferences settings = context.getSharedPreferences(PREF_NAME, 0);
		asSMS = settings.getBoolean(AS_SMS, false);
		asMail = settings.getBoolean(AS_MAIL, false);
		recipientNumber = settings.getString(RECIPIENT_NUMBER, "");
		recipientMail = settings.getString(RECIPIENT_MAIL, "");

		// ---get the SMS message passed in---
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");

			// Should perhaps parse them together, to support multi-messaging.
			// But not sure how to do that in a nice way
			for (Object pdu : pdus) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
				// Check how we should forward the message here
				if (asSMS)
					forwardSMStoPhone(sms);
				if (asMail)
					forwardSMStoMail(sms);
			}

			// ---display the new SMS message---
			// Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Construct a new message, with the sender's name parsed in
	 * 
	 * @param sms
	 * @return
	 */
	private String parseMessage(SmsMessage sms) {
		// Should try and get the name from the contact list here
		String sender = sms.getOriginatingAddress();

		String message = sms.getMessageBody();

		// Construct a new message, with the sender's name parsed in
		// The first word will get parsed out and used as sender it appears, it
		// even matches contact names on target phone on the emulator
		String newMessage = sender + " ";
		newMessage += message;

		return newMessage;
	}

	/**
	 * Forwards an SMS to another phone-number
	 * 
	 * @param sms
	 */
	private void forwardSMStoPhone(SmsMessage sms) {
		String newMessage = parseMessage(sms);

		// Divide it to support multi-messaging
		ArrayList<String> parts = SmsManager.getDefault().divideMessage(newMessage);

		// Should have some intents here to handle errors while sending
		if (!recipientNumber.equals(""))
			SmsManager.getDefault().sendMultipartTextMessage(recipientNumber, null, parts, null, null);
	}

	/**
	 * Forwards an SMS to an e-mail address
	 * 
	 * @param sms
	 */
	private void forwardSMStoMail(SmsMessage sms) {
		@SuppressWarnings("unused")
		String newMessage = parseMessage(sms);

	}

}
