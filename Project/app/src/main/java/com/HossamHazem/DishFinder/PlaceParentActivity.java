package com.HossamHazem.DishFinder;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.HossamHazem.DishFinder.layout.PlaceImageFragment;
import com.HossamHazem.DishFinder.layout.PlaceReviewsFragment;

public abstract class PlaceParentActivity extends AppCompatActivity {


    public void openReviewsFragment(Bundle bundle) {
        showDialog(PlaceReviewsFragment.newInstance(), bundle);
    }

    //
//    public void openTrailersFragment(Bundle bundle) {
//        showDialog(MovieTrailersFragment.newInstance(), bundle);
//    }
//
    public void openImageFragment(Bundle bundle) {
        showDialog(PlaceImageFragment.newInstance(), bundle);
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

}
