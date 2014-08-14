package com.yoavst.quickapps.launcher;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Yoav.
 */
@EActivity
public class LauncherActivity extends BaseQuickCircleActivity implements View.OnClickListener {
	public static Type listType = new TypeToken<ArrayList<ListItem>>() {
	}.getType();
	@Pref
	Preferences_ mPrefs;
	@ViewById(R.id.change_orientation)
	ImageButton mChange;
	ArrayList<ListItem> items;
	int marginSize;
	int iconSize;

	@AfterViews
	void init() {
		if (mPrefs.launcherItems().exists()) {
            ArrayList<ListItem> allItems = new Gson().fromJson(mPrefs.launcherItems().get(), listType);
            items = new ArrayList<>(allItems.size());
            for(ListItem item : allItems)
                if(item.enabled)
                    items.add(item);
        } else items = initDefaultIcons(this);
		iconSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				64,
				getResources().getDisplayMetrics()
		);
		marginSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				10,
				getResources().getDisplayMetrics()
		);
		boolean isVertical = mPrefs.launcherIsVertical().get();
        if(items.size() < 5) {
            isVertical = true;
            mChange.setVisibility(View.GONE);
            View backBtn = findViewById(R.id.quick_circle_back_btn);
            backBtn.setPadding(0, 0, 0, backBtn.getPaddingBottom());
        }
		setChangeDrawable(isVertical);
		getFragmentManager().beginTransaction().replace(R.id.quick_circle_fragment, isVertical ? new VerticalFragment() : new HorizontalFragment()).commit();
	}

	@Click(R.id.change_orientation)
	public void ChangeOrientation() {
		boolean newState = !mPrefs.launcherIsVertical().get();
		mPrefs.launcherIsVertical().put(newState);
		setChangeDrawable(newState);
		getFragmentManager().beginTransaction().replace(R.id.quick_circle_fragment, newState ? new VerticalFragment() : new HorizontalFragment()).commit();

	}

	void setChangeDrawable(boolean isVertical) {
		if (isVertical) mChange.setImageResource(R.drawable.ic_action_view_as_grid);
		else mChange.setImageResource(R.drawable.ic_action_view_as_list);
	}

	@Click(R.id.quick_circle_back_btn)
	public void onBackClicked() {
		finish();
	}

	@Override
	protected Intent getIntentForOpenCase() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.launcher_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toggles:
				com.yoavst.quickapps.toggles.TogglesActivity_.intent(this).start();
				break;
			case R.id.torch:
				com.yoavst.quickapps.torch.QuickActivity_.intent(this).start();
				break;
			case R.id.music:
				com.yoavst.quickapps.music.RemoteControlActivity_.intent(this).start();
				break;
			case R.id.notifications:
				com.yoavst.quickapps.notifications.CircleActivity_.intent(this).start();
				break;
			case R.id.calendar:
				com.yoavst.quickapps.calendar.QuickActivity_.intent(this).start();
				break;
			case R.id.stopwatch:
				com.yoavst.quickapps.clock.QuickActivity_.intent(this).start();
				break;
			case R.id.calculator:
				com.yoavst.quickapps.calculator.QuickActivity_.intent(this).start();
		}
		finish();
	}

	@SuppressLint("ValidFragment")
	public class VerticalFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.launcher_fragment_vertical, container, false);
			TableLayout layout = (TableLayout) view.findViewById(R.id.table_layout);
			TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
			TableRow.LayoutParams rowParams = new TableRow.LayoutParams(iconSize, iconSize);
			tableParams.setMargins(0, marginSize, 0, 0);
			rowParams.setMargins(0, 0, marginSize, 0);
			TableRow lastRow = null;
			for (int i = 0; i < items.size(); i++) {
				if (i % 2 == 0) {
					lastRow = new TableRow(getActivity());
					lastRow.setLayoutParams(tableParams);
					layout.addView(lastRow);
				}
				if (lastRow != null)
					lastRow.addView(setOnClick(createLauncherIcon(items.get(i), rowParams)));
			}
			return view;
		}
	}

	@SuppressLint("ValidFragment")
	public class HorizontalFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.launcher_fragment_horizontal, container, false);
			TableLayout layout = (TableLayout) view.findViewById(R.id.table_layout);
			final int maxItemsPerLine = items.size()%2==0?items.size()/2:(items.size() / 2 + 1);
			TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
			TableRow.LayoutParams rowParams = new TableRow.LayoutParams(iconSize, iconSize);
			tableParams.setMargins(0, marginSize, 0, 0);
			rowParams.setMargins(0, 0, marginSize, 0);
			TableRow lastRow = null;
			for (int i = 0; i < items.size(); i++) {
				if (i % maxItemsPerLine == 0) {
					lastRow = new TableRow(getActivity());
					lastRow.setLayoutParams(tableParams);
					layout.addView(lastRow);
				}
				int index = i < maxItemsPerLine ? i * 2 : (i - maxItemsPerLine) * 2 + 1;
				if (lastRow != null)
					lastRow.addView(setOnClick(createLauncherIcon(items.get(index), rowParams)));

			}
			return view;
		}
	}

	private View setOnClick(View view) {
		view.setOnClickListener(this);
		return view;
	}

	private ImageButton createLauncherIcon(ListItem item, ViewGroup.LayoutParams params) {
		ImageButton imageButton = new ImageButton(this);
		imageButton.setLayoutParams(params);
		imageButton.setBackground(null);
		imageButton.setImageResource(item.drawable);
		imageButton.setId(item.id);
		return imageButton;
	}

	public static ArrayList<ListItem> initDefaultIcons(Context context) {
		final String[] modules = context.getResources().getStringArray(R.array.modules_launcher);
		final TypedArray ids = context.getResources().obtainTypedArray(R.array.modules_ids);
		final TypedArray icon = context.getResources().obtainTypedArray(R.array.modules_icons);
		ArrayList<ListItem> ITEMS = new ArrayList<>(modules.length);
		for (int i = 0; i < modules.length; i++) {
			ITEMS.add(new ListItem(modules[i], icon.getResourceId(i, 0), ids.getResourceId(i, 0), true));
		}
		new Preferences_(context).launcherItems().put(new Gson().toJson(ITEMS, listType));
		return ITEMS;
	}

	public static ArrayList<ListItem> addNewIcons(Context context, ListItem... items) {
		Preferences_ prefs = new Preferences_(context);
		ArrayList<ListItem> listItems;
		if (!prefs.launcherItems().exists()) listItems = initDefaultIcons(context);
		else listItems = new Gson().fromJson(prefs.launcherItems().get(), listType);
		if (items.length != 0) {
			Collections.addAll(listItems, items);
			prefs.launcherItems().put(new Gson().toJson(items, listType));
		}
		return listItems;
	}

	public static class ListItem {
		public String name;
		public int drawable;
        public boolean enabled;
		public int id;

		public ListItem(String name, int drawable, int id, boolean enabled) {
			this.name = name;
			this.drawable = drawable;
			this.id = id;
            this.enabled = enabled;
		}

	}
}
