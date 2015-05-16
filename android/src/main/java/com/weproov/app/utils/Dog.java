package com.weproov.app.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Doro Logger (D(oroL)og(ger)
 */
public class Dog {

	private static final String DEFAULT_TAG = "TAG";

	private static final boolean SHOW_METHOD = true;

	/**
	 * Send a verbose log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void v(String msg) {
		Log.v(getTag(), msg);
	}

	/**
	 * Send a verbose log message and log the exception.
	 *
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void v(String msg, Throwable tr) {
		Log.v(getTag(), msg, tr);
	}

	/**
	 * Send a debug log message.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static void v(String tag, String msg) {
		Dog.v( msg);
	}

	/**
	 * Send a verbose log message and log the exception.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void v(String tag, String msg, Throwable tr) {
		Dog.v( msg, tr);
	}

	/**
	 * Send a debug log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void d(String msg) {
		Log.d(getTag(), msg);
	}

	/**
	 * Send a debug log message and log the exception.
	 *
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void d(String msg, Throwable tr) {
		Log.d(getTag(), msg, tr);
	}

	/**
	 * Send a debug log message.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static void d(String tag, String msg) {
		Dog.d( msg);
	}

	/**
	 * Send a debug log message and log the exception.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void d(String tag, String msg, Throwable tr) {
		Dog.d( msg, tr);
	}

	/**
	 * Send an info log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void i(String msg) {
		Log.i(getTag(), msg);
	}

	/**
	 * Send a info log message and log the exception.
	 *
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void i(String msg, Throwable tr) {
		Log.i(getTag(), msg, tr);
	}

	/**
	 * Send a info log message.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static void i(String tag, String msg) {
		Dog.i( msg);
	}

	/**
	 * Send a info log message and log the exception.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void i(String tag, String msg, Throwable tr) {
		Dog.i( msg, tr);
	}

	/**
	 * Send a warn log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void w(String msg) {
		Log.w(getTag(), msg);
	}

	/**
	 * Send a warn log message and log the exception.
	 *
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void w(String msg, Throwable tr) {
		Log.w(getTag(), msg, tr);
	}

	/**
	 * Send a warn log message and log the exception.
	 *
	 * @param tr An exception to log
	 */
	public static void w(Throwable tr) {
		Log.w(getTag(), tr);
	}

	/**
	 * Send a warn log message.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static void w(String tag, String msg) {
		Dog.w( msg);
	}

	/**
	 * Send a warn log message and log the exception.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void w(String tag, String msg, Throwable tr) {
		Dog.w( msg, tr);
	}


	/**
	 * Send an error log message.
	 *
	 * @param msg The message you would like logged.
	 */
	public static void e(String msg) {
		Log.e(getTag(), msg);
	}

	/**
	 * Send a error log message and log the exception.
	 *
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void e(String msg, Throwable tr) {
		Log.e(getTag(), msg, tr);
	}

	/**
	 * Send a error log message and log the exception.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void e(String tag, String msg, Throwable tr) {
		Dog.e( msg, tr);
	}

	/**
	 * Send a error log message and log the exception.
	 *
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *            the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static void e(String tag, String msg) {
		Dog.e( msg);
	}

	private static String getTag() {
		StackTraceElement[] elements = new Throwable().getStackTrace();
		StringBuilder tag = new StringBuilder(DEFAULT_TAG);
		if (elements != null && elements.length > 2) {
			tag.setLength(0);

			// Take the 3rd one as the first two are inside this class ;)
			StackTraceElement top = elements[2];
			String[] names = TextUtils.split(top.getClassName(), "\\.");

			tag.append(names != null && names.length > 0 ? names[names.length - 1] : "Unknown");
			//noinspection PointlessBooleanExpression,ConstantConditions
			if (SHOW_METHOD && tag.length() > 0 && !TextUtils.isEmpty(top.getMethodName())) {
				tag.append(".");
				tag.append(top.getMethodName());
				tag.append("(");
				tag.append(top.getLineNumber());
				tag.append(")");
			}
		}

		return tag.toString();
	}

}
