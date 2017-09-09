package com.example.pc.dishfinder.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.pc.dishfinder.PlaceDetailActivity;
import com.example.pc.dishfinder.R;
import com.example.pc.dishfinder.layout.PlaceDetailFragment;
import com.example.pc.dishfinder.utils.DataLoader;
import com.example.pc.dishfinder.utils.Place;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class HomeWidgetService extends RemoteViewsService {
    public HomeWidgetService() {
    }



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    ArrayList<Place> places;
    private int mAppWidgetId;
    private float mLatitude;
    private float mLongitude;

    public ListRemoteViewsFactory(Context applicationContext, Intent dataIntent){
        this.mContext = applicationContext;

        this.places = new ArrayList<>();

        mAppWidgetId = dataIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }



    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
//            SharedPreferences sharedPref = mContext.getSharedPreferences(RecipeStepActivity.SHARED_PREF_NAME, MODE_PRIVATE);
//            mData.addAll(sharedPref.getStringSet("data", null));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        long updatedAt = sharedPrefs.getLong("locationUpdatedAt", 0);
        if( updatedAt != 0) {
            mLatitude = sharedPrefs.getFloat("lastLatitude", 0);
            mLongitude = sharedPrefs.getFloat("lastLongitude", 0);
            String locationString = mLatitude + "," + mLongitude;
            //String searchRadius = prefs.getInt(getString(R.string.searchRadius), 1000)+"";
            String searchRadius = 1000+"";
            places = DataLoader.getPlaceDataFromJson(DataLoader.getPlacesJsonFromWeb(locationString, searchRadius));
            Collections.sort(places, Place.getDistanceComparator(mLatitude, mLongitude));
        }
    }

    @Override
    public void onDestroy() {

    }



    @Override
    public int getCount() {
        if(places == null)
            return 0;
        return places.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.home_widget_provider_item);
        Place place = places.get(position);
        String name = place.getName();
        String distance = place.getDistanceFormatted(mLatitude, mLongitude);

        remoteViews.setTextViewText(R.id.home_widget_provider_item_placeName, name);
        remoteViews.setTextViewText(R.id.home_widget_provider_item_distance, distance);

        Intent i=new Intent();
        Bundle extras=new Bundle();
        extras.putSerializable(PlaceDetailActivity.PLACE_SERIALIZABLE_KEY, place);
        i.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.home_widget_provider_item_placeName, i);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}


