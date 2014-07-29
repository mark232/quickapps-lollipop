package com.yoavst.quickapps.torch;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

/**
 * Created by Yoav.
 */
public class CameraManager {
	private static Camera mCamera;
	private static Camera.Parameters mParameters;
	public static boolean torchOn = false;

	public static void init() {
		if (mCamera == null) {
			mCamera = Camera.open();
			mParameters = mCamera.getParameters();
		}
	}

	public static void destroy() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * Toggle the torch
	 *
	 * @return false if the torch is now (after the process) off, true if on
	 */
	public static boolean toggleTorch() {
		init();
		boolean flashOnBefore = mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH);
		if (flashOnBefore) {
			disableTorch();
			return false;
		} else {
			torch();
			return true;
		}
	}

	public static void torch() {
		if (mCamera != null && mParameters != null) {
			if (!mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
				mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(mParameters);
				try {
					mCamera.setPreviewTexture(new SurfaceTexture(0));
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCamera.startPreview();
				torchOn = true;
			}
		}
	}

	public static void disableTorch() {
		if (mCamera != null && mParameters != null) {
			mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(mParameters);
			mCamera.stopPreview();
			torchOn = false;
		}
	}
}
