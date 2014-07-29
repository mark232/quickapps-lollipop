package com.yoavst.quickapps.toggles;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import com.yoavst.quickapps.toggles.fragments.*;
/**
 * Created by Yoav.
 */
public class TogglesAdapter extends FragmentPagerAdapter {
	public TogglesAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
			default:
			case 0:
				return BatteryFragment_.builder().build();
			case 1:
				return WifiFragment_.builder().build();
			case 2:
				return DataFragment_.builder().build();
			case 3:
				return BrightnessFragment_.builder().build();
			case 4:
				return SoundFragment_.builder().build();
			case 5:
				return BluetoothFragment_.builder().build();
			case 6:
				return AirplaneFragment_.builder().build();
		}
	}

	@Override
	public int getCount() {
		return 7;
	}
}
