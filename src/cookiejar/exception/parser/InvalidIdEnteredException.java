package cookiejar.exception.parser;

//@author A0088447N

public class InvalidIdEnteredException extends Exception{
	private static final long serialVersionUID = -7496604175093713839L;
	private final String DEFAULT_MESSAGE = "Invalid event ID entered";
	private String MESSAGE;

	public InvalidIdEnteredException() {
		MESSAGE = DEFAULT_MESSAGE;
	}

	public InvalidIdEnteredException(String message) {
		MESSAGE = message;
	}

	public String getMessage() {
		return MESSAGE;
	}

}
