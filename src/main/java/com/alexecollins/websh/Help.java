package com.alexecollins.websh;

import java.lang.annotation.*;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@java.lang.annotation.Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	String value();
}
