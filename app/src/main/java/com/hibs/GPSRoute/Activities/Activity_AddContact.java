package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.hibs.GPSRoute.Api_Task.GetLatLong_Process;
import com.hibs.GPSRoute.Database.SqliteContacts;
import com.hibs.GPSRoute.Listeners.GetLatLong_Listener;
import com.hibs.GPSRoute.Pojo.Contacts;
import com.hibs.GPSRoute.R;
import com.hibs.GPSRoute.Utils.GPSTracker;
import com.hibs.GPSRoute.Utils.Utility;

public class Activity_AddContact extends Activity implements GetLatLong_Listener {

    ImageButton btnhome, btnadd, btndel, btnedit;
    EditText efname, elname, etArea, etAddress, etMobile;
    Spinner contype;
    private Activity mActivity;
    private String fName, lName, mobile, area, address, latitude = "", longitude = "";
    private ProgressDialog pDialog;
    private SqliteContacts sqliteContacts;
    private Contacts contacts;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        mActivity = Activity_AddContact.this;
        pDialog = new ProgressDialog(mActivity);
        sqliteContacts = new SqliteContacts(mActivity);
        contacts = new Contacts();
        gpsTracker = new GPSTracker(mActivity);

        efname = (EditText) findViewById(R.id.efname);
        elname = (EditText) findViewById(R.id.elname);
        etArea = (EditText) findViewById(R.id.Area);
        etAddress = (EditText) findViewById(R.id.Address);
        etMobile = (EditText) findViewById(R.id.mobile);

        btnhome = (ImageButton) findViewById(R.id.btnhome);
        btnadd = (ImageButton) findViewById(R.id.btnadd);
        btndel = (ImageButton) findViewById(R.id.btndel);
        btnedit = (ImageButton) findViewById(R.id.btnedit);


        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Activity_AddContact.this, Activity_Main.class);
                startActivity(i1);
            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validation()) {
                    insert();
                    clearText();
                }

            }
        });
        btnedit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validation()) {
                    update();
                    clearText();
                }
            }
        });
        btndel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               if (efname.getText().toString().trim().length() == 0) {
                    showMessage("Error", "Please enter Proper name");
                    return;
                }
                delete();

            }
        });

    }

    private boolean validation() {
        boolean valid = false;
        fName = efname.getText().toString().trim();
        lName = elname.getText().toString().trim();
        area = etArea.getText().toString().trim();
        address = etAddress.getText().toString().trim();
        mobile = etMobile.getText().toString().trim();

        if (fName.length() == 0 ||
                lName.length() == 0 ||
                area.length() == 0 ||
                address.length() == 0 ||
                mobile.length() == 0) {
            showMessage("Error", "Please enter all values");
        } else {
            contacts = new Contacts().setAndGetContacts(fName, lName, mobile, area, address, latitude, longitude);
            valid = true;
        }
        return valid;
    }

    private void insert() {

        boolean isExist = sqliteContacts.checkContactExists(mobile);
        if (!isExist) {
            if (sqliteContacts.addContact(contacts) != -1) {
                new GetLatLong(area, "source").execute();
                showMessage("Success", "Record added");
                clearText();
            } else {
                showMessage("Error", "Try again later!");
            }

        } else {
            showMessage("Error", "Mobile already exists!");

        }

    }

    private void update() {
        boolean isExist = sqliteContacts.checkContactExists(mobile);
        if (isExist) {
            if (sqliteContacts.updateContact(contacts) != -1) {
                new GetLatLong(area, "source").execute();
                showMessage("Success", "Record modified");
                clearText();
            } else {
                showMessage("Error", "Try again later!");
            }

        } else {
            showMessage("Error", "Invalid mobile");
        }
    }

    private void delete() {

        if (sqliteContacts.deleteContactByMobile(mobile)) {
            showMessage("Success", "Record Deleted");
            clearText();
        } else {
            showMessage("Error", "Invalid mobile");
        }

    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void clearText() {
        efname.setText("");
        elname.setText("");
        etArea.setText("");
        etAddress.setText("");
        etMobile.setText("");
        efname.requestFocus();
    }

    private void updateLatLong() {
        contacts = new Contacts().setAndGetContacts(fName, lName, mobile, area, address, latitude, longitude);
        sqliteContacts.updateContact(contacts);
    }

    private class GetLatLong extends AsyncTask<Void, Void, Void> {
        String city, key;

        GetLatLong(String city, String key) {
            this.city = city;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            getLatLong(city, key);
            return null;
        }

        @Override
        protected void onPreExecute() {
            showpDialog("", "loading...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hidepDialog();
            super.onPostExecute(aVoid);
        }
    }

    private void getLatLong(String city, String key) {
        double[] latlong = Utility.getLatLongByPlace(city, mActivity);
        Log.e("geocode", "" + latlong[0]);
        Log.e("geocode", "" + latlong[1]);
        if (latlong[0] != 0) {
            latitude = "" + latlong[0];
            longitude = "" + latlong[1];
            updateLatLong();
        } else {
            getLatLonViaGoogleAPI(city, key);
        }
    }

    private void getLatLonViaGoogleAPI(String city, String key) {
        GetLatLong_Process getLatLong_process = new GetLatLong_Process(mActivity, Activity_AddContact.this, city, key);
        getLatLong_process.GetLatLong();
    }


    @Override
    public void onLatLongReceived(String latitude, String longitude, String key) {
        Log.e("lat", "" + latitude.toString());
        if (latitude != null) {
            this.latitude = "" + latitude;
            this.longitude = "" + longitude;
            updateLatLong();
        }
    }

    @Override
    public void onLatLongReceivedError(String Str_Message) {
        Log.e("error", "" + Str_Message);
    }

    private void showpDialog(String title, String msg) {
        if (!pDialog.isShowing()) {
            pDialog = ProgressDialog.show(mActivity, title, msg);
            pDialog.show();
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
