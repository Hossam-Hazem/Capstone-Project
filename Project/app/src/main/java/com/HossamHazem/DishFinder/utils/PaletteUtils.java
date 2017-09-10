package com.HossamHazem.DishFinder.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Hossam on 9/9/2017.
 */

public class PaletteUtils {

    public static void setTitleBarColor(Bitmap bitmap, final View titleBar) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette p) {
                int mutedColor = p.getDarkMutedColor(0xFF333333);
                int mTransparentMutedColor = mutedColor & 0x99FFFFFF;
                titleBar.setBackgroundColor(mTransparentMutedColor);
            }
        });
    }

    public static void setTitleBarColor(ImageView imageView, View titleBar) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        setTitleBarColor(bitmap, titleBar);
    }

    public static Target getTargetCallback(final ImageView imageView, final View titleBar) {
        return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                PaletteUtils.setTitleBarColor(bitmap, titleBar);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

}
