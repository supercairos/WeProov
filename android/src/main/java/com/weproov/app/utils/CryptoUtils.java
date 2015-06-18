package com.weproov.app.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

	public static String sha1(String toHash) {
		String hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = toHash.getBytes("UTF-8");
			digest.update(bytes, 0, bytes.length);
			bytes = digest.digest();

			// This is ~55x faster than looping and String.formating()
			hash = bytesToHex(bytes);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			Dog.e(e, "Ex");
		}
		return hash;
	}

	// http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	private static final char[] mHexArray = "0123456789ABCDEF".toCharArray();
	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = mHexArray[v >>> 4];
			hexChars[j * 2 + 1] = mHexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
