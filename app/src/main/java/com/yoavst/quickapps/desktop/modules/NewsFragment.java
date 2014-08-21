package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
import android.content.Intent;
import android.widget.Toast;

import com.yoavst.quickapps.R;
import com.yoavst.quickapps.news.Prefs_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_news)
public class NewsFragment extends Fragment {
	@Pref
	Prefs_ mPrefs;
	@Click(R.id.logout_row)
	void onLogoutClicked() {
		mPrefs.clear();
		Toast.makeText(getActivity(), R.string.news_logout_feedly, Toast.LENGTH_SHORT).show();
	}
}
