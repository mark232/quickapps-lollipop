package com.yoavst.quickapps.desktop.modules;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;
import com.yoavst.quickapps.dialer.DialerFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.HashMap;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_module_dialer)
public class DialerFragment extends Fragment {

	@Pref
	Preferences_ mPrefs;
	HashMap<Integer, Pair<String, String>> mQuickNumbers = new HashMap<>(10);
	int lastNum = -1;
	public static final int PICK_CONTACT_REQUEST = 42;

	@AfterViews
	void init() {
		mQuickNumbers = new Gson().fromJson(mPrefs.quickDials().get(), DialerFragment_.QUICK_NUMBERS_TYPE);
	}

	@Click({R.id.digit1, R.id.digit2, R.id.digit3,
			R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9})
	void onQuickDialClicked(View view) {
		lastNum = Integer.parseInt((String) view.getTag());
		startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT_REQUEST);
	}

	@LongClick({R.id.digit1, R.id.digit2, R.id.digit3,
			R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9})
	void onQuickDialLongClicked(View view) {
		lastNum = Integer.parseInt((String) view.getTag());
		if (mQuickNumbers.containsKey(lastNum)) {
			Pair<String, String> number = mQuickNumbers.get(lastNum);
			Toast.makeText(getActivity(), number.first + " " + number.second, Toast.LENGTH_SHORT).show();
		} else Toast.makeText(getActivity(), R.string.empty_speed_dial, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				Uri contentUri = data.getData();
				String contactId = contentUri.getLastPathSegment();
				Cursor cursor = getActivity().getContentResolver().query(
						ContactsContract.Data.CONTENT_URI,
						new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
						new String[]{contactId},
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " ASC");
				if (cursor.moveToFirst()) {
					final String name = cursor.getString(0);
					Cursor numberCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[]{contactId},
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " ASC");
					numberCursor.moveToFirst();
					int count = numberCursor.getCount();
					if (count != 0) {
						if (count == 1)
							putNumber(name, numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
						else {
							final String[] phones = new String[count];
							int index = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
							for (int i = 0; i < count; i++) {
								phones[i] = numberCursor.getString(index);
								numberCursor.moveToNext();
							}
							new AlertDialog.Builder(getActivity())
									.setTitle(R.string.choose_number)
									.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).setItems(phones, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									putNumber(name, phones[which]);
								}
							}).show();
						}
					} else
						Toast.makeText(getActivity(), android.R.string.emptyPhoneNumber, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	void putNumber(String name, String number) {
		mQuickNumbers.put(lastNum, Pair.create(name, number));
		mPrefs.quickDials().put(new Gson().toJson(mQuickNumbers, com.yoavst.quickapps.dialer.DialerFragment.QUICK_NUMBERS_TYPE));
		lastNum = -1;
	}

}
