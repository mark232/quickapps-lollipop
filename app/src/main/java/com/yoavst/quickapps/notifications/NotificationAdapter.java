package com.yoavst.quickapps.notifications;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by Yoav.
 */
public class NotificationAdapter extends FragmentPagerAdapter {
	public NotificationAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		return NotificationsFragment_.builder().mNotification(NotificationsManager.getNotifications().get(i)).build();
	}

	@Override
	public int getCount() {
		return NotificationsManager.getNotifications() == null ? 0 : NotificationsManager.getNotifications().size();
	}
}
