//@author a0082927
package cookiejar.exception.storage;

/**
 * Occurs when file content is invalid
 */
public class InvalidFileContentException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String DEFAULT_MESSAGE = "File content is invalid";
	private String MESSAGE;

	public InvalidFileContentException() {
		MESSAGE = DEFAULT_MESSAGE;
	}

	public InvalidFileContentException(String message) {
		MESSAGE = message;
	}

	public String getMessage() {
		return MESSAGE;
	}

}
