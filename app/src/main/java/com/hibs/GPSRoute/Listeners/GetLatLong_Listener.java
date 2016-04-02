package com.hibs.GPSRoute.Listeners;



public interface GetLatLong_Listener
{
    public void onLatLongReceived(String latitude, String longitude, String key);
    public void onLatLongReceivedError(String Str_Message);
}
