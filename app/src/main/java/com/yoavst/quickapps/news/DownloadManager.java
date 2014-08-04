package com.yoavst.quickapps.news;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.news.types.AuthResponse;
import com.yoavst.quickapps.news.types.Entry;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yoav.
 */
@EBean
public class DownloadManager {
	private OAuthService mService;
	@Pref
	Prefs_ mPrefs;
	@RootContext
	Context mContext;
	private Type listType = new TypeToken<ArrayList<Entry>>(){}.getType();
	private boolean isDownloadingNow = false;

	public synchronized void saveToken(Token token) {
		AuthResponse response = new Gson().fromJson(token.getRawResponse(), AuthResponse.class);
		mPrefs.edit()
				.userId().put(response.getId())
				.refreshToken().put(response.getRefreshToken())
				.accessToken().put(response.getAccessToken())
				.rawResponse().put(token.getRawResponse())
				.apply();
	}

	public synchronized Token getTokenFromPrefs() {
		if (mPrefs.accessToken().exists()) {
			try {
				return new Token(mPrefs.accessToken().get(), "", mPrefs.rawResponse().get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public synchronized OAuthService getService() {
		if (mService == null) {
			mService = new ServiceBuilder()
					.provider(FeedlyApi.Sandbox.class)
					.apiKey(mContext.getResources().getString(R.string.client_id))
					.apiSecret(mContext.getResources().getString(R.string.client_secret))
					.callback("http://localhost:8080")
					.scope(FeedlyApi.SCOPE)
					.build();
		}
		return mService;
	}

	@Background
	public void download(DownloadingCallback callback) {
		if (!isNetworkAvailable()) {
			if (callback != null) callback.onFail(DownloadError.Internet);
		} else {
			Token accessToken = getTokenFromPrefs();
			if (accessToken == null) {
				if (callback != null) callback.onFail(DownloadError.Login);
			} else if (!isDownloadingNow) {
				isDownloadingNow = true;
				// Init the service
				getService();
				OAuthRequest request = new OAuthRequest(Verb.GET, "https://sandbox.feedly.com/v3/streams/contents?streamId=user/" + mPrefs.userId().get() + "/category/global.all");
				request.addHeader("Authorization", accessToken.getToken());
				request.setConnectTimeout(10, TimeUnit.SECONDS);
				mService.signRequest(accessToken, request);
				try {
					Response response = request.send();
					JsonObject jsonObject;
					try {
						jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
					} catch (JsonParseException exception) {
						if (callback != null) callback.onFail(DownloadError.Other);
						return;
					}
					String items = jsonObject.get("items").toString();
					try {
						ArrayList<Entry> entries = new Gson().fromJson(items, listType);
						if (callback != null) callback.onSuccess(entries);
						mPrefs.feed().put(new Gson().toJson(entries, listType));
					} catch (JsonParseException exception) {
						if (callback != null) callback.onFail(DownloadError.Other);
					}
				} catch (OAuthConnectionException exception) {
					if (callback != null) callback.onFail(DownloadError.Internet);
				} finally {
					isDownloadingNow = false;
				}
			}
		}
	}

	public ArrayList<Entry> getFeedFromPrefs() {
		return  mPrefs.feed().exists() ? (ArrayList<Entry>) new Gson().fromJson(mPrefs.feed().get(),listType) : null;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static interface DownloadingCallback {
		public void onFail(DownloadError error);

		public void onSuccess(ArrayList<Entry> entries);
	}

	public enum DownloadError {Login, Internet, Other}
}
