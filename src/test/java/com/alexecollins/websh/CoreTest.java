package com.alexecollins.websh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/alexecollins/websh/applicationContext.xml")
public class CoreTest {
	@Autowired
	WebSh sut;

	@Test
	public void testHelpSimple() throws Exception {
		final ModelAndView help = sut.execute(URI.create(""), "help");
		assertEquals("connect\nhelp\nquit", TestUtils.readerToString(help.getReader()));
	}

	@Test
	public void testHelpComplex() throws Exception {
	fail();
	}
}
