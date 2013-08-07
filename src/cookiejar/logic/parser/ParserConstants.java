package cookiejar.logic.parser;

import org.joda.time.DateTime;

//@author A0088447N

/**
 * This class contains all constants used among other classes in <b>parser</b>
 * package
 * 
 * @author Tien
 * 
 */
public class ParserConstants {
	public static final char HASH_TAG_MARKER = '#';
	public static final String EMPTY_STRING = "";
	public static final String DATE_TIME_SPLIT = "TO";
	public static final String FROM = "FROM";
	public static final DateTime DATE_TIME_MAX = new DateTime(3000, 1, 1, 0, 0, 0, 0);
	public static final DateTime DATE_TIME_MIN = new DateTime(0);
	public static final long NOW_THRESHOLD = 500;
	public static final long SAME_TIME_THRESHOLD = 500;
	public static final String[] WEEK_DAY_STRINGS = { "monday", "mon", "tuesday",
			"tue", "wednesday", "wed", "thursday", "thu", "friday", "fri",
			"saturday", "sat", "sunday", "sun" };
}
