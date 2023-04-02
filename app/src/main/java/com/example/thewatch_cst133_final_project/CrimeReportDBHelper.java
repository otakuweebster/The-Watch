package com.example.thewatch_cst133_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Purpose: The purpose of the class is to help save and retrieve crime reports to the crime db.
 * @author CST133
 */
public class CrimeReportDBHelper extends SQLiteOpenHelper
{
    /**
     * This portions creates the DB fields name
     */
    private static final String DB_NAME = "crimereport.db";
    private static final String TABLE_NAME = "CrimeReports";
    private static final String ID = "_id";
    public static final String OFFENCE = "offence";
    public static final String NEIGBOURHOOD = "neigborhood";
    public static final String DESCRIPTION_CRIME = "descriptionCrime";
    public static final String DESCRIPTION_SUSPECT = "descriptionSuspect";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String LATITUDE = "latitude";
    public static final String LONGTITUDE = "longtitude";
    public static final String PHOTO = "image_report";
    private static final int DB_VERSION = 1;

    public SQLiteDatabase sqlDB;

    public CrimeReportDBHelper(Context context) {super(context, DB_NAME, null, DB_VERSION);}

    public void open() throws SQLException
    {
        sqlDB = this.getWritableDatabase();
    }

    public void close()
    {
        sqlDB.close();
    }

    //CREATES ANY TABLES to the database.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sCreate = "CREATE TABLE " +
                TABLE_NAME + " (" +
                ID + " integer primary key autoincrement, " +
                OFFENCE + " text not null, " +
                NEIGBOURHOOD + " text not null, " +
                DESCRIPTION_CRIME + " text not null, " +
                DESCRIPTION_SUSPECT + " text not null, " +
                TIME + " text not null, " +
                DATE + " text not null, " +
                LATITUDE + " double not null, " +
                LONGTITUDE + " double not null, " +
                PHOTO + " BLOB);";

        //RUN THE COMMAND
        db.execSQL(sCreate);

    }

    /**
     * This basically creates a table if you want to.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //drop the table if its already there
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * This basically grabs the CrimeReport, dissect it one by one by grabbing its values,and sends it to the database.
     * @param obReport
     * @return
     */
    public long addCrimeReport(CrimeReport obReport)
    {
        ContentValues cvs = new ContentValues();
        cvs.put(OFFENCE, obReport.offence);
        cvs.put(NEIGBOURHOOD, obReport.neighborhood);
        cvs.put(DESCRIPTION_CRIME, obReport.descriptionCrime);
        cvs.put(DESCRIPTION_SUSPECT, obReport.descriptionSuspect);
        cvs.put(TIME, obReport.time);
        cvs.put(DATE, obReport.date);
        cvs.put(LATITUDE, obReport.latitude);
        cvs.put(LONGTITUDE, obReport.longtitude);
        cvs.put(PHOTO, DbBitmapUtility.getBytes(obReport.incidentPhoto));

        long autoID = sqlDB.insert(TABLE_NAME, null, cvs);
        obReport.id = autoID;

        return autoID;
    }

    /**
     * It returns all of queried crimes from the database.
     * @return
     */
    public Cursor getAllCrimeReports()
    {
        String[] sFields = new String [] {ID, OFFENCE, NEIGBOURHOOD,  DESCRIPTION_CRIME, DESCRIPTION_SUSPECT,TIME,DATE, LATITUDE, LONGTITUDE, PHOTO };
        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null);
    }

    /**
     * It returns a queried crimes from the database based on their ID.
     * @param id
     * @return
     * @throws SQLException
     */
    public Cursor getSpecificCrimeReports(long id) throws SQLException
    {
        String[] sFields = new String [] {ID, OFFENCE, NEIGBOURHOOD,  DESCRIPTION_CRIME, DESCRIPTION_SUSPECT,TIME,DATE, LATITUDE, LONGTITUDE, PHOTO };
        Cursor cursor = sqlDB.query(TABLE_NAME, sFields, ID + " = " + id, null, null, null, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * CREDITS TO Lazy Ninja for converting bitmap from bytes to bitmap or bitmap to bytes needed to store into the database.
     * Hence, this is the reason why the photo in the db needs to be placed as a blob to store it as binary values and when it returns that,
     * one method below converts the bytes into a readable bitmap.
     */
    public static class DbBitmapUtility {

        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }
}
