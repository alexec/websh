package com.alexecollins.websh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@Component
@Path(".*")
@Help("core commands")
public class Core {
	@Autowired
	ApplicationContext context;
	@Autowired WebSh sh;

	@Path("")
	@Help("connect to a new web site, e.g. connect e.g. http://www.google.com\"")
	public ModelAndView connect(URI path, String newPath) throws URISyntaxException {
		return ModelAndView.of(new URI(newPath), new StringReader("connected to " + newPath));
	}

	@Path("")
	@Help("quit shell")
	public ModelAndView quit(URI path) {
		return ModelAndView.of(null, new StringReader("quit"));
	}

	@Path
	@Help("show basic help")
	public ModelAndView help(URI path) {
		final StringBuilder help = new StringBuilder();
		for (Object bean: context.getBeansWithAnnotation(Path.class).values()) {
			help.append('\n').append(bean.getClass().getSimpleName()).append(" (").append(bean.getClass().getAnnotation(Path.class).value()).append(")");
			if (bean.getClass().isAnnotationPresent(Help.class)) {
				help.append(" - ").append(bean.getClass().getAnnotation(Help.class).value());
			}
			help.append("\n---\n");
			for (Method method : bean.getClass().getMethods()) {
				if (method.isAnnotationPresent(Path.class)) {
					help.append("- ").append(method.getName());
					if (method.isAnnotationPresent(Help.class)) {
						help.append(" - ").append(method.getAnnotation(Help.class).value());
					}
					help.append('\n');
				}
			}
		}

		return ModelAndView.of(path, new StringReader(help.toString()));
	}
}
