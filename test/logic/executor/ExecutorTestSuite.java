package logic.executor;

import java.io.IOException;
import java.util.Vector;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.exception.parser.IncompleteCommandException;
import cookiejar.exception.parser.InvalidIdEnteredException;
import cookiejar.exception.parser.InvalidNameEnteredException;
import cookiejar.exception.parser.InvalidTimeEnteredException;
import cookiejar.exception.storage.InvalidFileContentException;
import cookiejar.logic.Executor;
import cookiejar.logic.methodHandler.AddHandler;
import cookiejar.logic.methodHandler.DeleteHandler;
import cookiejar.logic.methodHandler.DisplayHandler;
import cookiejar.logic.methodHandler.DoneHandler;
import cookiejar.logic.methodHandler.EditHandler;
import cookiejar.logic.methodHandler.ExitException;
import cookiejar.logic.methodHandler.Handler;
import cookiejar.logic.methodHandler.RedoHandler;
import cookiejar.logic.methodHandler.UndoHandler;
import cookiejar.logic.methodHandler.UndoneHandler;
import cookiejar.storage.StorageManager;

public class ExecutorTestSuite extends Executor{
	public ExecutorTestSuite() throws InvalidFileContentException, IOException{
		super();
		try {
			store = new StorageStub();
			parserAdd = new ParserStub();
			parserDisplay = new ParserStub();
			parserWithID = new ParserStub();
		} catch (IOException e) {
			System.out.print("Unable to create StorageStub");
		}
	}

	@Test
	public void getAddHandlerTest() throws InvalidNameEnteredException, InvalidIdEnteredException, InvalidTimeEnteredException, IncompleteCommandException, IOException{
		//Add Handler
		try {
			Handler Handle = super.parseAndGetHandlerOnCommand("Add New Task Stub",COMMAND_TYPE.ADD);

			if(!(Handle instanceof AddHandler)){
				Assert.fail("getAddHandler Test failed with wrong Handler");
			}

			Event event = ((AddHandler) Handle).getEvent();
			Assert.assertTrue(eventEqual(event,new EventStub()));
		} catch (ExitException e) {
			Assert.fail("getAddHandlerTest failed with exit exception");
		}
	}

	@Test
	public void getUndoHandlerTest() throws InvalidNameEnteredException, InvalidIdEnteredException, InvalidTimeEnteredException, IncompleteCommandException, IOException{
		//Undo Handler
		try{
			Handler Handle = super.parseAndGetHandlerOnCommand("Undo",COMMAND_TYPE.UNDO);

			if(!(Handle instanceof UndoHandler)){
				Assert.fail("getUndoHandler Test failed with wrong Handler");
			}
		} catch (ExitException e){
			Assert.fail("getUndoHandlerTest failed with exit exception");
		}
	}

	@Test
	public void getExitException() throws InvalidNameEnteredException, InvalidIdEnteredException, InvalidTimeEnteredException, IncompleteCommandException, IOException{
		try{
			Handler Handle = super.parseAndGetHandlerOnCommand("Exit",COMMAND_TYPE.EXIT);
			Assert.fail("getExitException failed to create Exit Exception");
		} catch (ExitException e){

		}
	}

	@Test
	public void getInvalidHandlerTest() throws InvalidNameEnteredException, InvalidIdEnteredException, InvalidTimeEnteredException, IncompleteCommandException, IOException{
		//Invalid Handler
		try{
			Handler Handle = super.parseAndGetHandlerOnCommand("Invalid",COMMAND_TYPE.INVALID);
			Assert.assertNull(Handle);
		} catch (ExitException e){
			Assert.fail("getInvalidHandlerTest failed with exit exception");
		} catch (IllegalArgumentException e){
			return;
		}
		Assert.fail("getInvalidHandlerTest failed to throw IllegalArgumentException");
	}

	@Test
	public void addHandlerExecuteTest() throws IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Handler Handle = new AddHandler(new EventStub(),"ADD",store);
		Handle.execute();
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastInserted(),new EventStub()));
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastInserted(),Handle.getResult().get(0)));
	}

	@Test
	public void addHandlerUndoTest() throws IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Handler Handle = new AddHandler(new EventStub(),"ADD",store);
		Handle.execute();
		Handle.undo();
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastDropped(),new EventStub()));
	}
	
	@Test
	public void deleteHandlerExecuteTest() throws IOException, InvalidFileContentException, IncompleteCommandException{
		StorageManager store = new StorageStub();
		Handler Handle = new DeleteHandler(new EventStub(),"Delete",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastDropped(),new EventStub()));
	}
	
	@Test
	public void deleteHandlerUndoTest() throws IOException, InvalidFileContentException, IncompleteCommandException{
		StorageManager store = new StorageStub();
		Handler Handle = new DeleteHandler(new EventStub(),"Delete",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Handle.undo();
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastInserted(),new EventStub()));
	}
	
	@Test
	public void editHandlerExecuteTest() throws IncompleteCommandException, IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Event event = new EventStub();
		event.setStartTime(new DateTime(5000));
		Handler Handle = new EditHandler(event,"Edit 1",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastUpdated(),event));
		Assert.assertFalse(eventEqual(((StorageStub)store).getLastUpdated(),new EventStub()));
	}
	
	@Test
	public void editHandlerUndoTest() throws IncompleteCommandException, IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Event event = new EventStub();
		event.setStartTime(new DateTime(5000));
		Handler Handle = new EditHandler(event,"Edit 1",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Handle.undo();
		Assert.assertFalse(eventEqual(((StorageStub)store).getLastUpdated(),event));
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastUpdated(),new EventStub()));
	}
	
	@Test
	public void doneHandlerExecuteTest() throws IncompleteCommandException, IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Event event = new EventStub();
		Handler Handle = new DoneHandler(event,"Done 1",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Assert.assertFalse(eventEqual(((StorageStub)store).getLastUpdated(),event));
		Vector<String> expected = new Vector<String>();
		expected.add("#done");
		Assert.assertEquals(((StorageStub)store).getLastUpdated().getHashTags(),expected);
	}
	
	@Test
	public void doneHandlerUndoTest() throws IncompleteCommandException, IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Event event = new EventStub();
		Handler Handle = new DoneHandler(event,"Done 1",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Handle.undo();
		Vector<String> expected = new Vector<String>();
		expected.add("#done");
		Assert.assertTrue(eventEqual(((StorageStub)store).getLastUpdated(),event));
		Assert.assertNotSame(((StorageStub)store).getLastUpdated().getHashTags(),expected);
	}
	
	@Test
	public void undoneHandlerExecuteTest() throws IncompleteCommandException, IOException, InvalidFileContentException{
		try{
		StorageManager store = new StorageStub();
		Event event = new EventStub();
		event.setID(StorageStub.DONE_EVENT_ID);
		Handler Handle = new UndoneHandler(event,"undone 1",store, new DisplayHandler(new EventStub(),"Display",store));
		boolean res = Handle.execute();
		Assert.assertFalse(eventEqual(((StorageStub)store).getLastUpdated(),event));
		Vector<String> expected = new Vector<String>();
		expected.add("#done");
		Assert.assertNotSame(((StorageStub)store).getLastUpdated().getHashTags(),expected);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void undoneHandlerUndoTest() throws IncompleteCommandException, IOException, InvalidFileContentException{
		StorageManager store = new StorageStub();
		Event event = new EventStub();
		Handler Handle = new UndoneHandler(event,"undone 1",store, new DisplayHandler(new EventStub(),"Display",store));
		Handle.execute();
		Handle.undo();
		Vector<String> expected = new Vector<String>();
		expected.add("#done");
		Assert.assertFalse(eventEqual(((StorageStub)store).getLastUpdated(),event));
		Assert.assertEquals(((StorageStub)store).getLastUpdated().getHashTags(),expected);
	}
	
	@Test
	public void setDisplayCodeTest(){
		Vector<Event> event_list = new Vector<Event>();
		for(int i=0;i<10;i++){
			event_list.add(new EventStub());
		}
		assignDisplayCodes(event_list);
		for(int i=0;i<10;i++){
			Assert.assertEquals(event_list.get(i).getDisplayCode(),i+1);
		}
	}
	
	@Test
	public void globalToDisplayCodeMapTest(){
		Vector<Event> event_list = new Vector<Event>();
		for(int i=1;i<11;i++){
			Event event = new EventStub();
			event.setID(i*2);
			event_list.add(event);
		}
		@SuppressWarnings("unchecked")
		Vector<Event> event_list_2 = (Vector<Event>) event_list.clone();
		assignDisplayCodes(event_list);
		createDisplayCodeToGlobalIDMap(event_list);
		
		for(int i=1;i<1;i++){
			convertDisplayCodeToGlobalID(event_list_2.get(i));
			Assert.assertEquals(event_list_2.get(i).getID(),i*2);
		}
	}

	private boolean eventEqual(Event e1, Event e2){
		if(e1.getID()==e2.getID()
				&& e1.getStartTime().isEqual(e2.getStartTime())
				&& e1.getEndTime().isEqual(e2.getEndTime())
				&& e1.getHashTags().equals(e2.getHashTags())
				&& e1.getName().equals(e2.getName())){

			return true;
		}else{
			return false;
		}
	}
}
