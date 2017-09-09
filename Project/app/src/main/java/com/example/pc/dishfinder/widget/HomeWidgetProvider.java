package com.example.pc.dishfinder.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.pc.dishfinder.MainActivity;
import com.example.pc.dishfinder.PlaceDetailActivity;
import com.example.pc.dishfinder.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class HomeWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_ACTION = "updateWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget_provider);

        setTextView(context, views);

        setListView(context, views, appWidgetId);

        setUpdateAtView(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }



    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setTextView(Context context, RemoteViews views){
//        CharSequence widgetText = "shrug";
//        // Construct the RemoteViews object
//
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent clickIntent=new Intent(context, MainActivity.class);
        PendingIntent clickPI=PendingIntent
                .getActivity(context, 0,
                        clickIntent,
                        0);
        views.setOnClickPendingIntent(R.id.appwidget_text, clickPI);
    }

    private static void setListView(Context context, RemoteViews widget, int appWidgetId){
        Intent svcIntent=new Intent(context, HomeWidgetService.class);

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        widget.setRemoteAdapter(appWidgetId, R.id.widgetPlacesListView,
                svcIntent);

        Intent clickIntent=new Intent(context, PlaceDetailActivity.class);
        PendingIntent clickPI=PendingIntent
                .getActivity(context, 0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.widgetPlacesListView, clickPI);

    }

    private static void setUpdateAtView(Context context, RemoteViews views) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        long yourmilliseconds = sharedPrefs.getLong("locationUpdatedAt", 0)*1000;
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

        Date resultdate = new Date(yourmilliseconds);
        CharSequence widgetText = "updated at: " + sdf.format(resultdate);

        views.setTextViewText(R.id.appwidget_updatedAt, widgetText);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HomeWidgetProvider.class));

        if(intent.getAction().equals(UPDATE_ACTION)){
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetPlacesListView);
        }


        if (appWidgetIds != null && appWidgetIds.length > 0) {
            this.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}

