//@author A0059827N
package cookiejar.gui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cookiejar.Launcher;
import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.common.Result;
import cookiejar.logic.Executor;

/**
 * This class is the logic of the entire GUI component which coordinates all
 * sub-components.
 */
@SuppressWarnings("serial")
public class GUIManager {
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------
	private static final int APPLICATION_INITIAL_Y = 100;

	// Positions. GUI components' relative positions to the commandBar's one.
	protected static final int NOTIFICATION_RELATIVE_X = (CommandBar.COMMAND_BAR_WIDTH - NotificationWindow.NOTIFICATION_WINDOW_WIDTH) / 2;
	protected static final int NOTIFICATION_RELATIVE_Y = -2
			- NotificationWindow.NOTIFICATION_WINDOW_HEIGHT;
	protected static final int RESULT_PANE_RELATIVE_X = (CommandBar.COMMAND_BAR_WIDTH - ResultWindow.RESULT_WINDOW_WIDTH) / 2;
	protected static final int RESULT_PANE_RELATIVE_Y = CommandBar.COMMAND_BAR_HEIGHT + 2;
	protected static final int HELP_PANE_RELATIVE_X = RESULT_PANE_RELATIVE_X;
	protected static final int HELP_PANE_RELATIVE_Y = RESULT_PANE_RELATIVE_Y;

	// Others.
	protected static final String APPLICATION_TITLE = "Cookie Jar";
	protected static final String ICON_PATH = "/resources/images/icon.png";
	protected static final String TRAY_ICON_TOOLTIP = "Cookie Jar";
	protected static final Image ICON_IMAGE = getIconImage();

	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	private CommandBar commandBar;
	private NotificationWindow notificationWindow;
	private ResultWindow resultWindow;
	private HelpWindow helpWindow;
	private Executor executor;
	private Result result;
	private InactivityListener inactivityListener;
	private Launcher cookiejar;
	private WinKeyHook winKeyHook;

	// -------------------------------------------------------------------------
	// Constructors.
	// -------------------------------------------------------------------------
	/**
	 * Creates the GUI manager.
	 * 
	 * @param cookiejar
	 */
	public GUIManager(Launcher cookiejar) throws Exception {
		this.cookiejar = cookiejar;

		winKeyHook = new WinKeyHook(this);

		int initialX = getHorizontalCenterOfUserScreen();
		int initialY = APPLICATION_INITIAL_Y;

		// It is required to set the look and feel before instantiating
		// components.
		// Otherwise, fonts are not rendered as expected.
		setWindowLookAndFeel();
		commandBar = new CommandBar(this, initialX, initialY);
		notificationWindow = new NotificationWindow(this);
		resultWindow = new ResultWindow(this);
		helpWindow = new HelpWindow(this);
		executor = new Executor();
		inactivityListener = new InactivityListener(
				fadeOutResultWindowOrHelpWindow, 10);
		inactivityListener.setIntervalInMillis(60000);
		createTrayIcon(this);
		addFontsToGraphicsEnvironment();
	}

	// -------------------------------------------------------------------------
	// Methods.
	// -------------------------------------------------------------------------
	/**
	 * Returns the main frame on which the GUI components are built.
	 * 
	 * @return the main frame
	 */
	public JFrame getMainFrame() {
		return commandBar;
	}

	/**
	 * Returns the current command typed in by the user.
	 * 
	 * @return the current command
	 */
	public COMMAND_TYPE getCurrentCommand() {
		if (result == null) {
			return COMMAND_TYPE.INCOMPLETE;
		} else {
			return result.getCommand();
		}
	}

	/**
	 * Enables the shortcut key hook.
	 */
	public void enableKeyHook() {
		winKeyHook.registerGlobalKeyHook();
	}

	/**
	 * Returns the x coordinates of the GUI main frame.
	 * 
	 * @return the x coordinates of the GUI main frame.
	 */
	public int getX() {
		return commandBar.getX();
	}

	/**
	 * Returns the y coordinates of the GUI main frame.
	 * 
	 * @return the y coordinates of the GUI main frame.
	 */
	public int getY() {
		return commandBar.getY();
	}

	/**
	 * Determines whether the GUI is visible.
	 * 
	 * @return <code>true</code> if the component is visible, <code>false</code>
	 *         otherwise
	 */
	public boolean isVisible() {
		return commandBar.isVisible();
	}

	/**
	 * Shows or hides the GUI depending on the value of parameter <code>b</code>
	 * .
	 * 
	 * @param b
	 *            if <code>true</code>, shows the GUI; otherwise, hides the GUI
	 */
	public void setVisible(boolean b) {

		commandBar.setVisible(b);
		notificationWindow.setVisible(false);
		resultWindow.setVisible(false);
	}

	/**
	 * Set the location of the application GUI. All the components' locations
	 * are set relative the command bar's location (its top left corner).
	 * 
	 * @param x
	 *            the x coordinates of the GUI main frame.
	 * @param y
	 *            the y coordinates of the GUI main frame.
	 */
	public void setLocation(int x, int y) {

		commandBar.setLocation(x, y);
		notificationWindow.setLocation(x + NOTIFICATION_RELATIVE_X, y
				+ NOTIFICATION_RELATIVE_Y);
		resultWindow.setLocation(x + RESULT_PANE_RELATIVE_X, y
				+ RESULT_PANE_RELATIVE_Y);
		helpWindow.setLocation(x + HELP_PANE_RELATIVE_X, y
				+ HELP_PANE_RELATIVE_Y);
	}

	/**
	 * Sets the input command.
	 * 
	 * @param commandText
	 *            the command to be set
	 */
	public void setCommand(String commandText) {
		commandBar.setCommand(commandText);
	}

	/**
	 * Sets the focus on the command bar.
	 */
	public void setFocusOnCommandBar() {
		commandBar.requestFocus();
	}

	/**
	 * Sets the focus on the result window.
	 */
	public void setFocusOnResultWindow() {
		resultWindow.requestFocus();
	}

	/**
	 * Executes a command as if it is entered by the user.
	 * 
	 * @param command
	 *            the command to be executed
	 */
	public void executeCommandAsEntered(String command) {
		result = executor.process(command, false);
	}

	/**
	 * Executes a command and display the outcome: notification message and the
	 * result table.
	 * 
	 * @param command
	 *            the command to be executed
	 * @param isInstant
	 *            <code>true</code> if the command is executed as "instant"
	 *            feature; otherwise, the command is executed as user presses
	 *            Enter key.
	 */
	public void executeCommandAndDisplayOuput(String command, boolean isInstant) {
		String notificationMessage;
		Vector<Event> events;

		result = executor.process(command, isInstant);
		notificationMessage = result.getNotification();
		events = result.getEvents();

		
		displayNotification(notificationMessage);
		if (result.getCommand() == COMMAND_TYPE.HELP) {
			displayHelp();
		} else {
			displayResult(events);
		}
		
		if (!isInstant) {
			if (result.isSuccess()) {
				commandBar.clearCommand();
			}

			if (result.isExit()) {
				cookiejar.terminate();
			}
		}

		if (result.getFocusedEventID() > 0) {
			// EventID and rowID used in result table are different by 1.
			// because EventID starts at 1 while rowID at 0.
			resultWindow.setSelectedRow(result.getFocusedEventID() - 1);
		} else {
			resultWindow.setSelectedRow(ResultWindow.NO_HIGHLIGHTED_ROW);
		}
	}

	/**
	 * Displays the ErrorMessageDialog.
	 * 
	 * @param message
	 *            the error message to be displayed
	 */
	public static void showErrorMessageDialogForApplicationTermination(
			String message) {
		JOptionPane.showMessageDialog(null, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private int getHorizontalCenterOfUserScreen() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenDimension.getWidth();

		return (screenWidth - CommandBar.COMMAND_BAR_WIDTH) / 2;
	}

	private void displayNotification(String notificationMessage) {
		if (notificationMessage != null && notificationMessage.length() > 0) {
			notificationWindow.setMessage(notificationMessage);
			notificationWindow.fadeInAndOut(2000, 10, 0.05f, 100, 0.05f);
		} else {
			notificationWindow.setVisible(false);
		}
	}

	private void displayResult(Vector<Event> events) {
		if (events != null && events.size() > 0) {
			helpWindow.setVisible(false);
			resultWindow.setResultTable(events);
			inactivityListener.stop();
			resultWindow.setVisible(true);
			inactivityListener.start();
		} else {
			resultWindow.setVisible(false);
		}
	}

	private void displayHelp() {
		resultWindow.setVisible(false);
		helpWindow.setVisible(true);
	}

	private void createTrayIcon(final GUIManager applicationGUI)
			throws Exception {
		if (!SystemTray.isSupported()) {
			throw new Exception("System does not support tray icon.");
		}

		final SystemTray tray = SystemTray.getSystemTray();
		final TrayIcon trayIcon = new TrayIcon(ICON_IMAGE);
		final PopupMenu popup = new PopupMenu();
		MenuItem displayItem = new MenuItem("Open");
		MenuItem exitItem = new MenuItem("Exit");

		popup.add(displayItem);
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip(TRAY_ICON_TOOLTIP);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			throw new Exception("Unable to set tray icon.");
		}

		trayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationGUI.setVisible(true);
			}
		});

		displayItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationGUI.setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				System.exit(0);
			}
		});
	}

	private static Image getIconImage() {
		URL imageURL = GUIManager.class.getResource(ICON_PATH);

		if (imageURL == null) {
			return null;
		} else {
			return (new ImageIcon(imageURL)).getImage();
		}
	}

	private void setWindowLookAndFeel() throws Exception {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			throw new Exception("Unable to load Window look and feel");
		} catch (IllegalAccessException ex) {
			throw new Exception("Unable to load Window look and feel");
		} catch (InstantiationException ex) {
			throw new Exception("Unable to load Window look and feel");
		} catch (ClassNotFoundException ex) {
			throw new Exception("Unable to load Window look and feel");
		}
	}

	private void addFontsToGraphicsEnvironment() throws Exception {
		GraphicsEnvironment graphicEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		try {
			graphicEnvironment
					.registerFont(Font.createFont(
							Font.TRUETYPE_FONT,
							GUIManager.class
									.getResourceAsStream("/resources/fonts/HelveticaNeueLTCom-Lt.ttf")));
			graphicEnvironment
					.registerFont(Font.createFont(
							Font.TRUETYPE_FONT,
							GUIManager.class
									.getResourceAsStream("/resources/fonts/HelveticaNeueLTCom-Md.ttf")));
			graphicEnvironment
					.registerFont(Font.createFont(
							Font.TRUETYPE_FONT,
							GUIManager.class
									.getResourceAsStream("/resources/fonts/HelveticaNeueLTCom-Roman.ttf")));
			graphicEnvironment
					.registerFont(Font.createFont(
							Font.TRUETYPE_FONT,
							GUIManager.class
									.getResourceAsStream("/resources/fonts/Consola.ttf")));
			graphicEnvironment
					.registerFont(Font.createFont(
							Font.TRUETYPE_FONT,
							GUIManager.class
									.getResourceAsStream("/resources/fonts/Segoe UI.ttf")));
			graphicEnvironment
					.registerFont(Font.createFont(
							Font.TRUETYPE_FONT,
							GUIManager.class
									.getResourceAsStream("/resources/fonts/Segoe UI SemiBold.ttf")));
		} catch (FontFormatException e) {
			throw new Exception("Unable to load required font.");
		} catch (IOException e) {
			throw new Exception("Unable to load required font.");
		}
	}

	// -------------------------------------------------------------------------
	// Event & Action constants.
	// -------------------------------------------------------------------------
	protected final MouseAdapter mouseListener = new MouseAdapter() {
		int initialAbsoluteMouseX, initialAbsoluteMouseY;
		int initialGUIAbsoluteCoordX, initialGUIAbsoluteCoordY;

		@Override
		public void mousePressed(MouseEvent mouseEvent) {
			if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
				initialAbsoluteMouseX = mouseEvent.getXOnScreen();
				initialAbsoluteMouseY = mouseEvent.getYOnScreen();

				initialGUIAbsoluteCoordX = getX();
				initialGUIAbsoluteCoordY = getY();
			}
		}

		@Override
		public void mouseDragged(MouseEvent mouseEvent) {
			if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
				setLocation(mouseEvent.getXOnScreen() - initialAbsoluteMouseX
						+ initialGUIAbsoluteCoordX, mouseEvent.getYOnScreen()
						- initialAbsoluteMouseY + initialGUIAbsoluteCoordY);
			}
		}
	};

	private final Action fadeOutResultWindowOrHelpWindow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			resultWindow.fadeOut(10, 0.05f);
			helpWindow.fadeOut(10, 0.05f);
		}
	};
}