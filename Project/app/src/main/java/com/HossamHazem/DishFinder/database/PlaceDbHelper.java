package com.HossamHazem.DishFinder.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.HossamHazem.DishFinder.database.PlaceContract.FavoriteEntry;

public class PlaceDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movie.db";

    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_PLACE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteEntry.COLUMN_FAVORITE_ID + " TEXT UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_NAME + " TEXT UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_PHONE_NUMBER + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_RATING + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_ADDRESS + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_LAT + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_LNG + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_WEBSITE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORITE_LOGO + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_PLACE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
