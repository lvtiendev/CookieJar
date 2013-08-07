package logic.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cookiejar.common.COMMAND_TYPE;
import cookiejar.logic.parser.Parser;

//@author A0088447N

public class ParserTest {

	@Test
	public void testParseCommand() {
		assertEquals("Command type: ", COMMAND_TYPE.INVALID, Parser.parseCommand("abc def "));
		assertEquals("Command type: ", COMMAND_TYPE.INVALID, Parser.parseCommand("5bc"));
		assertEquals("Command type: ", COMMAND_TYPE.INCOMPLETE, Parser.parseCommand(""));
		assertEquals("Command type: ", COMMAND_TYPE.ADD, Parser.parseCommand("add sleep 6pm"));
		assertEquals("Command type: ", COMMAND_TYPE.EDIT, Parser.parseCommand("eDit 4 today"));
		assertEquals("Command type: ", COMMAND_TYPE.DELETE, Parser.parseCommand("delete today"));
		assertEquals("Command type: ", COMMAND_TYPE.DISPLAY, Parser.parseCommand("disPlay #work"));
		assertEquals("Command type: ", COMMAND_TYPE.REDO, Parser.parseCommand("REDo"));
		assertEquals("Command type: ", COMMAND_TYPE.UNDO, Parser.parseCommand("uNDo"));
		assertEquals("Command type: ", COMMAND_TYPE.HELP, Parser.parseCommand("HElP"));
		assertEquals("Command type: ", COMMAND_TYPE.EXIT, Parser.parseCommand("Exit"));
	}
}
