package cookiejar.logic.parser;

import org.joda.time.DateTime;

//@author A0088447N

/**
 * This class represents a set of 2 Joda DateTime objects
 * 
 * @author Tien
 * 
 */
public class TimePair {
	private DateTime first;
	private DateTime second;

	public TimePair() {
		first = null;
		second = null;
	}

	public TimePair(DateTime first, DateTime second) {
		this.first = first;
		this.second = second;
	}

	public DateTime getFirst() {
		return first;
	}

	public DateTime getSecond() {
		return second;
	}

	public void setFirst(DateTime dt) {
		if (dt != null)
			first = dt;
	}

	public void setSecond(DateTime dt) {
		if (dt != null)
			second = dt;
	}

	public TimePair clone() {
		TimePair result = new TimePair();
		result.setFirst(first);
		result.setSecond(second);
		return result;
	}
}
