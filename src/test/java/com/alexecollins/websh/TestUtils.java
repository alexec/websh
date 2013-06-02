package com.alexecollins.websh;

import java.io.Reader;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
public class TestUtils {
	public static String readerToString(Reader is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
