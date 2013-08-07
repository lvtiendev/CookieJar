package cookiejar.logic.parser;

import java.util.Vector;

import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.exception.parser.InvalidIdEnteredException;
import cookiejar.exception.parser.InvalidNameEnteredException;

//@author A0088447N

/**
 * This abstract class represents Parser component. It is used to extract
 * information of command types and events from user commands. It provides
 * necessary methods for all other extended parsers.
 * 
 * @author Tien
 * 
 */
public abstract class Parser {
	protected Event newEvent;
	protected EventHashTagsParser eventHashTagsParser;
	protected EventTimeParser eventTimeParser;
	protected EventNameParser eventNameParser;

	public Parser() {
		eventHashTagsParser = new EventHashTagsParser();
		eventTimeParser = new EventTimeParser();
		eventNameParser = new EventNameParser();
	}

	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform
	 * 
	 * @param userCommand
	 *            is the command from user
	 * @return COMMAND_TYPE of the command
	 */
	public static COMMAND_TYPE parseCommand(String userCommand) {
		String commandWord = StringTools.getFirstWord(userCommand.trim());
		int numOfWords = StringTools.countWords(userCommand);
		COMMAND_TYPE commandType = getCommandType(commandWord);
		if (numOfWords < getNumOfWordsNeeded(commandType)) {
			return COMMAND_TYPE.INCOMPLETE;
		} else {
			return commandType;
		}
	}

	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform
	 * 
	 * @param commandWord
	 *            is the first word of user command
	 * @return COMMAND_TYPE of the word
	 */
	private static COMMAND_TYPE getCommandType(String commandWord) {
		try {
			return COMMAND_TYPE.valueOf(commandWord.toUpperCase());
		} catch (IllegalArgumentException e) {
			for (COMMAND_TYPE commandType : COMMAND_TYPE.values()) {
				String commandTypeString = commandType.toString();
				if (commandTypeString.contains(commandWord.toUpperCase())) {
					return COMMAND_TYPE.INCOMPLETE;
				}
			}
			return COMMAND_TYPE.INVALID;
		}
	}

	/**
	 * This operation returns the minimum number of words needed for a
	 * COMMAND_TYPE to be completed.
	 * 
	 * @param commandType
	 *            is a COMMAND_TYPE supported
	 * @return minimum number of words
	 */
	private static int getNumOfWordsNeeded(COMMAND_TYPE commandType) {
		switch (commandType) {
		case ADD:
		case DELETE:
		case EDIT:
		case UNDONE:
		case DONE:
			return 2;

		default:
			return 1;
		}
	}

	/**
	 * Abstract operation to get all information of event from the user command
	 * The method will vary with different types of parser
	 * 
	 * @param userCommand
	 *            is a command from user
	 * @return new event
	 * @throws InvalidNameEnteredException
	 * @throws InvalidIdEnteredException
	 */
	public abstract Event getEventFromString(String userCommand)
			throws InvalidNameEnteredException, InvalidIdEnteredException;
	
	
	protected void resetEvent() {
		newEvent = new Event();
	}

	/**
	 * This operation parse and remove hash tags information from a string, at
	 * the same time update the Event object associated with the parser
	 * 
	 * @param infoString
	 *            is the information part of a command
	 * @return remaining string
	 */
	protected String tryParseHashTags(String infoString) {
		assert (infoString == null);

		String newInfoString = eventHashTagsParser.extractHashTagsFromString(infoString);
		Vector<String> tagList = eventHashTagsParser.getResultHashTags();
		setHashTags(tagList);
		
		return newInfoString;
	}
	
	
	/**
	 * Set a hash tag list for the event
	 * @param tagList
	 */
	private void setHashTags(Vector<String> tagList) {
		newEvent.setHashTags(tagList);
	}

	/**
	 * This operation parse and remove date time information from a string, at
	 * the same time update the Event object associated with the parser
	 * 
	 * @param infoString
	 *            is the information part of a command
	 * @return remaining string
	 */
	protected String tryParseTime(String infoString) {
		assert (infoString == null);

		String newInfoString = eventTimeParser.extractTimeFromString(infoString);
		TimePair timePair = eventTimeParser.getResultTimePair();
		setTime(timePair);
		
		return newInfoString;
	}
	
	/**
	 * Set the start time and end time for the event
	 * @param timePair
	 */
	private void setTime(TimePair timePair){
		newEvent.setStartTime(timePair.getFirst());
		newEvent.setEndTime(timePair.getSecond());
	}

	/**
	 * This operation parse and remove event's name information from a string,
	 * at the same time update the Event object associated with the parser This
	 * method should be called after <code>tryParseHashTags</code> and
	 * <code>tryParseTime</code>.
	 * 
	 * @param infoString
	 *            is the information part of a command
	 * @return remaining string
	 */
	protected String tryParseName(String infoString) {
		assert (infoString == null);

		String eventName = eventNameParser.getEventNameFromString(infoString);
		setName(eventName);

		return ParserConstants.EMPTY_STRING;
	}
	
	/**
	 * Set the name for the event
	 * @param eventName
	 */
	private void setName(String eventName){
		newEvent.setName(eventName);
	}
}
