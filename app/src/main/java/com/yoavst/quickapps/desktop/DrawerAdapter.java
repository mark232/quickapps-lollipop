package com.yoavst.quickapps.desktop;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoavst.quickapps.R;

/**
 * Created by Yoav.
 */
public class DrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {
	NavigationDrawerItem[] mItems;
	AdapterView.OnItemClickListener mListener;
	int smallItemSize;
	int normalItemSize;
	public DrawerAdapter(Context context, NavigationDrawerItem[] objects) {
		super(context, R.layout.desktop_drawer_item, objects);
		mItems = objects;
		normalItemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getContext().getResources().getDisplayMetrics());
		smallItemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getContext().getResources().getDisplayMetrics());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.desktop_drawer_item, parent, false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else viewHolder = (ViewHolder) convertView.getTag();
		NavigationDrawerItem item = mItems[position];
		if (item.isMainItem()) {
			viewHolder.title.setText(item.getItemName());
			viewHolder.title.setTextSize(22);
			viewHolder.icon.setVisibility(View.GONE);
			convertView.getLayoutParams().height = normalItemSize;
		} else {
			viewHolder.title.setText(item.getItemName());
			viewHolder.title.setTextSize(14);
			viewHolder.icon.setImageDrawable(getIcon(item.getItemIcon()));
			viewHolder.icon.setVisibility(View.VISIBLE);
			convertView.setBackgroundColor(getContext().getResources().getColor(R.color.grey_background));
			convertView.getLayoutParams().height = smallItemSize;
		}
		if (item.isSelected()) {
			viewHolder.title.setTypeface(null, Typeface.BOLD);
		} else {
			viewHolder.title.setTypeface(null, Typeface.NORMAL);
		}
		return convertView;
	}

	public static class ViewHolder {
		ImageView icon;
		TextView title;

		public ViewHolder(View view) {
			icon = (ImageView) view.findViewById(R.id.icon);
			title = (TextView) view.findViewById(R.id.title);
		}
	}

	private Drawable getIcon(int res) {
		return getContext().getResources().getDrawable(res);
	}
}
