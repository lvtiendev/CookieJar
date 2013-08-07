package cookiejar.logic.parser;

import cookiejar.common.Event;
import cookiejar.exception.parser.InvalidNameEnteredException;

//@author A0088447N

/**
 * Specific class to handle ADD commands from user. This is an extended class of
 * abstract class Parser.
 * 
 * @author Tien
 * 
 */
public class ParserAdd extends Parser {
	public ParserAdd() {
		super();
	}

	/**
	 * This operation gets all information from a ADD command. It will parse the
	 * following information:
	 * <ul>
	 * <li>List of hash tags</li>
	 * <li>Date time information</li>
	 * <li>Event name</li>
	 * </ul>
	 * The event ID will be set to be default
	 */
	public Event getEventFromString(String userCommand)
			throws InvalidNameEnteredException {
		String infoString = StringTools.normalizeCommandString(userCommand);
		resetEvent();

		infoString = tryParseHashTags(infoString);
		infoString = tryParseTime(infoString);
		infoString = tryParseName(infoString);

		if (newEvent.getName().equalsIgnoreCase(ParserConstants.EMPTY_STRING)) {
			throw new InvalidNameEnteredException("Please name your new cookie");
		}

		return newEvent;
	}
}
