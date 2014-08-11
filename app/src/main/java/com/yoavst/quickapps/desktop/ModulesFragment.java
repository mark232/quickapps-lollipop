package com.yoavst.quickapps.desktop;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;

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
		mPager.setOnPageChangeListener(this);
		onPageSelected(0);

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
