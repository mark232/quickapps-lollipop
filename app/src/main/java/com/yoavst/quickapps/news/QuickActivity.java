package com.yoavst.quickapps.news;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.news.types.Entry;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.scribe.model.Token;

import java.util.ArrayList;

/**
 * Created by Yoav.
 */
@EActivity
public class QuickActivity extends BaseQuickCircleActivity implements DownloadManager.DownloadingCallback {
	@ViewById(R.id.pager)
	ViewPager mPager;
	@ViewById(R.id.title_error)
	TextView mTitleError;
	@ViewById(R.id.extra_error)
	TextView mExtraError;
	@ViewById(R.id.error_layout)
	RelativeLayout mErrorLayout;
	@ViewById(R.id.loading)
	ProgressBar mLoading;
	@Bean
	DownloadManager mManager;
	boolean shouldOpenLogin = false;
	ArrayList<Entry> entries;

	@Override
	public void onFail(DownloadManager.DownloadError error) {
		switch (error) {
			case Login:
				showError(Error.Login);
				break;
			case Internet:
			case Other:
				if (entries == null || entries.size() == 0)
					showError(Error.Internet);
				// Else show toast
				Toast toast = Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				break;
		}
	}

	@Override
	public void onSuccess(ArrayList<Entry> entries) {
		this.entries = entries;
		showEntries();
	}

	enum Error {Login, Internet, Empty}

	@AfterViews
	void init() {
		Token token = mManager.getTokenFromPrefs();
		if (token == null) {
			// User not login in
			showError(Error.Login);
		} else {
			entries = mManager.getFeedFromPrefs();
			if (entries != null) showEntries();
			downloadEntries();
		}
	}

	@UiThread
	void showEntries() {
		mLoading.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
		if (entries == null || entries.size() == 0) showError(Error.Empty);
		else {
			mPager.setAdapter(new NewsAdapter(getFragmentManager(), entries));
		}
	}

	@UiThread
	@Click(R.id.refresh)
	void downloadEntries() {
		mErrorLayout.setVisibility(View.GONE);
		if (mManager.isNetworkAvailable()) {
			if (entries == null || entries.size() == 0) {
				// Show loading
				mLoading.setVisibility(View.VISIBLE);
			}
			// Else inform the user we start Downloading but still show content
			else {
				Toast toast = Toast.makeText(this, getString(R.string.start_downloading), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}
			mManager.download(this);
		} else {
			if (entries == null || entries.size() == 0) {
				// Show internet error
				showError(Error.Internet);
			}
			// Else inform the user that he has no connection
			else {
				Toast toast = Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}
		}
	}

	@UiThread
	void showError(Error error) {
		mErrorLayout.setVisibility(View.VISIBLE);
		switch (error) {
			case Login:
				mTitleError.setText(R.string.news_should_login);
				mExtraError.setText(R.string.news_should_login_subtext);
				findViewById(R.id.refresh).setVisibility(View.GONE);
				shouldOpenLogin = true;
				break;
			case Internet:
				mTitleError.setText(R.string.news_network_error);
				mExtraError.setText(R.string.news_network_error_subtext);
				break;
			case Empty:
				mTitleError.setText(R.string.news_no_content);
				mTitleError.setText(R.string.news_no_content_subtext);
				break;
		}
	}

	@Override
	protected Intent getIntentForOpenCase() {
		if (shouldOpenLogin)
			return LoginActivity_.intent(this).get();
		else if (entries == null || entries.size() == 0)
			return null;
		else {
			int id = ((NewsFragment_) (getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mPager.getCurrentItem()))).entryNumber;
			return new Intent(Intent.ACTION_VIEW, Uri.parse(NewsAdapter.getEntry(id).getAlternate().get(0).getHref()));
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.news_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	@Click(R.id.quick_circle_back_btn)
	void onBackClicked() {
		finish();
	}
}
