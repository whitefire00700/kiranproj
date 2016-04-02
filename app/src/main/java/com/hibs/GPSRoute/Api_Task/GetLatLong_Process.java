package com.hibs.GPSRoute.Api_Task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hibs.GPSRoute.Constants.Constants_App;
import com.hibs.GPSRoute.Listeners.GetLatLong_Listener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class GetLatLong_Process {
    public static final String MODULE = "GetLatLong_Process";
    public static String TAG = "";

    public String _key = Constants_App.Google_BrowserKey;
    public String Str_Url = "";
    public Activity mActivity;

    String Str_Msg = "", Str_Code = "", Str_Key;
    GetLatLong_Listener mCallBack;
    SharedPreferences.Editor editor;
    Object object;

    public GetLatLong_Process(Activity mActivity, GetLatLong_Listener listener, String city, String str_key) {
        try {
            this.mActivity = mActivity;
            mCallBack = listener;
            city = URLEncoder.encode(city, "UTF-8");
            Str_Url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + city + "&key=" + _key;
            Str_Key = str_key;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetLatLong() {

        TAG = " GetStations";

        try {
            RequestQueue rq = Volley.newRequestQueue(mActivity);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Str_Url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.e("geocoding_api response", "" + response);
                        String status = response.getString("status");
                        if (status.equalsIgnoreCase("OK")) {
                            JSONArray array = response.getJSONArray("results");
                            JSONObject obj = array.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            String lat = obj.getString("lat");
                            String lon = obj.getString("lng");
                            mCallBack.onLatLongReceived("" + Double.parseDouble(lat), "" + Double.parseDouble(lon), Str_Key);

                        } else {
                            mCallBack.onLatLongReceived(null, null, Str_Key);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(MODULE, TAG + " UnknownResponse");
                        Str_Msg = e.toString();
                        mCallBack.onLatLongReceivedError(Str_Msg);
                    }


                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("Error: ", error.getMessage());
                            Str_Msg = error.getMessage();
                            mCallBack.onLatLongReceivedError(Str_Msg);
                        }
                    });

            int socketTimeout = 60000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            rq.add(req);
        } catch (Exception e) {
            Log.e(MODULE, TAG + " Exception Occurs - " + e);
            Str_Msg = e.toString();
            mCallBack.onLatLongReceivedError(Str_Msg);
        }
    }

}
