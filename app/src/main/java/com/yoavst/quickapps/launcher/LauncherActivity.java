package com.yoavst.quickapps.launcher;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Yoav.
 */
@EActivity
public class LauncherActivity extends BaseQuickCircleActivity implements View.OnClickListener {
	final static int[] ICON_IDS = {R.id.torch, R.id.calendar, R.id.notification, R.id.music, R.id.toggles, R.id.stopwatch/*, R.id.news*/};
	@Pref
	LauncherPrefs_ mPrefs;
	@ViewById(R.id.change_orientation)
	ImageButton mChange;

	@AfterViews
	void init() {
		boolean isVertical = mPrefs.isVertical().get();
		setChangeDrawable(isVertical);
		getFragmentManager().beginTransaction().replace(R.id.quick_circle_fragment, isVertical ? new VerticalFragment() : new HorizontalFragment()).commit();
	}

	@Click(R.id.change_orientation)
	public void ChangeOrientation() {
		boolean newState = !mPrefs.isVertical().get();
		mPrefs.isVertical().put(newState);
		setChangeDrawable(newState);
		getFragmentManager().beginTransaction().replace(R.id.quick_circle_fragment, newState ? new VerticalFragment() : new HorizontalFragment()).commit();

	}

	void setChangeDrawable(boolean isVertical) {
		if (isVertical) mChange.setImageResource(R.drawable.ic_action_view_as_grid);
		else mChange.setImageResource(R.drawable.ic_action_view_as_list);
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
			case R.id.stopwatch:
				com.yoavst.quickapps.stopwatch.QuickActivity_.intent(this).start();
				break;
			/*case R.id.news:
				com.yoavst.quickapps.news.QuickActivity_.intent(this).start();
				break;*/
		}
		finish();
	}

	@SuppressLint("ValidFragment")
	public class VerticalFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.launcher_fragment_vertical, container, false);
			for (int id : ICON_IDS) {
				view.findViewById(id).setOnClickListener(LauncherActivity.this);
			}
			return view;
		}
	}

	@SuppressLint("ValidFragment")
	public class HorizontalFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.launcher_fragment_horizontal, container, false);
			for (int id : ICON_IDS) {
				view.findViewById(id).setOnClickListener(LauncherActivity.this);
			}
			return view;
		}
	}
}
