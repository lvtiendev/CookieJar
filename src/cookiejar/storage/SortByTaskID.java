//@author a0082927
package cookiejar.storage;

import java.util.Comparator;

import cookiejar.common.Event;

/**
 * Comparator to compare Event TaskID
 */

class SortByTaskID implements Comparator<Event> 
{
	public int compare(Event event1, Event event2) 
	{
		Integer id1 = event1.getID();
		Integer id2 = event2.getID();
		return id1.compareTo(id2);
	}
}