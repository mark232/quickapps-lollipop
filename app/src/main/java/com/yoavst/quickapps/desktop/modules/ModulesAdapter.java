package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.yoavst.quickapps.R;

/**
 * Created by Yoav.
 */
public class ModulesAdapter extends FragmentPagerAdapter {
	private final String[] TITLES;
	public ModulesAdapter(FragmentManager fm, Context context) {
		super(fm);
		TITLES = context.getResources().getStringArray(R.array.modules);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment;
		switch (i) {
			default:
			case 0:
				fragment = TorchFragment_.builder().build();
				break;
			case 1:
				fragment = MusicFragment_.builder().build();
				break;
			case 2:
				fragment = CalendarFragment_.builder().build();
				break;
			case 3:
				fragment = NotificationsFragment_.builder().build();
				break;
			case 4:
				fragment = TogglesFragment_.builder().build();
				break;
			case 5:
				fragment = LauncherFragment_.builder().build();
				break;
			case 6:
				fragment = StopwatchFragment_.builder().build();
				break;
			case 7:
				fragment = CalculatorFragment_.builder().build();
				break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}
}
