package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hibs.GPSRoute.R;

import java.util.ArrayList;
import java.util.Locale;


public class Activity_SendSMS extends Activity {

	private TextView txtSpeechInput;
	EditText contact;
	TextView number,temptext;
	Button contact_list,SendSms;
	private final int REQ_CODE_SPEECH_SUBJECT1 = 100;
	private static final int RESULT_PICK_CONTACT = 85500;
	Spinner spin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_sms);
		contact=(EditText)findViewById(R.id.getcontacts);
		number=(TextView)findViewById(R.id.number);
		SendSms=(Button)findViewById(R.id.sendSms);
		txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
		temptext=(TextView)findViewById(R.id.temp);
		spin=(Spinner)findViewById(R.id.spinner_sms);
		
	spin.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			  String str = spin.getSelectedItem().toString();
		        temptext.setText(str);			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	});
		
		// hide the action bar
		//getActionBar().hide();


		SendSms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
			try 
			{
			         SmsManager smsManager = SmsManager.getDefault();
			         smsManager.sendTextMessage(number.getText().toString(), null, temptext.getText().toString(), null, null);
			         Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
			      } 
			      
			      catch (Exception e) {
			         Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
			         e.printStackTrace();
			      }
			}
		});

		
		txtSpeechInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
			}
		});
	}

	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_SUBJECT1);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_SUBJECT1: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				txtSpeechInput.setText(result.get(0));
				temptext.setText(result.get(0));
			}
			break;
		}
		 case RESULT_PICK_CONTACT:if(resultCode == RESULT_OK)
		 {
             						contactPicked(data);
             						break;
		 }
		}
	}
	public void pickContact(View v)
	{
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
	    startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
	}
	private void contactPicked(Intent data) {
		Cursor cursor = null;
        try {
        	String phoneNo = null ;
        	String name = null;
        	Uri uri = data.getData();
        	cursor = getContentResolver().query(uri, null, null, null, null);
        	cursor.moveToFirst();

        	int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        	int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        	
        	phoneNo = cursor.getString(phoneIndex);
        	name = cursor.getString(nameIndex);
        	
        	contact.setText(name);
        	number.setText(phoneNo);
        } catch (Exception e) {
        	e.printStackTrace();
        }		
	}
	
	}