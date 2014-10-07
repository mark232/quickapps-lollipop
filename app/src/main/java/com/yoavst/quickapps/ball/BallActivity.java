package com.yoavst.quickapps.ball;

import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.yoavst.quickapps.BaseQuickCircleActivity;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.Random;

/**
 * Created by Yoav.
 */
@EActivity
public class BallActivity extends BaseQuickCircleActivity {
	@ViewById(R.id.text)
	TextView mText;
	@ViewById(R.id.cover_main_view)
	RelativeLayout mView;
	@StringArrayRes(R.array.magic_ball)
	String[] mAnswers;
	Random random = new Random();

	@Override
	protected Intent getIntentForOpenCase() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.ball_activity;
	}

	@Override
	protected int getMainCircleLayoutId() {
		return R.id.cover_main_view;
	}

	@Click(R.id.quick_circle_back_btn)
	void onBackClicked() {
		finish();
	}

	@Click(R.id.cover_main_view)
	void generateNewText() {
		YoYo.with(Techniques.Shake)
				.duration(1000)
				.withListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
						mText.setText(R.string.thinking);
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						int selected = random.nextInt(mAnswers.length);
						mText.setText(mAnswers[selected]);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}
				})
				.playOn(mView);
	}
}
