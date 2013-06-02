package com.alexecollins.websh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@Component
public class WebSh implements Lifecycle {
	@Autowired
	private ApplicationContext context;

	private boolean running;
	private Thread thread;
	private volatile boolean cancelled;

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("/com/alexecollins/websh/applicationContext.xml").start();
	}

	@Override
	public void start() {

		verifyContext();

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				URI pwd = URI.create("");
				final Scanner sc = new Scanner(System.in);
				while (!cancelled) {
					final String commandName = sc.next();
					// TODO hack
					final String[] args = sc.nextLine().trim().split(" ");

					try {
						final ModelAndView execute = execute(findTarget(pwd, commandName, args), pwd, args);
						final BufferedReader in = new BufferedReader(execute.getReader());
						String l;
						while ((l = in.readLine()) !=null){
							System.out.println(l);
						}
						pwd = execute.getPath();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();

		running = true;
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

	Target findTarget(URI pwd, String commandName, String[] args) {

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

	ModelAndView execute(Target target, URI path, String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
		final List<Object> params = new ArrayList<Object>();
		int i = 0;
		for (Class<?> c : target.getMethod().getParameterTypes()) {
			if (c.equals(URI.class)) {
				assert i ==0;
				params.add(path);
			} else {
				params.add(c.getConstructor(String.class).newInstance(args[i]));
				i++;
			}
		}

		return (ModelAndView)target.getMethod().invoke(target.getBean(), params.toArray());
	}
}
