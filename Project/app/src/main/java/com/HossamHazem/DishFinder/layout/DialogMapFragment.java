package com.HossamHazem.DishFinder.layout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.HossamHazem.DishFinder.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hossam on 9/11/2017.
 */

public class DialogMapFragment extends DialogFragment {

    private SupportMapFragment fragment;
    private String title;
    private float lat;
    private float lng;

    public DialogMapFragment() {
        fragment = new SupportMapFragment();
    }

    public static DialogMapFragment newInstance(String title, float lat, float lng){
        DialogMapFragment dialogMapFragment = new DialogMapFragment();
        dialogMapFragment.title = title;
        dialogMapFragment.lat = lat;
        dialogMapFragment.lng = lng;
        return dialogMapFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        fragment.getMapAsync((OnMapReadyCallback) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapdialog, container, false);
//        getDialog().setTitle("");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.mapView, fragment).commit();

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.closeButton)
    public void close(View view) {
        getDialog().dismiss();
    }

    @OnClick(R.id.showOnMapButton)
    public void showOnMaps(View view){
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?z=19");
        //
        //                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //                // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");
        //
        //                // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }


    public SupportMapFragment getFragment() {
        return fragment;
    }
}
