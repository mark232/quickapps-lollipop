package com.yoavst.quickapps.toggles.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Toast;

import com.yoavst.quickapps.R;
import com.yoavst.quickapps.toggles.Connectivity;
import com.yoavst.quickapps.toggles.ToggleFragment;
import com.yoavst.quickapps.toggles.TogglesActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by Yoav.
 */
@EFragment
public class AirplaneFragment extends ToggleFragment {
	@StringRes(R.string.airplane_mode)
	String AIRPLANE;
	@StringRes(R.string.airplane_off)
	String AIRPLANE_OFF;
	@StringRes(R.string.airplane_on)
	String AIRPLANE_ON;
	Resources mSystemUiResources;
	// resources id of system ui stuff
	static int airplaneOnIcon = -1;
	static int airplaneOffIcon = -1;
	@SystemService
	ConnectivityManager mConnectivityManager;
	BroadcastReceiver mDataReceiver;

	@AfterViews
	void init() {
		mToggleTitle.setText(AIRPLANE);
		mSystemUiResources = ((TogglesActivity) getActivity()).getSystemUiResource();
		if (airplaneOnIcon == -1 || airplaneOffIcon == -1) {
			airplaneOnIcon = mSystemUiResources.getIdentifier("indi_noti_flight_mode_on", "drawable", "com.android.systemui");
			airplaneOffIcon = mSystemUiResources.getIdentifier("indi_noti_flight_mode_off", "drawable", "com.android.systemui");
		}
		if (mDataReceiver == null) {
			mDataReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					setToggleData();
				}
			};
		}
		IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		getActivity().registerReceiver(mDataReceiver, filter);
		setToggleData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			getActivity().unregisterReceiver(mDataReceiver);
		} catch (Exception ignored) {
			// Do nothing - receiver not registered
		}
	}

	void setToggleData() {
		if (Connectivity.isAirplaneMode(getActivity())) {
			mToggleIcon.setImageDrawable(mSystemUiResources.getDrawable(airplaneOnIcon));
			mToggleText.setText(AIRPLANE_ON);
		} else {
			mToggleIcon.setImageDrawable(mSystemUiResources.getDrawable(airplaneOffIcon));
			mToggleText.setText(AIRPLANE_OFF);
		}
	}

	@Override
	public void onToggleButtonClicked() {
		Toast toast = Toast.makeText(getActivity(), R.string.cant_change_airplane_mode, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	@Override
	public Intent getIntentForLaunch() {
		return new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
	}

}
