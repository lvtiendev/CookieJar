//@author A0082927
package cookiejar.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import cookiejar.common.Event;

/**
 * Stores events from Vector<Event>() into text file
 */

public class FileStorer {
	
	private File storageFile;

	private FileWriter fileWriter;
	private Converter storageConverter;
	private BufferedWriter outputToFile;

	public FileStorer(File storageFile, Converter storageConverter) throws IOException {
		this.storageConverter = storageConverter;
		this.storageFile = storageFile;
	}

	/**
	 * This method writes a list of events into the file associated with outputToFile
	 * 
	 * @param eventsToBeStored List of Events that will be written into a file
	 * @throws IOException occurs when access to file has failed
	 */
	public void store(Vector<Event> eventsToBeStored, int taskID) throws IOException {
		fileWriter = new FileWriter(storageFile);
		outputToFile = new BufferedWriter(fileWriter);
		Integer newLastTaskID = new Integer(taskID);
		
		for (Event event : eventsToBeStored) {
			String stringToWrite = storageConverter.eventObjectToString(event);
			outputToFile.write(stringToWrite);
			outputToFile.newLine();
		}
		outputToFile.write(StorageConstants.END_OF_FILE);
		outputToFile.newLine();
		outputToFile.write(newLastTaskID.toString());
		outputToFile.newLine();
		outputToFile.close();
	}
}
