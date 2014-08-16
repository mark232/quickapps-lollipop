package com.yoavst.quickapps.desktop.modules;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.launcher.LauncherActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_launcher)
public class LauncherFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    @ViewById(R.id.load_externalg_checkbox)
    CheckBox mLoadExternal;
    @ViewById(R.id.auto_load_checkbox)
    CheckBox mAutoLoad;
	@Pref
	Preferences_ mPrefs;
	ArrayList<LauncherActivity.ListItem> mItems;
    BroadcastReceiver installReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LauncherActivity.defaultItems = null;
            mItems = LauncherActivity.getIconsFromPrefs(getActivity());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(installReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(installReceiver);
    }

    @AfterViews
	void init() {
		if (mPrefs.launcherItems().exists())
			mItems = LauncherActivity.getIconsFromPrefs(getActivity());
		else
			mItems = LauncherActivity.initDefaultIcons(getActivity());
        mLoadExternal.setChecked(mPrefs.launcherLoadExternalModules().get());
        mAutoLoad.setChecked(mPrefs.launcherAutoAddModules().get());
        mLoadExternal.setOnCheckedChangeListener(this);
        mAutoLoad.setOnCheckedChangeListener(this);
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
				mPrefs.launcherItems().put(LauncherActivity.gson.toJson(mItems, LauncherActivity.listType));
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
                holder.enabled.setTag(position);
                holder.enabled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox check = (CheckBox) view;
                        getItem((Integer) check.getTag()).enabled = check.isChecked();
                    }
                });
				convertView.setTag(holder);
			} else holder = (ViewHolder) convertView.getTag();
			holder.name.setText(getItem(position).name);
			holder.icon.setImageDrawable(getItem(position).icon);
            holder.enabled.setChecked(getItem(position).enabled);
            holder.enabled.setTag(position);
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			ImageView icon;
            CheckBox enabled;

			public ViewHolder(View view) {
				name = (TextView) view.findViewById(R.id.name);
				icon = (ImageView) view.findViewById(R.id.icon);
                enabled = (CheckBox) view.findViewById(R.id.enabled);
			}
		}
	}

    @Click({R.id.modules_auto_load_row, R.id.modules_load_external_row})
    void clickRow(View view) {
        switch (view.getId()) {
            case R.id.modules_load_external_row:
                mLoadExternal.toggle();
                break;
            case R.id.modules_auto_load_row:
                mAutoLoad.toggle();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.load_externalg_checkbox:
                mPrefs.launcherLoadExternalModules().put(isChecked);
                LauncherActivity.defaultItems = null;
                mItems = LauncherActivity.getIconsFromPrefs(getActivity());
                Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
                break;
            case R.id.auto_load_checkbox:
                mPrefs.launcherAutoAddModules().put(isChecked);
                Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
