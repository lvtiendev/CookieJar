//@author A0059827N
package cookiejar.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class creates the notification window which displayed the notification
 * message informing the user whether the transaction is successfully performed
 * or whether an error occurs.
 */
@SuppressWarnings("serial")
public class NotificationWindow extends FadableWindow {
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------
	protected static final int NOTIFICATION_WINDOW_WIDTH = ResultWindow.RESULT_WINDOW_WIDTH;
	protected static final int NOTIFICATION_WINDOW_HEIGHT = 30;

	private static final Color CONTENT_PANE_BACKGROUND_COLOR = new Color(250,
			250, 210);
	private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.PLAIN,
			15);

	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	private GUIManager GUI;
	private JPanel contentPane;
	private JLabel displayedMessage;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * Creates a Notification Window.
	 * 
	 * @param GUI
	 *            the GUIManager to which this Notification Window belongs to
	 */
	public NotificationWindow(GUIManager GUI) {
		super(GUI.getMainFrame());

		this.GUI = GUI;
		configureNotificationWindow();
		createAndSetContentPaneToNotificationWindow();
		createAndAddDisplayedMessageToNotificationWindow();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * Sets the notification message.
	 * 
	 * @param message
	 *            the message to be displayed
	 */
	public void setMessage(String message) {
		displayedMessage.setText(message);
	}

	private void configureNotificationWindow() {
		int x = GUI.getX() + GUIManager.NOTIFICATION_RELATIVE_X;
		int y = GUI.getY() + GUIManager.NOTIFICATION_RELATIVE_Y;

		setLocation(x, y);
		setSize(NOTIFICATION_WINDOW_WIDTH, NOTIFICATION_WINDOW_HEIGHT);
		addMouseListener(GUI.mouseListener);
		addMouseMotionListener(GUI.mouseListener);
	}

	private void createAndSetContentPaneToNotificationWindow() {
		contentPane = new JPanel();
		contentPane.setBackground(CONTENT_PANE_BACKGROUND_COLOR);
		contentPane.setLayout(null);
		setContentPane(contentPane);
	}

	private void createAndAddDisplayedMessageToNotificationWindow() {
		displayedMessage = new JLabel();
		displayedMessage.setFont(MESSAGE_FONT);
		displayedMessage.setBounds(0, 0, NOTIFICATION_WINDOW_WIDTH,
				NOTIFICATION_WINDOW_HEIGHT);
		displayedMessage.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(displayedMessage);
	}
}