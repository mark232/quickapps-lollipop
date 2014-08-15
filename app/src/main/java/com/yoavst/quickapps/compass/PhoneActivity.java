package com.yoavst.quickapps.compass;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lge.app.floating.FloatableActivity;
import com.lge.app.floating.FloatingWindow;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.compass_activity_phone)
public class PhoneActivity extends FloatableActivity {
    @ViewById(R.id.cover_main_view)
    RelativeLayout mCoverLayout;
    @ViewById(R.id.compass_background)
    RelativeLayout mCompassBackground;
    @ViewById(R.id.needle)
    ImageView mNeedle;
    Compass mCompass;
	@Pref
	Preferences_ prefs;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
	    switchToFloatingMode(true, false, false, null);
	}

    @AfterViews
    public void init() {
        mCompass = new Compass(this, mNeedle);
    }

    @Override
    public void onAttachedToFloatingWindow(FloatingWindow w) {
        super.onAttachedToFloatingWindow(w);
        FloatingWindow window = getFloatingWindow();
        if (null != window) {
            Resources res = getResources();

            View titleBackground = (View) window.findViewWithTag
                    (FloatingWindow.Tag.TITLE_BACKGROUND);
            if (titleBackground != null) {
               mCompassBackground.setBackground(titleBackground.getBackground());
            }
            TextView titleText = (TextView) w.findViewWithTag
                    (FloatingWindow.Tag.TITLE_TEXT);
            if (titleText != null) {
                titleText.setText(getString(R.string.compass_module_name));
            }
            ImageButton fullscreenButton = (ImageButton) w.findViewWithTag
                    (FloatingWindow.Tag.FULLSCREEN_BUTTON);
            if (fullscreenButton != null) {
                ((ViewGroup) fullscreenButton.getParent()).removeView(fullscreenButton);
            }
        }

    }

    @Override
	protected void onPause() {
		super.onPause();
        if(!isSwitchingToFloatingMode())
		    mCompass.unregisterService();
	}

    @Override
	public void onResume() {
        super.onResume();
        mCompass.registerService();
    }
}
