package com.yoavst.quickapps.calculator;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yoavst.quickapps.Expression;
import com.yoavst.quickapps.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * Created by Yoav.
 */
@EFragment(R.layout.calculator_fragment)
public class CalculatorFragment extends Fragment {
	@ViewById(R.id.text_line)
	TextView mText;
	@ViewById(R.id.answer_line)
	TextView mAnswer;
	@StringRes(R.string.dot)
	String DOT;
	@StringRes(R.string.plus)
	String PLUS;
	@StringRes(R.string.minus)
	String MINUS;
	@StringRes(R.string.mul)
	String MULTIPLE;
	@StringRes(R.string.div)
	String DIVIDE;
	@StringRes(R.string.exponentiation)
	String POW;
	@StringRes(R.string.error)
	String ERROR;
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String INFINITY = "\u221E";
	@StringArrayRes(R.array.operators)
	String[] OPERATORS;
	boolean showingAnswer = false;

	@Click({R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3,
			R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9, R.id.dot})
	void onNumberClicked(View view) {
		cleanAnswer(false);
		if (!view.getTag().equals(DOT))
			mText.append((CharSequence) view.getTag());
		else {
			String text = mText.getText().toString();
			if (text.length() == 0) mText.append("0" + DOT);
			else if (isOperator(getLastChar()) || getLastChar().equals(OPEN_BRACKET))
				mText.append("0" + DOT);
			else if (!text.contains(DOT)) mText.append(DOT);
			else {
				int lastIndex = text.length() - 1;
				String c;
				do {
					c = new String(new char[]{text.charAt(lastIndex)});
					if (c.equals(DOT)) return;
					lastIndex--;
				} while (!(isOperator(c) || lastIndex == -1));
				mText.append(DOT);
			}
		}
	}

	@Click({R.id.div, R.id.exponentiation, R.id.mul, R.id.minus,
			R.id.plus})
	void onOperatorClicked(View view) {
		cleanAnswer(true);
		if (mText.length() == 0) {
			if (view.getTag().equals(MINUS))
				mText.append((CharSequence) view.getTag());
		} else if (!isOperator(getLastChar())) {
			String lastChar = getLastChar();
			if ((!lastChar.equals(OPEN_BRACKET) && !lastChar.equals(CLOSE_BRACKET)))
				mText.append((CharSequence) view.getTag());
			else if (view.getTag().equals(MINUS))
				mText.append((CharSequence) view.getTag());
		} else if (mText.length() != 1) {
			String charBefore = getLastChar();
			String twoCharBefore = new String(new char[]{mText.getText().charAt(mText.length() - 2)});
			if ((!charBefore.equals(OPEN_BRACKET) && !twoCharBefore.equals(OPEN_BRACKET)) ||
					view.getTag().equals(MINUS)) {
				if (!charBefore.equals(POW) || view.getTag().toString().equals(DIVIDE) ||
						view.getTag().toString().equals(MULTIPLE) || view.getTag().toString().equals(PLUS) ||
						view.getTag().equals(POW))
					deleteLastChar();
				mText.append((CharSequence) view.getTag());
			}
		}

	}

	@Click(R.id.del)
	void deleteLastChar() {
		mAnswer.setText(null);
		if (mText.length() != 0) {
			mText.setText(mText.getText().toString().substring(0, mText.getText().toString().length() - 1));
		}
	}

	@Click(R.id.paren)
	void onBracketsClicked() {
		cleanAnswer(true);
		int numberOfOpenBrackets = calcOpenBrackets();
		if (numberOfOpenBrackets == 0) {
			if (mText.length() == 0 || isOperator(getLastChar()))
				mText.append(OPEN_BRACKET);
			else mText.append(MULTIPLE + OPEN_BRACKET);
		} else {
			String lastChar = mText.getText().toString().substring(mText.getText().toString().length() - 1);
			if (isOperator(lastChar) || lastChar.equals(OPEN_BRACKET)) {
				mText.append(OPEN_BRACKET);
			} else if (lastChar.equals(CLOSE_BRACKET)) {
				mText.append(MULTIPLE + OPEN_BRACKET);
			} else {
				mText.append(CLOSE_BRACKET);
			}
		}

	}

	@Click(R.id.allClear)
	@LongClick(R.id.del)
	void clearAll() {
		mText.setText(null);
		mAnswer.setText(null);
	}

	@Click(R.id.equal)
	void compute() {
		if (mText.length() != 0 && !showingAnswer) {
			String math = fixFormat(removeLastOperator(addMissingBrackets(mText.getText().toString())));
			Log.v("Calculator", math);
			try {
				Expression expression = new Expression(math).setPrecision(100);
				BigDecimal decimal = expression.eval().stripTrailingZeros();
				int numberOfDigits = numberOfDigits(decimal);
				String text;
				if (numberOfDigits >= 14) {
					// Force scientific notation but check if the regular toString has different value
					text = new DecimalFormat("0.00E00").format(decimal);
					String[] parts = text.split("E");
					if (parts[0].equals("1.00")) text = "10" + POW + parts[1];
					else {
						text = formatNumber(decimal);
					}
				} else if (numberOfDigits >= 7) {
					// Allow scientific notation, but do not force.
					text = decimal.toString()
							// 1E+7 to 10^7
							.replace("1E+", "10" + POW)
									// 2E+7 to 2x10^7
							.replace("E+", MULTIPLE + "10" + POW);
				} else if (decimal.compareTo(BigDecimal.ONE) >= 0 || decimal.compareTo(new BigDecimal("-1")) <= 0)
					// Force regular decimal formatting
					text = decimal.toPlainString();
				else {
					int numberOfUnscaledDigits = decimal.scale();
					if (numberOfUnscaledDigits < 7)
						// Force regular decimal formatting
						text = decimal.toPlainString();
					else if (numberOfUnscaledDigits < 14) {
						// Allow scientific notation, but do not force.
						text = decimal.toString()
								// 1E-7 to 10^-7
								.replace("1E-", "10" + POW + MINUS)
										// 2E-7 to 2x10^-7
								.replace("E+", MULTIPLE + "10" + POW + MINUS);
					} else if (numberOfUnscaledDigits == 100) {
						text = decimal.toPlainString();
					} else
						text = formatNumber(decimal);

				}
				mAnswer.setText(text);
			} catch (RuntimeException e) {
				if (e.getMessage().toLowerCase().contains("division by zero")) {
					mAnswer.setText(INFINITY);
				} else mAnswer.setText(ERROR);
				e.printStackTrace();
			}
			showingAnswer = true;
		}
	}

	private void cleanAnswer(boolean copyAnswer) {
		if (showingAnswer) {
			String answer = mAnswer.getText().toString();
			mAnswer.setText(null);
			if (copyAnswer && !(answer.contains(ERROR) || answer.contains(INFINITY))) {
				mText.setText(answer.length() == 14 ? formatNumber(new BigDecimal(answer)) : answer);
			} else mText.setText(null);
			showingAnswer = false;
		}
	}

	private int calcOpenBrackets() {
		String text = mText.length() == 0 ? "" : mText.getText().toString();
		char open = OPEN_BRACKET.charAt(0);
		char close = CLOSE_BRACKET.charAt(0);
		int numberOfOpen = 0;
		for (char c : text.toCharArray()) {
			if (c == open) numberOfOpen++;
			else if (c == close) numberOfOpen--;
		}
		return numberOfOpen;
	}

	private String addMissingBrackets(String original) {
		int openBrackets = calcOpenBrackets();
		if (openBrackets == 0) return original;
		else {
			StringBuilder builder = new StringBuilder(original);
			while (openBrackets != 0) {
				builder.append(CLOSE_BRACKET);
				openBrackets--;
			}
			return builder.toString();
		}
	}

	private String fixFormat(String original) {
		if (original == null || original.length() == 0) return original;
		else {
			if (original.startsWith(MINUS + OPEN_BRACKET))
				original = original.replaceFirst(MINUS + "\\" + OPEN_BRACKET, MINUS + "1" + MULTIPLE + OPEN_BRACKET);
			original = original.replace(MULTIPLE, "*").replace(DIVIDE, "/");
			return original;
		}
	}

	private String removeLastOperator(String original) {
		if (original == null || original.length() == 0 || !isOperator(getLastChar()))
			return original;
		else return original.substring(0, original.length() - 1);
	}

	private String getLastChar() {
		return new String(new char[]{mText.getText().charAt(mText.length() - 1)});
	}

	private boolean isOperator(CharSequence string) {
		if (string != null) {
			for (String operator : OPERATORS) {
				if (operator.contentEquals(string)) return true;
			}
		}
		return false;
	}

	private String formatNumber(BigDecimal decimal) {
		String text = new DecimalFormat("0.00E00").format(decimal);
		String[] parts = text.split("E");
		if (parts[0].equals("1.00")) text = "10" + POW + parts[1];
		else {
			// Deal with part 1
			double value = Double.parseDouble(parts[0]);
			if (value == (int) value)
				text = String.valueOf((int) value) + MULTIPLE + "10" + POW;
			else if (value * 10 == (int) value * 10)
				text = String.valueOf(((int) value * 10) / 10) + DOT +
						String.valueOf(((int) value * 10) % 10) + MULTIPLE + "10" + POW;
			else text = String.valueOf(value) + MULTIPLE + "10" + POW;
			// Deal with part 2
			double val = Double.parseDouble(parts[1]);
			if (val == (int) val)
				text += String.valueOf((int) val);
			else if (val * 10 == (int) val * 10)
				text += String.valueOf(((int) val * 10) / 10) + DOT +
						String.valueOf(((int) val * 10) % 10);
			else text += String.valueOf(val);
		}
		return text;
	}

	private static int numberOfDigits(BigDecimal bigDecimal) {
		return numberOfDigits(bigDecimal.toBigInteger());
	}

	private static int numberOfDigits(BigInteger digits) {
		BigInteger ten = BigInteger.valueOf(10);
		int count = 0;
		do {
			digits = digits.divide(ten);
			count++;
		} while (!digits.equals(BigInteger.ZERO));
		return count;
	}
}
