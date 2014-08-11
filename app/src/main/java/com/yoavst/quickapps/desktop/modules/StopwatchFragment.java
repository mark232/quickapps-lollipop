package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
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
@EFragment(R.layout.desktop_module_stopwatch)
public class StopwatchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

	@ViewById(R.id.millis_checkbox)
	CheckBox mMillis;
	@Pref
	Preferences_ mPrefs;

	@AfterViews
	void init() {
		mMillis.setChecked(mPrefs.stopwatchShowMillis().get());
		mMillis.setOnCheckedChangeListener(this);

	}

	@Click({R.id.millis_row})
	void clickRow(View view) {
		switch (view.getId()) {
			case R.id.millis_row:
				mMillis.toggle();
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.millis_checkbox:
				mPrefs.stopwatchShowMillis().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
