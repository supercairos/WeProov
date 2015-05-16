package com.weproov.app.models;

import android.net.Uri;

public class UserProfile {

	/**
	 * The primary email address
	 */
	public String email;

	/**
	 * The primary name
	 */
	public String givenName;

	/**
	 * The primary name
	 */
	public String familyName;

	/**
	 * The primary phone number
	 */
	public String phoneNumber;

	/**
	 * A possible photo for the user
	 */
	public Uri photo;

	@Override
	public String toString() {
		return "UserProfile{" +
				"email='" + email + '\'' +
				", givenName='" + givenName + '\'' +
				", familyName='" + familyName + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", photo=" + photo +
				'}';
	}
}
