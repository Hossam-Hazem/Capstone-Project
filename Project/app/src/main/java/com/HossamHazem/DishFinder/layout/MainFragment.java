package com.HossamHazem.DishFinder.layout;

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

import com.HossamHazem.DishFinder.PlaceParentActivity;
import com.HossamHazem.DishFinder.R;
import com.HossamHazem.DishFinder.utils.OnCreateViewCommand;
import com.HossamHazem.DishFinder.utils.Place;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    TabAdapter tabAdapter;
    ArrayList<Place> allPlaces;
    ArrayList<Place> favoritePlaces;
    PlaceListFragment allPlacesFragment;
    PlaceListFragment favoritesFragment;
    ArrayList<OnCreateViewCommand> onCreateViewCommands = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(ArrayList<Place> allPlaces, ArrayList<Place> favoritePlaces) {
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
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        ViewPager viewPager = (ViewPager) fragmentView.findViewById(R.id.viewpager);

        if (!((PlaceParentActivity) getActivity()).checkConnection()) {
            Snackbar.make(fragmentView, R.string.need_network_main, Snackbar.LENGTH_LONG).show();
        }

        if (savedInstanceState == null) {
            allPlacesFragment = PlaceListFragment.newInstance(getContext(), allPlaces);
            favoritesFragment = PlaceListFragment.newInstance(getContext(), favoritePlaces);
        }


        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        OnCreateViewCommand.execute(onCreateViewCommands);
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
        favoritesFragment.removeItem(item);

    }

    public void addFavoriteToAdapter(Place item) {
        favoritesFragment.addItem(item);
    }

    public void notifyAllPlacesSetChanged() {
        allPlacesFragment.notifyDataSetChanged();
    }

    public void notifyFavoritesSetChanged() {
        favoritesFragment.notifyDataSetChanged();
    }

    public void reloadLists(ArrayList<Place> allPlaces, ArrayList<Place> favoritePlaces) {
        this.allPlaces = allPlaces;
        allPlacesFragment.reloadList(allPlaces);
        this.favoritePlaces = favoritePlaces;
        favoritesFragment.reloadList(favoritePlaces);
    }

    public void isApiLoading(final boolean flag){
        final OnCreateViewCommand onCreateViewCommand = new OnCreateViewCommand() {
            @Override
            public void run() {
                allPlacesFragment.isDataLoading(flag);
            }
        };
        if(allPlacesFragment == null){
            /*
                soo if the allPlacesFragment is null when it is initialized add that command
                to the allPlacesFragment command queue until its view is created.
                sorry about this Russian doll if you have a better option please suggest.
             */
            addOnCreateViewCommand(new OnCreateViewCommand() {
                @Override
                public void run() {
                    allPlacesFragment.addOnCreateViewCommand(onCreateViewCommand);
                }
            });
            return;
        }
        if(allPlacesFragment.getView() == null) {
            allPlacesFragment.addOnCreateViewCommand(onCreateViewCommand);
            return;
        }
        allPlacesFragment.isDataLoading(flag);
    }

    public void isFavoritesLoading(final boolean flag){
        final OnCreateViewCommand onCreateViewCommand = new OnCreateViewCommand() {
            @Override
            public void run() {
                favoritesFragment.isDataLoading(flag);
            }
        };
        if(favoritesFragment == null){
            addOnCreateViewCommand(new OnCreateViewCommand() {
                @Override
                public void run() {
                    favoritesFragment.addOnCreateViewCommand(onCreateViewCommand);
                }
            });
            return;
        }
        if(favoritesFragment.getView() == null) {
            favoritesFragment.addOnCreateViewCommand(onCreateViewCommand);
            return;
        }
        favoritesFragment.isDataLoading(flag);
    }

    public boolean isApiLoading(){
        if(allPlacesFragment == null || allPlacesFragment.getView() == null){
            return false;
        }
        return allPlacesFragment.isDataLoading();
    }

    public boolean isFavoritesLoading(){
        if(favoritesFragment == null || favoritesFragment.getView() == null){
            return false;
        }
        return favoritesFragment.isDataLoading();
    }


    public void addOnCreateViewCommand(OnCreateViewCommand onCreateViewCommand){
        onCreateViewCommands.add(onCreateViewCommand);
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
