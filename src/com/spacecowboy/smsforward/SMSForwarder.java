package com.spacecowboy.smsforward;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The configuration screen for the SMS Forwarder. Allows the user to save his preferences in an easy way.
 * 
 * @author Space Cowboy
 *
 */
public class SMSForwarder extends Activity {
	protected static final String TAG = "Configurator";
	private CheckBox chkSMS;
	private CheckBox chkMail;
	private EditText txtPhoneNumber;
	private EditText txtMailAddress;
	private Button btnSave;
	private TextView txtMessage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Obtain handles to GUI-elements
		chkSMS = (CheckBox) findViewById(R.id.chkSMS);
		chkMail = (CheckBox) findViewById(R.id.chkMail);
		txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
		txtMailAddress = (EditText) findViewById(R.id.txtMailAddress);
		btnSave = (Button) findViewById(R.id.btnSave);
		txtMessage = (TextView) findViewById(R.id.txtMessage);

		// Load previously saved values
		SharedPreferences settings = getSharedPreferences(SMSreceiver.PREF_NAME, 0);
		boolean asSMS = settings.getBoolean(SMSreceiver.AS_SMS, false);
		boolean asMail = settings.getBoolean(SMSreceiver.AS_MAIL, false);
		String recipientNumber = settings.getString(SMSreceiver.RECIPIENT_NUMBER, "");
		String recipientMail = settings.getString(SMSreceiver.RECIPIENT_MAIL, "");

		// Set values to GUI elements
		chkSMS.setChecked(asSMS);
		chkMail.setChecked(asMail);
		txtPhoneNumber.setText(recipientNumber);
		txtMailAddress.setText(recipientMail);

		// Register on-click action listener
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "btnSave clicked");
				saveSettings();
			}
		});
	}

	private void saveSettings() {
		boolean asSMS = chkSMS.isChecked();
		boolean asMail = chkMail.isChecked();
		String recipientNumber = txtPhoneNumber.getText().toString();
		String recipientMail = txtMailAddress.getText().toString();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(SMSreceiver.PREF_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		// Make changes
		editor.putBoolean(SMSreceiver.AS_MAIL, asMail);
		editor.putBoolean(SMSreceiver.AS_SMS, asSMS);
		// Only if we are to use them should we overwrite the old
		if (asMail)
			editor.putString(SMSreceiver.RECIPIENT_MAIL, recipientMail);
		if (asSMS)
			editor.putString(SMSreceiver.RECIPIENT_NUMBER, recipientNumber);

		// Commit the edits!
		editor.commit();

		// Display success message
		txtMessage.setText("Settings saved!");
	}
}