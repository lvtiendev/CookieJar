package logic.parser;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import cookiejar.logic.parser.DateTimeTools;

//@author A0088447N

public class DateTimeToolsTest {

	@Test
	public void testIsSameTime() {
		DateTime dt1 = new DateTime(2012, 11, 8, 20, 0, 0);
		DateTime dt2 = new DateTime(2012, 11, 9, 20, 0, 0);
		assertEquals("Same time:", true, DateTimeTools.isSameTime(dt1, dt2));

		dt2 = new DateTime(2012, 11, 8, 20, 1, 0);
		assertEquals("Same time:", false, DateTimeTools.isSameTime(dt1, dt2));
	}

	@Test
	public void testIsSameDate() {
		DateTime dt1 = new DateTime(2012, 11, 8, 20, 0, 0);
		DateTime dt2 = new DateTime(2012, 11, 8, 5, 0, 0);
		assertEquals("Same date:", true, DateTimeTools.isSameDate(dt1, dt2));

		dt2 = new DateTime(2012, 11, 10, 20, 0, 0);
		assertEquals("Same date:", false, DateTimeTools.isSameDate(dt1, dt2));
	}

	@Test
	public void testIsAboutNow() {
		DateTime dt = new DateTime();
		assertEquals("Is now: ", true, DateTimeTools.isAboutNow(dt));

		dt = (new DateTime()).plusSeconds(1);
		assertEquals("Is now: ", false, DateTimeTools.isAboutNow(dt));
	}

}
