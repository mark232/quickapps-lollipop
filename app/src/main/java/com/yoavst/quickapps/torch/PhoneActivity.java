package com.yoavst.quickapps.torch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;

import com.lge.app.floating.FloatableActivity;
import com.lge.app.floating.FloatingWindow;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.torch_activity_phone)
public class PhoneActivity extends FloatableActivity {
	@ViewById(R.id.layout)
	ViewGroup mLayout;
	@ViewById(R.id.torch)
	IconTextView mTorch;
	@ColorRes(R.color.torch_background_color_on)
	static int mColorBackgroundOn;
	@ColorRes(R.color.torch_background_color_off)
	static int mColorBackgroundOff;
	@ColorRes(R.color.torch_color_on)
	static int mColorTorchOn;
	@ColorRes(R.color.torch_color_off)
	static int mColorTorchOff;
	NotificationManager mNotificationManager;
	@Pref
	Preferences_ prefs;
	public static Notification mNotification;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		createNotification(this);
		if (prefs.torchForceFloating().get())
			switchToFloatingMode();

	}

	@Override
	public void onAttachedToFloatingWindow(FloatingWindow w) {
		super.onAttachedToFloatingWindow(w);
		mLayout = (ViewGroup) findViewById(R.id.layout);
		mTorch = (IconTextView) findViewById(R.id.torch);
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleTorch();
			}
		});
	}

	@Override
	public boolean onDetachedFromFloatingWindow(FloatingWindow w, boolean isReturningToFullScreen) {
		if (!isReturningToFullScreen) {
			if (CameraManager.torchOn)
				mNotificationManager.notify(NotificationReceiver.NOTIFICATION_ID, mNotification);
			else CameraManager.destroy();
		}
		return super.onDetachedFromFloatingWindow(w, isReturningToFullScreen);
	}

	public static void createNotification(Context context) {
		if (mNotification == null) {
			Intent intent = new Intent("com.yoavst.notificationtorch");
			PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			// Build notification
			Notification.Builder notificationBuilder = new Notification.Builder(context)
					.setContentTitle(context.getString(R.string.torch_is_on))
					.setContentText(context.getString(R.string.touch_to_turn_off)).setSmallIcon(R.drawable.torch_icon)
					.setAutoCancel(true)
					.setContentIntent(pIntent);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				mNotification = notificationBuilder.build();
			else mNotification = notificationBuilder.getNotification();
			mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		}
	}

	@Click(R.id.layout)
	void toggleTorch() {
		if (CameraManager.toggleTorch()) {
			showTorchOn();
		} else {
			showTorchOff();
		}
	}

	private void showTorchOn() {
		mTorch.setText("{fa-bolt}");
		mTorch.setTextColor(mColorTorchOn);
		mLayout.setBackgroundColor(mColorBackgroundOn);
	}

	private void showTorchOff() {
		mTorch.setText("{fa-power-off}");
		mTorch.setTextColor(mColorTorchOff);
		mLayout.setBackgroundColor(mColorBackgroundOff);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancel(NotificationReceiver.NOTIFICATION_ID);
		CameraManager.destroy();
	}

	@Override
	public void onBackPressed() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (CameraManager.torchOn)
			mNotificationManager.notify(NotificationReceiver.NOTIFICATION_ID, mNotification);
		else CameraManager.destroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		CameraManager.init();
		mNotificationManager.cancel(NotificationReceiver.NOTIFICATION_ID);
		if (CameraManager.torchOn) {
			showTorchOn();
			CameraManager.torch();
		} else
			showTorchOff();
	}
}
