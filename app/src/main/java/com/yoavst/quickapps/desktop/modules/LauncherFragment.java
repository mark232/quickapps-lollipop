package com.yoavst.quickapps.desktop.modules;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobeta.android.dslv.DragSortListView;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.launcher.LauncherActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_launcher)
public class LauncherFragment extends Fragment {
	@Pref
	Preferences_ mPrefs;
	ArrayList<LauncherActivity.ListItem> mItems;

	@AfterViews
	void init() {
		if (mPrefs.launcherItems().exists())
			mItems = new Gson().fromJson(mPrefs.launcherItems().get(), LauncherActivity.listType);
		else
			mItems = LauncherActivity.initDefaultIcons(getActivity());
	}

	@Click(R.id.modules_order_row)
	void onOpenSettingsClicked() {
		final DragSortListView listview = (DragSortListView) LayoutInflater.from(getActivity()).inflate(R.layout.desktop_module_drag_list, null);
		final Adapter adapter = new Adapter();
		listview.setAdapter(adapter);
		listview.setRemoveListener(new DragSortListView.RemoveListener() {

			public void remove(int which) {
				adapter.remove(adapter.getItem(which));
			}
		});
		listview.setDropListener(new DragSortListView.DropListener() {

			public void drop(int from, int to) {
				LauncherActivity.ListItem item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);
			}
		});
		new AlertDialog.Builder(getActivity()).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPrefs.launcherItems().put(new Gson().toJson(mItems, LauncherActivity.listType));
			}
		}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				init();
			}
		}).setView(listview).show();
	}

	private class Adapter extends ArrayAdapter<LauncherActivity.ListItem> {

		public Adapter() {
			super(getActivity(), R.layout.desktop_module_launcher_item, mItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.desktop_module_launcher_item, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else holder = (ViewHolder) convertView.getTag();
			holder.name.setText(getItem(position).name);
			holder.icon.setImageResource(getItem(position).drawable);
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			ImageView icon;

			public ViewHolder(View view) {
				name = (TextView) view.findViewById(R.id.name);
				icon = (ImageView) view.findViewById(R.id.icon);
			}
		}
	}

}
