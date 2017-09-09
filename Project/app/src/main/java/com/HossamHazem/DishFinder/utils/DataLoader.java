package com.HossamHazem.DishFinder.utils;



import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.HossamHazem.DishFinder.config.MyConfig;
import com.HossamHazem.DishFinder.database.PlaceContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hossam on 8/28/2017.
 */

public class DataLoader implements Serializable{

    public static ArrayList<DataLoader> instances;

    public static DataLoader getInstance(FragmentActivity activity){
        if(instances == null){
            instances = new ArrayList<>();
        }
        for(DataLoader instance : instances){
            if(instance.mContext.getClass().equals(activity.getClass())) {
                instance.mContext = activity;
                return instance;
            }
        }
        DataLoader instance = new DataLoader(activity);
        instances.add(instance);
        return instance;
    }

    public interface ApiLoaderFinishedCallback {
        void onSuccess(ArrayList<Place> data);
    }

    public interface DatabaseLoaderFinishedCallback {
        void onSuccess(ArrayList<Place> data);
    }

    ArrayList<Place> places;
    ArrayList<Place> favoritePlaces;

    final static String BASEURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final int GET_ALL_PLACES_LOADER  = 11;
    private static final int GET_FAVORITES_LOADER  = 12;

    private FragmentActivity mContext;
    private ApiLoaderFinishedCallback mApiLoaderFinishedCallback;
    private DatabaseLoaderFinishedCallback mDatabaseLoaderFinishedCallback;

    private LoaderManager.LoaderCallbacks<String> apiLoaderListener
            = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            switch(id){
                case GET_ALL_PLACES_LOADER: {
                    final String location = args.getString("location");
                    final String radius = args.getString("radius");
                    return new AsyncTaskLoader<String>(mContext) {

                        @Override
                        protected void onStartLoading() {


                            // COMPLETED (8) Force a load
                            forceLoad();
                        }

                        @Override
                        public String loadInBackground() {
                            return getPlacesJsonFromWeb(location, radius);
                        }
                    };
                }
                default: return null;
            }

        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            places = getPlaceDataFromJson(data);
            mApiLoaderFinishedCallback.onSuccess(places);

        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    };
    private LoaderManager.LoaderCallbacks<Cursor> databaseLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>(){
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            switch(id){
                case GET_FAVORITES_LOADER: {
                    return new AsyncTaskLoader<Cursor>(mContext) {
                        @Override
                        protected void onStartLoading() {


                            // COMPLETED (8) Force a load
                            forceLoad();
                        }

                        @Override
                        public Cursor loadInBackground() {
                            return mContext.getContentResolver().query(
                                    PlaceContract.FavoriteEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null
                            );
                        }
                    };
                }
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            favoritePlaces = PlaceContract.FavoriteEntry.getFavoritesFromCursor(cursor);
            mDatabaseLoaderFinishedCallback.onSuccess(favoritePlaces);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    public DataLoader(FragmentActivity context){
        this.mContext = context;
    }

    public void loadPlacesApi(Bundle args, ApiLoaderFinishedCallback apiLoaderFinishedCallback){
        this.mApiLoaderFinishedCallback = apiLoaderFinishedCallback;
        LoaderManager loaderManager = mContext.getSupportLoaderManager();
        Loader<String> allPlacesLoader = loaderManager.getLoader(GET_ALL_PLACES_LOADER);

        if(allPlacesLoader == null){
            loaderManager.initLoader(GET_ALL_PLACES_LOADER, args,  apiLoaderListener);
        }
    }

    public void loadPlacesFavorites(DatabaseLoaderFinishedCallback databaseLoaderFinishedCallback){
        this.mDatabaseLoaderFinishedCallback = databaseLoaderFinishedCallback;
        LoaderManager loaderManager = mContext.getSupportLoaderManager();
        Loader<String> favoritePlacesLoader = loaderManager.getLoader(GET_FAVORITES_LOADER);

        if(favoritePlacesLoader == null || favoritePlacesLoader.isReset()){
            loaderManager.initLoader(GET_FAVORITES_LOADER, null,  databaseLoaderListener);
        }
        else{
            loaderManager.restartLoader(GET_FAVORITES_LOADER, null,  databaseLoaderListener);
        }
    }

    public static String getPlacesJsonFromWeb(String location, String radius){
        final String API_KEY = MyConfig.GOOGLE_PLACES_API_KEY;
        final String API_PARAM = "key";
        final String TYPE_PARAM = "types";
        final String TYPE_DATA="bar|cafe|casino";
        final String RADIUS_PARAM = "radius";
        final String LOCATION_PARAM = "location";


        Uri uri =  Uri.parse(BASEURL).buildUpon()
                .appendQueryParameter(API_PARAM,API_KEY)
                .appendQueryParameter(TYPE_PARAM,TYPE_DATA)
                .appendQueryParameter(RADIUS_PARAM,radius)
                .appendQueryParameter(LOCATION_PARAM,location)
                .build();

        return MyConnection.connect(uri.toString());
    }


    public static ArrayList<Place> getPlaceDataFromJson(String placesJSONStr){
        JSONArray placesJSON;
        ArrayList<Place> places = new ArrayList<>();
        try {
            if(placesJSONStr != null) {
                placesJSON = (new JSONObject(placesJSONStr)).getJSONArray("results");
                int total;
                total = placesJSON.length();
                JSONObject placeJSON;
                String placeId;
                String placeName;
                Boolean placeIsOpen;
                String placeRating;
                ArrayList<String> placeTypes;
                String placeLng;
                String placeLat;
                String placePhoneNumber;
                String placeAddress;
                String placeWebsite;
                ArrayList<String> placePictures;
                for (int c = 0; c < total; c++) {
                    placeJSON = placesJSON.getJSONObject(c);

                    placeId = placeJSON.getString("place_id");
                    placeName = placeJSON.getString("name");
                    if(placeJSON.has("opening_hours") && placeJSON.getJSONObject("opening_hours").has("open_now")) {
                        placeIsOpen = placeJSON.getJSONObject("opening_hours").getBoolean("open_now");
                    }
                    else{
                        placeIsOpen = null;
                    }
                    placeRating = placeJSON.optString("rating","0");
                    placeAddress = placeJSON.optString("vicinity","Not Available");
                    placeTypes = new ArrayList<>();
                    JSONArray placeTypesJSON = placeJSON.getJSONArray("types");
                    for(int i = 0;i<placeTypesJSON.length();i++){
                        placeTypes.add(placeTypesJSON.getString(i));
                    }
                    JSONObject locationJSON = placeJSON.getJSONObject("geometry").getJSONObject("location");
                    placeLng = locationJSON.getString("lng");
                    placeLat = locationJSON.getString("lat");
                    placePictures = new ArrayList<>();
                    if(placeJSON.has("photos")) {
                        JSONArray placePicturesJSON = placeJSON.optJSONArray("photos");
                        for (int i = 0; i < placePicturesJSON.length(); i++) {
                            JSONObject photo = placePicturesJSON.getJSONObject(i);
                            placePictures.add(photo.getString("photo_reference"));
                        }
                    }

                    Place place = new Place(
                            placeId,
                            placeName,
                            placeIsOpen,
                            placeRating,
                            placeLat,
                            placeLng,
                            placeTypes,
                            placePictures);
                    place.setAddress(placeAddress);
                    places.add(place);
                }
            }

            return places;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
