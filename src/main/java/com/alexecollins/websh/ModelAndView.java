package com.alexecollins.websh;

import java.io.Reader;
import java.net.URI;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
public class ModelAndView {
	private final URI path;
	private final Reader reader;

	private ModelAndView(URI path, Reader reader) {
		this.path = path;
		this.reader = reader;
	}

	public static ModelAndView of(URI path, Reader reader) {
		return new ModelAndView(path,reader);
	}

	public URI getPath() {
		return path;
	}

	public Reader getReader() {
		return reader;
	}
}
