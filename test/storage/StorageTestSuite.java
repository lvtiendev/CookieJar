//@author A0082927
package storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.joda.time.DateTime;
import org.junit.Test;

import cookiejar.common.Event;
import cookiejar.exception.storage.InvalidFileContentException;
import cookiejar.storage.Converter;
import cookiejar.storage.StorageConstants;

public class StorageTestSuite {
	
	Converter storageConverter;
	
	@Test
	public void testFlattenHashTags() {
		storageConverter = new Converter();
		
		Vector<String> hashTagsToBeTested = new Vector<String> ();
		hashTagsToBeTested.add("#testtag1");
		hashTagsToBeTested.add("#testtag2");
		String expectedHashTagsInString = "#testtag1#testtag2";
		
		assertEquals(expectedHashTagsInString, storageConverter.flattenHashTags(hashTagsToBeTested));
	}
	
	@Test
	public void testExpandHashTags() {
		storageConverter = new Converter();
		
		Vector<String> expectedHashTags = new Vector<String> ();
		expectedHashTags.add("#testtag1");
		expectedHashTags.add("#testtag2");
		String hashTagsInStringToBeTested = "#testtag1#testtag2";
		
		assertEquals(expectedHashTags, storageConverter.expandHashTags(hashTagsInStringToBeTested));
	}

	@Test
	public void testEventObjectToStringEventName() {
		storageConverter = new Converter();
		
		Event eventToBeTested = new Event(1, "testEvent", null, null, new Vector<String>());
		String expectedEventInString = "1~testEvent~*EMPTY*~*EMPTY*~*EMPTY*";
		
		assertEquals(expectedEventInString, storageConverter.eventObjectToString(eventToBeTested));
	}
	
	@Test
	public void testEventObjectToStringDateTime() {
		storageConverter = new Converter();
		
		DateTime startTime = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Event eventToBeTested = new Event(1, StorageConstants.EMPTY_STRING, startTime, endTime, new Vector<String>());
		String expectedEventInString = "1~*EMPTY*~2012-10-21T14:10:55.618+08:00~2012-10-22T14:10:55.618+08:00~*EMPTY*";
		
		assertEquals(expectedEventInString, storageConverter.eventObjectToString(eventToBeTested));
	}

	@Test
	public void testEventObjectToStringHashTags() {
		storageConverter = new Converter();
		
		Vector<String> hashTagsToBeTested = new Vector<String> ();
		hashTagsToBeTested.add("#testtag1");
		hashTagsToBeTested.add("#testtag2");
		
		Event eventToBeTested = new Event(1, StorageConstants.EMPTY_STRING, null, null, hashTagsToBeTested);
		String expectedEventInString = "1~*EMPTY*~*EMPTY*~*EMPTY*~#testtag1#testtag2";
		
		assertEquals(expectedEventInString, storageConverter.eventObjectToString(eventToBeTested));
	}
	
	@Test
	public void testEventObjectToStringAllFields() {
		storageConverter = new Converter();
		
		DateTime startTime = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> hashTagsToBeTested = new Vector<String> ();
		hashTagsToBeTested.add("#testtag1");
		hashTagsToBeTested.add("#testtag2");
		
		Event eventToBeTested = new Event(1, "testEvent", startTime, endTime, hashTagsToBeTested);
		String expectedEventInString = "1~testEvent~2012-10-21T14:10:55.618+08:00~2012-10-22T14:10:55.618+08:00~#testtag1#testtag2";
	
		assertEquals(expectedEventInString, storageConverter.eventObjectToString(eventToBeTested));
	}
	
	@Test
	public void testEventObjectToStringEmptyFields() {
		storageConverter = new Converter();
		
		Event eventToBeTested = new Event(StorageConstants.TASK_ID_NULL, StorageConstants.EMPTY_STRING, null, null, new Vector<String>());
		String expectedEventInString = "0~*EMPTY*~*EMPTY*~*EMPTY*~*EMPTY*";
		assertEquals(expectedEventInString, storageConverter.eventObjectToString(eventToBeTested));
	}
	
	@Test
	public void testStringToEventObjectName() {
		storageConverter = new Converter();

		Event expectedEvent = new Event(1, "testEvent", null, null, new Vector<String>());
		String eventInString = "1~testEvent~*EMPTY*~*EMPTY*~*EMPTY*";
		
		try {
			assertTrue(expectedEvent.equals(storageConverter.stringToEventObject(eventInString)));
		} catch (InvalidFileContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringToEventObjectDateTime() {
		storageConverter = new Converter();
		
		DateTime startTime = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Event expectedEvent = new Event(1, StorageConstants.EMPTY_STRING, startTime, endTime, new Vector<String>());
		String eventInString = "1~*EMPTY*~2012-10-21T14:10:55.618+08:00~2012-10-22T14:10:55.618+08:00~*EMPTY*";
		
		try {
			assertTrue(expectedEvent.equals(storageConverter.stringToEventObject(eventInString)));
		} catch (InvalidFileContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testStringToEventObjectHashTags() {
		storageConverter = new Converter();
		
		Vector<String> expectedHashTags = new Vector<String> ();
		expectedHashTags.add("#testtag1");
		expectedHashTags.add("#testtag2");
		
		Event expectedEvent = new Event(1, StorageConstants.EMPTY_STRING, null, null, expectedHashTags);
		String eventInString = "1~*EMPTY*~*EMPTY*~*EMPTY*~#testtag1#testtag2";
		
		try {
			assertTrue(expectedEvent.equals(storageConverter.stringToEventObject(eventInString)));
		} catch (InvalidFileContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringToEventObjectAllFields() {
		storageConverter = new Converter();
		
		DateTime startTime = new DateTime("2012-10-21T14:10:55.618+08:00");
		DateTime endTime = new DateTime("2012-10-22T14:10:55.618+08:00");
		
		Vector<String> expectedHashTags = new Vector<String> ();
		expectedHashTags.add("#testtag1");
		expectedHashTags.add("#testtag2");
		
		Event expectedEvent = new Event(1, "testEvent", startTime, endTime, expectedHashTags);
		String eventInString = "1~testEvent~2012-10-21T14:10:55.618+08:00~2012-10-22T14:10:55.618+08:00~#testtag1#testtag2";
	
		try {
			assertTrue(expectedEvent.equals(storageConverter.stringToEventObject(eventInString)));
		} catch (InvalidFileContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringToEventObjectEmptyFields() {
		storageConverter = new Converter();
		
		Event expectedEvent = new Event(StorageConstants.TASK_ID_NULL, StorageConstants.EMPTY_STRING, null, null, new Vector<String>());
		String eventInString = "0~*EMPTY*~*EMPTY*~*EMPTY*~*EMPTY*";
		
		try {
			assertTrue(expectedEvent.equals(storageConverter.stringToEventObject(eventInString)));
		} catch (InvalidFileContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
