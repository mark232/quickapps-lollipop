package com.yoavst.quickapps.news;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.yoavst.quickapps.news.types.Entry;


import java.util.ArrayList;

/**
 * Created by Yoav.
 */
public class NewsAdapter extends FragmentPagerAdapter {
	private static ArrayList<Entry> entries;
	public NewsAdapter(FragmentManager fm, ArrayList<Entry> entries) {
		super(fm);
		NewsAdapter.entries = entries;
	}

	@Override
	public Fragment getItem(int i) {
		return NewsFragment_.builder().entryNumber(i).build();
	}

	public static Entry getEntry(int i) {
		if (entries == null || i < 0 || entries.size() <= i) return null;
		else return entries.get(i);
	}

	@Override
	public int getCount() {
		return entries != null ? entries.size() : 0;
	}
}
