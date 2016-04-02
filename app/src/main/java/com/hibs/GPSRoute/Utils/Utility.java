package com.hibs.GPSRoute.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hibs.GPSRoute.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Selva on 28/3/16.
 */
public class Utility {

    private Context _context;

    public Utility() {
    }

    private Context mContext;
    private MaterialDialog progressDialog;

    public Utility(Context con) {
        mContext = con;
    }


    public static void alertBox(Context context, String msg) {
        new MaterialDialog.Builder(context)
                .content(msg)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.txt_color)
                .positiveColorRes(R.color.colorPrimaryDark)
                .positiveText("Ok")
                .show();
    }

    public static void alertBox(Context context, int id) {
        new MaterialDialog.Builder(context)
                .content(context.getResources().getString(id))
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.txt_color)
                .positiveColorRes(R.color.colorPrimaryDark)
                .positiveText("Ok")
                .show();
    }

    public static void alertBox(Context context,String title,String message) {
        new MaterialDialog.Builder(context)
                .content(message)
                .title(title)
                .titleColorRes(R.color.black)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.txt_color)
                .positiveColorRes(R.color.colorPrimaryDark)
                .positiveText("Ok")
                .show();
    }




    public void showProgress(String title, String mes) {
        progressDialog = new MaterialDialog.Builder(mContext)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.txt_color)
                .content(mes)
                .progress(true, 0)
                .show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void hideProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.hide();
            }
        }
    }


    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static double[] getLatLongByPlace(String location, Context context) {
        double[] latLong = {0, 0};
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(context);
                List<Address> addresses = gc.getFromLocationName(location, 1); // get the found Address Objects

                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        latLong[0] = a.getLatitude();
                        latLong[1] = a.getLongitude();
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }
        return latLong;
    }


    public static float getDistance(double sLat, double sLong, double dLat, double dLong) {
        Location locationA = new Location("point A");
        locationA.setLatitude(sLat);
        locationA.setLongitude(sLong);

        Location locationB = new Location("point B");
        locationB.setLatitude(dLat);
        locationB.setLongitude(dLong);

        float distance = locationA.distanceTo(locationB);
        Log.e("distance: ", "" + distance);
        distance = distance / 1000;
        Log.e("distance: ", "" + distance);
        distance = Math.round(distance);
        return distance;
    }

    public static boolean isLocationEnabled(Context context) {
        boolean isNetworkEnabled = false;
        LocationManager locationManager = (LocationManager) context
                .getSystemService(context.LOCATION_SERVICE);
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isNetworkEnabled;
    }


    public static String distance(double latitude, double longitude,
                                  double destination_lat, double destination_lag) {
        // TODO Auto-generated method stub
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");//from
        urlString.append(latitude);
        urlString.append(",");
        urlString.append(longitude);
        urlString.append("&destination=");//to
        urlString.append(destination_lat);
        urlString.append(",");
        urlString.append(destination_lag);
        urlString.append("&mode=walking&sensor=true");
        HttpURLConnection urlConnection = null;
        URL url = null;

        try {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String sDistance = "";
        InputStream inStream;
        try {
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }

            bReader.close();
            inStream.close();
            urlConnection.disconnect();
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray array = object.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject distance = steps.getJSONObject("distance");
            sDistance = distance.getString("text");


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sDistance;
    }
/*
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
      *//*  AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        if(status != null)
            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();*//*

    }*/

}
