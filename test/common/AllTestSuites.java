package common;

import logic.executor.ExecutorTestSuite;
import logic.executor.SystemTest;
import logic.parser.ParserTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import storage.StorageTestSuite;


import gui.GuiTestSuite;

//@author A0088447N

@RunWith(Suite.class)
@SuiteClasses({ StorageTestSuite.class,
				EventTest.class,
				ParserTestSuite.class,
				GuiTestSuite.class,
				ExecutorTestSuite.class,
				SystemTest.class})
public class AllTestSuites {
}
