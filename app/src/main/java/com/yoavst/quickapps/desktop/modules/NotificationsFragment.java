package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
@EFragment(R.layout.desktop_module_notifications)
public class NotificationsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

	@ViewById(R.id.privacy_checkbox)
	CheckBox mPrivacy;
	@ViewById(R.id.am_pm_checkbox)
	CheckBox mAmPm;
	@Pref
	Preferences_ mPrefs;

	@Click(R.id.listener_row)
	void onOpenSettingsClicked() {
		getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
	}

	@AfterViews
	void init() {
		mPrivacy.setChecked(mPrefs.notificationShowContent().get());
		mPrivacy.setOnCheckedChangeListener(this);
		mAmPm.setChecked(mPrefs.amPmInNotifications().get());
		mAmPm.setOnCheckedChangeListener(this);
	}


	@Click({R.id.privacy_row, R.id.am_pm_row})
	void clickRow(View view) {
		switch (view.getId()) {
			case R.id.privacy_row:
				mPrivacy.toggle();
				break;
			case R.id.am_pm_row:
				mAmPm.toggle();
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.privacy_checkbox:
				mPrefs.notificationShowContent().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully,Toast.LENGTH_SHORT).show();
				break;
			case R.id.am_pm_checkbox:
				mPrefs.amPmInNotifications().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully,Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
