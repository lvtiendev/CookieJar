package cookiejar.logic.parser;

import cookiejar.common.Event;
import cookiejar.exception.parser.InvalidIdEnteredException;

//@author A0088447N

/**
 * Specific class to handle DELETE/EDIT/DONE/UNDONE commands from user. This is
 * an extended class of abstract class Parser.
 * 
 * @author Tien
 * 
 */
public class ParserWithID extends Parser {
	public ParserWithID() {
		super();
	}

	/**
	 * This operation gets all information from a command that requires the ID
	 * of an event. It will parse the following information:
	 * <ul>
	 * <li>Event ID</li>
	 * <li>List of hash tags</li>
	 * <li>Date time information</li>
	 * <li>Event name</li>
	 * </ul>
	 */
	public Event getEventFromString(String userCommand)
			throws InvalidIdEnteredException {
		String infoString = StringTools.normalizeCommandString(userCommand);
		resetEvent();

		try {
			// First get the ID of the event
			String idString = StringTools.getFirstWord(infoString);
			infoString = StringTools.removeFirstWord(infoString);
			int ID = Integer.parseInt(idString);
			if (ID < 0) {
				throw new InvalidIdEnteredException(
						"Have you checked your ID ?");
			}
			newEvent.setID(ID);

			infoString = tryParseHashTags(infoString);
			infoString = tryParseTime(infoString);
			infoString = tryParseName(infoString);
		} catch (NumberFormatException e) {
			throw new InvalidIdEnteredException("Have you checked your ID ?");
		}

		return newEvent;
	}
}
