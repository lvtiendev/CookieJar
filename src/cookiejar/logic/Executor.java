//@author A0081223U
package cookiejar.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import cookiejar.Launcher;
import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.exception.parser.IncompleteCommandException;
import cookiejar.exception.parser.InvalidIdEnteredException;
import cookiejar.exception.parser.InvalidNameEnteredException;
import cookiejar.exception.parser.InvalidTimeEnteredException;
import cookiejar.exception.storage.InvalidFileContentException;
import cookiejar.logic.methodHandler.AddHandler;
import cookiejar.logic.methodHandler.DeleteHandler;
import cookiejar.logic.methodHandler.DisplayHandler;
import cookiejar.logic.methodHandler.DoneHandler;
import cookiejar.logic.methodHandler.EditHandler;
import cookiejar.logic.methodHandler.ExitException;
import cookiejar.logic.methodHandler.Handler;
import cookiejar.logic.methodHandler.HelpHandler;
import cookiejar.logic.methodHandler.RedoHandler;
import cookiejar.logic.methodHandler.UndoHandler;
import cookiejar.logic.methodHandler.UndoneHandler;
import cookiejar.logic.parser.Parser;
import cookiejar.logic.parser.ParserAdd;
import cookiejar.logic.parser.ParserDisplay;
import cookiejar.logic.parser.ParserWithID;
import cookiejar.storage.StorageManager;


public class Executor {
	protected static final int TASK_ID_NULL = 0;
	public static final String MESSAGE_ILLEGAL_COMMAND = "display | add | edit | delete | undo | redo | done | undone | help | exit";

	protected StorageManager store;
	protected Parser parserAdd = new ParserAdd();
	protected Parser parserDisplay = new ParserDisplay();
	protected Parser parserWithID = new ParserWithID();

	HashMap<Integer,Integer> displayCodeToGlobalMap = new HashMap<Integer,Integer>();
	Stack<Handler> handle_undo = new Stack<Handler>();
	Stack<Handler> handle_redo = new Stack<Handler>();

	DisplayHandler lastDisplayed;  //lastDisplayed is kept as a handle so that the event list can be refreshed on recall

	/**
	 * Constructor
	 * Initialises the Storage and loads lastDisplayed result with a DisplayHandler
	 * Throws IOException if it is unable to read/write in the current directory
	 * @throws InvalidFileContentException 
	 */
	public Executor() throws IOException, InvalidFileContentException{
		store = new StorageManager();
		lastDisplayed = DisplayHandler.getDisplayAll(store);
		grabLastDisplay(COMMAND_TYPE.DISPLAY,true,lastDisplayed);
	}

	/**
	 * Main API to process user input string.
	 * isInstant specifies that the process does not modify the storage and return Instant notifications
	 * Acts as the Invoker of all Handler objects
	 * @param user_input
	 * @param isInstant
	 * @return Result Object
	 */
	public Result process(String user_input, boolean isInstant){
		COMMAND_TYPE command = COMMAND_TYPE.INVALID;
		try{
			command = Parser.parseCommand(user_input);
			Handler handle = parseAndGetHandlerOnCommand(user_input,command);
			boolean success;
			
			if(isInstant){
				success = handle.executeInstant(handle_undo,handle_redo);
				return packInstantResult(command,success,handle);
			}else{
				success = handle.execute(handle_undo,handle_redo);
				return packResult(command,success,handle);
			}
		} catch (IllegalArgumentException e){
			return packExceptionResult(MESSAGE_ILLEGAL_COMMAND,command);
		} catch (InvalidNameEnteredException e){
			return packExceptionResult(e.getMessage(),command);
		} catch (InvalidIdEnteredException e){
			return packExceptionResult(e.getMessage(),command);
		} catch (IncompleteCommandException e) {
			return packResultLastDisplayed("",COMMAND_TYPE.INCOMPLETE,Result.EVENT_ID_NO_FOCUS);
		} catch (ExitException e){
			Result r = new Result();
			r.setExitCommand(true);
			return r;
		} catch (NullPointerException e){
			Launcher.log("EXECUTOR : "+e.getMessage());
			return packExceptionResult("GG",command);
		} catch (IOException e) {
			Launcher.log("EXECUTOR : "+e.getMessage());
			return packExceptionResult(e.getMessage(),command);
		}
	}

	/**
	 * Switch to parse the user input using the designated parser and returns the desired Handler that is pre-loaded with the parsed Event object
	 * @param user_input
	 * @param command
	 * @return Handler
	 * @throws ExitException
	 * @throws InvalidNameEnteredException
	 * @throws InvalidIdEnteredException
	 * @throws InvalidTimeEnteredException
	 * @throws IncompleteCommandException
	 * @throws IOException 
	 */
	protected Handler parseAndGetHandlerOnCommand(String user_input, COMMAND_TYPE command) throws ExitException, InvalidNameEnteredException, InvalidIdEnteredException, IncompleteCommandException, IOException{
		Event event;
		switch(command){
		case ADD:
			event = parserAdd.getEventFromString(user_input);
			return new AddHandler(event,user_input,store);
		case DELETE:
			event = parserWithID.getEventFromString(user_input);
			convertDisplayCodeToGlobalID(event);
			return new DeleteHandler(event,user_input,store,lastDisplayed);
		case EDIT:
			event = parserWithID.getEventFromString(user_input);
			convertDisplayCodeToGlobalID(event);
			return new EditHandler(event,user_input,store,lastDisplayed);
		case DISPLAY:
			event = parserDisplay.getEventFromString(user_input);
			convertDisplayCodeToGlobalID(event);
			return new DisplayHandler(event,user_input,store);
		case DONE:
			event = parserWithID.getEventFromString(user_input);
			convertDisplayCodeToGlobalID(event);
			return new DoneHandler(event,user_input,store,lastDisplayed);
		case UNDONE:
			event = parserWithID.getEventFromString(user_input);
			convertDisplayCodeToGlobalID(event);
			return new UndoneHandler(event,user_input,store,lastDisplayed);
		case REDO:
			return new RedoHandler(store);
		case UNDO:
			return new UndoHandler(store);
		case HELP:
			return new HelpHandler();
		case EXIT:
			throw new ExitException();
		case INCOMPLETE:
			throw new IncompleteCommandException();
		case INVALID:
			throw new IllegalArgumentException();
		}
		return null;
	}

	protected void convertDisplayCodeToGlobalID(Event event) {
		int displayCode = event.getID();
		if(displayCodeToGlobalMap.containsKey(displayCode)){
			event.setID(displayCodeToGlobalMap.get(displayCode));
			//			System.out.println("displayCode: "+displayCode+" eventID: "+event.getID());
		}else{
			throw new IllegalArgumentException("Have you entered a wrong ID?");
		}
	}

	protected int getFocusedDisplayCodeFromGlobalID(int ID){
		for(int i : displayCodeToGlobalMap.keySet()){
			if(displayCodeToGlobalMap.get(i)==ID){
				return i;
			}
		}
		return Result.DISPLAY_CODE_NULL;
	}

	/**
	 * Creates the Result Object based on the successful execution of handler
	 * Notifications and List-of-Events results are extracted from the handler here 
	 * This must not be an instant command
	 * @param command
	 * @param success
	 * @param handle
	 * @return Result object
	 */
	protected Result packResult(COMMAND_TYPE command, boolean success, Handler handle){
		grabLastDisplay(command,success,handle);
		if(success){
			if(command == COMMAND_TYPE.ADD){
				grabAllDisplay(command);
			}
			int focusedDisplayCode = getFocusedDisplayCodeFromGlobalID(handle.getFocusedID());
			return packResultLastDisplayed(handle.getSuccessNotification(),command,focusedDisplayCode);
		}else{
			return packResultLastDisplayed(handle.getFailureNotification(),command,Result.EVENT_ID_NO_FOCUS);

		}
	}

	/**
	 * Creates the Result Object based on the successful execution of handler
	 * INSTANT Notifications and List-of-Events results are extracted from the handler here 
	 * This must be an instant command
	 * @param command
	 * @param success
	 * @param handle
	 * @return Result object
	 */
	protected Result packInstantResult(COMMAND_TYPE command, boolean success, Handler handle){
		if(success){
			Vector<Event> event_list = handle.getInstantResult();
			//assignDisplayCodes(event_list);
			int focusedDisplayCode = getFocusedDisplayCodeFromGlobalID(handle.getFocusedID());
			return new Result(event_list,focusedDisplayCode,handle.getInstantNotification(),success,command);
		}else{
			return packResultLastDisplayed("",command,Result.EVENT_ID_NO_FOCUS);
		}
	}

	/**
	 * Creates the Result Object based on the Exception String given.
	 * Does not show any Events. Only notification
	 * @param exceptionString
	 * @return Result object
	 */
	protected Result packExceptionResult(String exceptionString, COMMAND_TYPE command){
		return new Result(new Vector<Event>(), Result.EVENT_ID_NO_FOCUS,exceptionString, false,command);
	}

	/**
	 * Creates the Result Object based on the notification string and the last successful non-instant Display command sent to packResult()
	 * @param notification
	 * @return Result Object
	 */
	protected Result packResultLastDisplayed(String notification,COMMAND_TYPE command, int focusedID){
		boolean success = lastDisplayed.execute(handle_undo,handle_redo);
		Vector<Event> res = lastDisplayed.getResult();
		//assignDisplayCodes(res);
		createDisplayCodeToGlobalIDMap(res);
		return new Result(res,focusedID,notification,success,command);
	}

	/**
	 * Used by packResult to update the last successful non-instant Display result.
	 * @param command
	 * @param success
	 * @param handle
	 */
	protected void grabLastDisplay(COMMAND_TYPE command, boolean success, Handler handle){
		if(success && command==COMMAND_TYPE.DISPLAY){
			lastDisplayed = (DisplayHandler) handle;
			lastDisplayed.execute();
			//assignDisplayCodes(lastDisplayed.getResult());
			createDisplayCodeToGlobalIDMap(lastDisplayed.getResult());
		}
	}

	protected void grabAllDisplay(COMMAND_TYPE command){
		if(command!=COMMAND_TYPE.DISPLAY){
			lastDisplayed = DisplayHandler.getDisplayAll(store);
			lastDisplayed.execute();
			//assignDisplayCodes(lastDisplayed.getResult());
			createDisplayCodeToGlobalIDMap(lastDisplayed.getResult());
		}
	}

	protected void createDisplayCodeToGlobalIDMap(Vector<Event> event_list){
		displayCodeToGlobalMap.clear();
		for(Event event:event_list){
			displayCodeToGlobalMap.put(event.getDisplayCode(),event.getID());
		}
		displayCodeToGlobalMap.put(0, 0);
	}

	public static void assignDisplayCodes(Vector<Event> event_list){
		int i=1;
		for(Event event:event_list){
			if(event.getDisplayCode()>=Result.DISPLAY_CODE_NULL){
				event.setDisplayCode(i);
				i++;
			}
		}
	}
}
