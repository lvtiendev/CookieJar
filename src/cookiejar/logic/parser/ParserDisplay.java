package cookiejar.logic.parser;

import org.joda.time.DateTime;

import cookiejar.common.Event;

//@author A0088447N

/**
 * Specific class to handle DISPLAY commands from user. This is an extended
 * class of abstract class Parser.
 * 
 * @author Tien
 * 
 */
public class ParserDisplay extends Parser {
	public ParserDisplay() {
		super();
	}

	private boolean isDisplayIdCommand(String infoString) {
		String[] words = StringTools.splitParameters(infoString);
		if (words.length == 1) {
			try {
				Integer.parseInt(words[0]);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean isDefaultDisplayCommand(String infoString) {
		return (infoString.equals(ParserConstants.EMPTY_STRING));
	}

	private boolean hasOnlyEndTime(Event event) {
		return (event.getEndTime() != null) && (event.getStartTime() == null);
	}

	/**
	 * This operation gets all information from a DISPLAY command. Support three
	 * different types of commands:
	 * <ul>
	 * <li>Default display : the userCommand has no information</li>
	 * <li>ID display : the userCommand has ID of the event</li>
	 * <li>General display : the userCommand has some event information</li>
	 * </ul>
	 */
	public Event getEventFromString(String userCommand) {
		String infoString = StringTools.normalizeCommandString(userCommand);
		resetEvent();

		if (isDefaultDisplayCommand(infoString)) {
			newEvent.setStartTime(ParserConstants.DATE_TIME_MIN);
			newEvent.setEndTime(ParserConstants.DATE_TIME_MAX);
		} else if (isDisplayIdCommand(infoString)) {
			int ID = Integer.parseInt(infoString.trim());
			newEvent.setID(ID);
		} else {
			infoString = tryParseHashTags(infoString);
			infoString = tryParseTime(infoString);
			infoString = tryParseName(infoString);

			// If there is only end date time information in the command,
			// add a start date time information to support Executor
			// new start time is 00:00
			// new end time is 23:59
			if (hasOnlyEndTime(newEvent)) {
				DateTime tempStartTime = newEvent.getEndTime();
				tempStartTime = DateTimeTools.setTimeStartDay(tempStartTime);

				DateTime tempEndTime = tempStartTime;
				tempEndTime = DateTimeTools.setTimeEndDay(tempEndTime);

				newEvent.setStartTime(tempStartTime);
				newEvent.setEndTime(tempEndTime);
			}
		}

		return newEvent;
	}
}
