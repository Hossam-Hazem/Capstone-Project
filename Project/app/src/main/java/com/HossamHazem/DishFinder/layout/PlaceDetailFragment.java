package com.HossamHazem.DishFinder.layout;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.HossamHazem.DishFinder.MainActivity;
import com.HossamHazem.DishFinder.PlaceDetailsConnector;
import com.HossamHazem.DishFinder.PlaceParentActivity;
import com.HossamHazem.DishFinder.R;
import com.HossamHazem.DishFinder.utils.OnCreateViewCommand;
import com.HossamHazem.DishFinder.utils.PaletteUtils;
import com.HossamHazem.DishFinder.utils.Place;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.HossamHazem.DishFinder.PlaceDetailActivity.PLACE_SERIALIZABLE_KEY;

public class PlaceDetailFragment extends Fragment implements View.OnClickListener {
    Place placeDetails;
    View fragmentView;
    boolean isFavorite;
    List<OnCreateViewCommand> onCreateViewCommands = new LinkedList<>();

    @BindBool(R.bool.isTablet)
    boolean mTwoPane;

    @BindView(R.id.ratingBar)
    RatingBar mRatingBar;

    @BindView(R.id.place_title)
    TextView mPlaceTitle;

    @BindView(R.id.place_distance)
    TextView mPlaceDistance;

    @BindView(R.id.backdrop)
    ImageView mBackdrop;

    @BindView(R.id.phoneNumber)
    TextView mPhoneNumber;

    @BindView(R.id.address)
    TextView mAddress;

    @BindView(R.id.meta_bar)
    View mMetaBar;

    @BindView(R.id.website)
    TextView mWebsite;

    @BindView(R.id.placeType)
    TextView mPlaceType;

    @BindView(R.id.favorite_button)
    FloatingActionButton mFavoriteButton;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.placeReviewsButton)
    Button mReviews;

    @BindView(R.id.placeMapButton)
    Button mMap;


    public PlaceDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_place_detail, container, false);
        ButterKnife.bind(this, fragmentView);

        if (!((PlaceParentActivity) getActivity()).checkConnection()) {
            Snackbar.make(fragmentView, R.string.need_network_details, Snackbar.LENGTH_LONG).show();
        }

        AppCompatActivity currentActivity = (AppCompatActivity) getActivity();
        if (!mTwoPane) {
            Toolbar toolbar = (Toolbar) fragmentView.findViewById(R.id.toolbar);
            currentActivity.setSupportActionBar(toolbar);
            currentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                fragmentView.findViewById(R.id.place_detail_container).setFitsSystemWindows(true);
            }
        }
        Place place = null;
        if (getArguments() != null) {
            place = (Place) getArguments().getSerializable("placeDetails");
        }

        if (savedInstanceState != null) {
            place = (Place) savedInstanceState.getSerializable("place");
        }

        setPlaceDetails(place);

        mFavoriteButton.setOnClickListener(this);
        mReviews.setOnClickListener(this);
        mBackdrop.setOnClickListener(this);
        mMap.setOnClickListener(this);

        OnCreateViewCommand.execute(onCreateViewCommands);

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("place", placeDetails);
    }

    public void setPlaceDetails(Place place) {

        if (place == null) {
            if (fragmentView != null) {
                fragmentView.setVisibility(View.GONE);
            }
            return;
        }

        placeDetails = place;
        isFavorite = placeDetails.isFavorite(getContext());

        PlaceDetailsConnector connector = new PlaceDetailsConnector(placeDetails, new PlaceDetailsConnector.OnFinishCallback() {
            @Override
            public void onFinished() {
                if (fragmentView != null) {
                    linkPlaceDetailsUI();
                } else {
                    onCreateViewCommands.add(new OnCreateViewCommand() {
                        @Override
                        public void run() {
                            linkPlaceDetailsUI();
                        }
                    });
                }
            }
        });
        connector.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button: {
                if (isFavorite) {
                    removePlaceFavorite(v);
                } else {
                    setPlaceFavorite(v);
                }
                break;
            }
            case R.id.placeReviewsButton: {
                Bundle bundle = new Bundle();
                bundle.putSerializable("reviews", placeDetails.getReviews());
                ((PlaceParentActivity) getActivity()).openReviewsFragment(bundle);
                break;
            }
            case R.id.backdrop: {
                Bundle bundle = new Bundle();
                bundle.putString("uri", placeDetails.getDefaultImageURL());
                ((PlaceParentActivity) getActivity()).openImageFragment(bundle);
                break;
            }
            case R.id.placeMapButton: {
                Bundle bundle = new Bundle();
                bundle.putString("title", placeDetails.getName());
                bundle.putFloat("lat", Float.parseFloat(placeDetails.getLat()));
                bundle.putFloat("lng", Float.parseFloat(placeDetails.getLng()));
                ((PlaceParentActivity) getActivity()).openMapFragment(bundle);
            }

        }
    }

    private void setToolbar() {
        // mMetaBar.setBackgroundColor(0x99777777);
        mPlaceTitle.setText(placeDetails.getName());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        long updatedAt = sharedPrefs.getLong("locationUpdatedAt", 0);
        if (updatedAt != 0) {
            float latitude = sharedPrefs.getFloat("lastLatitude", 0);
            float longitude = sharedPrefs.getFloat("lastLongitude", 0);
            mPlaceDistance.setText(placeDetails.getDistanceFormatted(latitude, longitude) + " " + getString(R.string.distance_away));
        }

        AppBarLayout appBarLayout = (AppBarLayout) fragmentView.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(placeDetails.getName());
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void linkPlaceDetailsUI() {
        if (placeDetails != null) {
            fragmentView.setVisibility(View.VISIBLE);
            loadBackdrop();
            setPlaceRating();
            setPlaceAddress();
            setPlacePhoneNumber();
            setPlaceWebsite();
            setPlaceType();
            setToolbar();
            setFavButton(fragmentView);
        }
    }

    private void setPlaceRating() {
        float fiveBasedRating = Float.parseFloat(placeDetails.getRating());
        mRatingBar.setRating(fiveBasedRating);
    }

    private void loadBackdrop() {
        String imageUrl = placeDetails.getDefaultImageURL();
        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(mBackdrop, new Callback() {
                @Override
                public void onSuccess() {
                    PaletteUtils.setTitleBarColor(mBackdrop, mMetaBar);
                }

                @Override
                public void onError() {

                }
            });
        } else {
            Picasso.with(getContext()).load(Place.getPlaceHolderImage()).fit().centerCrop().into(mBackdrop, new Callback() {
                @Override
                public void onSuccess() {
                    PaletteUtils.setTitleBarColor(mBackdrop, mMetaBar);
                }

                @Override
                public void onError() {

                }
            });
        }
    }


    private void setPlacePhoneNumber() {
        mPhoneNumber.setText(placeDetails.getPhoneNumber());
    }

    private void setPlaceAddress() {
        mAddress.setText(placeDetails.getAddress());
    }

    private void setPlaceWebsite() {
        mWebsite.setText(placeDetails.getWebsite());
    }

    private void setPlaceType() {
        mPlaceType.setText(placeDetails.getTypeString());
    }

    private void setFavButtonUnFavorite(View v) {

        mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_unfavorite));

        setFavButtonBackground(mFavoriteButton, 1);
    }

    private void setFavButtonFavorite(View v) {

        mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));

        setFavButtonBackground(mFavoriteButton, 0);
    }

    private void setFavButton(View v, int mode) {
        switch (mode) {
            case 0:
                setFavButtonFavorite(v);
                break;
            case 1:
                setFavButtonUnFavorite(v);
                break;
        }
    }

    private void setFavButtonBackground(FloatingActionButton button, int mode) {
        switch (mode) {
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    button.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.theme_accent), PorterDuff.Mode.MULTIPLY);
                break;
            case 1: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    button.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.theme_accent_dark), PorterDuff.Mode.MULTIPLY);
                break;
            }

        }
    }

    private void setFavButton(View v) {
        if (isFavorite) {
            setFavButton(v, 1);
        } else {
            setFavButton(v, 0);
        }
    }

    public void setPlaceFavorite(View v) {
        boolean isSuccess = placeDetails.setFavorite(getContext());
        if (isSuccess) {
            isFavorite = true;
            if (mTwoPane)
                ((MainActivity) getActivity()).addPlaceToFavorites(placeDetails);
            setFavButton(v);
        } else {
            throw new UnsupportedOperationException("error in insert favorite");
        }
    }

    public void removePlaceFavorite(View v) {
        boolean isSuccess = placeDetails.removeFavorite(getContext());

        if (isSuccess) {
            isFavorite = false;
            if (mTwoPane)
                ((MainActivity) getActivity()).removePlaceFromFavorites(placeDetails);
            setFavButton(v);
        } else {
            throw new UnsupportedOperationException("error fel delete favorite");
        }
    }

    public static PlaceDetailFragment newFragmentWithBundle(Place place, boolean twoPane) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle bundle = new Bundle();
//        bundle.putBoolean("twoPane", twoPane);
        bundle.putSerializable(PLACE_SERIALIZABLE_KEY, place);
        fragment.setArguments(bundle);
        return fragment;
    }

}
