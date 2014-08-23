package com.yoavst.quickapps.notifications;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.IconButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;
import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

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
	@ViewById(R.id.notification_cancel)
	IconButton mCancelButton;

	public void initNotifications() {
		if (mBound && !shouldRegister) {
			mService.setActiveNotifications();
			mAdapter = new NotificationAdapter(getFragmentManager());
			mPager.setAdapter(mAdapter);
			mIndicator.setViewPager(mPager);
			final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int i) {
					try {
						if (getActiveFragment().getNotification().isClearable()) {
							mCancelButton.setEnabled(true);
						} else {
							mCancelButton.setEnabled(false);
						}
					} catch (NullPointerException exception) {
						mCancelButton.setEnabled(false);
					}
				}
			};
			mIndicator.setOnPageChangeListener(onPageChangeListener);
			if (NotificationsManager.getCount() == 0)
				showEmpty();
			else {
				hideError();
				mIndicator.post(new Runnable() {
					@Override
					public void run() {
						onPageChangeListener.onPageSelected(0);
					}
				});
			}
		}
	}

	public void cancelNotification(StatusBarNotification notification) {
		if (notification != null && mBound && !shouldRegister && mService != null) {
			mService.cancelNotification(notification.getPackageName(), notification.getTag(), notification.getId());
			Toast toast = Toast.makeText(this, R.string.notification_removed, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
	}

	@Click(R.id.notification_cancel)
	public void onCancelClicked() {
		try {
			Fragment fragment = getActiveFragment();
			if (fragment != null)
				cancelNotification(((NotificationsFragment) fragment).getNotification());
			if (NotificationsManager.getCount() == 0)
				showEmpty();
		} catch (Exception exception) {
			Toast toast = Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
	}

	@Override
	public void onNotificationPosted(StatusBarNotification statusBarNotification) {
		onNotificationPostedUi(statusBarNotification);
	}

	@UiThread
	protected void onNotificationPostedUi(StatusBarNotification statusBarNotification) {
		NotificationsManager.addNotification(statusBarNotification);
		updateAdapter();
		hideError();
		showContent();
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
		onNotificationRemovedUi(statusBarNotification);
	}

	@UiThread
	protected void onNotificationRemovedUi(StatusBarNotification statusBarNotification) {
		NotificationsManager.removeNotification(statusBarNotification);
		updateAdapter();
		if (NotificationsManager.getCount() == 0) {
			showEmpty();
		}
	}

	@UiThread
	void updateAdapter() {
		mPager.getAdapter().notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();
	}

	private NotificationsFragment getActiveFragment() {
		try {
			return (NotificationsFragment) mAdapter.getActiveFragment(mPager.getCurrentItem());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected Intent getIntentForOpenCase() {
		if (shouldRegister)
			return new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		else {
			try {
				StatusBarNotification statusBarNotification = getActiveFragment().getNotification();
				statusBarNotification.getNotification().contentIntent.send();
			} catch (Exception e) {
				// Do nothing
			}
			return null;
		}
	}

	private void showEmpty() {
		hideContent();
		mErrorLayout.setVisibility(View.VISIBLE);
		mTitleError.setText(R.string.notification_empty);
		mExtraError.setText("");
		mImageError.setImageResource(R.drawable.ic_empty);
		mCancelButton.setEnabled(false);
	}

	@UiThread
	protected void hideError() {
		mErrorLayout.setVisibility(View.GONE);
	}

	@UiThread
	protected void hideContent() {
		mPager.setVisibility(View.INVISIBLE);
		mIndicator.setVisibility(View.INVISIBLE);
	}

	@UiThread
	protected void showContent() {
		mPager.setVisibility(View.VISIBLE);
		mIndicator.setVisibility(View.VISIBLE);
	}

	@Click(R.id.quick_circle_back_btn)
	protected void onBackClicked() {
		finish();
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
		mCancelButton.setEnabled(false);
		mIndicator.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}
	//</editor-fold>
}
