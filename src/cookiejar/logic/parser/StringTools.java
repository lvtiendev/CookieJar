package cookiejar.logic.parser;

//@author A0088447N
/**
 * This class contains some static methods to work and modify String
 * 
 * @author Tien
 * 
 */
public class StringTools {

	/**
	 * Split a string into separated words by white spaces
	 * 
	 * @param inputString
	 *            is the input string
	 * @return an array of separated words
	 */
	public static String[] splitParameters(String inputString) {
		String[] parameters = inputString.trim().split("\\s+");
		return parameters;
	}

	/**
	 * Get the first word in a string
	 * 
	 * @param inputString
	 *            is the input string
	 * @return the first word
	 */
	public static String getFirstWord(String inputString) {
		String[] words = inputString.trim().split(" ");
		return words[0];
	}

	/**
	 * Remove the first word in a string
	 * 
	 * @param inputString
	 * @return the string after removing first word if the original string is
	 *         blank, return blank string
	 */
	public static String removeFirstWord(String inputString) {
		String result = "";
		String[] words = splitParameters(inputString);

		for (int i = 1; i < words.length; i++) {
			result += words[i] + " ";
		}

		return result.trim();
	}

	/**
	 * Make a string from an array of separated words The result string is
	 * formatted: one white space between two words and no white space trailing
	 * at both ends
	 * 
	 * @param words
	 *            is an array of string
	 * @return result string
	 */
	public static String makeStringFromArray(String[] words) {
		String result = ParserConstants.EMPTY_STRING;

		for (String word : words) {
			result += word + " ";
		}

		return result.trim();
	}

	/**
	 * Make a string from an array of separated words, only from position
	 * <code>begin</code> to position <code>end</code>-1 The result string is formatted: one
	 * white space between two words and no white space trailing at both ends
	 * 
	 * @param words
	 * @param begin
	 *            is an index of the array
	 * @param end
	 *            is an index of the array
	 * @return result string
	 */
	public static String makeStringFromArray(String[] words, int begin, int end) {
		String result = ParserConstants.EMPTY_STRING;

		for (int i = begin; i < end; i++) {
			result += words[i] + " ";
		}

		return result.trim();
	}

	/**
	 * Normalize an input user command string as follow:
	 * <ul>
	 * <li>Replace <b>till</b> , <b>til</b> , <b>until</b> by <b>to</b></li>
	 * <li>Replace <b>tmr</b> , <b>tml</b> by <b>tomorrow</b></li>
	 * </ul>
	 * 
	 * @param userCommand
	 *            is a command from user
	 * @return a normalized string
	 */
	public static String normalizeCommandString(String userCommand) {
		String infoString = removeFirstWord(userCommand);
		String[] params = splitParameters(infoString);
		for (int i = 0; i < params.length; i++) {
			if ((params[i].equalsIgnoreCase("till")
					|| (params[i].equalsIgnoreCase("til")) 
					|| (params[i].equalsIgnoreCase("until")))) {
				params[i] = "to";
			} else if (params[i].equalsIgnoreCase("tmr")
					&& !params[i].equals("TMR")) {
				params[i] = "tomorrow";
			} else if (params[i].equalsIgnoreCase("tml")
					&& !params[i].equals("TML")) {
				params[i] = "tomorrow";
			}
		}
		infoString = makeStringFromArray(params);
		return infoString.trim();
	}

	/**
	 * Count the number of words in a string
	 * 
	 * @param inputString
	 *            is a String
	 * @return number of words
	 */
	public static int countWords(String inputString) {
		if (inputString.trim().isEmpty()) {
			return 0;
		} else {
			return inputString.trim().split("\\s+").length;
		}
	}
}
