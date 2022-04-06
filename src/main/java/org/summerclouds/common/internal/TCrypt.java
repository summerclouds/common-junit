package org.summerclouds.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TCrypt {

	public static String md5(InputStream is) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
	    DigestInputStream dis = new DigestInputStream(is, md);
	    while ( dis.read() >= 0) {}
		byte[] digest = md.digest();
		return Base64.getEncoder().encodeToString(digest);
	}
	
}
