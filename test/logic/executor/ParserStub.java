package logic.executor;

import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.logic.parser.Parser;

public class ParserStub extends Parser{
	public ParserStub(){
		super();
	}
	
	public static COMMAND_TYPE parseCommand(String userCommand){
		if(userCommand.equalsIgnoreCase("add")){
			return COMMAND_TYPE.ADD;
		} else if (userCommand.equalsIgnoreCase("delete")){
			return COMMAND_TYPE.DELETE;
		} else if (userCommand.equalsIgnoreCase("edit")){
			return COMMAND_TYPE.EDIT;
		} else if (userCommand.equalsIgnoreCase("display")){
			return COMMAND_TYPE.DISPLAY;
		} else if (userCommand.equalsIgnoreCase("exit")){
			return COMMAND_TYPE.EXIT;
		} else if (userCommand.equalsIgnoreCase("undo")){
			return COMMAND_TYPE.UNDO;
		} else if (userCommand.equalsIgnoreCase("redo")){
			return COMMAND_TYPE.REDO;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	@Override
	public Event getEventFromString(String userCommand) {
		return new EventStub();
	}
}
