package cookiejar.logic.parser;

import org.joda.time.DateTime;

//@author A0088447N

/**
 * This class contains some static methods to work and modify Joda DateTime
 * objects
 * 
 * @author Tien
 * 
 */
public class DateTimeTools {
	/**
	 * Set the time of an Joda DateTime object to 00:00
	 * 
	 * @param dt
	 *            is original DateTime object
	 * @return a copy with modified time as 00:00
	 */
	public static DateTime setTimeStartDay(DateTime dt) {
		if (dt == null) {
			return null;
		}
		return dt.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
				.withMillisOfSecond(0);
	}

	/**
	 * Set the time of an Joda DateTime object to 23:59
	 * 
	 * @param dt
	 *            is original DateTime object
	 * @return a copy with modified time as 23:59
	 */
	public static DateTime setTimeEndDay(DateTime dt) {
		if (dt == null) {
			return null;
		}
		return dt.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(0)
				.withMillisOfSecond(0);
	}

	/**
	 * This operation check if 2 DateTime objects have same dates and times The
	 * milliseconds difference limit is defined as
	 * <code>ParserConstants.SAME_TIME_THRESHOLD</code>
	 * 
	 * @param dt1
	 *            is the first date time
	 * @param dt2
	 *            is the second date time
	 * @return true if 2 objects have same date and same time within the
	 *         threshold or false if otherwise
	 * 
	 */
	public static boolean isSameDateTime(DateTime dt1, DateTime dt2) {
		if (dt1 == null && dt2 == null) {
			return true;
		} else if (dt1 == null && dt2 != null) {
			return false;
		} else if (dt1 != null && dt2 == null) {
			return false;
		}

		long millisDifference = Math.abs(dt1.getMillis() - dt2.getMillis());
		return (millisDifference < ParserConstants.SAME_TIME_THRESHOLD);
	}

	/**
	 * This operation check if 2 DateTime objects have same time The
	 * milliseconds difference limit is defined as
	 * <code>ParserConstants.SAME_TIME_THRESHOLD</code>
	 * 
	 * @param dt1
	 *            is first date time
	 * @param dt2
	 *            is second date time
	 * @return true if 2 objects have same time within the threshold or false if
	 *         otherwise
	 * 
	 */
	public static boolean isSameTime(DateTime dt1, DateTime dt2) {
		if (dt1 == null && dt2 == null)
			return true;
		if (dt1 == null && dt2 != null)
			return false;
		if (dt1 != null && dt2 == null)
			return false;

		long millisDifference = Math.abs(dt1.getMillisOfDay()
				- dt2.getMillisOfDay());
		return (millisDifference < ParserConstants.SAME_TIME_THRESHOLD);
	}

	/**
	 * This operation check if 2 DateTime objects have exactly same date
	 * 
	 * @param dt1
	 *            is first date time
	 * @param dt2
	 *            is second date time
	 * @return true if 2 objects have same date or false if otherwise
	 * 
	 */
	public static boolean isSameDate(DateTime dt1, DateTime dt2) {
		if (dt1 == null && dt2 == null)
			return true;
		if (dt1 == null && dt2 != null)
			return false;
		if (dt1 != null && dt2 == null)
			return false;

		return ((dt1.getYear() == dt2.getYear())
				&& (dt1.getMonthOfYear() == dt2.getMonthOfYear()) 
				&& (dt1.getDayOfMonth() == dt2.getDayOfMonth()));
	}

	/**
	 * This operation check if the input DateTime object is approximately equal
	 * to now The milliseconds difference limit is defined as
	 * <code>ParserConstants.SAME_TIME_THRESHOLD</code>
	 * 
	 * @param dt
	 *            need to be checked
	 * @return true if <code>dt</code> is now or false if otherwise
	 * 
	 */
	public static boolean isAboutNow(DateTime dt) {
		if (dt == null) {
			return false;
		}

		DateTime currentTime = new DateTime();
		long timeDifference = dt.getMillis() - currentTime.getMillis();
		return Math.abs(timeDifference) < ParserConstants.SAME_TIME_THRESHOLD;
	}
}
