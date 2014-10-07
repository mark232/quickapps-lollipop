package com.yoavst.quickapps;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.personagraph.api.PGAgent;

/**
 * Created by Yoav.
 */
public abstract class BaseQuickCircleActivity extends AbstractQuickCircleActivity {
	GestureDetector mDetector;
	private static final String DEBUG_TAG = "touch_event";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Log.d(DEBUG_TAG, "onDoubleTap: " + e.toString());
				DoubleTapper.getInstance().onDoubleTap(BaseQuickCircleActivity.this);
				return true;
			}
		};
		mDetector = new GestureDetector(this, listener);
		mDetector.setOnDoubleTapListener(listener);
	}

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

	@Override
	public boolean onTouchEvent(MotionEvent event){
		this.mDetector.onTouchEvent(event);
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

}
