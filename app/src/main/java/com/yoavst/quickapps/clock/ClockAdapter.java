package com.yoavst.quickapps.clock;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by Yoav.
 */
public class ClockAdapter extends FragmentPagerAdapter {
	public ClockAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
			default:
			case 0:
				return StopwatchFragment_.builder().build();
		}
	}

	@Override
	public int getCount() {
		return 1;
	}
}
