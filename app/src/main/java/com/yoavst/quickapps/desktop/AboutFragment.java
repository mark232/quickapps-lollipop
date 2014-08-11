package com.yoavst.quickapps.desktop;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.yoavst.quickapps.FloatingActionButton;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_about_fragment)
public class AboutFragment extends Fragment {
	@ViewById(R.id.about)
	TextView mAbout;
	@ViewById(R.id.about_2)
	TextView mAboutExtra;
	@ColorRes(R.color.material_blue)
	int blueColor;
	@ColorRes(R.color.material_light_blue)
	int blueLightColor;
	@ViewById(R.id.contact_button)
	FloatingActionButton mContactButton;

	@AfterViews
	void init() {
		final SpannableString text1 = new SpannableString(getString(R.string.about));
		final SpannableString text2 = new SpannableString(getString(R.string.about_2));
		colorize(setBigger(text1, 1.5f, 0, 17), blueColor, 0, 17);
		bold(colorize(setBigger(text1, 2f, 44, 49), blueColor, 44, 49), 44, 49);
		colorize(setBigger(text1, 1.5f, 50, 67), blueColor, 50, 67);
		colorize(setBigger(text1, 1.5f, 96, 102), blueColor, 96, 102);
		mAbout.setText(text1);
		colorize(setBigger(text2, 1.5f, text2.length() - 17, text2.length() - 1), blueColor, text2.length() - 17, text2.length() - 1);
		mAboutExtra.setText(text2);
		mContactButton.setColor(blueLightColor);
		mContactButton.setDrawable(getResources().getDrawable(R.drawable.ic_action_email_fab));
	}

	@Click(R.id.contact_button)
	void onContactClicked() {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri
				.fromParts("mailto", "yoav.goop@gmail.com", null));
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				getString(R.string.app_name));
		try {
			getActivity().startActivity(
					Intent.createChooser(emailIntent, getActivity().getString(R.string.about_mail_chooser)));
		} catch (ActivityNotFoundException exception) {
			Toast.makeText(getActivity(), getActivity().getString(R.string.about_intent_failed),
					Toast.LENGTH_LONG).show();
		}
	}

	private SpannableString setBigger(SpannableString string, float size, int start, int end) {
		string.setSpan(new RelativeSizeSpan(size), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return string;
	}

	private SpannableString colorize(SpannableString string, int color, int start, int end) {
		string.setSpan(new ForegroundColorSpan(color), start,
				end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return string;
	}

	private SpannableString bold(SpannableString string, int start, int end) {
		string.setSpan(new StyleSpan(Typeface.BOLD), start,
				end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return string;
	}
}
