package com.yoavst.quickapps.calendar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Yoav.
 */
public class EventsAdapter extends FragmentPagerAdapter {
	ArrayList<Event> events;

	public EventsAdapter(FragmentManager fm, Context context) {
		super(fm);
		events = CalendarUtil.getCalendarEvents(context);
	}

	public void clearEventsForGc() {
		if (events != null) {
			events.clear();
			events.trimToSize();
		}
	}

	@Override
	public Fragment getItem(int i) {
		return EventsFragment_.builder().event(events.get(i)).build();
	}

	@Override
	public int getCount() {
		return events.size();
	}
}
