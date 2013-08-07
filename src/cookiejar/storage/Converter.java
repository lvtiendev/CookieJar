//@author A0082927
package cookiejar.storage;

import java.util.Vector;

import org.joda.time.DateTime;

import cookiejar.common.Event;
import cookiejar.exception.storage.InvalidFileContentException;

public class Converter {

	/**
	 * Event Class to String Class converter.
	 * This method is called when events are written to file
	 * 
	 * @param event
	 * @return string associated with the converted event
	 */
	public String eventObjectToString(Event event) {
		int taskID;
		String name;
		String startTime;
		String endTime;
		String hashTags;

		taskID = event.getID();

		if (event.getName().equals(StorageConstants.EMPTY_STRING)) {
			name = StorageConstants.EVENT_FIELD_EMPTY;
		} else {
			name = event.getName();
		}

		if (event.getStartTime() == null) {
			startTime = StorageConstants.EVENT_FIELD_EMPTY;
		} else {
			startTime = event.getStartTimeToString();
		}

		if (event.getEndTime() == null) {
			endTime = StorageConstants.EVENT_FIELD_EMPTY;
		} else {
			endTime = event.getEndTimeToString();
		}

		if (event.getHashTags() == null) {
			hashTags = StorageConstants.EVENT_FIELD_EMPTY;
		} else {
			hashTags = flattenHashTags(event.getHashTags());
		}

		String eventString = new String();

		eventString = taskID + StorageConstants.EVENT_FIELD_DELIMITER + name + StorageConstants.EVENT_FIELD_DELIMITER 
				+ startTime + StorageConstants.EVENT_FIELD_DELIMITER + endTime + StorageConstants.EVENT_FIELD_DELIMITER
				+ hashTags;

		return eventString;
	}

	/**
	 * String Class to Event Class converter.
	 * This method is called when stored events are loaded from file
	 * 
	 * @param eventInString
	 * @return event associated with the string
	 * @throws InvalidFileContentException 
	 */
	public Event stringToEventObject(String eventInString) throws InvalidFileContentException {
		String[] eventFieldsInString = eventInString.trim().split(StorageConstants.EVENT_FIELD_DELIMITER);

		if (eventFieldsInString.length != 5) {
			throw new InvalidFileContentException();
		}

		for (int i = 0; i < eventFieldsInString.length; ++i) {
			if (eventFieldsInString[i].equals(StorageConstants.EVENT_FIELD_EMPTY)) {
				eventFieldsInString[i] = StorageConstants.EMPTY_STRING;
			}
		}

		int taskID = Integer.parseInt(eventFieldsInString[0]);

		String name = eventFieldsInString[1];

		DateTime startTime = null;
		if (eventFieldsInString[2].equals(StorageConstants.EMPTY_STRING)) {
			startTime = null;
		} else {
			startTime = new DateTime(eventFieldsInString[2]);
		}

		DateTime endTime = null;
		if (eventFieldsInString[3].equals(StorageConstants.EMPTY_STRING)) {
			endTime = null;
		} else {
			endTime = new DateTime(eventFieldsInString[3]);
		}

		Vector<String> hashTags = new Vector<String>();
		if (eventFieldsInString[4].equals(StorageConstants.EMPTY_STRING)) {
			hashTags = new Vector<String>();
		} else {
			hashTags = expandHashTags(eventFieldsInString[4]);
		}


		return new Event(taskID, name, startTime, endTime, hashTags);
	}

	/**
	 * List of hashtags to String converter.
	 * 
	 * @param vector of hashtags
	 * @return string containing all the hashtags
	 */
	public String flattenHashTags(Vector<String> hashTags) {
		StringBuilder hashTagsInString = new StringBuilder();

		if (hashTags.isEmpty()) {
			return StorageConstants.EVENT_FIELD_EMPTY;
		} else {
			for (String hashTag : hashTags) {
				hashTagsInString.append(hashTag);
			}

			return hashTagsInString.toString();
		}
	}

	/**
	 * String to list of hashtags converter.
	 * 
	 * @param string containing all the hashtags
	 * @return vector of hashtags
	 */
	public Vector<String> expandHashTags(String hashTagsInString) {
		Vector<String> hashTags = new Vector<String>();
		if (hashTagsInString.equals(StorageConstants.EMPTY_STRING)) {
			return hashTags;
		} else {
			String[] hashTagsInStringArray = hashTagsInString.trim().split(StorageConstants.EVENT_FIELD_HASH);

			for (String hashTag : hashTagsInStringArray) {
				if (hashTag.equals(StorageConstants.EMPTY_STRING)) {
					continue;
				}
				hashTags.add(StorageConstants.EVENT_FIELD_HASH + hashTag);
			}

			return hashTags;
		}
	}

}
