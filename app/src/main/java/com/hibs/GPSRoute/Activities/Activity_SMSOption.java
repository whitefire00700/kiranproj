package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.hibs.GPSRoute.Database.SqliteContacts;
import com.hibs.GPSRoute.R;
import com.hibs.GPSRoute.Utils.GPSTracker;
import com.hibs.GPSRoute.Utils.Utility;

public class Activity_SMSOption extends Activity {
    Button B1, B2;
    RadioButton rbNormal, rbEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsto);
        B1 = (Button) findViewById(R.id.btnSendto);
        B2 = (Button) findViewById(R.id.btnsmshome);
        rbNormal = (RadioButton) findViewById(R.id.radioBtnnorm);
        rbEmergency = (RadioButton) findViewById(R.id.radioBtnem);

        rbEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbNormal.setChecked(false);
                }
            }
        });

        rbNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbEmergency.setChecked(false);
                }
            }
        });


        B1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (rbEmergency.isChecked()) {
                    sendEmergencySMS();
                } else {
                    Intent i1 = new Intent(Activity_SMSOption.this, Activity_SendSMS.class);
                    startActivity(i1);
                    finish();
                }
            }
        });

        B2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Activity_SMSOption.this, Activity_Main.class);
                startActivity(i1);
                finish();
            }
        });

    }

    private void sendEmergencySMS() {
        GPSTracker gpsTracker = new GPSTracker(Activity_SMSOption.this);
        SqliteContacts sqliteContacts = new SqliteContacts(Activity_SMSOption.this);

        if (Utility.isLocationEnabled(Activity_SMSOption.this)) {
            String cLat = "" + gpsTracker.getLatitude();
            String cLong = "" + gpsTracker.getLongitude();
            String mobile = sqliteContacts.getNearByContact(cLat, cLong);
            Log.e("Activity_Nearby Mobile", "" + mobile);
            if (mobile.length() > 0) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobile, null, "Emergency!!!!! Contact this number urgently!", null, null);
                showMessage("Success", "Message sent!");

            } else {
                showMessage("Error", "Contacts empty!");

            }
        } else {
            showMessage("Error", "Location service isn't enabled!");
        }

    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
