//@author A0081223U
package cookiejar.common;

import java.util.Vector;

public class Result {
	public static final int EVENT_ID_NO_FOCUS = 0;
	public static final int DISPLAY_CODE_NULL = 0;
	public static final int DISPLAY_CODE_ADD = -1;
	public static final int DISPLAY_CODE_DELETE = -2;
	
	private Vector<Event> event_list;
	private String notification;
	private int focusedEventID;
	private boolean isExitCommand = false;
	private boolean isSuccess;
	private COMMAND_TYPE command;
	
	public Result(){
		event_list = new Vector<Event>();
		notification = "";
		focusedEventID = EVENT_ID_NO_FOCUS;
		isExitCommand = false;
		isSuccess = false;
	}
	
	public Result(Vector<Event> event_list, int focusedEventID, String notification, boolean isSuccess, COMMAND_TYPE command){
		this.event_list = event_list;
		this.notification = notification;
		this.isSuccess = isSuccess;
		this.focusedEventID = focusedEventID;
		this.command = command;
	}
	
	public Vector<Event> getEvents(){
		return event_list;
	}
	
	public Event getEventWithDisplayCode(int code){
		for(Event event : event_list){
			if(event.getDisplayCode()==code){
				return event;
			}
		}
		return null;
	}
	
	public String getNotification(){
		return notification;
	}
	
	public boolean isSuccess(){
		return isSuccess;
	}
	
	public String toString(){
		return "Notification: "+notification+"\n"+event_list.toString();
	}
	
	public void setExitCommand(boolean isExitCommand){
		this.isExitCommand = isExitCommand;
	}
	
	public boolean isExit(){
		return isExitCommand;
	}
	
	public int getFocusedEventID(){
		return focusedEventID;
	}
	
	public void setFocusedEventID(int focusedEventID){
		this.focusedEventID = focusedEventID;
	}
	
	public void setCommand(COMMAND_TYPE command){
		this.command = command;
	}
	
	public COMMAND_TYPE getCommand(){
		return command;
	}
}
