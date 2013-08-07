package logic.parser;


import static org.junit.Assert.assertEquals;

import java.util.Vector;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import cookiejar.common.Event;
import cookiejar.exception.parser.InvalidNameEnteredException;
import cookiejar.logic.parser.ParserAdd;

//@author A0088447N

public class ParserAddTest {
	ParserAdd p;

	@Before
	public void setUp() {
		p = new ParserAdd();
	}

	@Test(expected = InvalidNameEnteredException.class)
	public void testGetEventFromStringWithException()
			throws InvalidNameEnteredException {
		p.getEventFromString("add 30/11/2012 #fun");
	}

	@Test
	public void testGetEventFromString() throws InvalidNameEnteredException {
		Event output = p.getEventFromString("add deadline 30/11/2012 5pm #study");

		DateTime dt = new DateTime(2012, 11, 30, 17, 0, 0);
		Vector<String> hashTagsList = new Vector<String>();
		hashTagsList.add("#study");
		Event expected = new Event(0, "deadline", null, dt, hashTagsList);
		assertEquals("Event: ", true, output.equals(expected));
	}
}
