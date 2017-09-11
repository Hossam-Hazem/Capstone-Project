package com.HossamHazem.DishFinder;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.HossamHazem.DishFinder.layout.DialogMapFragment;
import com.HossamHazem.DishFinder.layout.PlaceImageFragment;
import com.HossamHazem.DishFinder.layout.PlaceReviewsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public abstract class PlaceParentActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MyOnMapReadyCallback mOnMapReadyCallback;

    public void openReviewsFragment(Bundle bundle) {
        showDialog(PlaceReviewsFragment.newInstance(), bundle);
    }

    public void openImageFragment(Bundle bundle) {
        showDialog(PlaceImageFragment.newInstance(), bundle);
    }

    public void openMapFragment(Bundle bundle){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        closeOpenedDialog(ft);

        final float lat = bundle.getFloat("lat");
        final float lng = bundle.getFloat("lng");
        final String title = bundle.getString("title");
        MyOnMapReadyCallback onMapReadyCallback = new MyOnMapReadyCallback() {
            @Override
            public void run() {
                mGoogleMap.clear();
                LatLng position = new LatLng(lat, lng);
                mGoogleMap.addMarker(new MarkerOptions().position(position)
                        .title(title));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,19));
            }
        };

        if(mGoogleMap == null){
            mOnMapReadyCallback = onMapReadyCallback;
        }
        else{
            onMapReadyCallback.run();
        }

        DialogMapFragment dialog = DialogMapFragment.newInstance(title, lat, lng);
        dialog.show(ft, "dialog");
    }


    private void showDialog(DialogFragment newFragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        closeOpenedDialog(ft);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");

    }

    private void showDialog(DialogFragment newFragment, Bundle bundle) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        closeOpenedDialog(ft);

        newFragment.setArguments(bundle);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    private void closeOpenedDialog(FragmentTransaction ft) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }


    public boolean checkConnection() {
        ConnectivityManager
                cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(mOnMapReadyCallback != null){
            mOnMapReadyCallback.run();
        }

    }

    private interface MyOnMapReadyCallback{
        void run();
    }


}
