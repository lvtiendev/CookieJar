//@author A0059827N
package cookiejar.gui;

/**
 * This class is a data structure which contains the name and tags of
 * an event. This data structure is designed to pass the event data
 * to a call of the table model of the <code>rowsTable</code> in the 
 * <code>ResultWindow</code>.
 */
public class EventNameAndTagsCellData {
	private String eventName;
	private String eventTags;
	
	/**
	 * Create the data structure.
	 */
	public EventNameAndTagsCellData() {
		eventName = "";
		eventTags = "";
	}
	
	/**
	 * Creates the data structure with data values.
	 * 
	 * @param eventName
	 *            the name of the event
	 * @param eventTags
	 *            the tags of the event as one string
	 */
	public EventNameAndTagsCellData(String eventName, String eventTags) {
		this.eventName = eventName;
		this.eventTags = eventTags;
	}
	
	/**
	 * Get the name of the event.
	 * 
	 * @return the name of the event.
	 */
	public String getEventName() {
		return eventName;
	}
	
	/**
	 * Gets the tags of the event as one string.
	 * 
	 * @return the tags of the event in a string.
	 */
	public String getEventTags() {
		return eventTags;
	}
}