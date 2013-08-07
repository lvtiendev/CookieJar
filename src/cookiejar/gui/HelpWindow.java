//@author A0059827N
package cookiejar.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * This class creates the help window which is shown when help command is
 * called.
 */
@SuppressWarnings("serial")
public class HelpWindow extends FadableWindow {
	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------
	// Help Pane.
	protected static final int HELP_WINDOW_WIDTH = ResultWindow.RESULT_WINDOW_WIDTH;
	protected static final int HELP_WINDOW_HEIGHT = ResultWindow.MAX_RESULT_WINDOW_HEIGHT;
	
	// Content Pane.
	private static final Color CONTENT_PANE_LINE_BORDER_COLOR = new Color(200, 200, 200);
	private static final int CONTENT_PANE_LINE_BORDER_THICKNESS = 1;
	private static final Border CONTENT_PANE_BORDER = new LineBorder(CONTENT_PANE_LINE_BORDER_COLOR, CONTENT_PANE_LINE_BORDER_THICKNESS);
	
	// Command Descriptions.
	private static final int COMMAND_LABEL_HEIGHT = 23;
	private static final int COMMAND_LABEL_WIDTH = 80;
	private static final int COMMAND_LABEL_X = 10;
	private static final int COMMAND_VERTICAL_SPACING = 40;
	
	private static final int ADD_LABEL_Y = 10;
	private static final int DELETE_LABEL_Y = ADD_LABEL_Y + COMMAND_VERTICAL_SPACING;
	private static final int EDIT_LABEL_Y = DELETE_LABEL_Y + COMMAND_VERTICAL_SPACING;
	private static final int DISPLAY_LABEL_Y = EDIT_LABEL_Y + COMMAND_VERTICAL_SPACING;
	private static final int UNDO_LABEL_Y = DISPLAY_LABEL_Y + COMMAND_VERTICAL_SPACING;
	private static final int REDO_LABEL_Y = UNDO_LABEL_Y + COMMAND_VERTICAL_SPACING;
	private static final int EXIT_LABEL_Y = REDO_LABEL_Y + COMMAND_VERTICAL_SPACING;
	
	private static final int DESCRIPTION_LONG_WIDTH = 500;
	private static final int DESCRIPTION_SHORT_WIDTH = 50;
	private static final int DESCRIPTION_X = 85;
	
	private static final Font COMMAND_LABEL_FONT = new Font("HelveticaNeueLT Com 65 Md", Font.PLAIN, 15);
	private static final Font COMMAND_SYNTAX_FONT = new Font("Consolas", Font.PLAIN, 14);
	
	private static final String ADD_LABEL_TEXT = "ADD";
	private static final String DELETE_LABEL_TEXT = "DELETE";
	private static final String EDIT_LABEL_TEXT = "EDIT";
	private static final String DISPLAY_LABEL_TEXT = "DISPLAY";
	private static final String UNDO_LABEL_TEXT = "UNDO";
	private static final String REDO_LABEL_TEXT = "REDO";
	private static final String EXIT_LABEL_TEXT = "EXIT";
	
	private static final String ADD_SYNTAX_TEXT = "add {Name} [[StartDatetime] {EndDatetime}] [#Tags]";
	private static final String DELETE_SYNTAX_TEXT = "delete {ID}";
	private static final String EDIT_SYNTAX_TEXT = "edit {ID} [Name] [[StartDatetime] {EndDatetime}] [#Tags]";
	private static final String DISPLAY_SYNTAX_TEXT = "display [KeyWord] [[StartDatetime] {EndDatetime}] [#Tags]";
	private static final String UNDO_SYNTAX_TEXT = "undo";
	private static final String REDO_SYNTAX_TEXT = "redo";
	private static final String EXIT_SYNTAX_TEXT = "exit";
	
	// Datetime Format Panel.
	private static final Font DATETIME_FORMAT_PANEL_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Font DATETIME_FORMAT_PANEL_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
	private static final Font DATETIME_FORMAT_PANEL_TITLE_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 13);
	
	private static final int DATETIME_FORMAT_PANEL_X = 160;
	private static final int DATETIME_FORMAT_PANEL_Y = 165;
	private static final int DATETIME_FORMAT_PANEL_WIDTH = 475;
	private static final int DATETIME_FORMAT_PANEL_HEIGHT = 145;
	private static final LineBorder DATETIME_FORMAT_PANEL_BORDER = new LineBorder(new Color(0, 0, 0));
	
	// The following measurement is with respect to the Date Time Format Panel.
	private static final int DATE_FORMAT_LABEL_X = 10;
	private static final int DATE_FORMAT_LABEL_Y = 10;
	private static final int DATE_FORMAT_WIDTH = 80;
	private static final int DATE_FORMAT_HEIGHT = 16;
	private static final int DATE_FORMAT_DESCRIPTION_X = 90;
	private static final int DATE_FORMAT_DESCRIPTION_WIDTH = 195;
	private static final int FORMAL_DATE_Y = 35;
	private static final int FORMAL_DATE_HEIGHT = 32;
	private static final int RELATIVE_DATE_Y = 80;
	private static final int RELATIVE_DATE_HEIGHT = 32;
	private static final int TIME_FORMAT_LABEL_X = 295;
	private static final int TIME_FORMAT_LABEL_Y = DATE_FORMAT_LABEL_Y;
	private static final int TIME_FORMAT_WIDTH = DATE_FORMAT_WIDTH;
	private static final int TIME_FORMAT_HEIGHT = DATE_FORMAT_HEIGHT;
	private static final int TIME_FORMAT_DESCRIPTION_X = 370;
	private static final int TIME_FORMAT_DESCRIPTION_WIDTH = 95;
	private static final int FORMAL_TIME_Y = FORMAL_DATE_Y;
	private static final int FORMAL_TIME_HEIGHT = 16;
	private static final int RELATIVE_TIME_Y = 60;
	private static final int RELATIVE_TIME_HEIGHT = 80;
	
	private static final String DATE_FORMAT_TITLE_TEXT = "Date Format";
	private static final String FORMAL_DATE_LABEL_TEXT = "Formal Date";
	private static final String FORMAL_DATE_DESCRIPTION = "<html><p>3 January 2012, 3 Jan 12, 3rd Jan, 3rd Jan, 03/01/12, 3-1-12, 01/12</p></html>";
	private static final String RELATIVE_DATE_LABEL_TEXT = "Relative Date";
	private static final String RELATIVE_DATE_DESCRIPTION = "<html><p>tomorrow, tmr, tml, yesterday, next monday, last mon, next year/month</p></html>";

	private static final String TIME_FORMAT_TITLE_TEXT = "Time Format";
	private static final String FORMAL_TIME_LABEL_TEXT = "Formal Time";
	private static final String FORMAL_TIME_DESCRIPTION = "<html><p>7:25, 1800, 6pm</p></html>";
	private static final String RELATIVE_TIME_LABEL_TEXT = "Relative Time";
	private static final String RELATIVE_TIME_DESCRIPTION = "<html><p>morning (8am), noon (12pm), evening (7pm), night (8pm)</p></html>";
	
	//-------------------------------------------------------------------------
	// Variables
	//-------------------------------------------------------------------------
	private GUIManager GUI;
	private JPanel contentPane;
	
	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	/**
	 * Creates a Help Window.
	 * 
	 * @param GUI
	 *            the GUIManager to which this Help Window belongs to
	 */
	public HelpWindow(GUIManager GUI) {
		super(GUI.getMainFrame());
		
		this.GUI = GUI;
		configureHelpWindow();
		createAndSetContentPaneToHelpWindow();
	}
	
	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------
	private void configureHelpWindow() {
		int x = GUI.getX() + GUIManager.HELP_PANE_RELATIVE_X;
		int y = GUI.getY() + GUIManager.HELP_PANE_RELATIVE_Y;
		
		setLocation(x, y);
		setSize(HELP_WINDOW_WIDTH, HELP_WINDOW_HEIGHT);
		setFocusable(false);
		setAutoRequestFocus(false);
		addListenersToHelpPane();
	}
	
	private void addListenersToHelpPane() {
		addMouseListener(GUI.mouseListener);
    	addMouseMotionListener(GUI.mouseListener);
	}
	
	private void createAndSetContentPaneToHelpWindow() {
		contentPane = new JPanel();
		contentPane.setBorder(CONTENT_PANE_BORDER);
		contentPane.setBackground(new Color(250, 250, 210));
		contentPane.setLayout(null);

		createAndAddCommandsDescriptionsToContentPane();
		createAndAddSupportedDatetimeFormatPanelToContentPane();
		
		setContentPane(contentPane);
	}

	private void createAndAddCommandsDescriptionsToContentPane() {
		createAndAddAddCommandDescriptionsToContentPane();
		createAndAddDeleteCommandDescriptionToContentPane();
		createAndAddEditCommandDescriptionToContentPane();
		createAndAddDisplayCommandDescriptionToContentPane();
		createAndAddUndoCommandDescriptionToContentPane();
		createAndAddRedoCommandDescriptionToContentPane();
		createAndAddExitCommandDescriptionToContentPane();
	}

	private void createAndAddAddCommandDescriptionsToContentPane() {
		JLabel addCommand = new JLabel(ADD_LABEL_TEXT);
		addCommand.setFont(COMMAND_LABEL_FONT);
		addCommand.setBounds(COMMAND_LABEL_X, ADD_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(addCommand);
		
		JLabel addSyntaxDescription = new JLabel(ADD_SYNTAX_TEXT);
		addSyntaxDescription.setFont(COMMAND_SYNTAX_FONT);
		addSyntaxDescription.setBounds(DESCRIPTION_X, ADD_LABEL_Y, DESCRIPTION_LONG_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(addSyntaxDescription);
	}
	
	private void createAndAddDeleteCommandDescriptionToContentPane() {
		JLabel deleteCommand = new JLabel(DELETE_LABEL_TEXT);
		deleteCommand.setFont(COMMAND_LABEL_FONT);
		deleteCommand.setBounds(COMMAND_LABEL_X, DELETE_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(deleteCommand);
		
		JLabel deleteSyntaxDescription = new JLabel(DELETE_SYNTAX_TEXT);
		deleteSyntaxDescription.setFont(COMMAND_SYNTAX_FONT);
		deleteSyntaxDescription.setBounds(DESCRIPTION_X, DELETE_LABEL_Y, DESCRIPTION_LONG_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(deleteSyntaxDescription);
	}
	
	private void createAndAddEditCommandDescriptionToContentPane() {
		JLabel editCommand = new JLabel(EDIT_LABEL_TEXT);
		editCommand.setFont(COMMAND_LABEL_FONT);
		editCommand.setBounds(COMMAND_LABEL_X, EDIT_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(editCommand);
		
		JLabel editSynstaxDescription = new JLabel(EDIT_SYNTAX_TEXT);
		editSynstaxDescription.setFont(COMMAND_SYNTAX_FONT);
		editSynstaxDescription.setBounds(DESCRIPTION_X, EDIT_LABEL_Y, DESCRIPTION_LONG_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(editSynstaxDescription);
	}

	private void createAndAddDisplayCommandDescriptionToContentPane() {
		JLabel displayCommand = new JLabel(DISPLAY_LABEL_TEXT);
		displayCommand.setFont(COMMAND_LABEL_FONT);
		displayCommand.setBounds(COMMAND_LABEL_X, DISPLAY_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(displayCommand);
		
		JLabel displaySyntaxDescription = new JLabel(DISPLAY_SYNTAX_TEXT);
		displaySyntaxDescription.setFont(COMMAND_SYNTAX_FONT);
		displaySyntaxDescription.setBounds(DESCRIPTION_X, DISPLAY_LABEL_Y, DESCRIPTION_LONG_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(displaySyntaxDescription);
	}

	private void createAndAddUndoCommandDescriptionToContentPane() {
		JLabel undoCommand = new JLabel(UNDO_LABEL_TEXT);
		undoCommand.setFont(COMMAND_LABEL_FONT);
		undoCommand.setBounds(COMMAND_LABEL_X, UNDO_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(undoCommand);
		
		JLabel undoSyntaxDescription = new JLabel(UNDO_SYNTAX_TEXT);
		undoSyntaxDescription.setFont(COMMAND_SYNTAX_FONT);
		undoSyntaxDescription.setBounds(DESCRIPTION_X, UNDO_LABEL_Y, DESCRIPTION_SHORT_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(undoSyntaxDescription);
	}

	private void createAndAddRedoCommandDescriptionToContentPane() {
		JLabel redoCommand = new JLabel(REDO_LABEL_TEXT);
		redoCommand.setFont(COMMAND_LABEL_FONT);
		redoCommand.setBounds(COMMAND_LABEL_X, REDO_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(redoCommand);
		
		JLabel redoSyntaxDescription = new JLabel(REDO_SYNTAX_TEXT);
		redoSyntaxDescription.setFont(COMMAND_SYNTAX_FONT);
		redoSyntaxDescription.setBounds(DESCRIPTION_X, REDO_LABEL_Y, DESCRIPTION_SHORT_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(redoSyntaxDescription);
	}

	private void createAndAddExitCommandDescriptionToContentPane() {
		JLabel exitCommand = new JLabel(EXIT_LABEL_TEXT);
		exitCommand.setFont(COMMAND_LABEL_FONT);
		exitCommand.setBounds(COMMAND_LABEL_X, EXIT_LABEL_Y, COMMAND_LABEL_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(exitCommand);
		
		JLabel exitSyntaxDescription = new JLabel(EXIT_SYNTAX_TEXT);
		exitSyntaxDescription.setFont(COMMAND_SYNTAX_FONT);
		exitSyntaxDescription.setBounds(DESCRIPTION_X, EXIT_LABEL_Y, DESCRIPTION_SHORT_WIDTH, COMMAND_LABEL_HEIGHT);
		contentPane.add(exitSyntaxDescription);
	}

	private void createAndAddSupportedDatetimeFormatPanelToContentPane() {
		JPanel supportedDateTimeFormat = new JPanel();
		supportedDateTimeFormat.setBorder(DATETIME_FORMAT_PANEL_BORDER);
		supportedDateTimeFormat.setOpaque(false);
		supportedDateTimeFormat.setBounds(DATETIME_FORMAT_PANEL_X, DATETIME_FORMAT_PANEL_Y, DATETIME_FORMAT_PANEL_WIDTH, DATETIME_FORMAT_PANEL_HEIGHT);
		supportedDateTimeFormat.setLayout(null);
		createAndAddDateFormatToSupportedDatetimeFormatPanel(supportedDateTimeFormat);
		createAndAddTimeFormatToSupportedDatetimeFormatPanel(supportedDateTimeFormat);
		contentPane.add(supportedDateTimeFormat);
	}

	private void createAndAddDateFormatToSupportedDatetimeFormatPanel(JPanel supportedDateTimeFormat) {
		JLabel dateFormatTitle = new JLabel(DATE_FORMAT_TITLE_TEXT);
		dateFormatTitle.setFont(DATETIME_FORMAT_PANEL_TITLE_FONT);
		dateFormatTitle.setBounds(DATE_FORMAT_LABEL_X, DATE_FORMAT_LABEL_Y, DATE_FORMAT_WIDTH, DATE_FORMAT_HEIGHT);
		supportedDateTimeFormat.add(dateFormatTitle);
		
		JLabel formalDate = new JLabel(FORMAL_DATE_LABEL_TEXT);
		formalDate.setFont(DATETIME_FORMAT_PANEL_LABEL_FONT);
		formalDate.setBounds(DATE_FORMAT_LABEL_X, FORMAL_DATE_Y, DATE_FORMAT_WIDTH, DATE_FORMAT_HEIGHT);
		supportedDateTimeFormat.add(formalDate);
		
		JLabel formalDateDescription = new JLabel(FORMAL_DATE_DESCRIPTION);
		formalDateDescription.setFont(DATETIME_FORMAT_PANEL_DESCRIPTION_FONT);
		formalDateDescription.setBounds(DATE_FORMAT_DESCRIPTION_X, FORMAL_DATE_Y, DATE_FORMAT_DESCRIPTION_WIDTH, FORMAL_DATE_HEIGHT);
		supportedDateTimeFormat.add(formalDateDescription);
		
		JLabel relativeDate = new JLabel(RELATIVE_DATE_LABEL_TEXT);
		relativeDate.setFont(DATETIME_FORMAT_PANEL_LABEL_FONT);
		relativeDate.setBounds(DATE_FORMAT_LABEL_X, RELATIVE_DATE_Y, DATE_FORMAT_WIDTH, DATE_FORMAT_HEIGHT);
		supportedDateTimeFormat.add(relativeDate);
		
		JLabel relativeDateDescription = new JLabel(RELATIVE_DATE_DESCRIPTION);
		relativeDateDescription.setFont(DATETIME_FORMAT_PANEL_DESCRIPTION_FONT);
		relativeDateDescription.setBounds(DATE_FORMAT_DESCRIPTION_X, RELATIVE_DATE_Y, DATE_FORMAT_DESCRIPTION_WIDTH, RELATIVE_DATE_HEIGHT);
		supportedDateTimeFormat.add(relativeDateDescription);
	}
	

	private void createAndAddTimeFormatToSupportedDatetimeFormatPanel(JPanel supportedDateTimeFormat) {
		JLabel timeFormatTitle = new JLabel(TIME_FORMAT_TITLE_TEXT);
		timeFormatTitle.setFont(DATETIME_FORMAT_PANEL_TITLE_FONT);
		timeFormatTitle.setBounds(TIME_FORMAT_LABEL_X, TIME_FORMAT_LABEL_Y, TIME_FORMAT_WIDTH, TIME_FORMAT_HEIGHT);
		supportedDateTimeFormat.add(timeFormatTitle);
		
		JLabel formalTime = new JLabel(FORMAL_TIME_LABEL_TEXT);
		formalTime.setFont(DATETIME_FORMAT_PANEL_LABEL_FONT);
		formalTime.setBounds(TIME_FORMAT_LABEL_X, FORMAL_TIME_Y, TIME_FORMAT_WIDTH, TIME_FORMAT_HEIGHT);
		supportedDateTimeFormat.add(formalTime);
		
		JLabel formalTimeDescription = new JLabel(FORMAL_TIME_DESCRIPTION);
		formalTimeDescription.setFont(DATETIME_FORMAT_PANEL_DESCRIPTION_FONT);
		formalTimeDescription.setBounds(TIME_FORMAT_DESCRIPTION_X, FORMAL_TIME_Y, TIME_FORMAT_DESCRIPTION_WIDTH, FORMAL_TIME_HEIGHT);
		supportedDateTimeFormat.add(formalTimeDescription);
		
		JLabel relativeTime = new JLabel(RELATIVE_TIME_LABEL_TEXT);
		relativeTime.setFont(DATETIME_FORMAT_PANEL_LABEL_FONT);
		relativeTime.setBounds(TIME_FORMAT_LABEL_X, RELATIVE_TIME_Y, TIME_FORMAT_WIDTH, TIME_FORMAT_HEIGHT);
		supportedDateTimeFormat.add(relativeTime);
		
		JLabel relativeTimeDescription = new JLabel(RELATIVE_TIME_DESCRIPTION);
		relativeTimeDescription.setFont(DATETIME_FORMAT_PANEL_DESCRIPTION_FONT);
		relativeTimeDescription.setBounds(TIME_FORMAT_DESCRIPTION_X, RELATIVE_TIME_Y, TIME_FORMAT_DESCRIPTION_WIDTH, RELATIVE_TIME_HEIGHT);
		supportedDateTimeFormat.add(relativeTimeDescription);
	}
}