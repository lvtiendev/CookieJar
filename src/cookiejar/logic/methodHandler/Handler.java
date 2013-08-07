//@author A0081223U
package cookiejar.logic.methodHandler;

import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;

public abstract class Handler{
	public abstract Vector<Event> getResult();
	public abstract String getSuccessNotification();
	public abstract String getFailureNotification();
	public abstract String getInstantNotification();
	public abstract String getUndoNotification();
	public abstract Vector<Event> getInstantResult();
	public abstract boolean undo();
	public abstract Vector<Event> getInstantUndo();
	public abstract int getFocusedID();
	public abstract boolean execute();
	public abstract boolean execute(Stack<Handler> handle_undo,Stack<Handler> handle_redo);
	public abstract boolean executeInstant(Stack<Handler> handle_undo,Stack<Handler> handle_redo);
}
