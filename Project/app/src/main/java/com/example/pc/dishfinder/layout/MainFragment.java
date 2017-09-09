package com.example.pc.dishfinder.layout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pc.dishfinder.MainActivity;
import com.example.pc.dishfinder.PlaceParentActivity;
import com.example.pc.dishfinder.utils.Place;
import com.example.pc.dishfinder.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    TabAdapter tabAdapter;
    ArrayList<Place> allPlaces;
    ArrayList<Place> favoritePlaces;
    Fragment allPlacesFragment;
    Fragment favoritesFragment;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(ArrayList<Place> allPlaces, ArrayList<Place> favoritePlaces){
        MainFragment mainFragment = new MainFragment();
        mainFragment.allPlaces = allPlaces;
        mainFragment.favoritePlaces = favoritePlaces;
        return mainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_main, container, false);

        ViewPager viewPager = (ViewPager) fragmentView.findViewById(R.id.viewpager);

        if(!((PlaceParentActivity) getActivity()).checkConnection()){
            Snackbar.make(fragmentView, R.string.need_network_main, Snackbar.LENGTH_LONG).show();
        }

        if(savedInstanceState == null) {
            allPlacesFragment = PlaceListFragment.newInstance(getContext(), allPlaces);
            favoritesFragment = PlaceListFragment.newInstance(getContext(), favoritePlaces);
        }


        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("allPlaces", allPlaces);
        outState.putSerializable("favoritePlaces", favoritePlaces);
    }

    private void setupViewPager(ViewPager viewPager) {
        tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        tabAdapter.addFragment(allPlacesFragment, "All Places");
        tabAdapter.addFragment(favoritesFragment, "Favorites");
        viewPager.setAdapter(tabAdapter);
    }

    public void removeFavoriteFromAdapter(Place item) {
        ((PlaceListFragment)tabAdapter.getItem(1)).removeItem(item);

    }

    public void addFavoriteToAdapter(Place item) {
        ((PlaceListFragment)tabAdapter.getItem(1)).addItem(item);
    }

    public void notifyAllPlacesSetChanged(){
        ((PlaceListFragment) tabAdapter.getItem(0)).notifyDataSetChanged();
    }

    public void notifyFavoritesSetChanged(){
        ((PlaceListFragment) tabAdapter.getItem(1)).notifyDataSetChanged();
    }

    public void reloadLists(ArrayList<Place> allPlaces, ArrayList<Place> favoritePlaces){
        this.allPlaces = allPlaces;
        ((PlaceListFragment) tabAdapter.getItem(0)).reloadList(allPlaces);
        this.favoritePlaces = favoritePlaces;
        ((PlaceListFragment) tabAdapter.getItem(1)).reloadList(favoritePlaces);
    }

    static class TabAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }



        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }



}
