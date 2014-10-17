package com.yoavst.quickapps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by Yoav.
 */
public abstract class AbstractQuickCircleActivity extends Activity {

	// [START]declared in LGIntent.java of LG Framework
	public static final int EXTRA_ACCESSORY_COVER_OPENED = 0;
	public static final int EXTRA_ACCESSORY_COVER_CLOSED = 1;
	public static final String EXTRA_ACCESSORY_COVER_STATE = "com.lge.intent.extra.ACCESSORY_COVER_STATE";
	public static final String ACTION_ACCESSORY_COVER_EVENT = "com.lge.android.intent.action.ACCESSORY_COVER_EVENT";
	// [END]declared in LGIntent.java of LG Framework

	// [START] QuickCover Settings DB
	public static final String QUICKCOVERSETTINGS_QUICKCOVER_ENABLE = "quick_view_enable";
	// [END] QuickCover Settings DB

	// [START] QuickCircle info.
	static boolean quickCircleEnabled = false;
	static int quickCaseType = 0;
	static boolean quickCircleClosed = true;
	int circleWidth = 0;
	int circleHeight = 0;
	int circleXpos = 0;
	int circleYpos = 0;
	int circleDiameter = 0;
	// [END] QuickCircle info.

	Boolean isG3 = false;
	protected int mQuickCoverState = 0;
	ContentResolver mContentResolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		isG3 = Build.DEVICE.equals("g3") || Build.DEVICE.equals("tiger6");
		//Retrieve a view for the QuickCircle window.
		final View circleMainView = findViewById(getMainCircleLayoutId());
		//Get content resolver
		mContentResolver = getContentResolver();
		//Register an IntentFilter and a broadcast receiver
		registerIntentReceiver();
		//Set window flags
		setQuickCircleWindowParam();
		//Get QuickCircle window information
		initializeViewInformationFromDB();
		//Crops a layout for the QuickCircle window
		setCircleLayoutParam(circleMainView);
		// Remove the mask if needed
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mIntentReceiver);
	}

	private void registerIntentReceiver() {
		IntentFilter filter = new IntentFilter();
		// Add QCircle intent to the intent filter
		filter.addAction(ACTION_ACCESSORY_COVER_EVENT);
		// Register a broadcast receiver with the system
		registerReceiver(mIntentReceiver, filter);
	}

	private void setQuickCircleWindowParam() {
		Window win = getWindow();
		if (win != null) {
			// Show the sample application view on top
			win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	void setCircleLayoutParam(View view) {
		RelativeLayout layout = (RelativeLayout) view;
		RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) layout.getLayoutParams();
		//Set layout size same as a circle window size
		layoutParam.width = circleDiameter + 100;
		layoutParam.height = circleDiameter + 60;
		if (circleXpos < 0) {
			//Place a layout to the center
			layoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		} else {
			layoutParam.leftMargin = circleXpos;
		}
		//Set top margin to the offset
		if (isG3) {
			layoutParam.topMargin = circleYpos;
		} else {
			layoutParam.topMargin = circleYpos + (circleHeight - circleDiameter) / 2;
		}
		layout.setLayoutParams(layoutParam);
	}

	void initializeViewInformationFromDB() {
		if (mContentResolver == null) {
			return;
		}
		//Check the availability of the case
		quickCircleEnabled = Settings.Global.getInt(mContentResolver,
				QUICKCOVERSETTINGS_QUICKCOVER_ENABLE, 0) == 0;
		//Get a case type;
		quickCaseType = Settings.Global.getInt(mContentResolver, "cover_type", 0/*default value*/);
		//[START] Get the QuickCircle window information
		int id = getResources().getIdentifier("config_circle_window_width", "dimen",
				"com.lge.internal");
		circleWidth = getResources().getDimensionPixelSize(id);
		id = getResources()
				.getIdentifier("config_circle_window_height", "dimen", "com.lge.internal");
		circleHeight = getResources().getDimensionPixelSize(id);
		id = getResources()
				.getIdentifier("config_circle_window_x_pos", "dimen", "com.lge.internal");
		circleXpos = getResources().getDimensionPixelSize(id);
		id = getResources()
				.getIdentifier("config_circle_window_y_pos", "dimen", "com.lge.internal");
		circleYpos = getResources().getDimensionPixelSize(id);
		id = getResources().getIdentifier("config_circle_diameter", "dimen", "com.lge.internal");
		circleDiameter = getResources().getDimensionPixelSize(id);
	}

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == null) {
				return;
			}
			//Receives a LG QCirle intent for the cover event
			if (ACTION_ACCESSORY_COVER_EVENT.equals(action)) {
				//Gets the current state of the cover
				mQuickCoverState = intent.getIntExtra(EXTRA_ACCESSORY_COVER_STATE,
						EXTRA_ACCESSORY_COVER_OPENED);
				if (mQuickCoverState == EXTRA_ACCESSORY_COVER_CLOSED) { // closed
					//Set window flags
					setQuickCircleWindowParam();
				} else if (mQuickCoverState == EXTRA_ACCESSORY_COVER_OPENED) { // opened
					//Call intent on open
					Intent launchIntent = getIntentForOpenCase();
					if (launchIntent != null)
						startActivity(launchIntent);
					//Finish the activity
					AbstractQuickCircleActivity.this.finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}
		}
	};

	/**
	 * Return intent that will be called when the quick case is being opened. if it is null, it will go to the launcher
	 *
	 * @return null or intent that will be called when the quick case is being opened.
	 */
	protected abstract Intent getIntentForOpenCase();

	/**
	 * return the Layout id for the activity.
	 *
	 * @return The Layout id for the activity.
	 */
	protected abstract int getLayoutId();

	/**
	 * Return the view id for the main circle RelativeLayout.
	 *
	 * @return The view id for the main circle RelativeLayout.
	 */
	protected abstract int getMainCircleLayoutId();

	/**
	 * Return true if should show quick_circle_mask, false if not (G2 Quick Window)
	 *
	 * @return true if should show quick_circle_mask, false if not (G2 Quick Window)
	 */
	protected boolean isCircle() {
		return true;
	}
}
