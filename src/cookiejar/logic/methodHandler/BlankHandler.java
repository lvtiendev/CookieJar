//@author A0081223U
package cookiejar.logic.methodHandler;

import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;

public class BlankHandler extends Handler{

	@Override
	public boolean execute() {
		return false;
	}

	@Override
	public Vector<Event> getResult() {
		return null;
	}

	@Override
	public boolean undo() {
		return false;
		
	}

	@Override
	public boolean execute(Stack<Handler> handle_undo,Stack<Handler> handle_redo) {
		return false;
	}
	
	@Override
	public boolean executeInstant(Stack<Handler> handle_undo,Stack<Handler> handle_redo){
		return false;
	}

	@Override
	public String getSuccessNotification() {
		return "";
	}

	@Override
	public String getFailureNotification() {
		return "";
	}

	@Override
	public Vector<Event> getInstantResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstantNotification() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFocusedID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector<Event> getInstantUndo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUndoNotification() {
		// TODO Auto-generated method stub
		return null;
	}

}
