package com.alexecollins.websh;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author alexec (alex.e.c@gmail.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/alexecollins/websh/applicationContext.xml")
public class GoogleIT {
	@Autowired
	WebSh sut;

	@Before
	public void setUp() throws Exception {
		sut.execute("open", "http://www.google.com");
	}

	@Test
	public void testSearch() throws Exception {
		sut.execute( "search", "\"funny cat videos\"");
	}

	@Test
	public void testGo() throws Exception {
		sut.execute( "search", "\"funny cat videos\"");
		sut.execute("go", "1");
	}
}
