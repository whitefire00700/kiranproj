package com.hibs.GPSRoute.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class SqliteReview extends SQLiteOpenHelper {

    public static final String DbName = "Reviews.db";
    private static final String TbName = "Reviews";

    private static final String PLACE_ID = "place_id";
    private static final String RATING = "rating";
    private static final String REVIEW = "review";


    public SqliteReview(Context context) {
        super(context, DbName, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS "
                + TbName + "(" + PLACE_ID + " VARCHAR PRIMARY KEY,"
                + RATING + " VARCHAR,"
                + REVIEW + " VARCHAR"
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

    public boolean checkReviewExists(String placeId) {
        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TbName + " WHERE " + PLACE_ID + "='" + placeId + "'";
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


    public ArrayList<String> getReview(String placeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TbName + " WHERE " + PLACE_ID + "='" + placeId + "'";
        Cursor c = db.rawQuery(q, null);
        ArrayList<String> arrayList = new ArrayList<>();
        if (c.moveToFirst()) {
            String rating = (c.getString(c.getColumnIndex(RATING)));
            String review = (c.getString(c.getColumnIndex(REVIEW)));
            arrayList.add(rating);
            arrayList.add(review);
        }
        c.close();
        db.close();
        return arrayList;
    }


    public boolean deleteReviewTable() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete * from " + TbName);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public int addReview(String placeId, String rating, String review) {
        int resultId = -1;
        try {
            ContentValues cv = new ContentValues();
            cv.put(PLACE_ID, placeId);
            cv.put(RATING, rating);
            cv.put(REVIEW, review);
            SQLiteDatabase db = this.getWritableDatabase();
            resultId = (int) db.insert(TbName, null, cv);
            Log.e("Total reviews", "" + getTotalContactsCount());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultId;
    }

    public int updateReview(String placeId, String rating, String review) {
        int resultId = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(RATING, rating);
            cv.put(REVIEW, review);
            resultId = db.update(TbName, cv, PLACE_ID + "='" + placeId + "'", null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultId;
    }


}
