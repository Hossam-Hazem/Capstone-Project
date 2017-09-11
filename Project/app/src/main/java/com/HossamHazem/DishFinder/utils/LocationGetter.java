package com.HossamHazem.DishFinder.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Hossam on 8/28/2017.
 */

public class LocationGetter{
    PermissionCallback locationPermissionCallback;
    LocationCallback getLocationCallback;
    AppCompatActivity mContext;
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationGetter(AppCompatActivity context) {
        mContext = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
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
    public void getGPSLocation(final LocationCallback locationCallback) {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(mContext, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            getGpsLocationOnSuccess(location, locationCallback);
                        }
                        else{
                            locationCallback.onFail();
                        }
                    }

                });
    }

    private void getGpsLocationOnSuccess(Location location, LocationCallback locationCallback) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext()).edit();
        long locationUpdatedAt = System.currentTimeMillis() / 1000;
        editor.putFloat("lastLatitude", (float) location.getLatitude());
        editor.putFloat("lastLongitude", (float) location.getLongitude());
        editor.putLong("locationUpdatedAt", locationUpdatedAt);
        editor.commit();
        locationCallback.onSuccess(mContext, location);
    }

    public void requestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mContext.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                locationPermissionCallback.onAccept();
            }
        } else {
            //TODO handle permission handling
            locationPermissionCallback.onAccept();
        }
    }

    public void onLocationPermissionAccepted() {
        locationPermissionCallback.onAccept();
    }

    public void onLocationPermissionRejected() {
        locationPermissionCallback.onReject();
    }


    public interface PermissionCallback {
        void onAccept();

        void onReject();
    }

    public interface LocationCallback {
        void onSuccess(Context context, Location location);

        void onFail();

        void onPermissionRejected();
    }
}
