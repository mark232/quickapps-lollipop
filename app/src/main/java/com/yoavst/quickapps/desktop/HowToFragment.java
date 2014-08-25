package com.yoavst.quickapps.desktop;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.Switch;

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
@EFragment(R.layout.desktop_howto_fragment)
public class HowToFragment extends Fragment {

	@Click(R.id.open_quick_settings)
	void openQuickSettings() {
		ComponentName settingComp = new ComponentName("com.android.settings", "com.android.settings.lge.QuickWindowCase");
		Intent settingIntent = new Intent("android.intent.action.MAIN");
		settingIntent.setComponent(settingComp);
		startActivity(settingIntent);
	}
}
