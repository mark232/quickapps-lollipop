package com.yoavst.quickapps.torch;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.yoavst.quickapps.R;

/**
 * Created by Yoav.
 */
public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		ComponentName myWidget = new ComponentName( context, WidgetProvider.class );
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.torch_widget_1x1);
		Intent intent = new Intent("com.yoavst.toggletorch");
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		// Create an Intent to launch Browser
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
		appWidgetManager.updateAppWidget(myWidget, updateViews);
	}
}
