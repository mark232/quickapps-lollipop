package com.yoavst.quickapps.torch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
	public static final int NOTIFICATION_ID = 1423;

    @Override
    public void onReceive(Context context, Intent intent) {
	    // Turn on the torch and disable all
	    CameraManager.disableTorch();
        CameraManager.destroy();
    }
}
