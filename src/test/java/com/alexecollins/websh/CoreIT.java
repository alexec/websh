package com.alexecollins.websh;

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
public class CoreIT {
	@Autowired
	WebSh sut;

	@Test
	public void testHelpSimple() throws Exception {
		sut.execute("help", "");
	}

}
