package com.example.pc.dishfinder.layout;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pc.dishfinder.utils.Place;
import com.example.pc.dishfinder.R;
import com.example.pc.dishfinder.adapters.PlacesListAdapter;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceListFragment extends Fragment {
    public interface OnFinishAdapterEmpty {
        public void onFinished();
    }

    @BindView(R.id.placesRecyclerView) RecyclerView mRecyclerView;
    @BindBool(R.bool.isTablet) boolean mTwoPane;

    private PlacesListAdapter mAdapter;
    protected OnFinishAdapterEmpty onFinishAdapterEmpty;
    ArrayList<Place> places;
    private RecyclerView.LayoutManager mLayoutManager;
    private Place.SortType sortType;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_place_all, container, false);

        setRetainInstance(true);

        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(mAdapter);

        if(mTwoPane){
            mLayoutManager = new LinearLayoutManager(getActivity());
        }
        else{
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }


        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("places", places);
    }

    public static PlaceListFragment newInstance(Context mContext, ArrayList<Place> places){
        PlaceListFragment placeListFragment = new PlaceListFragment();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        String sortTypeString = prefs.getString(mContext.getString(R.string.sortType), "NAME");
        Place.SortType sortType = Place.SortType.valueOf(sortTypeString);
        placeListFragment.sortType = sortType;
        placeListFragment.places = places;
        placeListFragment.mAdapter = new PlacesListAdapter(mContext, places, sortType);
        return placeListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void reloadList(ArrayList<Place> places){
        this.places = places;
        mAdapter.replace(places);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        String sortTypeString = prefs.getString(getString(R.string.sortType), "NAME");
        Place.SortType sortType = Place.SortType.valueOf(sortTypeString);
        if(this.sortType != sortType){
            mAdapter.changeSortType(sortType);
            this.sortType = sortType;
        }
    }

    public void onCreateViewInit(View fragmentView){
//        gridView.setOnItemClickListener( new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Place item = (Place) mPlacesAdapter.getItem(position);
//                ((TwoPaneInterface) getActivity()).listItemClickCallback(item);
//
//
//            }
//        });
    }

    public interface TwoPaneInterface {
        void listItemClickCallback(Place placeItem);
    }

    public void initOnFinishAdapterEmpty(final String message) {
        onFinishAdapterEmpty = new OnFinishAdapterEmpty() {
            @Override
            public void onFinished() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                onFinishAdapterEmpty = null;
            }
        };
    }

    public void notifyDataSetChanged(){
        mAdapter.onNotifyDataSetChanged();
    }

    public void addItem(Place place){
        mAdapter.add(place);
    }

    public  void removeItem(Place place){
        mAdapter.remove(place);
    }

}
