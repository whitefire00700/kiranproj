package com.hibs.GPSRoute.Pojo;

/**
 * Created by Selva on 28/3/16.
 */
public class Contacts {
    private String fName,lName,mobile,area,address,latitude,longitude;

    public Contacts setAndGetContacts(String fName,String lName,String mobile,String area,String address,String latitude,String longitude)
    {
        Contacts contacts=new Contacts();
        contacts.setAddress(address);
        contacts.setArea(area);
        contacts.setfName(fName);
        contacts.setlName(lName);
        contacts.setLatitude(latitude);
        contacts.setLongitude(longitude);
        contacts.setMobile(mobile);
        return  contacts;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
