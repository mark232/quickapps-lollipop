package com.yoavst.quickapps;

import com.personagraph.api.PGAgent;

/**
 * Created by Yoav.
 */
public abstract class BaseQuickCircleActivity extends AbstractQuickCircleActivity {
	@Override
	protected void onStart() {
		super.onStart();
		PGAgent.startSession(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		PGAgent.endSession(this);
	}
}
