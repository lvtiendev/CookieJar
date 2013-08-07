package logic.executor;

import java.util.Vector;

import org.joda.time.DateTime;

import cookiejar.common.Event;

public class EventStub extends Event{
	public EventStub(){
		super();
		this.setID(1);
		this.setName("new Event Stub");
		this.setStartTime(new DateTime(10000));
		this.setEndTime(new DateTime(20000));
		this.setHashTags(new Vector<String>());
	}
}
