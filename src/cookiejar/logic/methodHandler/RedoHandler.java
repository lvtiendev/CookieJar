//@author A0081223U
package cookiejar.logic.methodHandler;

import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.storage.StorageManager;

public class RedoHandler extends Handler{
	private static final String MESSAGE_SUCCESS_REDO = "%1$s";
	private static final String MESSAGE_FAILURE_REDO = "Redo unsuccessful: %1$s";
	private static final String MESSAGE_STACK_EMPTY = "There is no need to redo your life";
	private static final String MESSAGE_INSTANT = "";
	private String message_success_handle = "Nothing";
	private String message_failure_handle = "Nothing";
	
	private Vector<Event> event_list = new Vector<Event>();
	private Vector<Event> instant_event_list = new Vector<Event>();
	private StorageManager store;
	
	
	public RedoHandler(StorageManager store){
		this.store = store;
	}
	
	@Override
	public boolean execute(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		if(handle_redo.isEmpty()){
			event_list.clear();
			message_failure_handle = MESSAGE_STACK_EMPTY;
			return false;
		}
		
		Handler handle = handle_redo.pop();
		boolean success = handle.execute();
		if(success){
			event_list = handle.getResult();
			message_success_handle = handle.getSuccessNotification();
			handle_undo.push(handle);
			return true;
		}else{
			message_failure_handle = String.format(MESSAGE_FAILURE_REDO,handle.getFailureNotification());
			return false;
		}
	}
	
	@Override
	public boolean executeInstant(Stack<Handler> handle_undo,Stack<Handler> handle_redo){
		if(handle_redo.isEmpty()){
			instant_event_list.clear();
			message_failure_handle = MESSAGE_STACK_EMPTY;
			return false;
		}
		
		Handler handle = handle_redo.peek();
		boolean success = handle.executeInstant(handle_undo,handle_redo);
		if(success){
			instant_event_list = handle.getInstantResult();
			message_success_handle = "";
			return true;
		}else{
			message_failure_handle = "";
			return false;
		}
	}
	
	@Override
	public boolean execute() {
		return false;
	}

	@Override
	public Vector<Event> getResult() {
		return event_list;
	}

	@Override
	public boolean undo() {
		return false;
	}

	@Override
	public String getSuccessNotification() {
		return message_success_handle;
	}

	@Override
	public String getFailureNotification() {
		return message_failure_handle;
	}

	@Override
	public Vector<Event> getInstantResult() {
		return instant_event_list;
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
