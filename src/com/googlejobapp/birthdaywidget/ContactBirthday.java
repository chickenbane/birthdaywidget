package com.googlejobapp.birthdaywidget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.Time;
import android.util.Log;

/**
 * It appears there are two date formats for the birthday in my Google contacts.
 * It also appears to be unrelated to the date format set on my phone. If a year
 * is supplied, it's: yyyy-MM-dd if no year is supplied, it's --MM-dd
 * 
 * TODO the no year format with a 02-29 leap-year birthdate is a bug
 */
public class ContactBirthday {
	private static final String TAG = "ContactBirthdate";

	private static final SimpleDateFormat BIRTHDAY_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.US);
	private static final SimpleDateFormat BIRTHDAY_NO_YEAR_FORMAT = new SimpleDateFormat(
			"--MM-dd", Locale.US);

	private final String mContactDate;
	private final Date mBirthDate;

	public static ContactBirthday createContactBirthday(final String contactDate) {
		if (contactDate == null) {
			return null;
		}

		final Date birthDate;
		try {
			if (contactDate.startsWith("--")) {
				birthDate = BIRTHDAY_NO_YEAR_FORMAT.parse(contactDate);
			} else {
				birthDate = BIRTHDAY_FORMAT.parse(contactDate);
			}
		} catch (final ParseException e) {
			Log.e(TAG, "Can't parse date string=" + contactDate, e);
			return null;
		}

		return new ContactBirthday(contactDate, birthDate);
	}

	public ContactBirthday(final String contactDate, final Date birthDate) {
		mContactDate = contactDate;
		mBirthDate = birthDate;
	}

	public long getNextBirthday() {
		final Calendar now = Calendar.getInstance();
		final int thisYear = now.get(Calendar.YEAR);
		final Calendar birthdate = Calendar.getInstance();
		birthdate.setTime(mBirthDate);
		birthdate.set(Calendar.YEAR, thisYear);

		if (now.after(birthdate)) {
			birthdate.set(Calendar.YEAR, thisYear + 1);
		}
		return birthdate.getTimeInMillis();
	}

	public Integer getNextBirthdayAge() {
		if (mContactDate.startsWith("--")) {
			return null;
		}

		final Calendar now = Calendar.getInstance();
		final int thisYear = now.get(Calendar.YEAR);
		final Calendar birthdate = Calendar.getInstance();
		birthdate.setTime(mBirthDate);
		final int birthYear = birthdate.get(Calendar.YEAR);
		final int years = thisYear - birthYear;
		birthdate.set(Calendar.YEAR, thisYear);

		if (now.after(birthdate)) {
			return years + 1;
		}
		return years;
	}

	public int getDaysAway() {
		final long now = System.currentTimeMillis();
		final long birthday = getNextBirthday();

		final Time nowTime = new Time();
		nowTime.set(now);
		final int today = Time.getJulianDay(now, nowTime.gmtoff);

		final Time birthdayTime = new Time();
		birthdayTime.set(birthday);
		final int nextBirthday = Time.getJulianDay(birthday,
				birthdayTime.gmtoff);

		return nextBirthday - today;
	}
}
