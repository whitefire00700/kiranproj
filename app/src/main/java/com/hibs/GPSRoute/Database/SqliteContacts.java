package com.hibs.GPSRoute.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hibs.GPSRoute.Pojo.Contacts;
import com.hibs.GPSRoute.Utils.Utility;

import java.util.ArrayList;

public class SqliteContacts extends SQLiteOpenHelper {

    public static final String DbName = "Contacts.db";
    private static final String TbName = "Contacts";

    private static final String FNAME = "fname";
    private static final String LNAME = "lname";
    private static final String AREA = "area";
    private static final String ADDRESS = "address";
    private static final String MOBILE = "mobile";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";


    public SqliteContacts(Context context) {
        super(context, DbName, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS "
                + TbName + "(" + MOBILE + " VARCHAR PRIMARY KEY,"
                + FNAME + " VARCHAR,"
                + LNAME + " VARCHAR,"
                + AREA + " VARCHAR,"
                + ADDRESS + " VARCHAR,"
                + LATITUDE + " VARCHAR,"
                + LONGITUDE + " VARCHAR"
                + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("Drop table if exists " + TbName);
        onCreate(db);
    }

    public int getTotalContactsCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = (int) DatabaseUtils.queryNumEntries(db, TbName);
        db.close();
        return count;
    }

    public boolean checkContactExists(String mobile) {
        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TbName + " WHERE " + MOBILE + "='" + mobile + "'";
        Cursor cursor = db.rawQuery(q, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                flag = true;
            }
        }
        cursor.close();
        db.close();
        return flag;
    }

    public boolean checkContactExistsByName(String name) {
        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TbName + " WHERE " + FNAME + "='" + name + "'";
        Cursor cursor = db.rawQuery(q, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                flag = true;
            }
        }
        cursor.close();
        db.close();
        return flag;
    }

    public Contacts getContactByMobile(String mobile1) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TbName + " WHERE " + MOBILE + "='" + mobile1 + "'";

        Cursor c = db.rawQuery(q, null);

        Contacts contacts = new Contacts();
        if (c.moveToFirst()) {
            String fName, lName, mobile, area, address, latitude, longitude;
            fName = (c.getString(c.getColumnIndex(FNAME)));
            lName = (c.getString(c.getColumnIndex(LNAME)));
            area = (c.getString(c.getColumnIndex(AREA)));
            address = (c.getString(c.getColumnIndex(ADDRESS)));
            latitude = (c.getString(c.getColumnIndex(LATITUDE)));
            mobile = (c.getString(c.getColumnIndex(MOBILE)));
            longitude = (c.getString(c.getColumnIndex(LONGITUDE)));
            contacts = new Contacts().setAndGetContacts(fName, lName, mobile, area, address, latitude, longitude);
        }
        c.close();
        db.close();
        return contacts;
    }

    public Contacts getContactByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TbName + " WHERE " + FNAME + "='" + name + "'";

        Cursor c = db.rawQuery(q, null);

        Contacts contacts = new Contacts();
        if (c.moveToFirst()) {
            String fName, lName, mobile, area, address, latitude, longitude;
            fName = (c.getString(c.getColumnIndex(FNAME)));
            lName = (c.getString(c.getColumnIndex(LNAME)));
            area = (c.getString(c.getColumnIndex(AREA)));
            address = (c.getString(c.getColumnIndex(ADDRESS)));
            latitude = (c.getString(c.getColumnIndex(LATITUDE)));
            mobile = (c.getString(c.getColumnIndex(MOBILE)));
            longitude = (c.getString(c.getColumnIndex(LONGITUDE)));
            contacts = new Contacts().setAndGetContacts(fName, lName, mobile, area, address, latitude, longitude);
        }
        c.close();
        db.close();
        return contacts;
    }



    public ArrayList<Contacts> getAllContacts() {
        Cursor c = null;
        try {
            ArrayList<Contacts> listObj = new ArrayList<Contacts>();
            SQLiteDatabase db = this.getReadableDatabase();
            c = db.rawQuery("select * from " + TbName, null);

            c.moveToFirst();

            while (c.isAfterLast() == false) {
                String fName, lName, mobile, area, address, latitude, longitude;
                fName = (c.getString(c.getColumnIndex(FNAME)));
                lName = (c.getString(c.getColumnIndex(LNAME)));
                area = (c.getString(c.getColumnIndex(AREA)));
                address = (c.getString(c.getColumnIndex(ADDRESS)));
                latitude = (c.getString(c.getColumnIndex(LATITUDE)));
                mobile = (c.getString(c.getColumnIndex(MOBILE)));
                longitude = (c.getString(c.getColumnIndex(LONGITUDE)));
                Contacts contacts = new Contacts().setAndGetContacts(fName, lName, mobile, area, address, latitude, longitude);
                listObj.add(contacts);
                c.moveToNext();
            }
            c.close();
            db.close();
            return listObj;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return null;
    }


    public boolean deleteContactByMobile(String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TbName, MOBILE + "=?", new String[]{mobile});
        db.close();
        return true;
    }

    public boolean deleteUserTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete * from " + TbName);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public int addContact(Contacts obj) {
        int resultId = -1;
        try {
            ContentValues cv = setAndGetContentValues(obj);
            SQLiteDatabase db = this.getWritableDatabase();
            resultId = (int) db.insert(TbName, null, cv);
            Log.e("Total users", "" + getTotalContactsCount());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultId;
    }

    public int updateContact(Contacts obj) {
        int resultId = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = setAndGetContentValues(obj);
            resultId = db.update(TbName, cv, MOBILE + "='" + obj.getMobile() + "'", null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultId;
    }

    private ContentValues setAndGetContentValues(Contacts obj) {
        ContentValues cv = new ContentValues();
        cv.put(FNAME, obj.getfName());
        cv.put(LNAME, obj.getlName());
        cv.put(AREA, obj.getArea());
        cv.put(ADDRESS, obj.getAddress());
        cv.put(MOBILE, obj.getMobile());
        cv.put(LATITUDE, obj.getLatitude());
        cv.put(LONGITUDE, obj.getLongitude());
        return cv;
    }

    public String getNearByContact(String currentLat, String currentLong) {
        String mobile = "";
        try
        {
            double currentLatitude = Double.parseDouble(currentLat);
            double currentLongitude = Double.parseDouble(currentLong);
            ArrayList<Contacts> listObj = getAllContacts();
            float distance = 10000000;
            for (int i = 0; i < listObj.size(); i++) {
                Contacts contacts = listObj.get(i);
                double userLatitude = Double.parseDouble(contacts.getLatitude());
                double userLongitude = Double.parseDouble(contacts.getLongitude());
                float tempDistance = Utility.getDistance(currentLatitude, currentLongitude, userLatitude, userLongitude);
                if (distance > tempDistance) {
                    mobile = contacts.getMobile();
                    distance = tempDistance;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return mobile;
    }


}
