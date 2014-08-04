package com.yoavst.quickapps.toggles;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
public class TogglesActivity extends BaseQuickCircleActivity {
	@ViewById(R.id.quick_circle_fragment)
	ViewPager mPager;
	TogglesAdapter mAdapter;
	Resources mSystemUiResources;

	@AfterViews
	void init() {
		mAdapter = new TogglesAdapter(getFragmentManager());
		mPager.setAdapter(mAdapter);
		final PackageManager pm = getPackageManager();
		final ApplicationInfo applicationInfo;
		try {
			applicationInfo = pm.getApplicationInfo("com.android.systemui", PackageManager.GET_META_DATA);
			mSystemUiResources = pm.getResourcesForApplication(applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			// Congratulations user, you are so dumb that there is no system ui...
			e.printStackTrace();
		}
	}

	@Override
	protected Intent getIntentForOpenCase() {
		return ((ToggleFragment) (getFragmentManager().findFragmentByTag("android:switcher:" + R.id.quick_circle_fragment + ":" + mPager.getCurrentItem()))).getIntentForLaunch();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.toggles_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}


	public Resources getSystemUiResource() {
		return mSystemUiResources;
	}
}
