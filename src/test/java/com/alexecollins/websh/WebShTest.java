package com.alexecollins.websh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/alexecollins/websh/applicationContext.xml")
public class WebShTest {

	@Autowired
	WebSh sut;

	@Test
	public void testVerifyContext() throws Exception {
		sut.verifyContext();
	}

	@Test
	public void testFindTarget() throws Exception {
		final Target next = sut.findTarget(URI.create(""), "connect", new String[]{"http://www.google.com"});
		assertEquals("connect", next.getMethod().getName());
	}

	@Test
	public void testExecute() throws Exception {
		final String[] args = {"http://www.google.com"};
		final ModelAndView connect = sut.execute(sut.findTarget(URI.create(""), "connect", args), URI.create(""), args);
		assertEquals(URI.create(args[0]), connect.getPath());
	}
}
