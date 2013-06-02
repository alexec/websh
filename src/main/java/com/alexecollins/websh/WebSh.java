package com.alexecollins.websh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@Component
public class WebSh implements Lifecycle {
	@Autowired
	private ConfigurableApplicationContext context;

	private boolean running;
	private Thread thread;
	private volatile boolean cancelled;

	public static void main(String[] args) throws Exception {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/com/alexecollins/websh/applicationContext.xml");

		if (args.length > 0) {
			context.getBean(WebSh.class).execute( "open", args[0]);
		}
		context.start();
	}

	private URI pwd = URI.create("");

	@Override
	public void start() {

		verifyContext();

		thread = new Thread(new Runnable() {
			@Override
			public void run() {

				System.out.println("type help or quit");

				final Scanner sc = new Scanner(System.in);
				while (!cancelled) {
					System.out.print("% ");
					System.out.flush();
					final String commandName = sc.next();
					final String line = sc.nextLine();
					try {
						execute(commandName, line);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();

		running = true;
	}

	void execute(String commandName, String line) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, IOException {
		final List<String> list = new ArrayList<String>();
		final Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line.trim());
		while (m.find())
		list.add(m.group(1));
		final String[] args = list.toArray(new String[list.size()]);
		final ModelAndView execute = execute(findTarget(commandName, args), args);
		final BufferedReader in = new BufferedReader(execute.getReader());
		try {
			String l;
			while ((l = in.readLine()) !=null){
				System.out.println(l);
			}
		} finally {
			in.close();
		}
		if (execute.getPath() == null) {
			System.exit(0);
		}
		pwd = execute.getPath();
	}

	void verifyContext() {

		for (Object bean : context.getBeansWithAnnotation(Path.class).values()) {
			for (Method method : bean.getClass().getMethods()) {
				if (method.isAnnotationPresent(Path.class)) {
					if (!ModelAndView.class.isAssignableFrom(method.getReturnType())) {
						throw new IllegalStateException("invalid return type on " + method + ", should be " + ModelAndView.class);
					}
				}
			}
		}
	}

	Target findTarget(String commandName, String... args) {

		for (Object bean : context.getBeansWithAnnotation(Path.class).values()) {
			String root = bean.getClass().getAnnotation(Path.class).value();

			final Set<Method> candidates = new HashSet<Method>();
			for (Method method : bean.getClass().getMethods()) {
				if (method.getName().equals(commandName) && method.isAnnotationPresent(Path.class)) {
					final String path = root + method.getAnnotation(Path.class).value();
					if (pwd.toString().matches(path) &&
							method.getParameterTypes().length == args.length + 1) {
						candidates.add(method);
					}
				}
			}
			switch (candidates.size()) {
				case 0:
					break;
				case 1:
					return new Target(bean, candidates.iterator().next());
				default:
					throw new IllegalStateException("found ambiguous candidates");
			}
		}
		throw new IllegalStateException("no candidates found for " + pwd + " " + commandName + " " + Arrays.toString(args));
	}

	ModelAndView execute(Target target, String... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
		final List<Object> params = new ArrayList<Object>();
		int i = 0;
		for (Class<?> c : target.getMethod().getParameterTypes()) {
			if (c.equals(URI.class)) {
				assert i ==0;
				params.add(pwd);
			} else {
				final Class<?> c1 =
					c.equals(int.class) ? Integer.class :
							c;
				params.add(c1.getConstructor(String.class).newInstance(args[i]));
				i++;
			}
		}

		return (ModelAndView)target.getMethod().invoke(target.getBean(), params.toArray());
	}

	@Override
	public void stop() {
		cancelled = true;
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
