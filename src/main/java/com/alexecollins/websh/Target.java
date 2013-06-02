package com.alexecollins.websh;

import java.lang.reflect.Method;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
public class Target {
	private Object bean;
	private Method method;

	Target(Object bean, Method method) {
		this.bean = bean;
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public Object getBean() {
		return bean;
	}
}