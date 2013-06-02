package com.alexecollins.websh;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@Component
@Path("http://www.google.com")
@Help("accessing Google search")
public class Google {

	private static class Result {
		private URI uri;
		private String title;

		private Result(URI uri, String title) {
			this.uri = uri;
			this.title = title;
		}
	}

	private final List<Result> results = new ArrayList<Result>();

	@Path(".*")
	public ModelAndView search(URI path, String terms) throws IOException {


		final Document q = Jsoup.connect(path + "/search").data("q", terms)
				// sneaky
				.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5")
				.get();
		results.clear();

		final StringBuilder out = new StringBuilder();
		for (Element element : q.select("li.g h3 a")) {
			final Result r = new Result(URI.create(element.attr("abs:href")), element.text());
			results.add(r);
			out.append(results.size()).append(". ").append(r.title).append('\n');
		}

		return ModelAndView.of(URI.create("http://www.google.com/search"), new StringReader(out.toString()));
	}

	@Path(".*")
	public ModelAndView go(URI path, int num) throws IOException {
		final int index = num -1;
		Desktop.getDesktop().browse(results.get(index).uri);
		return ModelAndView.of(path, new StringReader(results.get(index).uri.toString()));
	}
}
