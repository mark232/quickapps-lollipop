package com.yoavst.quickapps.desktop;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.personagraph.api.PGAgent;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.launcher.LauncherActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by Yoav.
 */
@EActivity(R.layout.desktop_main_activity)
public class MainActivity extends Activity {
	@ViewById(R.id.extended_header)
	TextView mHeader;
	@ViewById(R.id.leftDrawerListView)
	ListView mListView;
	@ViewById(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;
	@ViewById(R.id.toolbar_title)
	TextView mTitle;
	@ViewById(R.id.circleLayout)
	LinearLayout mCircleHeader;
	@ViewById(R.id.title)
	TextView mCircleTitle;
	@ViewById(R.id.jump_to)
	ImageView mJumpTo;
	@ViewById(R.id.adView)
	AdView mAdView;
	@StringRes(R.string.app_name)
	String APP_NAME;
	DrawerLayout.DrawerListener mDrawerToggle;

	@AfterViews
	void init() {
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
				mAdView.setVisibility(View.GONE);
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				mAdView.setVisibility(View.VISIBLE);
			}
		});
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addKeyword("Quick Circle, LG G3")
				.build();
		mAdView.loadAd(adRequest);
		mJumpTo.setImageDrawable(new IconDrawable(this, Iconify.IconValue.fa_arrows_h).color(Color.WHITE));
		// create our manager instance after the content view is set
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// enable status bar tint
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.primary_dark);
		// init Drawer
		String[] primaryItems = getResources().getStringArray(R.array.drawer_primary_items);
		String[] secondaryItems = getResources().getStringArray(R.array.drawer_secondary_items);
		TypedArray mIcons = getResources().obtainTypedArray(R.array.drawer_secondary_icons);
		NavigationDrawerItem[] drawerItems = new NavigationDrawerItem[primaryItems.length + secondaryItems.length];
		for (int i = 0; i < primaryItems.length; i++)
			drawerItems[i] = new NavigationDrawerItem(primaryItems[i], true);
		for (int i = 0; i < secondaryItems.length; i++)
			drawerItems[i + primaryItems.length] = new NavigationDrawerItem(secondaryItems[i], mIcons.getResourceId(i, -1), false);
		mIcons.recycle();
		DrawerAdapter adapter = new DrawerAdapter(this, drawerItems);
		mListView.setAdapter(adapter);
		mDrawerToggle = new DrawerLayout.SimpleDrawerListener() {
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mListView.post(new Runnable() {
			@Override
			public void run() {
				selectItem(2);
			}
		});
		mListView.addFooterView(getLayoutInflater().inflate(R.layout.desktop_hide_settings, mListView, false), null, false);
		findViewById(R.id.hide_settings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherActivity_.removeSettings(MainActivity.this);
				Toast.makeText(MainActivity.this, R.string.reboot_for_update, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@ItemClick(R.id.leftDrawerListView)
	void selectItem(int position) {
		Fragment fragment;
		switch (position) {
			default:
			case 0:
				fragment = ModulesFragment_.builder().build();
				break;
			case 1:
				fragment = SourceFragment_.builder().build();
				break;
			case 2:
				fragment = HowToFragment_.builder().build();
				break;
			case 3:
				fragment = AboutFragment_.builder().build();
				break;
		}
		getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
		mHeader.setText(((NavigationDrawerItem) mListView.getItemAtPosition(position)).getItemName());
		if (position == 0) {
			mCircleHeader.setVisibility(View.VISIBLE);
			mTitle.setText("");
			mJumpTo.setVisibility(View.VISIBLE);
		} else {
			mCircleHeader.setVisibility(View.GONE);
			mTitle.setText(APP_NAME);
			mJumpTo.setVisibility(View.GONE);
		}
		mDrawerLayout.closeDrawers();
		// Set item selected
		((NavigationDrawerItem) mListView.getItemAtPosition(position)).setSelected(true);
		for (int j = 0; j < mListView.getChildCount() - 1; j++)
			((DrawerAdapter.ViewHolder) mListView.getChildAt(j).getTag()).title.setTypeface(null, Typeface.NORMAL);
		((DrawerAdapter.ViewHolder) mListView.getChildAt(position).getTag()).title.setTypeface(null, Typeface.BOLD);
	}

	@Click(R.id.drawer_icon)
	void onDrawerIconClicked() {
		mDrawerLayout.openDrawer(mListView);
	}

	public void setCircleTitle(CharSequence text) {
		mCircleTitle.setText(text);
	}

	@Override
	public void onDestroy() {
		mAdView.destroy();
		super.onDestroy();
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
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mListView)) {
			mDrawerLayout.closeDrawer(mListView);
		} else super.onBackPressed();
	}
}
