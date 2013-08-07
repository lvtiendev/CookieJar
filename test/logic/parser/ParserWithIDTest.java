package logic.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import cookiejar.common.Event;
import cookiejar.exception.parser.InvalidIdEnteredException;
import cookiejar.logic.parser.ParserWithID;

//@author A0088447N

@SuppressWarnings("unused")
public class ParserWithIDTest {

	ParserWithID p;
	
	@Before
	public void setUp() {
		p = new ParserWithID();
	}
	
	@Test
	public void testGetEventFromString() {
		try{
			Event output = p.getEventFromString("edit 5 go to school #study");
			Event expected = new Event();
			expected.setID(5);
			expected.setName("go to school");
			Vector<String> h = new Vector<String>();
			h.add("#study");
			expected.setHashTags(h);
			assertEquals("Event: ", true, output.equals(expected));
		} catch (Exception e) {
			fail();
		}
	}
	
	
	@Test(expected=InvalidIdEnteredException.class) 
	public void testParseIntException() throws InvalidIdEnteredException{
		Event event = p.getEventFromString("edit 30/11");
	}
	
	@Test(expected=InvalidIdEnteredException.class) 
	public void testNegativeException() throws InvalidIdEnteredException{
		Event event = p.getEventFromString("edit -4 #school");
	}
}