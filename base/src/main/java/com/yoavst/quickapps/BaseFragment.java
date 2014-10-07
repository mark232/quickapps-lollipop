package com.yoavst.quickapps;

import android.app.Fragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Yoav.
 */
public abstract class BaseFragment extends Fragment {
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				DoubleTapper.getInstance().onDoubleTap(getActivity());
				return true;
			}
		};
		final GestureDetector mDetector = new GestureDetector(view.getContext(), listener);
		mDetector.setOnDoubleTapListener(listener);
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mDetector.onTouchEvent(event);
			}
		});
	}
}
