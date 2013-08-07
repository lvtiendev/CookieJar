package cookiejar.logic.parser;

//@author A0088447N

/**
 * This class is used to contain and update a set of 3 String
 * 
 * @author Tien
 * 
 */
public class WordTrio {
	private String left, middle, right;

	WordTrio(String left, String middle, String right) {
		this.left = left;
		this.middle = middle;
		this.right = right;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public void setMiddle(String middle) {
		this.middle = middle;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getLeft() {
		return left;
	}

	public String getMiddle() {
		return middle;
	}

	public String getRight() {
		return right;
	}
}
