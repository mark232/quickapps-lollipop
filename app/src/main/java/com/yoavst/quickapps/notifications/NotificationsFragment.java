package com.yoavst.quickapps.notifications;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoavst.quickapps.BaseFragment;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.calendar.DateUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.notification_fragment)
public class NotificationsFragment extends BaseFragment {
	@ViewById(R.id.notification_icon)
	ImageView mNotificationIcon;
	@ViewById(R.id.notification_time)
	TextView mNotificationTime;
	@ViewById(R.id.notification_title)
	TextView mNotificationTitle;
	@ViewById(R.id.notification_text)
	TextView mNotificationText;
	@FragmentArg
	StatusBarNotification mNotification;
	static String today;
	static String yesterday;
	@Pref
	Preferences_ mPrefs;
	private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("MMM d");
	private static final SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat hourFormatterAmPm = new SimpleDateFormat("hh:mm a");

	public StatusBarNotification getNotification() {
		return mNotification;
	}

	@AfterViews
	void init() {
		if (today == null || yesterday == null) {
			today = getString(R.string.today);
			yesterday = getString(R.string.yesterday);
		}
		if (mNotification != null) {
			Bundle extras = mNotification.getNotification().extras;
			mNotificationTitle.setText(extras.getString(Notification.EXTRA_TITLE));
			try {
				mNotificationIcon.setImageDrawable(getActivity().createPackageContext(mNotification.getPackageName(), 0).getResources().getDrawable(mNotification.getNotification().icon));
			} catch (PackageManager.NameNotFoundException | Resources.NotFoundException ignored) {}
			if (mPrefs.notificationShowContent().get()) {
				CharSequence preText = extras.getCharSequence(Notification.EXTRA_TEXT);
				String text = preText == null ? null : preText.toString();
				if (text == null || text.length() == 0) {
					CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
					if (lines != null) {
						text = "";
						String newline = System.getProperty("line.separator");
						for (CharSequence line : lines) {
							text += line + newline;
						}
					}
				}
				mNotificationText.setText(text);
			}
			long time = mNotification.getPostTime();
			Date date = new Date(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			boolean isAmPm = mPrefs.amPmInNotifications().get();
			if (DateUtils.isToday(date)) {
				mNotificationTime.setText(today + " " + parseHour(date,isAmPm));
			}
			else if (DateUtils.isYesterday(calendar)) {
				mNotificationTime.setText(yesterday + " " + parseHour(date,isAmPm));
			}
			else {
				mNotificationTime.setText(dayFormatter.format(date) + ", " + parseHour(date,isAmPm));
			}
		}
	}

	String parseHour(Date date, boolean isAmPm) {
		return isAmPm ? hourFormatterAmPm.format(date) : hourFormatter.format(date);
	}
}
