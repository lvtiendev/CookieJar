//@author A0081223U
package cookiejar.logic.methodHandler;

import java.util.Stack;
import java.util.Vector;

import org.joda.time.DateTime;

import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.logic.Executor;
import cookiejar.logic.parser.ParserConstants;
import cookiejar.storage.StorageManager;

public class DisplayHandler extends Handler {
	private static final String EMPTY_STRING = "";
	private static final String MESSAGE_SUCCESS_DISPLAY_SINGLE = "Showing %1$s task";
	private static final String MESSAGE_SUCCESS_DISPLAY_MULTIPLE = "Showing %1$s tasks";
	private static final String MESSAGE_FAILURE_DISPLAY = "No events found";
	private static final String MESSAGE_INSTANT = "";
	private Event event;
	private Vector<Event> event_list = null;
	private String user_input;
	private StorageManager store;
	private static final int TASK_ID_NULL = 0;

	/**
	 * Function to generate DisplayHandler that displays all results
	 * 
	 * @return DisplayHandler
	 */
	public static DisplayHandler getDisplayAll(StorageManager store) {
		return new DisplayHandler(new Event(TASK_ID_NULL, "", ParserConstants.DATE_TIME_MIN, ParserConstants.DATE_TIME_MAX, new Vector<String>()), "", store);
	}

	public DisplayHandler(Event event, String user_input, StorageManager store) {
		this.event = event;
		this.user_input = user_input;
		this.store = store;
	}

	@Override
	public boolean execute(Stack<Handler> handle_undo,Stack<Handler> handle_redo) {
		return execute();
	}

	@Override
	public boolean execute() {
		event_list = selectAndUnion();
		return true;
	}

	@Override
	public boolean executeInstant(Stack<Handler> handle_undo,Stack<Handler> handle_redo) {
		return execute();
	}

	private Vector<Event> selectAndUnion() {
		Vector<Event> event_list_id = new Vector<Event>();

		Vector<Event> event_list_tags = new Vector<Event>();
		if (!event.getHashTags().isEmpty()) {
			for (String tag : event.getHashTags()) {
				event_list_tags = union(store.selectTag(tag), event_list_tags);
			}
		}

		Vector<Event> event_list_time = store.select(event.getStartTime(),
				event.getEndTime());

		// For Display all command
		Vector<Event> event_list_all = new Vector<Event>();
		boolean displayAll = (event.getID() == TASK_ID_NULL 
				&& event.getHashTags().isEmpty() 
				&& event.getName().equals("") 
				&& event.getStartTime() == ParserConstants.DATE_TIME_MIN
				&& event.getEndTime() == ParserConstants.DATE_TIME_MAX);
		if (displayAll) {
			event_list_all = store.select();
		}

		Vector<Event> event_list_keyword = new Vector<Event>();
		if (!event.getName().equals(EMPTY_STRING)) {
			event_list_keyword = store.select(event.getName());
		}

		// Need to add a select that selects TaskID
		event_list_tags = union(event_list_keyword, event_list_tags);
		event_list_tags = union(event_list_tags, event_list_all);
		event_list_tags = union(event_list_tags, event_list_time);
		event_list_tags = union(event_list_tags, event_list_id);
		event_list_tags = sortByTime(event_list_tags);
		event_list_tags = pushDoneToRear(event_list_tags);
		return event_list_tags;
	}

	private Vector<Event> sortByTime(Vector<Event> event_list) {
		Vector<Event> temp = new Vector<Event>();
		for (int i = 0; i < event_list.size(); i++) {
			int insert_ind = temp.size();
			Event event = event_list.get(i);
			for (int j = 0; j < temp.size(); j++) {
				if (beforeThan(event, temp.get(j))) {
					insert_ind = j;
					break;
				}
			}
			temp.add(insert_ind, event);
		}
		return temp;
	}

	private boolean beforeThan(Event e1, Event e2) {
		if (e1.getEndTime() == null) {
			return false;
		}
		if (e2.getEndTime() == null) {
			return true;
		}
		DateTime e1_time = e1.getStartTime();
		if (e1.getStartTime() == null) {
			e1_time = e1.getEndTime();
		}
		DateTime e2_time = e2.getStartTime();
		if (e2.getStartTime() == null) {
			e2_time = e2.getEndTime();
		}

		if (e1_time.compareTo(e2_time) == -1) {
			return true;
		}
		return false;
	}

	private Vector<Event> pushDoneToRear(Vector<Event> event_list) {
		Vector<Event> temp = new Vector<Event>();
		Vector<Event> done = new Vector<Event>();
		for (Event event : event_list) {
			if (event.getHashTags().contains("#done")) {
				done.add(event);
			} else {
				temp.add(event);
			}
		}
		return union(temp, done);
	}

	/*
	 * Return the union of 2 lists with the intersection placed on top
	 */
	private Vector<Event> union(Vector<Event> event_list1,
			Vector<Event> event_list2) {
		if (event_list2 == null || event_list2.size() == 0) {
			return event_list1;
		}
		Vector<Event> priority = new Vector<Event>();
		priority.addAll(event_list1);
		priority.addAll(event_list2);
		priority = removeDuplicates(priority);
		return priority;
	}
	
	private boolean containsIn(Vector<Event> event_list, Event e){
		for(Event other : event_list){
			if(e.equals(other)){
				return true;
			}
		}
		return false;
	}
	
	private Vector<Event> removeDuplicates(Vector<Event> event_list){
		Vector<Event> temp = new Vector<Event>();
		for(Event e : event_list){
			if(!containsIn(temp,e)){
				temp.add(e);
			}
		}
		return temp;
	}

	@Override
	public Vector<Event> getResult() {
		Executor.assignDisplayCodes(event_list);
		return event_list;
	}

	@Override
	public boolean undo() {
		return true;
	}

	@Override
	public String getSuccessNotification() {
		if (event_list.size() <= 1) {
			return String.format(MESSAGE_SUCCESS_DISPLAY_SINGLE,event_list.size());
		} else {
			return String.format(MESSAGE_SUCCESS_DISPLAY_MULTIPLE,event_list.size());
		}
	}

	@Override
	public String getFailureNotification() {
		return MESSAGE_FAILURE_DISPLAY;
	}

	public Event getEvent() {
		return this.event;
	}

	@Override
	public Vector<Event> getInstantResult() {
		return getResult();
	}

	@Override
	public String getInstantNotification() {
		return MESSAGE_INSTANT;
	}

	@Override
	public int getFocusedID() {
		return Result.EVENT_ID_NO_FOCUS;
	}

	@Override
	public Vector<Event> getInstantUndo() {
		return null;
	}

	@Override
	public String getUndoNotification() {
		return null;
	}
}
