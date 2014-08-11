package com.yoavst.quickapps.desktop.modules;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobeta.android.dslv.DragSortListView;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.toggles.TogglesAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_toggles)
public class TogglesFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

	@ViewById(R.id.battery_checkbox)
	CheckBox mBattery;
	@Pref
	Preferences_ mPrefs;
	ArrayList<TogglesAdapter.ToggleItem> mItems;

	@Click(R.id.listener_row)
	void onOpenSettingsClicked() {
		getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
	}

	@AfterViews
	void init() {
		mBattery.setChecked(mPrefs.showBatteryToggle().get());
		mBattery.setOnCheckedChangeListener(this);
		initItems();
	}

	private void initItems() {
		if (mPrefs.togglesItems().exists())
			mItems = new Gson().fromJson(mPrefs.togglesItems().get(), TogglesAdapter.listType);
		else mItems = TogglesAdapter.initDefaultToggles(getActivity());
	}

	@Click({R.id.battery_row, R.id.toggles_order_row})
	void clickRow(View view) {
		switch (view.getId()) {
			case R.id.battery_row:
				mBattery.toggle();
				break;
			case R.id.toggles_order_row:
				final DragSortListView listview = (DragSortListView) LayoutInflater.from(getActivity()).inflate(R.layout.desktop_module_drag_list, null);
				final ArrayAdapter<TogglesAdapter.ToggleItem> adapter = new Adapter();
				listview.setAdapter(adapter);
				listview.setRemoveListener(new DragSortListView.RemoveListener() {

					public void remove(int which) {
						adapter.remove(adapter.getItem(which));
					}
				});
				listview.setDropListener(new DragSortListView.DropListener() {

					public void drop(int from, int to) {
						TogglesAdapter.ToggleItem item = adapter.getItem(from);
						adapter.remove(item);
						adapter.insert(item, to);
					}
				});
				new AlertDialog.Builder(getActivity()).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPrefs.togglesItems().put(new Gson().toJson(mItems, TogglesAdapter.listType));
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						initItems();
					}
				}).setView(listview).show();
				break;
		}
	}

	private class Adapter extends ArrayAdapter<TogglesAdapter.ToggleItem> {

		public Adapter() {
			super(getActivity(), android.R.layout.simple_list_item_1, mItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else holder = (ViewHolder) convertView.getTag();
			holder.name.setText(getItem(position).name);
			return convertView;
		}

		private class ViewHolder {
			TextView name;

			public ViewHolder(View view) {
				name = (TextView) view.findViewById(android.R.id.text1);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.battery_checkbox:
				mPrefs.showBatteryToggle().put(isChecked);
				Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
