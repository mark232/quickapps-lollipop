package com.yoavst.quickapps.dialer;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.yoavst.quickapps.Preferences_;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.dialer_fragment)
public class DialerFragment extends Fragment {
	@ViewById(R.id.number)
	TextView mNumber;
	@ViewById(R.id.name)
	TextView mName;
	@ColorRes(android.R.color.darker_gray)
	int mSuggestionColor;
	String originalOldText = "";
	String oldName = "";
	Handler handler = new Handler();
	ArrayList<Pair<String, String>> mPhoneNumbers = new ArrayList<>();
	HashMap<Integer,Pair<String,String>> mQuickNumbers = new HashMap<>(10);
	public static final Type QUICK_NUMBERS_TYPE = new TypeToken<HashMap<Integer,Pair<String,String>>>() {
	}.getType();


	@AfterViews
	void init() {
		mQuickNumbers = new Gson().fromJson(new Preferences_(getActivity()).quickDials().get(), QUICK_NUMBERS_TYPE);
		mName.setSelected(true);
		Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		phones.moveToFirst();
		handleContacts(phones);

	}

	@Background
	void handleContacts(Cursor cursor) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		while (cursor.moveToNext()) {
			try {
				Phonenumber.PhoneNumber phone = phoneUtil.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
						getActivity().getResources().getConfiguration().locale.getCountry());
				mPhoneNumbers.add(Pair.create(
						cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
						"0" + phone.getNationalNumber()));
			} catch (NumberParseException e) {
				break;
			}
		}
		closeCursor(cursor);
	}

	@UiThread
	void closeCursor(Cursor cursor) {
		cursor.close();
	}

	@Click(R.id.number)
	void onClickOnText() {
		SpannableString s = new SpannableString(mNumber.getText());
		ForegroundColorSpan[] spans = s.getSpans(0,
				s.length(),
				ForegroundColorSpan.class);
		for (ForegroundColorSpan span : spans) {
			s.removeSpan(span);
		}
		mNumber.setText(s);
		originalOldText = s.toString();
	}

	@Click(R.id.quick_circle_back_btn)
	void onBackClicked() {
		getActivity().finish();
	}

	@Click({R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3,
			R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9})
	void onNumberClicked(View view) {
		removeSuggestion();
		mNumber.append((String) view.getTag());
		updateSuggestion(mNumber.getText().toString());
	}

	@LongClick({R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3,
			R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9})
	void onNumberLongClicked(View view) {
		int num = Integer.parseInt((String) view.getTag());
		if (mQuickNumbers.containsKey(num)) {
			Pair<String,String> contact = mQuickNumbers.get(num);
			mNumber.setText(contact.second);
			originalOldText = contact.second;
			mName.setText(contact.first);
			oldName = contact.first;
		}
	}

	@Click(R.id.delete)
	void onDelete() {
		removeSuggestion();
		CharSequence text = mNumber.getText();
		if (text.length() > 0) {
			mNumber.setText(text.subSequence(0, text.length() - 1));
			originalOldText = mNumber.getText().toString();
			oldName = "";
			updateSuggestion(mNumber.getText().toString());
		}
	}

	@LongClick(R.id.delete)
	void deleteAll() {
		mNumber.setText("");
		originalOldText = "";
		mName.setText("");

	}

	@Background
	void updateSuggestion(String text) {
		originalOldText = text;
		if (originalOldText.length() >= 2) {
			for (Pair<String, String> num : mPhoneNumbers) {
				if (num.second.startsWith(originalOldText)) {
					setText(num, false);
					return;
				}
			}
			// Now again with adding 0
			for (Pair<String, String> num : mPhoneNumbers) {
				if (num.second.startsWith("0" + originalOldText)) {
					setText(num, true);
					return;
				}
			}
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				mName.setText("");
			}
		});

	}

	@UiThread
	void setText(Pair<String, String> num, boolean removeZero) {
		SpannableString text = new SpannableString(removeZero ? num.second.substring(1) : num.second);
		text.setSpan(new ForegroundColorSpan(mSuggestionColor), originalOldText.length(),
				text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mNumber.setText(text);
		if (!oldName.equals(num.first)) {
			mName.setText(num.first);
			oldName = num.first;
		}
	}

	void removeSuggestion() {
		mNumber.setText(originalOldText);
	}

	@Click(R.id.dial)
	void dial() {
		if (mNumber.getText().length() >= 3)
			startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNumber.getText())));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPhoneNumbers.clear();
		mPhoneNumbers = null;
	}
}
