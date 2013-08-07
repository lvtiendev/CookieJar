//@author A0082927
package cookiejar.storage;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import cookiejar.common.Event;

/**
 * Stores and retrieves all events in a HashMap
 */

public class HashTagsList {

	private HashMap<String, Vector<Event>> list;

	public HashTagsList() {
		this.list = new HashMap<String, Vector<Event>>();
	}

	public HashTagsList(HashMap<String, Vector<Event>> list) {
		this.list = list;
	}

	/**
	 * This method adds the event into the HashMap
	 * 
	 * @param event
	 */
	public void update(Event event) {
		Vector<String> hashTags = event.getHashTags();

		for (String tag : hashTags) {
			if (list.containsKey(tag)) {
				list.get(tag).add(event);
			} else {
				Vector<Event> events = new Vector<Event>();
				events.add(event);
				list.put(tag, events);
			}
		}
	}

	/**
	 * This method removes the event from the HashMap
	 * 
	 * @param event
	 */
	public void erase(Event event) {
		Vector<String> hashTags = event.getHashTags();
		for(String tag : hashTags) {
			if (list.containsKey(tag)) {
				list.get(tag).remove(event);
			}
		}
	}

	/**
	 * This method searches for a list of events with a particular hashTag in the HashMap
	 * 
	 * @param hashTag
	 * @return Vector of events
	 */
	public Vector<Event> search(String hashTag) {
		Vector<Event> searchResult = new Vector<Event>();
		Vector<Event> tempResult = new Vector<Event>();

		Set<String> keySet = list.keySet();

		for (String existingTag: keySet) {
			if (existingTag.contains(hashTag)) {
				tempResult.addAll(list.get(existingTag));
			}
		}

		for (int i = 0; i < tempResult.size(); i++) {
			Event event1 = tempResult.get(i);
			boolean hasNoDuplicate = true;

			for (int j = i + 1; j < tempResult.size(); j++) {
				Event event2 = tempResult.get(j);

				if (event1.getID() == event2.getID()) {
					hasNoDuplicate = false;
				}
			}

			if (hasNoDuplicate) {
				searchResult.add(event1);
			}
		}

		return searchResult;
	}

	public HashMap<String, Vector<Event>> getList() {
		return list;
	}

}
