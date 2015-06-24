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
			hash = bytesToHexV2(bytes);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			Dog.e(e, "Ex");
		}
		return hash;
	}

	private static String bytesToHexV2(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	// http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	private static final char[] sHexArray = "0123456789ABCDEF".toCharArray();
	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = sHexArray[v >>> 4];
			hexChars[j * 2 + 1] = sHexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
