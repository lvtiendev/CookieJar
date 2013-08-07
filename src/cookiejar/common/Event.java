//@author A0082927
package cookiejar.common;

import java.util.Collections;
import java.util.Vector;

import org.joda.time.DateTime;

/**
 * Event class Used to represent a event
 * 
 * The Event object is mutable
 */

public class Event implements Comparable<Event>{
	private int ID;
	private int displayCode;
	private String name;
	private DateTime startTime;
	private DateTime endTime;
	private Vector<String> hashTags;

	public static final int TASK_ID_NULL = 0;
	private static final String EMPTY_STRING = "";

	private static final String TO_STRING_FORMAT = "Event information: \n"
			+ "Event ID: %s\n" + "Event Name: %s\n"
			+ "Event startTime: %s\n" + "Event endTime: %s\n"
			+ "Event Hash Tags List: %s\n";

	/**
	 * Default Event constructor without any parameters.
	 */
	public Event(){
		this.ID = TASK_ID_NULL;
		this.name = EMPTY_STRING;
		this.startTime = null;
		this.endTime = null;
		this.hashTags = new Vector<String>();
		this.displayCode = Result.DISPLAY_CODE_NULL;
	}

	/**
	 * Event Constructor to initialize the Event Object
	 * 
	 * @param name
	 *            : the name of the event
	 * @param startTime
	 *            : the starting time of the event, can be null
	 * @param endTime
	 *            : the ending time of the event, can be null
	 * @param hashTags
	 *            : the list of hash tags of the event
	 */
	public Event(int ID, String name, DateTime startTime, 
			DateTime endTime, Vector<String> hashTags) {
		this.ID = ID;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.hashTags = hashTags;
		this.displayCode = Result.DISPLAY_CODE_NULL;
	}

	/**
	 * This method updates the ID of the event
	 * 
	 * @param ID
	 */
	public void setID(int ID) {
		this.ID = ID;
	}

	/**
	 * This method updates the name of the event
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method updates the start time of the event
	 * 
	 * @param startTime
	 */
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	/**
	 * This method updates the end time of the event
	 * 
	 * @param endTime
	 */
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	/**
	 * This method updates the list of hash tags of the event
	 * 
	 * @param hashTags
	 */
	public void setHashTags(Vector<String> hashTags) {
		this.hashTags = hashTags;
	}

	public void setDisplayCode(int displayCode){
		this.displayCode = displayCode;
	}

	/**
	 * This method gets the ID of the event
	 * 
	 * @return ID
	 */
	public int getID() {
		return this.ID;
	}

	/**
	 * This method gets the name of the event
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method gets the start time of the event
	 * 
	 * @return startTime
	 */
	public DateTime getStartTime() {
		return this.startTime;
	}

	/**
	 * This method gets the end time of the event
	 * 
	 * @return endTime
	 */
	public DateTime getEndTime() {
		return this.endTime;
	}

	/**
	 * This method gets the list of hash tags associated to the event
	 * 
	 * @return Vector of hashTags
	 */
	public Vector<String> getHashTags() {
		return this.hashTags;
	}

	/**
	 * This method returns the string format of the event
	 * 
	 * @return the string contains information about the event
	 */
	public String toString() {
		return String.format(TO_STRING_FORMAT, this.ID, this.name,
				this.getStartTimeToString(), this.getEndTimeToString(),
				this.getHashTagsAsString());
	}

	public String getStartTimeToString() {
		if (this.startTime == null) {
			return EMPTY_STRING;
		} else {
			return this.startTime.toString();
		}
	}

	public String getEndTimeToString() {
		if (this.endTime == null) {
			return EMPTY_STRING;
		} else {
			return this.endTime.toString();
		}
	}

	/**
	 * This method returns the list of hash tags of the event as a string
	 * The string is used in the toString method
	 * 
	 * @return a string which contains all the tags of the event
	 */
	public String getHashTagsAsString() {
		if (this.hashTags == null) {
			return EMPTY_STRING;
		} else {
			Collections.sort(hashTags);
			StringBuilder hashTagsList = new StringBuilder();
			for (String tag : this.hashTags) {
				hashTagsList.append(" ");
				hashTagsList.append(tag);
			}
			return hashTagsList.toString().trim();
		}
	}

	public int getDisplayCode(){
		return displayCode;
	}

	@Override
	public int compareTo(Event other) {

		if (other.getID() == this.ID) {
			if (this.name.equals(other.getName()) 
					&& isSameTime(this.startTime, other.getStartTime()) 
					&& isSameTime(this.endTime, other.getEndTime()) 
					&& this.hashTags.equals(other.getHashTags())) {
				return 0;
			} else {
				return -1;
			}
		} else if (other.getID() == TASK_ID_NULL) {
			return -1;
		} else if (other.getID() < this.ID) {
			return -1;
		} else {
			return 1;
		}
	}

	public boolean equals(Event other) {
		return this.compareTo(other) == 0;
	}

	public static boolean isSameTime(DateTime dt1, DateTime dt2) {
		if (dt1 == null || dt2 == null) {
			if (dt1 == null && dt2 == null) {
				return true;
			}
			else {
				return false;
			}
		} else {
			return dt1.equals(dt2);
		}
	}

	/**
	 * Clones a event
	 * 
	 * @return clone of the event
	 */
	public Event clone() {
		return new Event(ID, name, startTime, endTime, cloneHashTags());
	}
	
	/**
	 * Clones a list of hash tags
	 * 
	 * @return clone of the list of hash tags
	 */
	public Vector<String> cloneHashTags() {
		
		@SuppressWarnings("unchecked")
		Vector<String> clonedHashTags = (Vector<String>) this.hashTags.clone();
		
		return clonedHashTags;	
	}
}