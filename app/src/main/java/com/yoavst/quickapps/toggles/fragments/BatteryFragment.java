package com.yoavst.quickapps.toggles.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.BatteryManager;
import android.widget.Toast;

import com.yoavst.quickapps.R;
import com.yoavst.quickapps.toggles.ToggleFragment;
import com.yoavst.quickapps.toggles.TogglesActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by Yoav.
 */
@EFragment
public class BatteryFragment extends ToggleFragment {
	@StringRes(R.string.battery)
	String BATTERY;
	@StringRes(R.string.charging)
	String CHARGING;
	BroadcastReceiver mBatteryReceiver;
	int oldBatteryLevel = -1;
	int oldCharging = -1;

	@AfterViews
	void init() {
		mToggleTitle.setText(BATTERY);
		mToggleIcon.setBackground(null);
		mToggleIcon.setColorFilter(Color.BLACK);
		if (mBatteryReceiver == null)
			mBatteryReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					setToggleData(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
				}
			};
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = getActivity().registerReceiver(mBatteryReceiver, intentFilter);
		setToggleData(intent == null ? -1 : intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
				intent == null ? -1 : intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
	}

	void setToggleData(int percents, int charging) {
		// Do something only if it is changed
		if (percents != oldBatteryLevel || charging != oldCharging) {
			// If charging
			String resource;
			if (charging != 0) {
				if (percents <= 5) resource = "stat_sys_battery_weak_charging_05_vzw";
				else if (percents <= 20) resource = "stat_sys_battery_weak_charging_10_vzw";
				else if (percents <= 45) resource = "stat_sys_battery_weak_charging_30_vzw";
				else if (percents <= 75) resource = "stat_sys_battery_weak_charging_60_vzw";
				else if (percents != 100) resource = "stat_sys_battery_weak_charging_90_vzw";
				else resource = "stat_sys_battery_full_charging";
			} else {
				int percent1 = ((percents + 4) / 5 * 5);
				if (percent1 > 100) percent1 = 100;
				else if (percent1 < 0) percent1 = 0;
				resource = "stat_sys_battery_" + percent1;
			}
			try {
				mToggleIcon.setImageDrawable(((TogglesActivity) getActivity()).getSystemUiResource().getDrawable(
						((TogglesActivity) getActivity()).getSystemUiResource().getIdentifier(resource, "drawable", "com.android.systemui")));
			} catch (Resources.NotFoundException exception) {
				Toast.makeText(getActivity(), "Error - contact developer with battery level", Toast.LENGTH_SHORT).show();
			}
			mToggleText.setText(percents + "% ");
			if (charging != 0) {
				mToggleText.append(CHARGING);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			getActivity().unregisterReceiver(mBatteryReceiver);
		} catch (Exception ignored) {
			// Do nothing - receiver not registered
		}
	}

	@Override
	public void onToggleButtonClicked() {
		// Do nothing
	}

	@Override
	public Intent getIntentForLaunch() {
		return new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
	}
}
