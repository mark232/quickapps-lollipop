package com.yoavst.quickapps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by Yoav.
 */
public abstract class BaseQuickCircleActivity extends AbstractQuickCircleActivity {
	@Override
	protected boolean isCircle() {
		return new Preferences_(this).isCircle().get();
	}
}
