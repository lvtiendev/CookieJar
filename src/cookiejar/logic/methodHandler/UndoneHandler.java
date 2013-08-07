//@author A0081223U
package cookiejar.logic.methodHandler;

import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.exception.parser.IncompleteCommandException;
import cookiejar.logic.Executor;
import cookiejar.storage.StorageManager;

public class UndoneHandler extends Handler{
	private static final String MESSAGE_EVENT_NOT_FOUND = "Have you entered the wrong ID?";
	private static final String MESSAGE_SUCCESS_UNDONE = "Undone : %1$s";
	private static final String MESSAGE_UNDO = "Done : %1$s";
	private static final String MESSAGE_FAILURE_UNDONE = "You have already undone this";
	private static final String MESSAGE_INSTANT = "";
	private Vector<Event> event_list = new Vector<Event>();
	private DisplayHandler lastDisplayed;
	Event event;
	String user_input;
	StorageManager store;

	public UndoneHandler(Event event,String user_input,StorageManager store, DisplayHandler lastDisplayed) throws IncompleteCommandException{
		try{
			this.store = store;
			this.event = getEventWithID(event.getID());
			this.user_input = user_input;
			this.lastDisplayed = lastDisplayed;
			event_list.add(this.event);
		} catch (IOException e){
			throw new IncompleteCommandException();
		}
	}

	private Event getEventWithID(int TaskID) throws IOException{
		Vector<Event> selected = store.select(TaskID);
		if(selected!=null && selected.size()!=0){
			return selected.get(0);
		}else{
			throw new IOException(MESSAGE_EVENT_NOT_FOUND);
		}
	}

	@Override
	public Vector<Event> getResult() {
		Executor.assignDisplayCodes(event_list);
		return event_list;
	}

	@Override
	public String getSuccessNotification() {
		return String.format(MESSAGE_SUCCESS_UNDONE,event.getName());
	}

	@Override
	public String getFailureNotification() {
		return String.format(MESSAGE_FAILURE_UNDONE,event.getName());
	}

	@Override
	public String getInstantNotification() {
		return MESSAGE_INSTANT;
	}

	@Override
	public Vector<Event> getInstantResult() {
		return lastDisplayed.getInstantResult();
	}

	@Override
	public boolean undo() {
		try{
			event.getHashTags().add("#done");
			store.update(event);
			return true;
		} catch (IOException e){
			return false;
		}
	}

	@Override
	public Vector<Event> getInstantUndo() {
		Executor.assignDisplayCodes(event_list);
		return event_list;
	}

	@Override
	public int getFocusedID() {
		return event.getID();
	}

	@Override
	public boolean execute() {
		try{
			if(!event.getHashTags().contains("#done")){
				return false;
			}
			event.getHashTags().remove("#done");
			store.update(event);
			return true;
		} catch (IOException e){
			return false;
		}
	}

	@Override
	public boolean execute(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		if(execute()){
			handle_redo.clear();
			handle_undo.push(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean executeInstant(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		return true;
	}

	@Override
	public String getUndoNotification() {
		return String.format(MESSAGE_UNDO,event.getName());
	}

}
