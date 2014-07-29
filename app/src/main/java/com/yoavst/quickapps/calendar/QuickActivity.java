package com.yoavst.quickapps.calendar;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
public class QuickActivity extends BaseQuickCircleActivity {

	@ViewById(R.id.quick_circle_fragment)
	ViewPager mPager;

	@AfterViews
	void init() {
		mPager.setAdapter(new EventsAdapter(getFragmentManager(), this));
	}

	@Override
	protected Intent getIntentForOpenCase() {
		try {
			long id = ((EventsFragment_) (getFragmentManager().findFragmentByTag("android:switcher:" + R.id.quick_circle_fragment + ":" + mPager.getCurrentItem()))).event.getId();
			return CalendarUtil.launchEventById(id);
		} catch (Exception exception) {
			return null;
		}
	}

	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		((EventsAdapter)mPager.getAdapter()).clearEventsForGc();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.calendar_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}
}
