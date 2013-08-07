package cookiejar.exception.parser;

//@author A0088447N

public class InvalidNameEnteredException extends Exception{
	
	private static final long serialVersionUID = 5734614801614875593L;
	private final String DEFAULT_MESSAGE = "Invalid event name entered";
	private String MESSAGE;

	public InvalidNameEnteredException() {
		MESSAGE = DEFAULT_MESSAGE;
	}

	public InvalidNameEnteredException(String message) {
		MESSAGE = message;
	}

	public String getMessage() {
		return MESSAGE;
	}

}
