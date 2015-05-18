package com.weproov.app.ui.adapter;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Locale;

public class CustomPhoneNumberFormattingTextWatcher implements TextWatcher{

	/**
	 * Indicates the change was caused by ourselves.
	 */
	private boolean mSelfChange = false;

	/**
	 * Indicates the formatting has been stopped.
	 */
	private boolean mStopFormatting;
	private AsYouTypeFormatter mFormatter;

	public CustomPhoneNumberFormattingTextWatcher() {
		mFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(Locale.getDefault().getCountry());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		if (mSelfChange || mStopFormatting) {
			return;
		}
		// If the user manually deleted any non-dialable characters, stop formatting
		if (count > 0 && hasSeparator(s, start, count)) {
			stopFormatting();
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (mSelfChange || mStopFormatting) {
			return;
		}
		// If the user inserted any non-dialable characters, stop formatting
		if (count > 0 && hasSeparator(s, start, count)) {
			stopFormatting();
		}
	}

	@Override
	public synchronized void afterTextChanged(Editable s) {
		if (mStopFormatting) {
			// Restart the formatting when all texts were clear.
			mStopFormatting = !(s.length() == 0);
			return;
		}
		if (mSelfChange) {
			// Ignore the change caused by s.replace().
			return;
		}
		String formatted = reformat(s, Selection.getSelectionEnd(s));
		if (formatted != null) {
			int rememberedPos = mFormatter.getRememberedPosition();
			mSelfChange = true;
			s.replace(0, s.length(), formatted, 0, formatted.length());
			if (s.charAt(0) != '+') {
				s.insert(0, "+");
			}
			// The text could be changed by other TextWatcher after we changed it. If we found the
			// text is not the one we were expecting, just give up calling setSelection().
			if (formatted.equals(s.toString())) {
				Selection.setSelection(s, rememberedPos);
			}
			mSelfChange = false;
		} else if (s.length() == 0) {
			mSelfChange = true;
			s.insert(0, "+");
			mSelfChange = false;
		}
	}

	/**
	 * Generate the formatted number by ignoring all non-dialable chars and stick the cursor to the
	 * nearest dialable char to the left. For instance, if the number is  (650) 123-45678 and '4' is
	 * removed then the cursor should be behind '3' instead of '-'.
	 */
	private String reformat(CharSequence s, int cursor) {
		// The index of char to the leftward of the cursor.
		int curIndex = cursor - 1;
		String formatted = null;
		mFormatter.clear();
		char lastNonSeparator = 0;
		boolean hasCursor = false;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (PhoneNumberUtils.isNonSeparator(c)) {
				if (lastNonSeparator != 0) {
					formatted = getFormattedNumber(lastNonSeparator, hasCursor);
					hasCursor = false;
				}
				lastNonSeparator = c;
			}
			if (i == curIndex) {
				hasCursor = true;
			}
		}
		if (lastNonSeparator != 0) {
			formatted = getFormattedNumber(lastNonSeparator, hasCursor);
		}
		return formatted;
	}

	private String getFormattedNumber(char lastNonSeparator, boolean hasCursor) {
		return hasCursor ? mFormatter.inputDigitAndRememberPosition(lastNonSeparator) : mFormatter.inputDigit(lastNonSeparator);
	}

	private void stopFormatting() {
		mStopFormatting = true;
		mFormatter.clear();
	}

	private boolean hasSeparator(final CharSequence s, final int start, final int count) {
		for (int i = start; i < start + count; i++) {
			char c = s.charAt(i);
			if (!PhoneNumberUtils.isNonSeparator(c)) {
				return true;
			}
		}
		return false;
	}
}
