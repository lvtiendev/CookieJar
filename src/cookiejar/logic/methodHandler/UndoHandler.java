//@author A0081223U
package cookiejar.logic.methodHandler;

import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.storage.StorageManager;

public class UndoHandler extends Handler{
	private static final String MESSAGE_SUCCESS_REDO = "";
	private static final String MESSAGE_FAILURE_REDO = "Undo unsuccessful: %1$s";
	private static final String MESSAGE_STACK_EMPTY = "There is no need to undo your life";
	private static final String MESSAGE_INSTANT = "";
	private String message_success_handle = "Nothing";
	private String message_failure_handle = "Nothing";
	
	private Vector<Event> event_list = new Vector<Event>();
	private Vector<Event> instant_event_list = new Vector<Event>();
	private StorageManager store;
	
	public UndoHandler(StorageManager store){
		this.store = store;
	}
	
	@Override
	public boolean execute(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		if(handle_undo.isEmpty()){
			event_list.clear();
			message_failure_handle = MESSAGE_STACK_EMPTY;
			return false;
		}
		
		Handler handle = handle_undo.pop();
		handle_redo.push(handle);
		boolean success = handle.undo();
		if(success){
			event_list = handle.getResult();
			message_success_handle = handle.getUndoNotification();
			return true;
		}else{
			message_failure_handle = String.format(MESSAGE_FAILURE_REDO,handle.getFailureNotification());
			return false;
		}
	}
	
	@Override
	public boolean execute() {
		return false;
	}
	
	@Override
	public boolean executeInstant(Stack<Handler> handle_undo, Stack<Handler> handle_redo){
		if(handle_undo.isEmpty()){
			event_list.clear();
			message_failure_handle = MESSAGE_STACK_EMPTY;
			return false;
		}
		
		Handler handle = handle_undo.peek();
		instant_event_list = handle.getInstantUndo();
		return true;
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
