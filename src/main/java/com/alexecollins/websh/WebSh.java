package com.alexecollins.websh;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
public class WebSh {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("com/alexecollins/websh/applicationContext.xml").start();
	}
}
