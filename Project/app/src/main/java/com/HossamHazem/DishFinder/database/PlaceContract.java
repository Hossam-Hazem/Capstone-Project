package com.HossamHazem.DishFinder.database;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.HossamHazem.DishFinder.utils.Place;

import java.util.ArrayList;

public class PlaceContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.pc.dishfinder";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_FAVORITE = "favorite";

    /* Inner class that defines the table contents of the favorite table */
    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        // Table name
        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_FAVORITE_ID = "place_id";

        public static final String COLUMN_FAVORITE_NAME = "name";

        public static final String COLUMN_FAVORITE_PHONE_NUMBER = "phone_number";

        public static final String COLUMN_FAVORITE_ADDRESS = "address";

        public static final String COLUMN_FAVORITE_LAT = "lat";

        public static final String COLUMN_FAVORITE_LNG = "lng";

        public static final String COLUMN_FAVORITE_RATING = "rating";

        public static final String COLUMN_FAVORITE_WEBSITE = "website";

        public static final String COLUMN_FAVORITE_LOGO = "logo";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static boolean insert(Context context, Place place) {

            ContentValues placeValues = new ContentValues();
            placeValues.put(COLUMN_FAVORITE_ID, place.getId());
            placeValues.put(COLUMN_FAVORITE_NAME, place.getName());
            placeValues.put(COLUMN_FAVORITE_PHONE_NUMBER, place.getPhoneNumber());
            placeValues.put(COLUMN_FAVORITE_ADDRESS, place.getAddress());
            placeValues.put(COLUMN_FAVORITE_LAT,place.getLat());
            placeValues.put(COLUMN_FAVORITE_LNG,place.getLng());
            placeValues.put(COLUMN_FAVORITE_RATING,place.getRating());
            placeValues.put(COLUMN_FAVORITE_WEBSITE,place.getWebsite());
            placeValues.put(COLUMN_FAVORITE_LOGO,place.getLogo());
            Uri insertedUri = context.getContentResolver().insert(
                    CONTENT_URI,
                    placeValues
            );

            long placeId = ContentUris.parseId(insertedUri);
            if (placeId > -1)
                return true;
            return false;
        }

        public static boolean checkPlaceExistsById(Context context, String id) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(id).build();
            Cursor result = context.getContentResolver().query(
                    uri,
                    new String[]{_ID},
                    COLUMN_FAVORITE_ID + " = ?",
                    new String[]{id},
                    null
            );
            if (result != null && result.moveToFirst()) {
                return true;
            }
            result.close();
            return false;
        }

        public static boolean delete(Context context, String id) {
            int res = context.getContentResolver().delete(
                    CONTENT_URI,
                    FavoriteEntry.COLUMN_FAVORITE_ID + "=?",
                    new String[]{id});
            return res > 0;
        }

        public static ArrayList<Place> getFavorites(Context context) {
            Cursor cursor = context.getContentResolver().query(
                    CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            return getFavoritesFromCursor(cursor);
        }

        public static ArrayList<Place> getFavoritesFromCursor(Cursor cursor){
            ArrayList<Place> result = new ArrayList<>();
            while (cursor.moveToNext()) {

                Place place = new Place(
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_LAT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_LNG)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_PHONE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_RATING)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_WEBSITE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FAVORITE_LOGO))
                );

                result.add(place);

            }

            return result;
        }
    }
}
