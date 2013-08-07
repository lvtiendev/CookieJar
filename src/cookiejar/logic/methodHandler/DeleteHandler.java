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

public class DeleteHandler extends Handler {
	private static final String MESSAGE_EVENT_NOT_FOUND = "Have you entered the wrong ID?";
	private static final String MESSAGE_SUCCESS_DELETE = "Deleted : %1$s";
	private static final String MESSAGE_FAILURE_DELETE = "%1$s cannot be deleted";
	private static final String MESSAGE_INSTANT = "";
	private static final String MESSAGE_UNDO = "Added : %1$s";
	private Vector<Event> event_list = new Vector<Event>();
	private Event event;
	private String user_input;
	private StorageManager store;
	private DisplayHandler lastDisplayed;
	private boolean hasParameters;

	public DeleteHandler(Event event, String user_input, StorageManager store,DisplayHandler lastDisplayed) throws IncompleteCommandException {
		this.user_input = user_input;
		this.store = store;
		this.lastDisplayed = lastDisplayed;
		this.hasParameters = checkParams(event);
		if (hasParameters) {
			initEventList(event);
		} else {
			throw new IncompleteCommandException();
		}
	}

	private boolean checkParams(Event event) {
		if (event.getID() == Event.TASK_ID_NULL || event.getID()<0) {
			return false;
		} else {
			return true;
		}
	}
	
	private void initEventList(Event event) throws IncompleteCommandException{
		try{
			this.event = getEventWithID(event.getID());
			event_list.add(this.event);
		} catch (IOException e) {
			throw new IncompleteCommandException();
		}
	}

	@Override
	public boolean execute(Stack<Handler> handle_undo,Stack<Handler> handle_redo) {
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
			store.drop(event);
			return true;
		} catch (IOException e) {
			return false;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public boolean executeInstant(Stack<Handler> handle_undo,Stack<Handler> handle_redo) {
		return true;
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
		try {
			store.insert(event, event.getID());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getSuccessNotification() {
		return String.format(MESSAGE_SUCCESS_DELETE, event.getName());
	}

	@Override
	public String getFailureNotification() {
		boolean eventNotFound = (event == null);
		if (eventNotFound) {
			return String.format(MESSAGE_EVENT_NOT_FOUND, event.getID());
		} else {
			return String.format(MESSAGE_FAILURE_DELETE, event.getName());
		}
	}

	public Event getEvent() {
		return this.event;
	}

	@Override
	public Vector<Event> getInstantResult() {
		return lastDisplayed.getInstantResult();
	}

	@Override
	public String getInstantNotification() {
		return MESSAGE_INSTANT;
	}

	@Override
	public int getFocusedID() {
		if (hasParameters) {
			return event.getID();
		} else {
			return Result.EVENT_ID_NO_FOCUS;
		}
	}

	@Override
	public Vector<Event> getInstantUndo() {
		event.setDisplayCode(Result.DISPLAY_CODE_ADD);
		return event_list;
	}

	@Override
	public String getUndoNotification() {
		return String.format(MESSAGE_UNDO, event.getName());
	}
}
