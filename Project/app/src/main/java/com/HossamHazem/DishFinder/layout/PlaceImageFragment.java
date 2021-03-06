package com.HossamHazem.DishFinder.layout;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.HossamHazem.DishFinder.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceImageFragment extends DialogFragment {
    public PlaceImageFragment() {
        // Required empty public constructor
    }

    public static PlaceImageFragment newInstance() {
        PlaceImageFragment fragment = new PlaceImageFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_image, container, false);
        ButterKnife.bind(this, view);
        String uri = getArguments().getString("uri");
        ImageView imageView = ((ImageView) view.findViewById(R.id.place_image_view));
        Picasso.with(getContext()).load(uri).into(imageView);

        return view;
    }

    @OnClick(R.id.closeButton)
    public void close(View view) {
        getDialog().dismiss();
    }
}
