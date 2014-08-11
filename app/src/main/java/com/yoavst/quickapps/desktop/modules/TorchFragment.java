package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
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
@EFragment(R.layout.desktop_module_torch)
public class TorchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

	@ViewById(R.id.launcher_checkbox)
	CheckBox mLauncher;
	@ViewById(R.id.qslide_checkbox)
	CheckBox mQslide;
	@ViewById(R.id.floating_checkbox)
	CheckBox mFloating;
	@Pref
	Preferences_ mPrefs;
	private static final String LAUNCHER_CLASS_NAME = "com.yoavst.quickapps.torch.PhoneActivityLauncher";
	private static final String QSLIDE_CLASS_NAME = "com.yoavst.quickapps.torch.PhoneActivity";


	@AfterViews
	void init() {
		mLauncher.setChecked(isActivityEnabled(getActivity(), LAUNCHER_CLASS_NAME));
		mQslide.setChecked(isActivityEnabled(getActivity(), QSLIDE_CLASS_NAME));
		mFloating.setChecked(mPrefs.torchForceFloating().get());
		mLauncher.setOnCheckedChangeListener(this);
		mQslide.setOnCheckedChangeListener(this);
		mFloating.setOnCheckedChangeListener(this);
	}

	public static void setActivityEnabled(Context context, String activityName, final boolean enable) {
		final PackageManager pm = context.getPackageManager();
		final int enableFlag = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		pm.setComponentEnabledSetting(new ComponentName(context, activityName), enableFlag, PackageManager.DONT_KILL_APP);
	}

	public static boolean isActivityEnabled(Context context, String activityName) {
		final PackageManager pm = context.getPackageManager();
		int flags = pm.getComponentEnabledSetting(new ComponentName(context.getPackageName(), activityName));
		return (flags & PackageManager.COMPONENT_ENABLED_STATE_DISABLED) == 0;
	}

	@Click({R.id.launcher_row, R.id.qslide_row, R.id.floating_row})
	void clickRow(View view) {
		switch (view.getId()) {
			case R.id.launcher_row:
				mLauncher.toggle();
				break;
			case R.id.qslide_row:
				mQslide.toggle();
				break;
			case R.id.floating_row:
				mFloating.toggle();
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.launcher_checkbox:
				setActivityEnabled(getActivity(), LAUNCHER_CLASS_NAME, isChecked);
				Toast.makeText(getActivity(), R.string.restart_launcher_for_update,Toast.LENGTH_SHORT).show();
				break;
			case R.id.qslide_checkbox:
				setActivityEnabled(getActivity(), QSLIDE_CLASS_NAME, isChecked);
				Toast.makeText(getActivity(), R.string.reboot_for_update,Toast.LENGTH_SHORT).show();
				break;
			case R.id.floating_checkbox:
				mPrefs.torchForceFloating().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully,Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
