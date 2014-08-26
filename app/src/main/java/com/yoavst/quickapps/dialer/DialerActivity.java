package com.yoavst.quickapps.dialer;

import android.content.Intent;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Yoav.
 */
@EActivity
public class DialerActivity extends BaseQuickCircleActivity {

	@AfterViews
	void init() {
		getFragmentManager().beginTransaction().replace(R.id.quick_circle_fragment, DialerFragment_.builder().build()).commit();
	}

	@Override
	protected Intent getIntentForOpenCase() {
		return new Intent().setClassName("com.android.contacts",
				"alias.PeopleFloatingActivity").putExtra("com.lge.app.floating.launchAsFloating", true);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.dialer_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

}
