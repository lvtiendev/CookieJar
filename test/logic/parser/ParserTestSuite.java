package logic.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//@author A0088447N

@RunWith(Suite.class)
@SuiteClasses({ EventHashTagsParserTest.class,
				ParserTest.class,
				StringToolsTest.class,
				DateTimeToolsTest.class,
				EventTimeParserTest.class,
				ParserAddTest.class,
				ParserDisplayTest.class,
				ParserWithIDTest.class})
public class ParserTestSuite {
}
