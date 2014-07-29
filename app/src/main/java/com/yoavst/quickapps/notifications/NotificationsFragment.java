package com.yoavst.quickapps.notifications;

import android.app.Fragment;
import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoavst.quickapps.R;
import com.yoavst.quickapps.calendar.DateUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.notification_fragment)
public class NotificationsFragment extends Fragment {
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
	private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("MMM d, HH:mm");
	private static final SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");

	public StatusBarNotification getNotification() {
		return mNotification;
	}

	@AfterViews
	void init() {
		if (mNotification != null) {
			Bundle extras = mNotification.getNotification().extras;
			mNotificationTitle.setText(extras.getString(Notification.EXTRA_TITLE));
			try {
				mNotificationIcon.setImageDrawable(getActivity().createPackageContext(mNotification.getPackageName(), 0).getResources().getDrawable(mNotification.getNotification().icon));
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
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
			Log.e("Notification", extras.keySet().toString());
			long time = mNotification.getPostTime();
			Date date = new Date(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			if (DateUtils.isToday(date))
				mNotificationTime.setText("Today " + hourFormatter.format(date));
			else if (DateUtils.isYesterday(calendar))
				mNotificationTime.setText("Yesterday " + hourFormatter.format(date));
			else
				mNotificationTime.setText(dayFormatter.format(date));
		}
	}
}
