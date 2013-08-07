package logic.parser;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import cookiejar.logic.parser.DateTimeTools;
import cookiejar.logic.parser.EventTimeParser;
import cookiejar.logic.parser.TimePair;

//@author A0088447N

public class EventTimeParserTest {
	EventTimeParser p;

	@Before
	public void setUp() {
		p = new EventTimeParser();
	}

	@Test
	public void testOneTime() {
		String infoString = p.extractTimeFromString("go from home to school 30/11/2012 8am");
		assertEquals("String: ", infoString, "go from home to school");
		DateTime dt = new DateTime(2012, 11, 30, 8, 0, 0);
		TimePair expected = new TimePair(null, dt);
		TimePair output = p.getResultTimePair();
		assertEquals("Time pair: ", true,
				DateTimeTools.isSameDateTime(output.getFirst(),expected.getFirst())
				&& DateTimeTools.isSameDateTime(output.getSecond(),expected.getSecond()));
	}

	@Test
	public void testTwoTimes() {
		String infoString = p.extractTimeFromString("go home to school from 23/11/2012 to 30/11/2012");
		assertEquals("String: ", infoString, "go home to school");
		DateTime startTime = new DateTime(2012, 11, 23, 0, 0, 0);
		DateTime endTime = new DateTime(2012, 11, 30, 23, 59, 0);
		TimePair expected = new TimePair(startTime, endTime);
		TimePair output = p.getResultTimePair();
		assertEquals("Time pair: ", true,
				DateTimeTools.isSameDateTime(output.getFirst(), expected.getFirst())
				&& DateTimeTools.isSameDateTime(output.getSecond(), expected.getSecond()));
	}
}
