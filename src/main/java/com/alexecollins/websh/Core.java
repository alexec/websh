package com.alexecollins.websh;

import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@Component
@Path(".*")
public class Core {
	@Path("")
	public ModelAndView connect(URI path, String newPath) throws URISyntaxException {
		return ModelAndView.of(new URI(newPath), new StringReader("connected to " + newPath));
	}
}
