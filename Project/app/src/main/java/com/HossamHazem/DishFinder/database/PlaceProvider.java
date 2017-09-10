package com.HossamHazem.DishFinder.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlaceProvider extends ContentProvider {
    static final int FAVORITE = 100;
    static final int FAVORITE_ITEM = 101;

    PlaceDbHelper placeDbHelper;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public PlaceProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase database = placeDbHelper.getWritableDatabase();

        switch (match) {
            case FAVORITE: {
                return database.delete(PlaceContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case FAVORITE:
                return PlaceContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_ITEM:
                return PlaceContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = placeDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVORITE: {
                long _id = database.insert(PlaceContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > -1)
                    returnUri = PlaceContract.FavoriteEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("failed to insert row");
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        database.close();
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        placeDbHelper = new PlaceDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE:
                cursor = placeDbHelper.getReadableDatabase().query(
                        PlaceContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_ITEM:
                cursor = placeDbHelper.getReadableDatabase().query(
                        PlaceContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("QUeERY: unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlaceContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PlaceContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, PlaceContract.PATH_FAVORITE + "/*", FAVORITE_ITEM);
        return matcher;
    }
}
