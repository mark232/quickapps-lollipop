package com.yoavst.quickapps.news;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Yoav.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface Prefs {
	/**
	 * The user id
	 * @return The user id
	 */
	String userId();
	/**
	 * Feedly Refresh token
	 * @return The Feedly Refresh token
	 */
	String refreshToken();
	/**
	 * Feedly access token
	 * @return The Feedly access token
	 */
	String accessToken();

	/**
	 * Feedly raw auth response
	 * @return The Feedly raw auth response
	 */
	String rawResponse();

	/**
	 * The articles that being cached
	 * @return The articles that being cached
	 */
	String feed();

	/**
	 * The last refresh time
	 * @return The last refresh time
	 */
	long lastUpdateTime();
}
