package com.example.pc.dishfinder.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

/**
 * Created by Hossam on 8/28/2017.
 */

public class LocationGetter implements LocationListener {
    PermissionCallback locationPermissionCallback;
    LocationManager mLocationManager;
    LocationCallback getLocationCallback;
    AppCompatActivity mContext;

    public LocationGetter(AppCompatActivity context){
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);


    }

    public void run(LocationCallback locationCallback) {
        getLocationCallback = locationCallback;
        locationPermissionCallback = new PermissionCallback() {
            @Override
            public void onAccept() {
                getGPSLocation(getLocationCallback);
            }

            @Override
            public void onReject() {
                getLocationCallback.onPermissionRejected();
            }
        };
        requestLocation();
    }

    @SuppressWarnings({"MissingPermission"})
    public void getGPSLocation(LocationCallback locationCallback){
        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
 //           buildAlertMessageNoGps();
            System.out.println("error");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null || location.getTime() < Calendar.getInstance().getTimeInMillis() -  1000) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else{
            getGpsLocationOnSuccess(location, locationCallback);
        }
    }
    private void getGpsLocationOnSuccess(Location location, LocationCallback locationCallback) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext()).edit();
        long locationUpdatedAt =  System.currentTimeMillis()/1000;
        editor.putFloat("lastLatitude", (float)location.getLatitude());
        editor.putFloat("lastLongitude", (float)location.getLongitude());
        editor.putLong("locationUpdatedAt", locationUpdatedAt);
        editor.commit();
        locationCallback.onSuccess(mContext, location);
    }

//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        getLocationCallback.onFail();
//                        dialog.cancel();
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }

    public void requestLocation() {
        if(Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mContext.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                locationPermissionCallback.onAccept();
            }
        }
        else {
            //TODO handle permission handling
            locationPermissionCallback.onAccept();
        }
    }



    // Required functions
    public void onProviderDisabled(String arg0) {}
    public void onProviderEnabled(String arg0) {
        getGPSLocation(getLocationCallback);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.removeUpdates(this);
            getGpsLocationOnSuccess(location, getLocationCallback);
        }
        else {
            getLocationCallback.onFail();
        }
    }

    public void onLocationPermissionAccepted(){
        locationPermissionCallback.onAccept();
    }

    public void onLocationPermissionRejected(){
        locationPermissionCallback.onReject();
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

    public interface PermissionCallback {
        void onAccept();
        void onReject();
    }
    public interface LocationCallback{
        void onSuccess(Context context, Location location);
        void onFail();
        void onPermissionRejected();
    }
}
