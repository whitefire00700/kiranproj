package com.hibs.GPSRoute.Listeners;


import com.hibs.GPSRoute.Pojo.Review;

import java.util.ArrayList;

public interface GetReview_Listener
{
    public void onReviewsReceived(ArrayList<Review> list);
    public void onReviewsReceivedError(String Str_Message);
}
