package cookiejar.logic.parser;

//@author A0088447N

/**
 * This class helps to get the Event name
 * 
 * @author Tien
 * 
 */
public class EventNameParser {
	public EventNameParser() {
	}

	/**
	 * @param infoString
	 * @return the name of the event
	 */
	public String getEventNameFromString(String infoString) {
		return infoString.replaceAll("\\s+", " ").trim();
	}
}
