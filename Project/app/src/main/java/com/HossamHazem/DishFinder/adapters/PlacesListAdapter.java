package com.HossamHazem.DishFinder.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.HossamHazem.DishFinder.R;
import com.HossamHazem.DishFinder.utils.PaletteUtils;
import com.HossamHazem.DishFinder.utils.Place;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.HossamHazem.DishFinder.layout.PlaceListFragment;

/**
 * Created by Hossam on 9/2/2017.
 */

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.PlaceViewHolder>{
    ArrayList<Place> places;
    Context mContext;
    Place.SortType sortType;

    public PlacesListAdapter(Context context, ArrayList<Place> places, Place.SortType sortType) {
        this.mContext = context;
        this.places = places;
        this.sortType = sortType;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.places_list_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        final Place item = get(position);
        holder.bind(item);
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ((PlaceListFragment.TwoPaneInterface) mContext).listItemClickCallback(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void add(Place place){
        places.add(place);
        onNotifyDataSetChanged();
    }

    public void addAll(Collection<Place> places){
        this.places.addAll(places);
        onNotifyDataSetChanged();
    }

    public void replace(ArrayList<Place> places){
        this.places = places;
        onNotifyDataSetChanged();
    }

    public void remove(Place place){
        places.remove(place);
        onNotifyDataSetChanged();
    }

    public void onNotifyDataSetChanged(){
        sort();
        notifyDataSetChanged();
    }

    public void changeSortType(Place.SortType sortType){
        this.sortType = sortType;
        sort();
        notifyDataSetChanged();
    }

    private void sort(){
        Comparator<Place> comparator;
        switch(sortType){
            case NAME: comparator = Place.getNameComparator(); break;
            case DISTANCE: {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
                long updatedAt = prefs.getLong("locationUpdatedAt", 0);
                float latitude = 0;
                float longitude = 0;
                if( updatedAt != 0) {
                    latitude = prefs.getFloat("lastLatitude", 0);
                    longitude = prefs.getFloat("lastLongitude", 0);
                }
                comparator = Place.getDistanceComparator(latitude, longitude);
                break;
            }
            case RATING: comparator = Place.getRatingComparator(); break;
            default: comparator = null;
        }
        Collections.sort(places, comparator);
    }

    public Place get(int position){
        return places.get(position);
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.itemTextHolder)
        View layout;

        @BindView(R.id.place_list_item_imageView)
        ImageView selectPlaceImage;

        @BindView(R.id.place_list_item_name)
        TextView selectPlaceName;

        @BindView(R.id.place_list_item_address)
        TextView selectPlaceAddress;

        View mainView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(Place place) {

            String imageUrl = place.getLogoImageURL();

            if(imageUrl != null) {
                Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(selectPlaceImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        PaletteUtils.setTitleBarColor(selectPlaceImage, layout);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
            else {
                Picasso.with(mContext).load(Place.getPlaceHolderImage()).fit().centerCrop().into(selectPlaceImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        PaletteUtils.setTitleBarColor(selectPlaceImage, layout);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            selectPlaceName.setText(place.getName());

            selectPlaceAddress.setText(place.getAddress());

        }
    }
}
