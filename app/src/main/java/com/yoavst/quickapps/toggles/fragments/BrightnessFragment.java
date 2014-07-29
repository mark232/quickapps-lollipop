package com.yoavst.quickapps.toggles.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.provider.Settings;

import com.yoavst.quickapps.R;
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
public class BrightnessFragment extends ToggleFragment {
	@StringRes(R.string.brightness)
	String BRIGHTNESS;
	@StringRes(R.string.brightness_med)
	String MED;
	@StringRes(R.string.brightness_auto)
	String AUTO;
	@StringRes(R.string.brightness_max)
	String MAX;
	Resources mSystemUiResources;
	// resources id of system ui stuff
	static int maxBrightnessIcon = -1;
	static int medBrightnessIcon = -1;
	static int autoBrightnessIcon = -1;
	/**
	 * 0 is Auto
	 * 1 is Medium
	 * 2 is Max
	 */
	int selectedMode = -1;


	@AfterViews
	void init() {
		mToggleTitle.setText(BRIGHTNESS);
		mSystemUiResources = ((TogglesActivity)getActivity()).getSystemUiResource();
		if (maxBrightnessIcon == -1 || medBrightnessIcon == -1 || autoBrightnessIcon == -1) {
			maxBrightnessIcon = mSystemUiResources.getIdentifier("indi_noti_brightness_max_on", "drawable", "com.android.systemui");
			autoBrightnessIcon = mSystemUiResources.getIdentifier("indi_noti_brightness_auto_on", "drawable", "com.android.systemui");
			medBrightnessIcon = mSystemUiResources.getIdentifier("indi_noti_brightness_mid_on", "drawable", "com.android.systemui");
		}
		int brightness = getBrightness();
		if (brightness == -1) selectedMode = 0;
		else if (brightness < 150) selectedMode = 1;
		else selectedMode = 2;
		showBrightness();
	}

	@Override
	public void onToggleButtonClicked() {
		selectedMode++;
		if (selectedMode == 3) selectedMode = 0;
		if (selectedMode == 0) setBrightnessMode(true);
		else if (selectedMode == 1) setBrightness(127);
		else setBrightness(200);
		showBrightness();
	}

	private void showBrightness() {
		if (selectedMode == 0) {
			mToggleIcon.setImageDrawable(mSystemUiResources.getDrawable(autoBrightnessIcon));
			mToggleText.setText(AUTO);
		} else if (selectedMode == 1) {
			mToggleIcon.setImageDrawable(mSystemUiResources.getDrawable(medBrightnessIcon));
			mToggleText.setText(MED);
		} else {
			mToggleIcon.setImageDrawable(mSystemUiResources.getDrawable(maxBrightnessIcon));
			mToggleText.setText(MAX);
		}
	}

	private void setBrightnessMode(boolean auto) {
		Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, auto ? 1 : 0);
	}

	private void setBrightness(int level) {
		setBrightnessMode(false);
		Settings.System.putInt(getActivity().getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, level);
	}

	private int getBrightness() {
		try {
			if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) == 1) return -1;
			return Settings.System.getInt(getActivity().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
