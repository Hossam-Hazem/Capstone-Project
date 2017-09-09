package com.example.pc.dishfinder;


import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.pc.dishfinder.utils.MyConnection;
import com.example.pc.dishfinder.utils.Place;
import com.example.pc.dishfinder.utils.Review;
import com.example.pc.dishfinder.config.MyConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceDetailsConnector extends AsyncTask<Void, Void, Void> {
    public interface OnFinishCallback{
        void onFinished();
    }
    final String BASEURL = "https://maps.googleapis.com/maps/api/place/details/json";
    Place place;
    OnFinishCallback onFinishCallback;

    public PlaceDetailsConnector( Place place,OnFinishCallback onFinishCallback) {
        this.place = place;
        this.onFinishCallback = onFinishCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        getPlaceDetails();
        return null;
    }

    public void getPlaceDetails() {
        final String API_KEY = MyConfig.GOOGLE_PLACES_API_KEY;
        final String API_PARAM = "key";
        final String PLACE_ID_PARAM = "placeid";
        final String PLACE_ID_DATA = place.getId();


        Uri uri = Uri.parse(BASEURL).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(PLACE_ID_PARAM, PLACE_ID_DATA)
                .build();

        String JSONStr = MyConnection.connect(uri.toString());

        getPlaceDataFromJson(JSONStr);
    }
//    private ArrayList<Place> getPlacesListFromDB(String ACTION) {
//        return PlaceContract.FavoriteEntry.getFavorites(mContext);
//    }

    @Nullable
    public void getPlaceDataFromJson(String placeJSONStr) {
        try {
            if (placeJSONStr != null) {
                JSONObject placeJSON;

                String phoneNumber;
                String address;
                String website;
                ArrayList<Review> reviews;
                ArrayList<String> placePictures;
                ArrayList<String> types;
                placeJSON = (new JSONObject(placeJSONStr)).getJSONObject("result");

                phoneNumber = placeJSON.optString("international_phone_number", "not available");
                address = placeJSON.optString("vicinity", "not available");
                website = placeJSON.optString("website", "not available");
                placePictures = new ArrayList<>();
                reviews = new ArrayList<>();
                types = new ArrayList<>();
                if (placeJSON.has("reviews")) {
                    JSONArray placePicturesJSON = placeJSON.optJSONArray("reviews");
                    for (int i = 0; i < placePicturesJSON.length(); i++) {
                        JSONObject review = placePicturesJSON.getJSONObject(i);
                        String authorName = review.optString("author_name","");
                        double rating = review.optDouble("rating",-1);
                        String text = review.optString("text","");
                        String time = review.optString("time","");
                        reviews.add(new Review(authorName,rating,text,time));
                    }
                }
                if (placeJSON.has("photos")) {
                    JSONArray placePicturesJSON = placeJSON.optJSONArray("photos");
                    for (int i = 0; i < placePicturesJSON.length(); i++) {
                        JSONObject photo = placePicturesJSON.getJSONObject(i);
                        placePictures.add(photo.getString("photo_reference"));
                    }
                }
                if(placeJSON.has("types")) {
                    JSONArray placeTypesJSON = placeJSON.optJSONArray(("types"));
                    for (int i = 0; i < placeTypesJSON.length(); i++) {
                        types.add(placeTypesJSON.getString(i));
                    }
                }

                place.setAdditionalInfo(phoneNumber,address,website,reviews,placePictures,types);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinishCallback.onFinished();
    }
}
