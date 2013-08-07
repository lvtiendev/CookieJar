package logic.parser;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import cookiejar.common.Event;
import cookiejar.logic.parser.ParserConstants;
import cookiejar.logic.parser.ParserDisplay;

//@author A0088447N

public class ParserDisplayTest {
	ParserDisplay p;

	@Before
	public void setUp() {
		p = new ParserDisplay();
	}

	@Test
	public void testGetEventFromString() {
		Event expected = new Event();
		expected.setStartTime(new DateTime(2012, 11, 30, 0, 0));
		expected.setEndTime(new DateTime(2012, 11, 30, 23, 59));

		Event output = p.getEventFromString("display 30/11/2012");

		assertEquals("Event: ", true, expected.equals(output));
	}

	@Test
	public void testGetEventFromStringDefault() {
		Event expected = new Event();
		expected.setStartTime(ParserConstants.DATE_TIME_MIN);
		expected.setEndTime(ParserConstants.DATE_TIME_MAX);

		Event output = p.getEventFromString("display");

		assertEquals("Event: ", true, expected.equals(output));
	}

	@Test
	public void testGetEventFromStringID() {
		Event expected = new Event();
		expected.setID(5);

		Event output = p.getEventFromString("display 5");

		assertEquals("Event: ", true, expected.equals(output));
	}
}
