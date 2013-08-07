package cookiejar.logic.parser;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;

//@author A0088447N

/**
 * Class helps to get date time information of event from a String
 * 
 * @author Tien
 * 
 */
public class EventTimeParser {
	// Library parser
	private com.joestelmach.natty.Parser nattyParser;

	// Special vector to keep track of all words "from"
	private Vector<WordTrio> trackWordList;

	// Date time information will be stored in a TimePair
	private TimePair timePair;

	public EventTimeParser() {
		nattyParser = new com.joestelmach.natty.Parser();
		trackWordList = new Vector<WordTrio>();
		timePair = new TimePair();
	}

	/**
	 * Return the TimePair associated. This operation needs to be called after
	 * <code>extractTimeFromString</code>
	 * 
	 * @return result TimePair
	 */
	public TimePair getResultTimePair() {
		return timePair.clone();
	}

	/**
	 * Remove all date time information from a String. At the same time, update
	 * <code>TimePair</code> with the these information
	 * 
	 * @param infoString
	 *            is information part of the user command.
	 * @return remaining string
	 */
	public String extractTimeFromString(String infoString) {
		infoString = removeWordFROM(infoString);
		timePair = new TimePair();

		List<DateGroup> dateGroupList = nattyParser.parse(infoString);

		// helps to check if the infoString contains date time information
		DateGroup selected = null;

		for (DateGroup dateGroup : dateGroupList) {
			// only parse if the information is one completed chunk of words
			if (isOneWordChunk(dateGroup.getText(), infoString)) {
				selected = dateGroup;
				if (dateGroup.getDates().size() == 1) {
					timePair = extractOneTime(dateGroup);
				} else {
					timePair = extractTwoTimes(dateGroup);
					break;
				}
			}
		}

		if (selected == null) {
			return infoString.trim();
		} else {
			String timeString = selected.getText();
			infoString = infoString.replaceAll(timeString, " ").trim();
			infoString = restoreWordFROM(infoString);
			return infoString.trim();
		}
	}

	/**
	 * This operation removes all words "from" in the string and keeps them in a
	 * another vector
	 * 
	 * @param infoString
	 *            is the original string
	 * @return remaining string
	 */
	private String removeWordFROM(String infoString) {
		// word "from"
		String word = ParserConstants.FROM;
		trackWordList.removeAllElements();

		Vector<String> wordList = copyWordsToVector(infoString);

		String result = "";
		for (int i = 1; i < wordList.size() - 1; i++) {
			if (wordList.get(i).equalsIgnoreCase(word)) {
				// keep track of the current word and 2 words around it
				trackWordList.add(new WordTrio(wordList.get(i - 1), wordList
						.get(i), wordList.get(i + 1)));
			} else {
				result += wordList.get(i) + " ";
			}
		}

		return result.trim();
	}

	/**
	 * This operation takes all words "from" being tracked and add them back to
	 * the string in correct positions if they are not part of the date time
	 * information.
	 * 
	 * @param infoString
	 *            is the original string
	 * @return updated string
	 */
	private String restoreWordFROM(String infoString) {
		Vector<String> wordList = copyWordsToVector(infoString);

		String result = "";
		for (int i = 0; i < wordList.size() - 1; i++) {
			result += wordList.get(i) + " ";
			// try to insert "from" back into this position
			for (int j = 0; j < trackWordList.size(); j++) {
				WordTrio tripleWords = trackWordList.get(j);
				// If two words aside are still the same as in original string
				if (tripleWords.getLeft().equalsIgnoreCase(wordList.get(i))
						&& (tripleWords.getRight().equalsIgnoreCase(wordList
								.get(i + 1)))) {
					// add back
					result += tripleWords.getMiddle() + " ";
					trackWordList.remove(j);
					break;
				}
			}
		}

		return result.trim();
	}

	/**
	 * This operation splits a string into words, copy them into a Vector of
	 * string, then add 2 blank strings at both ends.
	 * 
	 * @param infoString
	 *            is an input string
	 * @return result Vector of string
	 */
	private Vector<String> copyWordsToVector(String infoString) {
		String params[] = StringTools.splitParameters(infoString);

		Vector<String> wordList = new Vector<String>();
		wordList.add("");
		for (int i = 0; i < params.length; i++) {
			wordList.add(params[i]);
		}
		wordList.add("");
		return wordList;
	}

	/**
	 * This operation extracts only end date time information from a Joda
	 * DateGroup object. <br>
	 * If there is no exact time information, set time to 23:59
	 * 
	 * @param dateGroup
	 *            is input object
	 * @return result TimePair
	 */
	private TimePair extractOneTime(DateGroup dateGroup) {
		List<Date> listDate = dateGroup.getDates();

		DateTime endTime = new DateTime(listDate.get(0));
		if (!hasSpecifiedTime(endTime)) {
			endTime = DateTimeTools.setTimeEndDay(endTime);
		}

		return new TimePair(null, endTime);
	}

	/**
	 * This operation gets start time and end time information in a Joda
	 * DateGroup object as strings.
	 * 
	 * @param dateGroup
	 *            is a Joda DateGroup object
	 * @return two date time strings as an array
	 */
	private String[] getDateTimeString(DateGroup dateGroup) {
		String[] result = new String[2];
		String[] params = StringTools.splitParameters(dateGroup.getText());

		for (int i = 0; i < params.length; i++) {
			if (params[i].equalsIgnoreCase(ParserConstants.DATE_TIME_SPLIT)) {
				result[0] = StringTools.makeStringFromArray(params, 0, i);
				result[1] = StringTools.makeStringFromArray(params, i + 1, params.length);
				break;
			}
		}

		return result;
	}

	/**
	 * This operation checks if a input date time string has information of
	 * weekly days.
	 * 
	 * @param dateTimeString
	 *            is the string to check
	 * @return true if dateTimeString has information of weekly days, false
	 *         otherwise.
	 */
	private boolean isWeekDay(String dateTimeString) {
		String[] params = StringTools.splitParameters(dateTimeString);
		for (int i = 0; i < params.length; i++) {
			for (int j = 0; j < ParserConstants.WEEK_DAY_STRINGS.length; j++) {
				String weekDayString = ParserConstants.WEEK_DAY_STRINGS[j];
				if (params[i].equalsIgnoreCase(weekDayString)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This operation extracts both start time and end time information from a
	 * Joda DateGroup object. It also modifies the information to ensure that
	 * start time is before end time. <br>
	 * In case no exact time provided:
	 * <ul>
	 * <li>Start time will be set to 00:00</li>
	 * <li>End time will be set to 23:59</li>
	 * </ul>
	 * 
	 * @param dateGroup
	 *            is input object
	 * @return result TimePair
	 */
	private TimePair extractTwoTimes(DateGroup dateGroup) {
		List<Date> listDate = dateGroup.getDates();

		DateTime startTime = new DateTime(listDate.get(0));
		if (!hasSpecifiedTime(startTime)) {
			startTime = DateTimeTools.setTimeStartDay(startTime);
		}

		DateTime endTime = new DateTime(listDate.get(1));
		if (!hasSpecifiedTime(endTime)) {
			endTime = DateTimeTools.setTimeEndDay(endTime);
		}

		String[] dateTimeString = getDateTimeString(dateGroup);

		// Modify information in case end time is before start time
		if (endTime.isBefore(startTime)) {
			if (DateTimeTools.isSameDate(endTime, startTime)) {
				endTime = endTime.plusDays(1);
			} else if (isWeekDay(dateTimeString[1])) {
				endTime = endTime.plusDays(7);
			} else {
				endTime = endTime.plusYears(1);
			}
		}

		return new TimePair(startTime, endTime);
	}

	/**
	 * Check if the input has exact time information by comparing to current
	 * time.
	 * 
	 * @param dt
	 * @return true if the input has exact time information, false otherwise.
	 */
	private boolean hasSpecifiedTime(DateTime dt) {
		if (DateTimeTools.isAboutNow(dt)) {
			return true;
		}

		DateTime currentTime = new DateTime();
		return !(DateTimeTools.isSameTime(dt, currentTime));
	}

	/**
	 * Check a pattern string is one completed chunk of words in a text string.
	 * 
	 * @param pattern
	 * @param text
	 * @return true if the pattern is one chunk of words, false otherwise.
	 */
	private boolean isOneWordChunk(String pattern, String text) {
		pattern = " " + pattern.trim() + " ";
		text = " " + text.trim() + " ";
		return text.contains(pattern);
	}
}
