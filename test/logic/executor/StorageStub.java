package logic.executor;

import java.io.IOException;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.exception.storage.InvalidFileContentException;
import cookiejar.storage.StorageManager;

public class StorageStub extends StorageManager{
	private Event lastInserted;
	private Event lastDropped;
	private Event lastUpdated;
	public static int DONE_EVENT_ID;

	public StorageStub() throws IOException, InvalidFileContentException {
		super();
	}
	
	@Override
	public Event insert(Event newEvent, int taskID) throws IOException {
		newEvent.setID(taskID);
		lastInserted = newEvent;
		return newEvent;
	}
	
	public Event getLastInserted(){
		return lastInserted;
	}
	
	@Override
	public void update(Event newEvent) throws IOException {
		lastUpdated = newEvent;
	}
	
	public Event getLastUpdated(){
		return lastUpdated;
	}
	
	@Override
	public void drop(Event eventToBeDropped) throws IOException {
		lastDropped = eventToBeDropped;
	}
	
	public Event getLastDropped(){
		return lastDropped;
	}
//	
//	@Override
//	public Vector<Event> select() {
//		
//	}
//	
	@Override
	public Vector<Event> select(int taskID) {
		Vector<Event> event_list = new Vector<Event>();
		if(taskID==DONE_EVENT_ID){
			Event event = new EventStub();
			Vector<String> done = new Vector<String>();
			done.add("#done");
			event.setHashTags(done);
			event_list.add(event);
		}else{
			event_list.add(new EventStub());
		}
		return event_list;
	}
//	
//	@Override
//	public Vector<Event> select(String keyWord) {
//		
//	}
//	
//	@Override
//	public Vector<Event> select(DateTime startTimeToBeFound, DateTime endTimeToBeFound) {
//		
//	}
//	
//	@Override
//	public Vector<Event> selectTag(String tag) {
//		
//	}
	
}
