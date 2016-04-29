package com.example.administrator.mymobile.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.service.UpdateAppWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent=new Intent(context, UpdateAppWidgetService.class);
        context.startService(intent);
        // There may be multiple widgets active, so update all of them
//        final int N = appWidgetIds.length;
//        for (int i = 0; i < N; i++) {
//            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
//        }
    }


    @Override
    public void onEnabled(Context context) {
        Intent intent=new Intent(context, UpdateAppWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent=new Intent(context, UpdateAppWidgetService.class);
        context.stopService(intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
////        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

