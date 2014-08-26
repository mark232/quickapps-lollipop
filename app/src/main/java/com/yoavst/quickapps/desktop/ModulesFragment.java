package com.yoavst.quickapps.desktop;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.viewpagerindicator.CirclePageIndicator;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.desktop.modules.ModulesAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_main_fragment)
public class ModulesFragment extends Fragment implements ViewPager.OnPageChangeListener {
	@ViewById(R.id.pager)
	ViewPager mPager;
	@ViewById(R.id.indicator)
	CirclePageIndicator mIndicator;
	int[] colors;

	@AfterViews
	void init() {
		TypedArray ta = getActivity().getResources().obtainTypedArray(R.array.icons_colors);
		colors = new int[ta.length()];
		for (int i = 0; i < ta.length(); i++) {
			colors[i] = ta.getColor(i, 0);
		}
		ta.recycle();
		mPager.setAdapter(new ModulesAdapter(getChildFragmentManager(), getActivity()));
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(this);
		mPager.post(new Runnable() {
			@Override
			public void run() {
				onPageSelected(0);
			}
		});
		View jumpTo = getActivity().findViewById(R.id.jump_to);
		jumpTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ModulesFragment.this.getActivity());
				builder.setTitle(R.string.jump_to_setting);
				builder.setItems(R.array.modules, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPager.setCurrentItem(which, true);
					}
				});
				builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});
	}

	@Override
	public void onPageScrolled(int i, float v, int i2) {
	}

	@Override
	public void onPageSelected(int i) {
		((MainActivity)getActivity()).setCircleTitle(mPager.getAdapter().getPageTitle(i));
		((GradientDrawable) getActivity().findViewById(R.id.circleLayout).getBackground())
				.setColor(colors[i]);
	}

	@Override
	public void onPageScrollStateChanged(int i) {
	}
}
