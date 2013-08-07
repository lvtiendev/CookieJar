//@author A0081223U
package cookiejar.logic.methodHandler;

import java.util.Stack;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.common.Result;

public class HelpHandler extends Handler{
	private static final String MESSAGE_INSTANT = null;
	
	public HelpHandler(){
	}
	
	private Event makeHelpEvent(String text){
		Event event = new Event();
		event.setName(text);
		return event;
	}
	
	private Vector<Event> generateHelpCatalog(){
		Vector<Event> res = new Vector<Event>();
		String[] help = new String[] {
				"Welcome to CookieJar Quick Help",
				"add <your task name,date/time and #tags>",
				"display < keyword | ID | #tag | datetime >",
				"delete <ID>",
				"edit <ID> <Changes you wish to make>",
				"undo",
				"redo",
				"exit"
				};
		for(String text : help){
			res.add(makeHelpEvent(text));
		}
		return res;
		
	}

	@Override
	public Vector<Event> getResult() {
		Vector<Event> res = generateHelpCatalog();
		return res;
	}

	@Override
	public String getSuccessNotification() {
		return null;
	}

	@Override
	public String getFailureNotification() {
		return null;
	}

	@Override
	public Vector<Event> getInstantResult() {
		return getResult();
	}

	@Override
	public boolean undo() {
		return false;
	}

	@Override
	public boolean execute() {
		return true;
	}

	@Override
	public boolean execute(Stack<Handler> handle_undo,
			Stack<Handler> handle_redo) {
		return execute();
	}

	@Override
	public boolean executeInstant(Stack<Handler> handle_undo, Stack<Handler> handle_redo) {
		return execute();
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
		// TODO Auto-generated method stub
		return null;
	}

}
