package logic.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import cookiejar.logic.parser.StringTools;

//@author A0088447N

public class StringToolsTest {

	@Test
	public void testGetFirstWord() {
		assertEquals("First word: ", "add", 
				StringTools.getFirstWord("add go to school tmr"));
		assertEquals("First word: ", "display", 
				StringTools.getFirstWord("display"));
		assertEquals("First word: ", "", 
				StringTools.getFirstWord(""));
	}

	@Test
	public void testRemoveFirstWord() {
		assertEquals("Remain string: ", "go to school tmr", 
				StringTools.removeFirstWord("add go to school tmr"));
		assertEquals("Remain string: ", "", 
				StringTools.removeFirstWord("display"));
		assertEquals("Remain string: ", "", 
				StringTools.removeFirstWord(""));
	}

	@Test
	public void testMakeStringFromArrayStringArray() {
		String[] params = new String[] {"go", "to", "school", "tmr"};
		assertEquals("New string: ", "go to school tmr", 
				StringTools.makeStringFromArray(params));
		assertEquals("New string: ", "go to school", 
				StringTools.makeStringFromArray(params, 0, 3));
	}

	@Test
	public void testNormalizeCommandString() {
		assertEquals("New string: ", "go home to school", 
				StringTools.normalizeCommandString("add go home til school"));
		assertEquals("New string: ", "go home to school tomorrow tomorrow TMR", 
				StringTools.normalizeCommandString("add go home until school tmr tMl TMR"));
	}

	@Test
	public void testCountWords() {
		assertEquals("Number of words: ", 0, StringTools.countWords(""));
		assertEquals("Number of words: ", 3, StringTools.countWords("  go to school "));
		assertEquals("Number of words: ", 5, StringTools.countWords("go to school fri morning"));
	}

}
