package com.yoavst.quickapps.notifications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
				final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int i) {
						try {
							if (((NotificationsFragment) mAdapter.getActiveFragment(i)).getNotification().isClearable()) {
								mCancelButton.setEnabled(true);
							} else {
								mCancelButton.setEnabled(false);
							}
						} catch (NullPointerException exception) {
							exception.printStackTrace();
							mCancelButton.setEnabled(false);
						}
					}
				};
				mIndicator.setOnPageChangeListener(onPageChangeListener);
				mPager.post(new Runnable() {
					@Override
					public void run() {
						onPageChangeListener.onPageSelected(0);
					}
				});
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
        mIndicator.notifyDataSetChanged();
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
		NotificationsManager.removeNotification(statusBarNotification);
		mPager.getAdapter().notifyDataSetChanged();
        mIndicator.notifyDataSetChanged();
	}

	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}

    @Click(R.id.notification_cancel)
    public void onCancelClicked() {
        cancelNotification(getActiveFragment().getNotification());
    }

    private NotificationsFragment getActiveFragment() {
        return (NotificationsFragment) mAdapter.getActiveFragment(mPager.getCurrentItem());
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
	}

	@Override
	public void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}
	//</editor-fold>
}
