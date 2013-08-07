//@author A0081223U
package cookiejar.logic.methodHandler;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.exception.parser.IncompleteCommandException;
import cookiejar.storage.StorageManager;

public class EditHandler extends Handler {
	private static final String MESSAGE_EVENT_NOT_FOUND = "Have you entered the wrong ID?";
	private static final String MESSAGE_SUCCESS_EDIT = "Edited : %1$s";
	private static final String MESSAGE_FAILURE_EDIT = "%1$s cannot be edited";
	private static final String MESSAGE_INSTANT = "";
	
	private Event event;
	private Event old_event;
	private Vector<Event> event_list = new Vector<Event>();
	private Vector<Event> event_list_undo = new Vector<Event>(); //just a reversed mirror of event_list;
	private String user_input;
	private StorageManager store;
	private DisplayHandler lastDisplayed;
	private boolean hasParameters;

	public EditHandler(Event event, String user_input, StorageManager store, DisplayHandler lastDisplayed) throws IncompleteCommandException {
		this.user_input = user_input;
		this.store = store;
		this.lastDisplayed = lastDisplayed;
		this.hasParameters = checkParams(event);
		if (hasID(event)) {
			initEventList(event);
		} else {
			throw new IncompleteCommandException();
		}
	}

	private boolean hasID(Event event) {
		if (event.getID() == Event.TASK_ID_NULL || event.getID()<0) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean checkParams(Event event){
		if (event.getStartTime()==null && event.getHashTags().size()==0 
				&& event.getEndTime()==null && event.getName().equals("")){
			return false;
		} else {
			return true;
		}
	}

	private void initEventList(Event event) throws IncompleteCommandException {
		try {
			this.old_event = getEventWithID(event.getID());
			this.event = mergeEvent(old_event, event);
			event_list.add(this.event);
			event_list.add(this.old_event);
			event_list_undo.add(this.old_event);
			event_list_undo.add(this.event);
		} catch (IOException e) {
			throw new IncompleteCommandException();
		}
	}

	@Override
	public boolean execute(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		if (execute()) {
			handle_redo.clear();
			handle_undo.push(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean execute() {
		try {
			store.update(event);
			return true;
		} catch (IOException e) {
			return false;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public boolean executeInstant(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		return true;
	}

	private Event mergeEvent(Event old_event, Event event) {
		// Overwrite properties from old_event into blank properties from event
		if (event.getEndTime() == null) {
			event.setEndTime(old_event.getEndTime());
		}
		if (event.getStartTime() == null) {
			event.setStartTime(old_event.getStartTime());
		}
		if (event.getName() == null || event.getName().trim().equals("")) {
			event.setName(old_event.getName());
		}
		if (event.getHashTags() == null || event.getHashTags().size() == 0) {
			event.setHashTags(old_event.getHashTags());
		}
		if (event.getHashTags().size() == 1
				&& event.getHashTags().get(0).equals("#")) {
			event.setHashTags(new Vector<String>());
		}
		return event;
	}

	private Event getEventWithID(int TaskID) throws IOException {
		Vector<Event> selected = store.select(TaskID);
		if (selected != null && selected.size() != 0) {
			return selected.get(0);
		} else {
			throw new IOException(
					String.format(MESSAGE_EVENT_NOT_FOUND, TaskID));
		}
	}

	@Override
	public Vector<Event> getResult() {
		return event_list;
	}

	@Override
	public boolean undo() {
		event.setDisplayCode(0);
		old_event.setDisplayCode(0);
		try {
			store.update(old_event);
			event_list.clear();
			event_list.add(event);
			event_list.add(old_event);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public String getSuccessNotification() {
		return String.format(MESSAGE_SUCCESS_EDIT, event.getName());
	}

	@Override
	public String getFailureNotification() {
		if (event_list == null || event_list.size() == 0) {
			return MESSAGE_EVENT_NOT_FOUND;
		} else {
			return String.format(MESSAGE_FAILURE_EDIT, old_event.getName());
		}
	}

	public Event getEvent() {
		return this.event;
	}

	@Override
	public String getInstantNotification() {
		return MESSAGE_INSTANT;
	}

	@Override
	public Vector<Event> getInstantResult() {
		if (hasParameters) {
			old_event.setDisplayCode(Result.DISPLAY_CODE_DELETE);
			event.setDisplayCode(Result.DISPLAY_CODE_ADD);
			return event_list;
		} else {
			return lastDisplayed.getInstantResult();
		}
	}

	@Override
	public int getFocusedID() {
		if (hasID(event)) {
			return event.getID();
		} else {
			return Result.EVENT_ID_NO_FOCUS;
		}
	}

	@Override
	public Vector<Event> getInstantUndo() {
		event.setDisplayCode(Result.DISPLAY_CODE_DELETE);
		old_event.setDisplayCode(Result.DISPLAY_CODE_ADD);
		return event_list_undo;
	}

	@Override
	public String getUndoNotification() {
		return String.format(MESSAGE_SUCCESS_EDIT, old_event.getName());
	}

}
