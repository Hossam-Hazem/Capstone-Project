package com.HossamHazem.DishFinder;

import android.content.Intent;
import android.os.Bundle;

import com.HossamHazem.DishFinder.layout.PlaceDetailFragment;
import com.HossamHazem.DishFinder.utils.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceDetailActivity extends PlaceParentActivity {

    public final static String PLACE_SERIALIZABLE_KEY = "placeDetails";

    public Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);


        Intent intent = getIntent();
        place = (Place) intent.getSerializableExtra(PLACE_SERIALIZABLE_KEY);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.place_detail_container, PlaceDetailFragment.newFragmentWithBundle(place, false))
                    .commit();
        }

    }
}
