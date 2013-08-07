//@author A0081223U
package cookiejar.logic.methodHandler;

import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.storage.StorageManager;

public class AddHandler extends Handler{
	private static final String MESSAGE_SUCCESS_ADD = "Added : %1$s";
	private static final String MESSAGE_FAILURE_ADD = "%1$s has failed to be added";
	private static final String MESSAGE_INSTANT = "";
	private static final String MESSAGE_UNDO = "Deleted : %1$s";
	private static final int TASK_ID_NULL = 0;
	private Event event;
	private Stack<Handler> handle_undo = null;
	private Vector<Event> event_list = new Vector<Event>();
	private String user_input;
	private StorageManager store;
	
	public AddHandler(Event event, String user_input, StorageManager store){
		this.event = event;
		this.user_input = user_input;
		this.store = store;
		event_list.add(event);
	}

	@Override
	public boolean execute() {
		try{
			if(event.getID()!=TASK_ID_NULL){
				//Case when this redoing an undone add. It keeps the original ID of the event
				event = store.insert(event,event.getID());
			}else{
				event = store.insert(event,TASK_ID_NULL);
			}
			return true;
		}catch(IOException e){
			return false;
		}
	}
	
	@Override
	public boolean execute(Stack<Handler> handle_undo,Stack<Handler> handle_redo) {
		boolean success = false;
		if(success = execute()){
			handle_redo.clear();
			handle_undo.push(this);
		}
		return success;
	}
	
	@Override
	public boolean executeInstant(Stack<Handler> handle_undo,Stack<Handler> handle_redo){
		return true;
	}
	
	@Override
	public Vector<Event> getResult() {
		event.setDisplayCode(Result.DISPLAY_CODE_ADD);
		return event_list;
	}

	@Override
	public boolean undo(){
		try{
			store.drop(event);
			event_list.clear();
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getSuccessNotification() {
		return String.format(MESSAGE_SUCCESS_ADD,event.getName());
	}

	@Override
	public String getFailureNotification() {
		return String.format(MESSAGE_FAILURE_ADD,event.getName());
	}
	
	public Event getEvent(){
		return this.event;
	}

	@Override
	public Vector<Event> getInstantResult() {
		event.setDisplayCode(Result.DISPLAY_CODE_ADD);
		return event_list;
	}

	@Override
	public String getInstantNotification() {
		return MESSAGE_INSTANT;
	}

	@Override
	public int getFocusedID() {
		return event.getID();
	}

	@Override
	public Vector<Event> getInstantUndo() {
		event.setDisplayCode(Result.DISPLAY_CODE_DELETE);
		return event_list;
	}

	@Override
	public String getUndoNotification() {
		return String.format(MESSAGE_UNDO,event.getName());
	}


}
