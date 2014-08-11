package com.yoavst.quickapps.calendar;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.calendar_fragment)
public class EventsFragment extends Fragment {
	@ViewById(R.id.title)
	TextView mTitle;
	@ViewById(R.id.location)
	TextView mLocation;
	@ViewById(R.id.date)
	TextView mDate;
	@ViewById(R.id.time_left)
	TextView mTimeLeft;
	@StringRes(R.string.unknown)
	static String UNKNOWN;
	@FragmentArg
	Event event;
	@Pref
	Preferences_ mPrefs;

	@AfterViews
	void init() {
		CalendarUtil.CalendarResources.init(getActivity());
		mTitle.setText(event.getTitle());
		if (!mPrefs.showLocation().get() || event.getLocation() == null || event.getLocation().length() == 0)
			mLocation.setVisibility(View.GONE);
		else {
			mLocation.setVisibility(View.VISIBLE);
			mLocation.setText("At " + event.getLocation());
		}
		mDate.setText(CalendarUtil.getDateFromEvent(event));
		mTimeLeft.setText(CalendarUtil.getTimeToEvent(event));
	}
}
