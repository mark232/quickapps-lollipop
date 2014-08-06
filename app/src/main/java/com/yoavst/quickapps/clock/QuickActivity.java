package com.yoavst.quickapps.clock;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Yoav.
 */
@EActivity
public class QuickActivity extends BaseQuickCircleActivity {
	@ViewById(R.id.quick_circle_fragment)
	ViewPager mPager;

	@AfterViews
	void init() {
		sendBroadcast(new Intent(PhoneActivity.ACTION_FLOATING_CLOSE));
		mPager.setAdapter(new ClockAdapter(getFragmentManager()));
	}



	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}

	@Override
	protected Intent getIntentForOpenCase() {
		return PhoneActivity_.intent(this).get().putExtra("com.lge.app.floating.launchAsFloating", true);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.clock_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

}
