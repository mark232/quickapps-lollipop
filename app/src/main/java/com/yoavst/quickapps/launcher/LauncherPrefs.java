package com.yoavst.quickapps.launcher;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Yoav.
 */
@SharedPref
public interface LauncherPrefs {
	@DefaultBoolean(true)
	boolean isVertical();
}
