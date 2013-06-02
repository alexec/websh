package com.alexecollins.websh;

import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.net.URI;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@Component
@Path("https://www.facebook.com")
@Help("accessing facebook")
public class Facebook {

	@Path("")
	@Help("show the news")
	public ModelAndView news(URI path) {
		return ModelAndView.of(path, new StringReader(""));
	}
}
