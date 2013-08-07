//@author A0059827N
package cookiejar.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.View;

import cookiejar.common.COMMAND_TYPE;

/**
 * This class creates the command bar frame which allows the user to type in
 * commands. The command bar is the main frame of the GUIManager.
 */
@SuppressWarnings("serial")
public class CommandBar extends JFrame {
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------
	// Command Bar.
	protected static final int COMMAND_BAR_WIDTH = ResultWindow.RESULT_WINDOW_WIDTH;
	protected static final int COMMAND_BAR_HEIGHT = 50;

	// Content Pane.
	private static final Color CONTENT_PANE_LINE_BORDER_COLOR = new Color(200,
			200, 200);
	private static final int CONTENT_PANE_LINE_BORDER_THICKNESS = 1;
	private static final Border CONTENT_PANE_BORDER = new LineBorder(
			CONTENT_PANE_LINE_BORDER_COLOR, CONTENT_PANE_LINE_BORDER_THICKNESS);

	// Text Field.
	private static final int TEXTFIELD_BUFFERED_ZONE_WIDTH = 5;
	private static final int TEXTFIELD_WIDTH = COMMAND_BAR_WIDTH - 2
			* TEXTFIELD_BUFFERED_ZONE_WIDTH;
	private static final int TEXTFIELD_HEIGHT = COMMAND_BAR_HEIGHT - 2
			* TEXTFIELD_BUFFERED_ZONE_WIDTH;

	private static final Color FOCUS_TEXTFIELD_OUTER_LINE_BORDER_COLOR = new Color(
			30, 144, 255);
	private static final int FOCUS_TEXTFIELD_OUTER_LINE_BORDER_THICKNESS = 2;
	private static final Border FOCUSED_TEXTFIELD_OUTER_LINE_BORDER = new LineBorder(
			FOCUS_TEXTFIELD_OUTER_LINE_BORDER_COLOR,
			FOCUS_TEXTFIELD_OUTER_LINE_BORDER_THICKNESS);
	private static final Color FOCUSED_TEXTFIELD_INNER_BEVEL_BORDER_SHADOW_COLOR = new Color(
			200, 200, 200);
	private static final Border FOCUSED_TEXTFIELD_INNER_BEVEL_BORDER = new BevelBorder(
			BevelBorder.LOWERED, Color.WHITE, Color.WHITE, Color.WHITE,
			FOCUSED_TEXTFIELD_INNER_BEVEL_BORDER_SHADOW_COLOR);
	private static final Border FOCUSED_TEXTFIELD_BORDER = new CompoundBorder(
			FOCUSED_TEXTFIELD_OUTER_LINE_BORDER,
			FOCUSED_TEXTFIELD_INNER_BEVEL_BORDER);

	private static final Color UNFOCUSED_TEXTFIELD_LINE_BORDER_COLOR = new Color(
			192, 192, 192);
	private static final int UNFOCUSED_TEXTFIELD_LINE_BORDER_THICKNESS = 1;
	private static final Border UNFOCUSED_TEXTFIELD_BORDER = new LineBorder(
			UNFOCUSED_TEXTFIELD_LINE_BORDER_COLOR,
			UNFOCUSED_TEXTFIELD_LINE_BORDER_THICKNESS);

	private static final Font TEXTFIELD_FONT = new Font(
			"HelveticaNeueLT Com 45 Lt", Font.PLAIN, 20);

	// Others.
	private static final int BACKSPACE_ASCII_CODE = 8;
	private static final int DELETE_ASCII_CODE = 127;
	private static final int LOWEST_ALLOWED_CHAR_ASCII_CODE = 32;
	private static final int HIGHEST_ALLOWED_CHAR_ASCII_CODE = 126;

	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	private GUIManager GUI;
	private JPanel contentPane;
	private JTextField textField;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * Creates a Command Bar frame.
	 * 
	 * @param GUI
	 *            the GUIManager to which this Help Window belongs to* @param x
	 * @param x
	 *            the x coordinate of the Command Bar
	 * @param y
	 *            the y coordinate of the Command Bar
	 */
	public CommandBar(final GUIManager GUI, int x, int y) {
		this.GUI = GUI;

		configureCommandBarFrame(x, y);
		createAndSetContentPaneToCommandBarFrame();
		createAndAddTextFieldToContentPane();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * Clears the Command Bar's text field.
	 */
	public void clearCommand() {
		textField.setText("");
	}

	/**
	 * Gets the Command Bar's input text.
	 * 
	 * @return the input text
	 */
	public String getCommand() {
		return textField.getText();
	}

	/**
	 * Requests focus on the Command Bar.
	 */
	@Override
	public void requestFocus() {
		textField.requestFocus();
	}

	/**
	 * Sets the command in the Command Bar.
	 * 
	 * @param command
	 *            the command to be set
	 */
	public void setCommand(String command) {
		textField.setText(command);
	}

	private void configureCommandBarFrame(int x, int y) {
		setUndecorated(true);
		setTitle(GUIManager.APPLICATION_TITLE);
		setIconImage(GUIManager.ICON_IMAGE);
		setLocation(x, y);
		setSize(COMMAND_BAR_WIDTH, COMMAND_BAR_HEIGHT);
		addListenersToCommandBar();
	}

	private void addListenersToCommandBar() {
		addMouseListener(GUI.mouseListener);
		addMouseMotionListener(GUI.mouseListener);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				setVisible(false);
			}
		});
		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent event) {
				textField.requestFocusInWindow();
				textField.setBorder(FOCUSED_TEXTFIELD_BORDER);
			}

			@Override
			public void windowLostFocus(WindowEvent event) {
				textField.setBorder(UNFOCUSED_TEXTFIELD_BORDER);
			}
		});
	}

	private void createAndSetContentPaneToCommandBarFrame() {
		contentPane = new JPanel();
		contentPane.setBorder(CONTENT_PANE_BORDER);
		contentPane.setLayout(null);
		setContentPane(contentPane);
	}

	private void createAndAddTextFieldToContentPane() {
		textField = new JTextField();
		textField.setLocation(TEXTFIELD_BUFFERED_ZONE_WIDTH,
				TEXTFIELD_BUFFERED_ZONE_WIDTH);
		textField.setSize(TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT);
		textField.setBorder(FOCUSED_TEXTFIELD_BORDER);
		textField.setFont(TEXTFIELD_FONT);
		textField.setFocusTraversalKeysEnabled(false);

		// Manually adjust the vertical position of the text because the font
		// type makes the text
		// no longer at the center of the textfield. Also adjust the horizontal
		// position.
		textField.setUI(new BasicTextFieldUI() {
			@Override
			public View create(Element element) {
				return new FieldView(element) {
					@Override
					protected Shape adjustAllocation(Shape shape) {
						if (shape == null) {
							return null;
						}

						Rectangle bounds = shape.getBounds();
						int height = bounds.height;
						int width = bounds.width;
						int y = bounds.y;
						int vspan = (int) getPreferredSpan(Y_AXIS);
						int hspan = (int) getPreferredSpan(X_AXIS);

						bounds = (Rectangle) super.adjustAllocation(shape);

						// This if-statement is where we adjust the vertical
						// alignment
						if (height != vspan) {
							int slop = height - vspan;

							bounds.y = y + slop - 1;
							bounds.height -= slop;
						}

						// Adjust the horizontal alignment.
						if (hspan < width) {
							bounds.x = 7;
						}

						return bounds;
					}
				};
			}
		});

		addListenersToTextField();
		addKeyBindingsToTextField();
		contentPane.add(textField);
	}

	private void addListenersToTextField() {
		textField.addMouseListener(GUI.mouseListener);
		textField.addMouseMotionListener(GUI.mouseListener);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				GUI.executeCommandAndDisplayOuput(textField.getText(), false);
			}
		});
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				int keyCode = keyEvent.getKeyChar();
				if (keyCode == BACKSPACE_ASCII_CODE
						|| keyCode == DELETE_ASCII_CODE
						|| (keyCode >= LOWEST_ALLOWED_CHAR_ASCII_CODE && keyCode <= HIGHEST_ALLOWED_CHAR_ASCII_CODE)) {
					GUI.executeCommandAndDisplayOuput(textField.getText(), true);
				}
			}
		});
	}

	private void addKeyBindingsToTextField() {
		InputMap textFieldInputMap = textField
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap textFieldActionMap = textField.getActionMap();

		textFieldInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
				"onTabFocusOnResultPane");
		textFieldActionMap
				.put("onTabFocusOnResultPane", onTabFocusOnResultPane);

		textFieldInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"onEscapeCloseGUI");
		textFieldActionMap.put("onEscapeCloseGUI", onEscapeCloseGUI);
	}

	// -------------------------------------------------------------------------
	// Actions constants.
	// -------------------------------------------------------------------------
	private final Action onTabFocusOnResultPane = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			COMMAND_TYPE currentCommand = GUI.getCurrentCommand();

			if (currentCommand == COMMAND_TYPE.DISPLAY) {
				// Call the executor so that it can keep track the current
				// result table
				// as the last displayed result table.
				GUI.executeCommandAsEntered(textField.getText());
				GUI.setFocusOnResultWindow();
			} else if (currentCommand == COMMAND_TYPE.INCOMPLETE
					|| currentCommand == COMMAND_TYPE.INVALID
					|| textField.getText().length() == 0) {
				GUI.setFocusOnResultWindow();
			}
		}
	};

	private final Action onEscapeCloseGUI = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			GUI.setVisible(false);
			GUI.enableKeyHook();
		}
	};
}