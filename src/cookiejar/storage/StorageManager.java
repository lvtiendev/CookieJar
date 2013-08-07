//@author A0082927
package cookiejar.storage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.joda.time.DateTime;

import cookiejar.Launcher;
import cookiejar.common.Event;
import cookiejar.exception.storage.InvalidFileContentException;

public class StorageManager {

	File storageFile = new File(StorageConstants.STORAGE_FILE_NAME);

	private Converter storageConverter = new Converter();
	private FileLoader storageFileLoader;
	private FileStorer storageFileStorer;
	private HashTagsList storageHashTagsList;

	private Vector<Event> events;
	private int lastTaskID = 0;

	/**
	 * Default constructor for Storage
	 * 
	 * @throws IOException occurs when access to file has failed
	 * @throws InvalidFileContentException 
	 */
	public StorageManager() throws IOException, InvalidFileContentException {
		storageFileLoader = new FileLoader(storageFile, storageConverter);
		events = storageFileLoader.load();
		lastTaskID = storageFileLoader.getLastTaskID();
		storageFileStorer = new FileStorer(storageFile, storageConverter);
		storageHashTagsList = new HashTagsList();
		for (Event event : events) {
			storageHashTagsList.update(event);
		}
	}

	/**
	 * Inserts an event into the list and stores this new list into the text file
	 * 
	 * @param newEvent
	 * @param taskID
	 * @throws IOException occurs when access to file has failed
	 */
	public Event insert(Event newEvent, int taskID) throws IOException {
		@SuppressWarnings("unchecked")
		Vector<Event> originalEvents = (Vector<Event>) events.clone();

		if (taskID == StorageConstants.TASK_ID_NULL) {
			lastTaskID++;
			newEvent.setID(lastTaskID);
		} else {
			newEvent.setID(taskID);
		}

		try {
			events.add(newEvent);

			Collections.sort(events, new SortByTaskID());

			storageFileStorer.store(events, lastTaskID);
			storageHashTagsList.update(newEvent);
		} catch (IOException e) {
			events = originalEvents;
			Launcher.log("STORAGE: " + e.getMessage());
			throw new IOException();
		}

		return newEvent.clone();
	}

	/**
	 * Updates the list with the event and stores this new list into the text file
	 * 
	 * @param newEvent
	 * @throws IOException occurs when access to file has failed
	 */
	public void update(Event newEvent) throws IOException {
		@SuppressWarnings("unchecked")
		Vector<Event> originalEvents = (Vector<Event>) events.clone();
		Event eventToBeFound = null;

		for (Event event: events) {
			if (event.getID() == newEvent.getID()) {
				eventToBeFound = event;
			}
		}

		try {
			if (eventToBeFound != null) {
				int index = events.indexOf(eventToBeFound);
				events.remove(index);
				events.add(index, newEvent);

				Collections.sort(events, new SortByTaskID());

				storageFileStorer.store(events, lastTaskID);
				storageHashTagsList.erase(eventToBeFound);
				storageHashTagsList.update(newEvent);
			}
		} catch (IOException e) {
			events = originalEvents;
			Launcher.log("STORAGE: " + e.getMessage());
			throw new IOException();
		}
	}

	/**
	 * Drops the event from the list and stores this new list into the text file
	 * 
	 * @param eventToBeDropped
	 * @throws IOException occurs when access to file has failed
	 */
	public void drop(Event eventToBeDropped) throws IOException {
		@SuppressWarnings("unchecked")
		Vector<Event> originalEvents = (Vector<Event>) events.clone();

		int taskIDToBeFound = eventToBeDropped.getID();
		Event eventDropped = null;

		for (Event event : events) {
			if (event.getID() == taskIDToBeFound) {
				eventDropped = event;
			}
		}

		try {
			if (eventDropped != null) {
				events.remove(eventDropped);

				Collections.sort(events, new SortByTaskID());

				storageFileStorer.store(events, lastTaskID);
				storageHashTagsList.erase(eventDropped);
			}
		} catch (IOException e) {
			events = originalEvents;
			Launcher.log("STORAGE: " + e.getMessage());
			throw new IOException();
		}
	}

	/**
	 * Selects all events from the list
	 * 
	 * @return clone of the list of events
	 */
	public Vector<Event> select() {
		return cloneEvents(events);
	}

	/**
	 * Selects all events from the list with the taskID
	 * 
	 * @param taskID
	 * @return clone of the list of events
	 */
	public Vector<Event> select(int taskID) {
		Vector<Event> eventsToBeFound = new Vector<Event>();

		for (Event event : events) {
			if (event.getID() == taskID) {
				eventsToBeFound.add(event);
				return cloneEvents(eventsToBeFound);
			}
		}

		return cloneEvents(eventsToBeFound);
	}

	/**
	 * Selects all events from the list that contains the keyWord
	 * 
	 * @param keyWord
	 * @return clone of the list of events
	 */
	public Vector<Event> select(String keyWord) {
		Vector<Event> eventsToBeFound = new Vector<Event>();

		for(Event event : events) {
			if (event.getName().toLowerCase().contains(keyWord.toLowerCase())) {
				eventsToBeFound.add(event);
			}
		}

		return cloneEvents(eventsToBeFound);
	}

	/**
	 * Selects all events from the list that has dateTime within the range
	 * 
	 * @param startTimeToBeFound
	 * @param endTimeToBeFound
	 * @return clone of the list of events
	 */
	public Vector<Event> select(DateTime startTimeToBeFound, DateTime endTimeToBeFound) {
		Vector<Event> eventsToBeFound = new Vector<Event>();

		if (endTimeToBeFound == null) {
			return cloneEvents(eventsToBeFound);
		}

		for (Event event : events) {
			DateTime startTime = event.getStartTime();
			DateTime endTime = event.getEndTime();
			if (startTimeToBeFound == null) {
				if (isWithinRange(endTimeToBeFound, startTime, endTime)) {
					eventsToBeFound.add(event);
				}
			} else {
				if (isWithinRange(startTime, startTimeToBeFound, endTimeToBeFound) 
						|| isWithinRange(endTime, startTimeToBeFound, endTimeToBeFound)) {
					eventsToBeFound.add(event);
				}
			}
		}

		return cloneEvents(eventsToBeFound);
	}

	/**
	 * Selects all events from the list that contains the tag
	 * 
	 * @param tag
	 * @return clone of the list of events
	 */
	public Vector<Event> selectTag(String tag) {
		Vector<Event> eventsToBeFound = new Vector<Event>();

		eventsToBeFound = storageHashTagsList.search(tag);

		return cloneEvents(eventsToBeFound);
	}

	/**
	 * Checks whether a particular date time falls within start time and end time range
	 * 
	 * @param date
	 * @param startTime
	 * @param endTime
	 * @return boolean
	 */
	private boolean isWithinRange(DateTime date, DateTime startTime,
			DateTime endTime) {

		if (date == null) {
			return false;
		} else {
			return (compareDate(startTime, date) <= 0 && compareDate(date, endTime) <= 0);
		}
	}

	/**
	 * Compares 2 date times
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private int compareDate(DateTime date1, DateTime date2) {
		int year1 = date1.getYear();
		int year2 = date2.getYear();
		if (year1 == year2) {
			int dayOfYear1 = date1.getDayOfYear();
			int dayOfYear2 = date2.getDayOfYear();
			return dayOfYear1 - dayOfYear2;
		} else {
			return year1 - year2;
		}
	}

	public Converter getStorageConverter() {
		return this.storageConverter;
	}

	public FileLoader getStorageFileLoader() {
		return this.storageFileLoader;
	}

	public FileStorer getStorageFileStorer() {
		return this.storageFileStorer;
	}

	public HashTagsList getStorageHashTagsList() {
		return this.storageHashTagsList;
	}

	public Vector<Event> getEvents() {
		return events;
	}

	/**
	 * Clone the list of events
	 * 
	 * @param eventsToBeCloned
	 * @return clone of the list of events
	 */
	private Vector<Event> cloneEvents(Vector<Event> eventsToBeCloned) {
		Vector<Event> clonedEvents = new Vector<Event>();

		for(Event event: eventsToBeCloned) {
			clonedEvents.add(event.clone());
		}

		return clonedEvents;
	}
}	