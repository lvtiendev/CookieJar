package cookiejar.exception.parser;

//@author A0088447N

public class InvalidTimeEnteredException extends Exception{
	private static final long serialVersionUID = 3919060889623406368L;
	private final String DEFAULT_MESSAGE = "Invalid event time entered";
	private String MESSAGE;

	public InvalidTimeEnteredException() {
		MESSAGE = DEFAULT_MESSAGE;
	}

	public InvalidTimeEnteredException(String message) {
		MESSAGE = message;
	}

	public String getMessage() {
		return MESSAGE;
	}
}
