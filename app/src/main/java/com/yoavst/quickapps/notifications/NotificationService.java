package com.yoavst.quickapps.notifications;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Yoav.
 */
public class NotificationService extends NotificationListenerService {
	private final IBinder mBinder = new LocalBinder();
	private Callback mCallback;
	public static final String NOTIFICATION_ACTION = "notification_action";

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		if (mCallback != null) mCallback.onNotificationPosted(sbn);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		if (mCallback != null) mCallback.onNotificationRemoved(sbn);
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (intent.getAction().equals(NOTIFICATION_ACTION)) {
			return mBinder;
		} else {
			return super.onBind(intent);
		}
	}

	public void setCallback(Callback callback, Runnable runnable) {
		mCallback = callback;
		if (mCallback != null) {
			ContentResolver contentResolver = getContentResolver();
			String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
			String packageName = getPackageName();
			// check to see if the enabledNotificationListeners String contains our package name
			if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName) || !enabledNotificationListeners.contains("NotificationService")) {
				mCallback.noPermissionForNotifications();
			} else runnable.run();
		}
	}

	public void setActiveNotifications() {
		try {
			NotificationsManager.setNotifications(new ArrayList<>(Arrays.asList(getActiveNotifications())));
		} catch (Exception exception) {
			mCallback.noPermissionForNotifications();
		}
	}

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		NotificationService getService() {
			// Return this instance of LocalService so clients can call public methods
			return NotificationService.this;
		}
	}

	public static interface Callback {
		public void onNotificationPosted(StatusBarNotification statusBarNotification);

		public void onNotificationRemoved(StatusBarNotification statusBarNotification);

		public void noPermissionForNotifications();
	}
}
