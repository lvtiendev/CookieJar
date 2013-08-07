package logic.executor;

import java.io.IOException;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.exception.storage.InvalidFileContentException;
import cookiejar.logic.Executor;

public class SystemTest {
	Executor exe; 
	@Before
	public void initialise() throws IOException, InvalidFileContentException{
		exe = new Executor();
	}
	
	@Test
	public void simpleAddDisplayDeleteTest(){
		Event e = addEvent();
		deleteEvent(e);
	}
	
	public Event addEvent(){
		long time = System.currentTimeMillis();
		String eventName = "new event important "+time;
		String fullEventName = eventName + " #simpleAddDisplayDeleteTest";
		Result res = exe.process("add "+fullEventName, false);
		Event expected = createExpectedEvent(eventName);
		
		Assert.assertEquals(res.getNotification(),"Added : "+eventName);
		Assert.assertEquals(res.getCommand(),COMMAND_TYPE.ADD);
		Assert.assertFalse(res.isExit());
		
		Event found = getSameEvent(res,expected);
		Assert.assertNotNull(found);
		Assert.assertTrue(res.getFocusedEventID()==found.getDisplayCode());
		return found;
	}
	
	private Event createExpectedEvent(String eventName){
		Event e = new Event();
		Vector<String> s = new Vector<String>();
		s.add("#simpleAddDisplayDeleteTest");
		e.setName(eventName);
		e.setHashTags(s);
		return e;
	}
	
	private Event getSameEvent(Result res, Event event){
		for(Event e : res.getEvents()){
			if(e.getName().equals(event.getName())){
				boolean sameStartTime = e.getStartTime()==event.getStartTime();
				boolean sameEndTime = e.getEndTime()==event.getEndTime();
				boolean sameTags = e.getHashTags().equals(event.getHashTags());
				if(sameStartTime && sameEndTime && sameTags){
					return e;
				}
			}
		}
		return null;
	}
	
	public void deleteEvent(Event e){
		Result res = exe.process("delete "+e.getDisplayCode(),false);
		
		Assert.assertEquals(res.getNotification(),"Deleted : "+e.getName());
		Assert.assertEquals(res.getCommand(),COMMAND_TYPE.DELETE);
		Assert.assertFalse(res.isExit());
		
		Event found = getSameEvent(res,e);
		Assert.assertNull(found);
	}
	
	@Test
	public void IncompleteCommandExceptionTest(){
		String[] incomplete = new String[] {"","a","add ","d","displa","delete","delete ","e","edit","edit ","u","und","r","red","undone","undone ","done","done ","exi","h","hel"};
		for(String s : incomplete){
			Result res = exe.process(s,false);
			performIncompleteCommandAssertions(res);
		}
		
		String[] complete = new String[] {"add a","display","undo","redo","help","exit"};
		for(String s : complete){
			Result res = exe.process(s,true);
			Assert.assertNotSame(res.getCommand(),COMMAND_TYPE.INCOMPLETE);
		}
	}
	
	private void performIncompleteCommandAssertions(Result res){
		Assert.assertEquals(res.getNotification(),"");
		Assert.assertEquals(res.getCommand(),COMMAND_TYPE.INCOMPLETE);
		Assert.assertFalse(res.isExit());
		Assert.assertEquals(res.getFocusedEventID(), Result.EVENT_ID_NO_FOCUS);
	}
	
	@Test
	public void InvalidIDEnteredExceptionTest(){
		Result res = exe.process("display", false);
		int invalid = res.getEvents().size()+1;
		
		String[] commands = new String[] {"delete ","edit ","done ","undone "};
		for(String s : commands){
			res = exe.process(s+invalid,true);
			Assert.assertEquals(res.getNotification(),Executor.MESSAGE_ILLEGAL_COMMAND);
			Assert.assertEquals(new Vector<Event>(),res.getEvents());
			res = exe.process(s+invalid, false);
			Assert.assertEquals(res.getNotification(),Executor.MESSAGE_ILLEGAL_COMMAND);
			Assert.assertEquals(new Vector<Event>(),res.getEvents());
		}
	}
	
	@Test
	public void InvalidNameEnteredException(){
		Result res = exe.process("add today", false);
		Assert.assertFalse(res.isSuccess());
		Assert.assertEquals(res.getEvents(),new Vector<Event>());
	}
}
