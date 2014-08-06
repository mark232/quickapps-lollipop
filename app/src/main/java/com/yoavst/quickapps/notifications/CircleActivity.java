package com.yoavst.quickapps.notifications;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;
import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Yoav.
 */
@EActivity
public class CircleActivity extends BaseQuickCircleActivity implements ServiceConnection, NotificationService.Callback {
	NotificationService mService;
	boolean mBound = false;

	public boolean shouldRegister = false;
	@ViewById(R.id.notification_pager)
	ViewPager mPager;

	NotificationAdapter mAdapter;
	@ViewById(R.id.title_error)
	TextView mTitleError;
	@ViewById(R.id.image_error)
	ImageView mImageError;
	@ViewById(R.id.extra_error)
	TextView mExtraError;
	@ViewById(R.id.error_layout)
	RelativeLayout mErrorLayout;
	@ViewById(R.id.notification_indicator)
	CirclePageIndicator mIndicator;

	void initNotifications() {
		if (mBound && !shouldRegister) {
			mService.setActiveNotifications();
			ArrayList<StatusBarNotification> notifications = NotificationsManager.getNotifications();
			if (notifications == null || notifications.size() == 0) {
				showEmpty();
			} else if (mPager.getAdapter() == null) {
				mErrorLayout.setVisibility(View.GONE);
				mAdapter = new NotificationAdapter(getFragmentManager());
				mPager.setAdapter(mAdapter);
				mIndicator.setViewPager(mPager);
			}

		}
	}

	public void cancelNotification(StatusBarNotification notification) {
		if (notification != null && mBound && mService != null) {
			mService.cancelNotification(notification.getPackageName(), notification.getTag(), notification.getId());
			Toast toast = Toast.makeText(this, R.string.notification_removed, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
	}

	private void showEmpty() {
		mErrorLayout.setVisibility(View.VISIBLE);
		mTitleError.setText(R.string.notification_empty);
		mExtraError.setText("");
		mImageError.setImageResource(R.drawable.ic_empty);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification statusBarNotification) {
		NotificationsManager.addNotification(statusBarNotification);
		mPager.getAdapter().notifyDataSetChanged();
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
		NotificationsManager.removeNotification(statusBarNotification);
		mPager.getAdapter().notifyDataSetChanged();
	}

	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}

	@Override
	protected Intent getIntentForOpenCase() {
		if (shouldRegister)
			return new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		else {
			try {
				StatusBarNotification statusBarNotification = ((NotificationsFragment_) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.notification_pager + ":" + mPager.getCurrentItem()))
						.getNotification();
				statusBarNotification.getNotification().contentIntent.send();
			} catch (Exception e) {
				// Do nothing
			}
			return null;
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.notification_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	//<editor-fold desc="Service bounding">
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mBound = true;
		mService = ((NotificationService.LocalBinder) service).getService();
		mService.setCallback(this, new Runnable() {
			@Override
			public void run() {
				initNotifications();
			}
		});
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mBound = false;
	}

	@Override
	public void onStart() {
		super.onStart();
		bindService(new Intent(this,
						NotificationService.class).setAction(NotificationService.NOTIFICATION_ACTION), this,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void noPermissionForNotifications() {
		shouldRegister = true;
		mErrorLayout.setVisibility(View.VISIBLE);
		mTitleError.setText(R.string.open_the_case);
		mExtraError.setText(R.string.register_us_please);
		mImageError.setImageResource(R.drawable.ic_error);
	}

	@Override
	public void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}
	//</editor-fold>
}
