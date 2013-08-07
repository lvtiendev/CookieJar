package cookiejar.exception.parser;

public class IncompleteCommandException extends Exception {
	private static final long serialVersionUID = 5734614801614875593L;
	private final String DEFAULT_MESSAGE = "Incomplete command entered";
	private String MESSAGE;

	public IncompleteCommandException() {
		MESSAGE = DEFAULT_MESSAGE;
	}

	public IncompleteCommandException(String message) {
		MESSAGE = message;
	}

	public String getMessage() {
		return MESSAGE;
	}

}
