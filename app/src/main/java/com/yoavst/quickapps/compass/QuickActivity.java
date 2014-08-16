package com.yoavst.quickapps.compass;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Yoav.
 */
@EActivity
public class QuickActivity extends BaseQuickCircleActivity {
	@ViewById(R.id.cover_main_view)
	RelativeLayout mCoverLayout;
	@ViewById(R.id.needle)
    ImageView mNeedle;
    Compass mCompass;

    @AfterViews
    public void init() {
        mCompass = new Compass(this, mNeedle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCompass.registerService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompass.unregisterService();
    }

    @Click(R.id.back_btn)
	void onBackClicked() {
		onBackPressed();
	}


	@Override
	protected Intent getIntentForOpenCase() {
		return PhoneActivity_.intent(this).get();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.compass_activity_quick;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

}
