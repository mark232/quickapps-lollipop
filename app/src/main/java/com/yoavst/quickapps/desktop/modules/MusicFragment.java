package com.yoavst.quickapps.desktop.modules;

import android.app.Fragment;
import android.content.Intent;

import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_music)
public class MusicFragment extends Fragment {
	@Click(R.id.listener_row)
	void onOpenSettingsClicked() {
		getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
	}
}
