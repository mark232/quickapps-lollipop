package com.yoavst.quickapps.notifications;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Yoav.
 */
public class NotificationsManager {
	private static ArrayList<StatusBarNotification> notifications;

	public static ArrayList<StatusBarNotification> getNotifications() {
		return notifications;
	}

	public static void setNotifications(ArrayList<StatusBarNotification> newNotifications) {
		for (int i = newNotifications.size() - 1; i >= 0; i--) {
			Bundle extras = newNotifications.get(i).getNotification().extras;
			String title = extras.getString(Notification.EXTRA_TITLE);
			if (title == null || title.length() == 0)
				newNotifications.remove(i);
		}
		notifications = newNotifications;
		sort();
	}

	public static void addNotification(StatusBarNotification statusBarNotification) {
		if (notifications == null) {
			notifications = new ArrayList<>();
		}
		notifications.add(statusBarNotification);
		sort();
	}

	public static void removeNotification(StatusBarNotification statusBarNotification) {
		if (notifications != null) {
			notifications.remove(statusBarNotification);
		}
	}

	private static void sort() {
		Collections.sort(notifications, new Comparator<StatusBarNotification>() {
			@Override
			// return an integer < 0 if lhs is less than rhs, 0 if they are equal, and > 0 if lhs is greater than rhs
			public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
				return lhs.isClearable() ? (rhs.isClearable() ? 0 : -1) : (rhs.isClearable() ? 0 : -1);
			}
		});
	}

	private NotificationsManager() {
	}
}
