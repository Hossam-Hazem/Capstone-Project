package com.example.pc.dishfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pc.dishfinder.utils.LocationGetter;
import com.example.pc.dishfinder.utils.Place;
import com.example.pc.dishfinder.utils.DataLoader;

import java.util.ArrayList;

import butterknife.BindBool;
import butterknife.ButterKnife;

import com.example.pc.dishfinder.layout.MainFragment;
import com.example.pc.dishfinder.layout.PlaceDetailFragment;
import com.example.pc.dishfinder.layout.PlaceListFragment;
import com.example.pc.dishfinder.widget.HomeWidgetProvider;

public class MainActivity extends PlaceParentActivity implements PlaceListFragment.TwoPaneInterface {

    @BindBool(R.bool.isTablet)
    boolean mTwoPane;

    public ArrayList<Place> allPlaces;

    public ArrayList<Place> favoritePlaces;

    LocationGetter locationGetter;

    DataLoader dataLoader;

    MainFragment mainFragment;

    @Override
    protected void onStart() {
        super.onStart();
       // mainFragment.reloadLists(allPlaces, favoritePlaces);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        allPlaces = new ArrayList<>();
        favoritePlaces = new ArrayList<>();

        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0);

        if (mTwoPane) {
            if (savedInstanceState == null) {
                PlaceDetailFragment placeDetailFragment = new PlaceDetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.place_detail_container, placeDetailFragment, "placeDetailFragment")
                        .commit();
            }
        }
        dataLoader = DataLoader.getInstance(this);
        dataLoader.loadPlacesFavorites(new DataLoader.DatabaseLoaderFinishedCallback() {
            @Override
            public void onSuccess(ArrayList<Place> data) {
                favoritePlaces.clear();
                favoritePlaces.addAll(data);
                mainFragment.notifyFavoritesSetChanged();
            }
        });
        if (savedInstanceState == null) {
            initLocationGetterAndLoadData(dataLoader);
            mainFragment = MainFragment.newInstance(allPlaces, favoritePlaces);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, mainFragment, "mainFragment")
                    .commit();
        } else {
            allPlaces = (ArrayList<Place>) savedInstanceState.getSerializable("allPlaces");
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            mainFragment.reloadLists(allPlaces, favoritePlaces);
        }


    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("allPlaces", allPlaces);
        outState.putSerializable("favoritePlaces", favoritePlaces);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Class intentClass;

        switch (id) {
            case R.id.action_settings:
                intentClass = MainSettingsActivity.class;
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        startActivity(new Intent(this, intentClass));
        return true;
    }


    @Override
    public void listItemClickCallback(Place place) {
        if (mTwoPane) {
            openPlaceDetailTwoPane(place);
        } else {
            openPlaceDetailOnePane(place);
        }
    }

    private void openPlaceDetailTwoPane(Place place) {
        PlaceDetailFragment placeDetailsFragment = (PlaceDetailFragment) getSupportFragmentManager().findFragmentById(R.id.place_detail_container);
        placeDetailsFragment.setPlaceDetails(place);
    }

    private void OpenPlaceDetailTwoPaneInit(Place place) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.place_detail_container, PlaceDetailFragment.newFragmentWithBundle(place, true))
                .commit();
    }

    private void openPlaceDetailOnePane(Place place) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra(PlaceDetailActivity.PLACE_SERIALIZABLE_KEY, place);

        startActivity(intent);
    }

    public void removePlaceFromFavorites(Place item) {
        MainFragment fragment = (MainFragment) this.getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        fragment.removeFavoriteFromAdapter(item);
    }

    public void addPlaceToFavorites(Place item) {
        MainFragment fragment = (MainFragment) this.getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        fragment.addFavoriteToAdapter(item);
    }

    public void onAdapterFinish(ArrayList<Place> places) {
        if (mTwoPane && !places.isEmpty()) {
            OpenPlaceDetailTwoPaneInit(places.get(0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationGetter.onLocationPermissionAccepted();
                } else {
                    locationGetter.onLocationPermissionRejected();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private DataLoader initDataLoader() {
        return new DataLoader(this);
    }


    private void initLocationGetterAndLoadData(final DataLoader dataLoader) {
        locationGetter = new LocationGetter(this);

        locationGetter.run(new LocationGetter.LocationCallback() {

            @Override
            public void onSuccess(Context context, Location location) {
                Log.v("location", location.toString());
                String locationString = location.getLatitude() + "," + location.getLongitude();
                Bundle loadPlacesBundle = new Bundle();
                loadPlacesBundle.putString("location", locationString);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                int searchRadius = prefs.getInt(getString(R.string.searchRadius), 1) * 1000;
                loadPlacesBundle.putString("radius", searchRadius+"");
                dataLoader.loadPlacesApi(loadPlacesBundle, new DataLoader.ApiLoaderFinishedCallback() {
                    @Override
                    public void onSuccess(ArrayList<Place> data) {
                        allPlaces.clear();
                        allPlaces.addAll(data);
                        mainFragment.notifyAllPlacesSetChanged();
                        if (mTwoPane) {
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag("placeDetailFragment");
                            PlaceDetailFragment placeDetailFragment = (PlaceDetailFragment) fragment;
                            fragment = null;
                            placeDetailFragment.setPlaceDetails(allPlaces.get(0));
                        }

                    }
                });
                //updateWidget();
            }

            @Override
            public void onFail() {
                Log.v("location", "failed");
            }

            @Override
            public void onPermissionRejected() {
                Log.v("location", "rejected");
            }
        });
    }

    public void updateWidget() {
        Intent i = new Intent(this, HomeWidgetProvider.class);
        i.setAction(HomeWidgetProvider.UPDATE_ACTION);
        sendBroadcast(i);
    }

}
