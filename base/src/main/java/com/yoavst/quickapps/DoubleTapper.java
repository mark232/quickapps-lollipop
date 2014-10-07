package com.yoavst.quickapps;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Yoav.
 */
public class DoubleTapper {
	private static final DoubleTapper INSTANCE = new DoubleTapper();

	private DoubleTapper() {
	}

	public static DoubleTapper getInstance() {
		return INSTANCE;
	}

	/**
	 * To be hooked using Xposed
	 */
	public void onDoubleTap(Activity activity) {
		// Need to work on it
		/*
		try {
			Runtime.getRuntime().exec(new String[] {"su", "-c", "input keyevent 26"});
			activity.finish();
		} catch (IOException e) {
			// No root, do nothing
		}*/
	}
}
