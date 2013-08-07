//@a0082927
package common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.joda.time.DateTime;
import org.junit.Test;

import cookiejar.common.Event;

public class EventTest {
	
	@Test
	public void isSameTimeTrueTest() {
		DateTime dt1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime dt2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		
		assertTrue(Event.isSameTime(dt1, dt2));
	}
	
	@Test
	public void isSameTimeNullTrueTest() {
		DateTime dt1 = null;
		DateTime dt2 = null;
		
		assertTrue(Event.isSameTime(dt1, dt2));
	}
	
	@Test
	public void isSameTimeFalseTest() {
		DateTime dt1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime dt2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		assertFalse(Event.isSameTime(dt1, dt2));
	}
	
	@Test
	public void isSameTimeNullFalseTest() {
		DateTime dt1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime dt2 = null;
		
		assertFalse(Event.isSameTime(dt1, dt2));
	}
	
	@Test
	public void compareToSameTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(1, "testEvent", startTime2, endTime2, hashTags2);
		
		assertEquals(0, event1.compareTo(event2));
	}
	
	@Test
	public void compareToDifferentSmallerTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(2, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(1, "testEvent", startTime2, endTime2, hashTags2);
		
		assertEquals(-1, event1.compareTo(event2));
	}
	
	@Test
	public void compareToDifferentLargerTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(2, "testEvent", startTime2, endTime2, hashTags2);
		
		assertEquals(1, event1.compareTo(event2));
	}
	
	@Test
	public void compareToDifferentNameTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent1", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(1, "testEvent2", startTime2, endTime2, hashTags2);
		
		assertEquals(-1, event1.compareTo(event2));
	}
	
	@Test
	public void compareToDifferentTimeTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-23T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-24T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(1, "testEvent", startTime2, endTime2, hashTags2);
		
		assertEquals(-1, event1.compareTo(event2));
	}
	
	@Test
	public void compareToDifferentHashTagsTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		
		Event event2 = new Event(1, "testEvent", startTime2, endTime2, hashTags2);
		
		assertEquals(-1, event1.compareTo(event2));
	}
	
	@Test
	public void equalsTrueTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(1, "testEvent", startTime2, endTime2, hashTags2);
		
		assertTrue(event1.equals(event2));
	}
	
	@Test
	public void equalsFalseTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		
		DateTime startTime2 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime2 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags2 = new Vector<String> ();
		hashTags2.add("#testtag1");
		hashTags2.add("#testtag2");
		
		Event event2 = new Event(2, "testEvent", startTime2, endTime2, hashTags2);
		
		assertFalse(event1.equals(event2));
	}

	@Test
	public void cloneHashTagsTrueTest() {
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", null, null, hashTags1);
		
		Vector<String> clonedHashTag = event1.cloneHashTags();
		
		assertTrue(clonedHashTag.equals(hashTags1));
	}
	
	@Test
	public void cloneHashTagsFalseTest() {
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", null, null, hashTags1);
		
		Vector<String> clonedHashTag = event1.cloneHashTags();
		
		hashTags1.remove(1);
		
		assertFalse(clonedHashTag.equals(hashTags1));
	}
	
	@Test
	public void cloneTest() {
		DateTime startTime1 = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime1 = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTags1 = new Vector<String> ();
		hashTags1.add("#testtag1");
		hashTags1.add("#testtag2");
		
		Event event1 = new Event(1, "testEvent", startTime1, endTime1, hashTags1);
		Event clonedEvent = event1.clone();
		
		assertTrue(clonedEvent.equals(event1));
	}

}
