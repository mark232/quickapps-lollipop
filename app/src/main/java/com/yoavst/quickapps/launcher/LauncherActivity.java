package com.yoavst.quickapps.launcher;

import android.content.Intent;
import android.view.View;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Yoav.
 */
@EActivity
public class LauncherActivity extends BaseQuickCircleActivity implements View.OnClickListener {
	final static int[] ICON_IDS = {R.id.torch, R.id.calendar, R.id.notification, R.id.music, R.id.toggles};
	@AfterViews
	void init() {
		for (int id : ICON_IDS) {
			findViewById(id).setOnClickListener(this);
		}
	}

	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}

	@Override
	protected Intent getIntentForOpenCase() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.launcher_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toggles:
				com.yoavst.quickapps.toggles.TogglesActivity_.intent(this).start();
				break;
			case R.id.torch:
				com.yoavst.quickapps.torch.QuickActivity_.intent(this).start();
				break;
			case R.id.music:
				com.yoavst.quickapps.music.RemoteControlActivity_.intent(this).start();
				break;
			case R.id.notification:
				com.yoavst.quickapps.notifications.CircleActivity_.intent(this).start();
				break;
			case R.id.calendar:
				com.yoavst.quickapps.calendar.QuickActivity_.intent(this).start();
				break;
		}
		finish();
	}
}
