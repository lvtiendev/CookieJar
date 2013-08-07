package cookiejar.logic.parser;

import java.util.Vector;

//@author A0088447N

/**
 * Class helps to get hash tags information of event from a String
 * 
 * @author Tien
 * 
 */

public class EventHashTagsParser {
	private Vector<String> hashTagsList;

	public EventHashTagsParser() {
		hashTagsList = new Vector<String>();
	}

	/**
	 * Remove all hash tags information from a String. At the same time, update
	 * <code>hashTagsList</code> vector with the these information
	 * 
	 * @param infoString
	 *            is information part of the user command.
	 * @return remaining string
	 */
	public String extractHashTagsFromString(String infoString) {
		String[] words = StringTools.splitParameters(infoString);
		String newInfoString = ParserConstants.EMPTY_STRING;
		hashTagsList = new Vector<String>();

		for (String word : words) {
			if (!word.equals(ParserConstants.EMPTY_STRING)) {
				if (word.charAt(0) == ParserConstants.HASH_TAG_MARKER) {
					if (!hashTagsList.contains(word)) {
						hashTagsList.add(word);
					}
				} else {
					newInfoString = newInfoString.concat(word + " ");
				}
			}
		}

		return newInfoString.trim();
	}

	
	/**
	 * Return hashTagsList vector associated.
	 * This method needs to be called after calling 
	 * <code>extractHashTagsFromString</code> method.
	 * @return a Vector containing all hash tags
	 */
	@SuppressWarnings("unchecked")
	public Vector<String> getResultHashTags() {
		return (Vector<String>) hashTagsList.clone();
	}
}
