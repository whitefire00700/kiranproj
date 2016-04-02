package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.hibs.GPSRoute.Api_Task.GetLatLong_Process;
import com.hibs.GPSRoute.Listeners.AccelerometerListener;
import com.hibs.GPSRoute.Listeners.GetLatLong_Listener;
import com.hibs.GPSRoute.R;
import com.hibs.GPSRoute.Utils.AccelerometerManager;
import com.hibs.GPSRoute.Utils.GPSTracker;
import com.hibs.GPSRoute.Utils.Utility;


public class Activity_Main extends Activity implements AccelerometerListener, GetLatLong_Listener {
    private RelativeLayout rl;
    private ImageView btnShowdistance, btnShowroute, btnShownearby, btnperson, btnSearch2, btnsendsms;
    private String currentLat = "", currentLong = "", desLat = "", desLong = "";
    private EditText dest;
    private Utility utility;
    private GPSTracker gps;
    private Activity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initUI();
    }

    private void initUI() {
        mActivity = Activity_Main.this;
        utility = new Utility(mActivity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnShowdistance = (ImageView) findViewById(R.id.btnShowdistance);
        btnShowroute = (ImageView) findViewById(R.id.btnShowroute);
        btnShownearby = (ImageView) findViewById(R.id.btnShownearby);

        btnsendsms = (ImageView) findViewById(R.id.btnsmsact);
        btnperson = (ImageView) findViewById(R.id.btnperson);
        btnSearch2 = (ImageView) findViewById(R.id.btnsearch2);
        dest = (EditText) findViewById(R.id.dest);
        initListeners();

    }

    private void initListeners() {
        btnShowdistance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                findDistance();
            }
        });

        btnShowroute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoute();
            }
        });

        btnShownearby.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onPickButtonClick();
            }
        });

        btnperson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Activity_Main.this, Activity_AddContact.class);
                startActivity(i1);
            }
        });
        btnSearch2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Activity_Main.this, Activity_ViewContacts.class);
                startActivity(i1);
            }
        });

        btnsendsms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Activity_Main.this, Activity_SMSOption.class);
                startActivity(i1);
            }
        });
    }

    private void showRoute() {
        gps = new GPSTracker(Activity_Main.this);

        if (gps.canGetLocation()) {

            if (dest.getText().toString().length() != 0) {
                String address = dest.getText().toString().trim();
                address = address.replace(' ', '+');
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            } else {
                            /*   Alert Dialog Code Start*/
                Utility.alertBox(Activity_Main.this, "Field Error", "Please enter your destination");
                return;

            }
        } else {
            gps.showSettingsAlert();
        }
    }


    private void findDistance() {
        gps = new GPSTracker(Activity_Main.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            currentLat = "" + gps.getLatitude();
            currentLong = "" + gps.getLongitude();
            if (dest.getText().toString().length() != 0) {
                new GetLatLong(dest.getText().toString().trim(), "source").execute();
            } else {
                Utility.alertBox(Activity_Main.this, "Field Error", "Please enter your destination");
                return;
            }
        } else {
            gps.showSettingsAlert();
        }

    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force) {
       /* Toast.makeText(getBaseContext(), "Motion detected",
                Toast.LENGTH_LONG).show();*/
        Intent i = new Intent(Activity_Main.this, Activity_SMSOption.class);
        startActivity(i);

    }

    public void onResume() {
        super.onResume();
      /*  Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
                Toast.LENGTH_LONG).show();*/

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            AccelerometerManager.stopListening();

           /* Toast.makeText(getBaseContext(), "onStop Accelerometer Stoped",
                    Toast.LENGTH_LONG).show();*/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service  distroy");

        if (AccelerometerManager.isListening()) {

            AccelerometerManager.stopListening();
          /*  Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stoped",
                    Toast.LENGTH_LONG).show();*/
        }

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
            utility.showProgress("", "loading..");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (desLat.length() > 0) {
                calulateAndShowDistance();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void getLatLong(String city, String key) {
        double[] latlong = Utility.getLatLongByPlace(city, mActivity);
        Log.e("getLatLong", "" + latlong[0]);
        Log.e("getLatLong", "" + latlong[1]);
        if (latlong[0] != 0) {
            desLat = "" + latlong[0];
            desLong = "" + latlong[1];
        } else {
            getLatLonViaGoogleAPI(city, key);
        }
    }

    private void getLatLonViaGoogleAPI(String city, String key) {
        GetLatLong_Process getLatLong_process = new GetLatLong_Process(mActivity, Activity_Main.this, city, key);
        getLatLong_process.GetLatLong();
    }


    @Override
    public void onLatLongReceived(String latitude, String longitude, String key) {
        Log.e("lat", "" + latitude.toString());
        if (latitude != null) {
            desLat = "" + latitude;
            desLong = "" + longitude;
            calulateAndShowDistance();
        }
    }

    private void calulateAndShowDistance() {
        utility.hideProgress();
        double lat1 = Double.parseDouble(currentLat);
        double long1 = Double.parseDouble(currentLong);
        double lat2 = Double.parseDouble(desLat);
        double long2 = Double.parseDouble(desLong);
        float distance = Utility.getDistance(lat1, long1, lat2, long2);
        Utility.alertBox(mActivity, "You have to travel " + distance + "KM");
    }

    @Override
    public void onLatLongReceivedError(String Str_Message) {
        Log.e("error", "" + Str_Message);
        utility.hideProgress();
    }

    public void onPickButtonClick() {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, 100);

        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == 100
                && resultCode == Activity.RESULT_OK) {

            Place place = PlacePicker.getPlace(data, this);

            String placeId = place.getId();
            Log.e("placeId", "" + placeId);
            Intent intent = new Intent(mActivity, Activity_Review.class);
            intent.putExtra("placeId", placeId);
            startActivity(intent);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
