//@author A0082927
package cookiejar.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import cookiejar.common.Event;
import cookiejar.exception.storage.InvalidFileContentException;

/**
 * Loads events from text file into a Vector<Event>()
 */

public class FileLoader {
	
	private File storageFile;
	private FileReader fileReader;
	private Converter storageConverter;
	private BufferedReader inputFromFile;
	
	private int lastTaskID;
	
	public FileLoader(File storageFile, Converter storageConverter) throws IOException {
		this.storageConverter = storageConverter;
		this.storageFile = storageFile;
	}
	
	/**
	 * This method reads a list of events from the file associated with inputFromFile
	 * 
	 * @return List of Events that is stored in the file
	 * @throws IOException occurs when access to file has failed
	 * @throws InvalidFileContentException 
	 */
	public Vector<Event> load() throws IOException, InvalidFileContentException {
		if (!storageFile.exists()) {
			storageFile.createNewFile();
		}
		
		fileReader = new FileReader(storageFile);
		inputFromFile = new BufferedReader(fileReader);
		Vector<Event> eventsToBeLoaded = new Vector<Event>();
		String eventInString = null;
		
		while ((eventInString = inputFromFile.readLine()) != null) {
			if (eventInString.equals(StorageConstants.END_OF_FILE)) {
				String lastLine = (eventInString = inputFromFile.readLine());
				if (lastLine == null || lastLine.trim().equals(StorageConstants.EMPTY_STRING)) {
					lastTaskID = 0;
				} else {
					lastTaskID = Integer.parseInt(eventInString);
				}
				continue;
			}
			Event newEvent = storageConverter.stringToEventObject(eventInString);
			eventsToBeLoaded.add(newEvent);
		}
		
		inputFromFile.close();
		return eventsToBeLoaded;
	}
	
	public int getLastTaskID() {
		return lastTaskID;
	}
}
