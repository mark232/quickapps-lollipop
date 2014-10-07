package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_calendar)
public class CalendarFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

	@ViewById(R.id.repeating_checkbox)
	CheckBox mRepeating;
	@ViewById(R.id.location_checkbox)
	CheckBox mLocation;
	@ViewById(R.id.am_pm_checkbox)
	CheckBox mAmPm;
	@Pref
	Preferences_ mPrefs;

	@AfterViews
	void init() {
		mRepeating.setChecked(mPrefs.showRepeatingEvents().get());
		mLocation.setChecked(mPrefs.showLocation().get());
		mAmPm.setChecked(mPrefs.amPmInCalendar().get());
		mRepeating.setOnCheckedChangeListener(this);
		mLocation.setOnCheckedChangeListener(this);
		mAmPm.setOnCheckedChangeListener(this);
	}

	@Click({R.id.repeating_row, R.id.location_row,R.id.am_pm_row})
	void clickRow(View view) {
		switch (view.getId()) {
			case R.id.repeating_row:
				mRepeating.toggle();
				break;
			case R.id.location_row:
				mLocation.toggle();
				break;
			case R.id.am_pm_row:
				mAmPm.toggle();
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.repeating_checkbox:
				mPrefs.showRepeatingEvents().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
				break;
			case R.id.location_checkbox:
				mPrefs.showLocation().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
				break;
			case R.id.am_pm_checkbox:
				mPrefs.amPmInCalendar().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
