package com.yoavst.quickapps;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.personagraph.api.PGAgent;

/**
 * Created by Yoav.
 */
public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// The following lines trigger the
		// initialization of RichInsights SDK
		int sensorConfig = PGAgent.APP; // for including App Sensor
		PGAgent.init(this, "539:6mrGrKvjx9ktpNyv", sensorConfig, true);
	}
}
