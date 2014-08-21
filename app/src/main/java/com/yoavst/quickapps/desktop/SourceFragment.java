package com.yoavst.quickapps.desktop;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.yoavst.quickapps.FloatingActionButton;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.Locale;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.desktop_source_fragment)
public class SourceFragment extends Fragment {
	@ColorRes(R.color.material_blue)
	int blueColor;
	@ColorRes(R.color.material_light_blue)
	int blueLightColor;
	@ViewById(R.id.github_button)
	FloatingActionButton mGithubButton;
	@ViewById(R.id.text)
	RobotoTextView mText;
	@ViewById(R.id.credits)
	RobotoTextView mCredits;

	@AfterViews
	void init() {
		mCredits.setText(Html.fromHtml(getString(R.string.credits)));
		mCredits.setMovementMethod(new LinkMovementMethod());
		if (Locale.getDefault().getLanguage().startsWith("en")) {
			final SpannableString text1 = new SpannableString(getString(R.string.source_description));
			colorize(setBigger(text1, 1.5f, 15, 32), blueColor, 15, 32);
			colorize(setBigger(text1, 1.5f, 46, 52), blueColor, 46, 52);
			colorize(setBigger(text1, 1.5f, 67, 73), blueColor, 67, 73);
			mText.setText(text1);
		}
		mGithubButton.setColor(blueLightColor);
		mGithubButton.setDrawable(getResources().getDrawable(R.drawable.ic_action_github_fab));
	}

	@Click(R.id.github_button)
	void onFloatingClicked() {
		getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yoavst/quickapps")));
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
