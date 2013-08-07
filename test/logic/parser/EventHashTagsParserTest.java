package logic.parser;

import static org.junit.Assert.assertEquals;

import java.util.Vector;

import org.junit.Test;

import cookiejar.logic.parser.EventHashTagsParser;

//@author A0088447N

public class EventHashTagsParserTest {

	@Test
	public void testExtractHashTagsFromString() {
		EventHashTagsParser p = new EventHashTagsParser();

		Vector<String> expected = new Vector<String>();
		expected.add("#study");
		expected.add("#deadline");
		assertEquals("Remain string: ", "add cs2103",
				p.extractHashTagsFromString("add #study cs2103 #deadline"));
		assertEquals("Hash tag vector: ", p.getResultHashTags(), expected);

		expected = new Vector<String>();
		assertEquals("Remain string: ", "go to school",
				p.extractHashTagsFromString("go to school"));
		assertEquals("Hash tag vector: ", p.getResultHashTags(), expected);

		expected = new Vector<String>();
		expected.add("#to");
		assertEquals("Remain string: ", "go school",
				p.extractHashTagsFromString("go #to #to school"));
		assertEquals("Hash tag vector: ", p.getResultHashTags(), expected);
	}

}
