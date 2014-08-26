package com.yoavst.quickapps.desktop.modules;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.HashMap;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_dialer)
public class DialerFragment extends Fragment {

	@Pref
	Preferences_ mPrefs;
	HashMap<Integer, Pair<String, String>> mQuickNumbers = new HashMap<>(10);
	int lastNum = -1;
	public static final int PICK_CONTACT_REQUEST = 42;

	@Click({R.id.digit1, R.id.digit2, R.id.digit3,
			R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9})
	void onQuickDialClicked(View view) {
		lastNum = Integer.parseInt((String) view.getTag());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				lastNum = -1;
			}
		}
	}

}
