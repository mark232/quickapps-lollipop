package com.yoavst.quickapps.torch;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.IconTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

/**
 * Created by Yoav.
 */
@EActivity
public class QuickActivity extends BaseQuickCircleActivity {
	@ViewById(R.id.cover_main_view)
	RelativeLayout mCoverLayout;
	@ViewById(R.id.torch)
	IconTextView mTorch;
	@ColorRes(R.color.torch_color_on)
	int mColorTorchOn;
	@ColorRes(R.color.torch_color_off)
	int mColorTorchOff;
	NotificationManager mNotificationManager;

	@AfterViews
	void init() {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Click(R.id.cover_main_view)
	public void toggleTorch() {
		if (CameraManager.toggleTorch()) {
			showTorchOn();
		} else {
			showTorchOff();
		}
	}
	private void showTorchOn() {
		mTorch.setText("{fa-bolt}");
		mTorch.setTextColor(mColorTorchOn);
		mCoverLayout.setBackgroundResource(R.drawable.torch_background_on);
	}

	private void showTorchOff() {
		mTorch.setText("{fa-power-off}");
		mTorch.setTextColor(mColorTorchOff);
		mCoverLayout.setBackgroundResource(R.drawable.torch_background_off);
	}

	@Click(R.id.back_btn)
	void onBackClicked() {
		CameraManager.disableTorch();
		CameraManager.destroy();
		mNotificationManager.cancel(NotificationReceiver.NOTIFICATION_ID);
		onBackPressed();
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			CameraManager.init();
		} catch (RuntimeException e) {
			Toast toast = Toast.makeText(this, "Error connect camera", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
		if (CameraManager.torchOn) {
			showTorchOn();
		}
	}

	@Override
	protected Intent getIntentForOpenCase() {
		return PhoneActivity_.intent(this).get();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.torch_activity_quick;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}
}
