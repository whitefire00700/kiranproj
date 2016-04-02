package com.hibs.GPSRoute.Api_Task;

import android.app.Activity;
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
import com.hibs.GPSRoute.Listeners.GetReview_Listener;
import com.hibs.GPSRoute.Pojo.Review;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class GetReview_Process {
    public static final String MODULE = "GetReview_Process";
    public static String TAG = "";

    public String _key = Constants_App.Google_BrowserKey;
    public String Str_Url = "";
    public Activity mActivity;

    String Str_Msg = "",  Str_Key, placeId;
    GetReview_Listener mCallBack;

    public GetReview_Process(Activity mActivity, GetReview_Listener listener, String placeid) {
        try {
            this.mActivity = mActivity;
            mCallBack = listener;
            placeId = URLEncoder.encode(placeid, "UTF-8");
            Str_Url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + _key;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetReviews() {

        TAG = " GetReviews";

        try {
            RequestQueue rq = Volley.newRequestQueue(mActivity);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Str_Url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        parseResponse(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(MODULE, TAG + " UnknownResponse");
                        Str_Msg = e.toString();
                        mCallBack.onReviewsReceivedError(Str_Msg);
                    }


                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("Error: ", error.getMessage());
                            Str_Msg = error.getMessage();
                            mCallBack.onReviewsReceivedError(Str_Msg);
                        }
                    });

            int socketTimeout = 60000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            rq.add(req);
        } catch (Exception e) {
            Log.e(MODULE, TAG + " Exception Occurs - " + e);
            Str_Msg = e.toString();
            mCallBack.onReviewsReceivedError(Str_Msg);
        }
    }

    private void parseResponse(JSONObject response) {

        try {
            Log.e("geocoding_api response", "" + response);
            String status = response.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                if (response.has("result")) {
                    JSONObject resultsObj = response.getJSONObject("result");

                    if (resultsObj.has("reviews")) {
                        JSONArray reviewsArray = resultsObj.getJSONArray("reviews");


                        if (reviewsArray.length() > 0) {
                            ArrayList<Review> reviewArrayList = new ArrayList<>();
                            for (int i = 0; i < reviewsArray.length(); i++) {
                                JSONObject reviewsObject = reviewsArray.getJSONObject(i);
                                String name = "", rating = "", review = "";
                                if (reviewsObject.has("author_name"))
                                    name = reviewsObject.getString("author_name");
                                if (reviewsObject.has("rating"))
                                    rating = reviewsObject.getString("rating");
                                if (reviewsObject.has("text"))
                                    review = reviewsObject.getString("text");
                                Review reviewObj = new Review();
                                reviewObj.setPlaceId(placeId);
                                reviewObj.setName(name);
                                reviewObj.setRating(rating);
                                reviewObj.setReview(review);
                                reviewArrayList.add(reviewObj);
                            }


                            mCallBack.onReviewsReceived(reviewArrayList);
                        } else {
                            mCallBack.onReviewsReceivedError("empty list");
                        }
                    } else {
                        mCallBack.onReviewsReceivedError("empty list");
                    }
                } else {
                    mCallBack.onReviewsReceivedError("empty list");
                }

            } else {
                mCallBack.onReviewsReceivedError("empty list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MODULE, TAG + " UnknownResponse");
            Str_Msg = e.toString();
            mCallBack.onReviewsReceivedError(Str_Msg);
        }


    }

}
