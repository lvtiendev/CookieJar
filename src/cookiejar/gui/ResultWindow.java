//@author A0059827N
package cookiejar.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cookiejar.common.COMMAND_TYPE;
import cookiejar.common.Event;
import cookiejar.gui.tablecellrenderer.DateTimeRenderer;
import cookiejar.gui.tablecellrenderer.DisplayedCodeRenderer;
import cookiejar.gui.tablecellrenderer.EventNameRenderer;
import cookiejar.gui.tablecellrenderer.HeaderRenderer;

/**
 * This class creates the result window which displays events in result table.
 * <p>
 * The result table consists of header table and rows table both of which are
 * special JTable with no header. Header table has only one row that contains
 * the header of the result table. Rows table contains the rows of the result
 * table.
 */
@SuppressWarnings("serial")
public class ResultWindow extends FadableWindow {
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------
	// Result Table.
	private static final int[] RESULT_TABLE_COLUMN_WIDTHS = { 35, 345, 120, 130 };
	private static final int RESULT_TABLE_WIDTH = Utilities
			.arraySum(RESULT_TABLE_COLUMN_WIDTHS);
	private static final int RESULT_TABLE_HEADER_HEIGHT = 30;
	private static final int RESULT_TABLE_ROW_HEIGHT = 55;
	private static final int RESULT_TABLE_BUFFERED_ZONE_WIDTH = 5;

	private static final String[] RESULT_TABLE_HEADER_TEXTS_IN_ARRAY = new String[] {
			"", "TASK", "START", "END" };
	private static final Vector<String> RESULT_TABLE_HEADER_TEXTS = Utilities
			.convertArrayToVector(RESULT_TABLE_HEADER_TEXTS_IN_ARRAY);
	private static final int NAME_TAGS_COLUMN_INDEX = 1;
	private static final int START_TIME_COLUMN_INDEX = 2;
	private static final int END_TIME_COLUMN_INDEX = 3;

	// Content Pane.
	private static Color CONTENT_PANE_LINE_BORDER_COLOR = new Color(200, 200,
			200);
	private static int CONTENT_PANE_LINE_BORDER_THICKNESS = 1;
	private static Border CONTENT_PANE_BORDER = new LineBorder(
			CONTENT_PANE_LINE_BORDER_COLOR, CONTENT_PANE_LINE_BORDER_THICKNESS);

	// Header Table.
	private static final Color HEADER_TABLE_LINE_BORDER_COLOR = new Color(150,
			150, 150);
	private static final int HEADER_TABLE_LINE_BORDER_THICKNESS = 1;
	private static final LineBorder HEADER_TABLE_BORDER = new LineBorder(
			HEADER_TABLE_LINE_BORDER_COLOR, HEADER_TABLE_LINE_BORDER_THICKNESS);

	// Scroll Pane.
	private static final int SCROLL_PANE_VERTICAL_SCROLL_BAR_DISPLAY_DURATION = 5000;
	private static final int MAX_NUM_OF_DISPLAYED_ROWS = 5;
	private static final int MAX_SCROLL_PANE_HEIGHT = MAX_NUM_OF_DISPLAYED_ROWS
			* RESULT_TABLE_ROW_HEIGHT;

	// Rows Table.
	private static final Color UNFOCUSED_ROWS_TABLE_LINE_BORDER_COLOR = new Color(
			150, 150, 150);
	private static final int UNFOCUSED_ROWS_TABLE_LINE_BORDER_THICKNESS = 1;
	private static final LineBorder UNFOCUSED_ROWS_TABLE_BORDER = new LineBorder(
			UNFOCUSED_ROWS_TABLE_LINE_BORDER_COLOR,
			UNFOCUSED_ROWS_TABLE_LINE_BORDER_THICKNESS);

	private static final Color FOCUSED_ROWS_TABLE_LINE_BORDER_COLOR = new Color(
			30, 144, 255);
	private static final int FOCUSED_ROWS_TABLE_LINE_BORDER_THICKNESS = 1;
	private static final LineBorder FOCUSED_ROWS_TABLE_BORDER = new LineBorder(
			FOCUSED_ROWS_TABLE_LINE_BORDER_COLOR,
			FOCUSED_ROWS_TABLE_LINE_BORDER_THICKNESS);

	private static final Dimension ROWS_TABLE_CELL_SPACING = new Dimension(0, 3);
	private static final Color ROWS_TABLE_GRID_COLOR = new Color(200, 200, 200);

	// Others.
	protected static final int NO_HIGHLIGHTED_ROW = -1;
	private static final int ARROW_DOWN_DISTANCE = 1;
	private static final int ARROW_UP_DISTANCE = -1;
	private static final int PAGE_DOWN_DISTANCE = 5;
	private static final int PAGE_UP_DISTANCE = -5;

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat
			.forPattern("dd MMM YYYY HH:mm");

	// Result Window.
	protected static final int RESULT_WINDOW_WIDTH = RESULT_TABLE_WIDTH + 2
			* RESULT_TABLE_BUFFERED_ZONE_WIDTH;
	protected static final int MAX_RESULT_WINDOW_HEIGHT = 2
			* RESULT_TABLE_BUFFERED_ZONE_WIDTH + RESULT_TABLE_HEADER_HEIGHT
			+ MAX_SCROLL_PANE_HEIGHT;

	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	private GUIManager GUI;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable headerTable, rowsTable;
	private NonEditableTableModel headerTableModel, rowsTableModel;
	private int currentSelectedRow, resultWindowHeight, scrollPaneHeight;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * Creates a Result Window.
	 * 
	 * @param GUI
	 *            the GUIManager to which this Notification Window belongs to
	 */
	public ResultWindow(GUIManager GUI) {
		super(GUI.getMainFrame());

		this.GUI = GUI;
		configureResultPane();
		createAndSetContentPaneToResultPane();
		createAndAddHeaderTableToContentPane();
		createAndAddScrollPaneToContentPane();
		createAndAddRowsTableToScrollPane();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * Requests focus on the Result Window which in turn focuses on the
	 * rowsTable.
	 */
	@Override
	public void requestFocus() {
		rowsTable.requestFocus();
		currentSelectedRow = adjustRowIDIfOutOfBound(currentSelectedRow);
	}

	/**
	 * Selects a row in the result table.
	 * 
	 * @param row
	 *            rowID starting from 1
	 */
	public void setSelectedRow(int row) {
		if (row < 0) {
			currentSelectedRow = NO_HIGHLIGHTED_ROW;
		} else {
			selectRow(row);
		}
	}

	/**
	 * Sets events to the result table
	 * 
	 * @param events
	 *            events to be displayed
	 */
	public void setResultTable(Vector<Event> events) {
		setDataToRowsTableModel(events);
		setTableWidths(rowsTable, RESULT_TABLE_COLUMN_WIDTHS);
		adjustResultWindowHeight(events.size());
	}

	/**
	 * Shows or hides this Window depending on the value of parameter <code>b</code>.
	 * 
	 * @param b
	 *            if true, shows this component; otherwise, hides this component
	 */
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);

		scrollBarFadingTimer.stop();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
	}

	private void configureResultPane() {
		int x = GUI.getX() + GUIManager.RESULT_PANE_RELATIVE_X;
		int y = GUI.getY() + GUIManager.RESULT_PANE_RELATIVE_Y;

		setLocation(x, y);
		setFocusable(false);
		setAutoRequestFocus(false);
		addListenersToResultPane();
	}

	private void addListenersToResultPane() {
		addMouseListener(GUI.mouseListener);
		addMouseMotionListener(GUI.mouseListener);
		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent event) {
				scrollPane.setBorder(FOCUSED_ROWS_TABLE_BORDER);
				if (currentSelectedRow < 0) {
					currentSelectedRow = 0;
				}
			}

			@Override
			public void windowLostFocus(WindowEvent event) {
				scrollPane.setBorder(UNFOCUSED_ROWS_TABLE_BORDER);
			}
		});
	}

	private void createAndSetContentPaneToResultPane() {
		contentPane = new JPanel();
		contentPane.setBorder(CONTENT_PANE_BORDER);
		contentPane.setLayout(null);
		setContentPane(contentPane);
	}

	/**
	 * HeaderTable is a special JTable with only 1 row which contains the header
	 * of the result table. NOTE: This JTable has no header.
	 */
	private void createAndAddHeaderTableToContentPane() {
		final HeaderRenderer headerRenderer = new HeaderRenderer();
		Vector<Vector<String>> rows = new Vector<Vector<String>>();

		headerTable = new JTable() {
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				return headerRenderer;
			}
		};

		headerTable.setLocation(RESULT_TABLE_BUFFERED_ZONE_WIDTH,
				RESULT_TABLE_BUFFERED_ZONE_WIDTH);
		headerTable.setSize(RESULT_TABLE_WIDTH, RESULT_TABLE_HEADER_HEIGHT);
		headerTable.setRowHeight(RESULT_TABLE_HEADER_HEIGHT);
		headerTable.setBorder(HEADER_TABLE_BORDER);
		headerTable.setRowSelectionAllowed(false);
		headerTable.setShowVerticalLines(false);
		headerTable.setFocusable(false);

		rows.add(RESULT_TABLE_HEADER_TEXTS);
		headerTableModel = new NonEditableTableModel();
		headerTableModel.setDataVector(rows, RESULT_TABLE_HEADER_TEXTS);
		headerTable.setModel(headerTableModel);

		setTableWidths(headerTable, RESULT_TABLE_COLUMN_WIDTHS);
		addEventListenersToHeaderTable();
		contentPane.add(headerTable);
	}

	private void addEventListenersToHeaderTable() {
		headerTable.addMouseListener(GUI.mouseListener);
		headerTable.addMouseMotionListener(GUI.mouseListener);
	}

	private void createAndAddScrollPaneToContentPane() {
		scrollPane = new JScrollPane();
		scrollPane.setLocation(RESULT_TABLE_BUFFERED_ZONE_WIDTH,
				RESULT_TABLE_BUFFERED_ZONE_WIDTH + RESULT_TABLE_HEADER_HEIGHT);
		scrollPane.setBorder(HEADER_TABLE_BORDER);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(
				RESULT_TABLE_ROW_HEIGHT);
		scrollPane.setFocusable(false);
		scrollBarFadingTimer.setRepeats(false);
		contentPane.add(scrollPane);
	}

	/**
	 * RowsTable is a JTable which contains rows of the result table. NOTE: This
	 * JTable has no header.
	 */
	private void createAndAddRowsTableToScrollPane() {
		final DisplayedCodeRenderer displayedCodeRenderer = new DisplayedCodeRenderer();
		final EventNameRenderer taskNameRenderer = new EventNameRenderer();
		final DateTimeRenderer dateTimeRenderer = new DateTimeRenderer();

		rowsTable = new JTable() {
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				switch (column) {
				case 0:
					return displayedCodeRenderer;
				case 1:
					return taskNameRenderer;
				case 2:
				case 3:
					return dateTimeRenderer;
				default:
					return super.getCellRenderer(row, column);
				}
			}

			// This is to render the highlighted row which is indicated by
			// currentHighlightedRow variable.
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (row == currentSelectedRow) {
					c.setBackground(new Color(232, 240, 255));
				} else {
					c.setBackground(Color.WHITE);
				}

				return c;
			}
		};

		rowsTable.setGridColor(ROWS_TABLE_GRID_COLOR);
		rowsTable.setIntercellSpacing(ROWS_TABLE_CELL_SPACING);
		rowsTable.setShowVerticalLines(false);
		rowsTable.setCellSelectionEnabled(false);
		rowsTable.setRowSelectionAllowed(true);
		rowsTable.setTableHeader(null); // This is needed; otherwise, header is
										// auto shown in the scroll pane.
		rowsTable.setFocusTraversalKeysEnabled(false); // Disable the default
														// TAB key.
		rowsTable.setFocusable(true);
		rowsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		rowsTable.setRowHeight(RESULT_TABLE_ROW_HEIGHT);

		rowsTableModel = new NonEditableTableModel();
		rowsTable.setModel(rowsTableModel);

		currentSelectedRow = NO_HIGHLIGHTED_ROW;

		addKeyBindingsToRowsTable();
		addListenersToRowsTable();
		scrollPane.setViewportView(rowsTable);
	}

	private void addKeyBindingsToRowsTable() {
		ComponentInputMap rowsTableInputMap = new ComponentInputMap(rowsTable);
		ActionMap rowsTableActionMap = new ActionMap();

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
				"onTabFocusOnCommandBar");
		rowsTableActionMap
				.put("onTabFocusOnCommandBar", onTabFocusOnCommandBar);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				"onArrowDownSelectNextRow");
		rowsTableActionMap.put("onArrowDownSelectNextRow",
				onArrowDownSelectNextRow);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				"onArrowUpSelectPreviousRow");
		rowsTableActionMap.put("onArrowUpSelectPreviousRow",
				onArrowUpSelectPreviousRow);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
				"onPageDownSelect5thNextRow");
		rowsTableActionMap.put("onPageDownSelect5thNextRow",
				onPageDownSelect5thNextRow);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
				"onPageUpSelect5thPreviousRow");
		rowsTableActionMap.put("onPageUpSelect5thPreviousRow",
				onPageUpSelect5thPreviousRow);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
				"onEndSelectLastRow");
		rowsTableActionMap.put("onEndSelectLastRow", onEndSelectLastRow);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
				"onHomeSelectFirstRow");
		rowsTableActionMap.put("onHomeSelectFirstRow", onHomeSelectFirstRow);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"onEnterEditEvent");
		rowsTableActionMap.put("onEnterEditEvent", onEnterEditEvent);

		rowsTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				"onDeleteDeleteEvent");
		rowsTableActionMap.put("onDeleteDeleteEvent", onDeleteDeleteEvent);

		rowsTable.setInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				rowsTableInputMap);
		rowsTable.setActionMap(rowsTableActionMap);
	}

	private void addListenersToRowsTable() {
		rowsTable.addMouseListener(GUI.mouseListener);
		rowsTable.addMouseMotionListener(GUI.mouseListener);
	}

	private void selectRowByOffset(int offset) {
		selectRow(currentSelectedRow + offset);
	}

	private void selectRow(int nextSelectedRow) {
		JViewport viewport = scrollPane.getViewport();
		Point currentViewPortPosition = viewport.getViewPosition();
		int currentX = currentViewPortPosition.x;
		int currentY = currentViewPortPosition.y;
		int nextSelectedY, newY;

		// The scrollBar must be enabled here for the GUI to be responsive.
		enableScrollbarByActivateScrollbarFadingTimer();
		nextSelectedRow = adjustRowIDIfOutOfBound(nextSelectedRow);
		nextSelectedY = nextSelectedRow * RESULT_TABLE_ROW_HEIGHT;

		if (nextSelectedY < currentY
				|| nextSelectedY + RESULT_TABLE_ROW_HEIGHT >= currentY
						+ MAX_SCROLL_PANE_HEIGHT) {
			// There can be no currentSelectedRow because the executor return -1
			// which in turn
			// assign to the currentSelectedRow when there is no need to
			// highlight a row. In
			// such case, we reassign the selectedRow to the first row in the
			// current view.
			if (currentSelectedRow == NO_HIGHLIGHTED_ROW) {
				currentSelectedRow = currentY / RESULT_TABLE_ROW_HEIGHT;
			}

			newY = currentY + (nextSelectedRow - currentSelectedRow)
					* RESULT_TABLE_ROW_HEIGHT;
			if (newY < 0) {
				newY = 0;
			}
			viewport.setViewPosition(new Point(currentX, newY));
		}

		currentSelectedRow = nextSelectedRow;
	}

	private int adjustRowIDIfOutOfBound(int row) {
		if (row < 0) {
			row = 0;
		} else if (row >= rowsTable.getRowCount()) {
			row = rowsTable.getRowCount() - 1;
		}

		return row;
	}

	private void enableScrollbarByActivateScrollbarFadingTimer() {
		scrollBarFadingTimer.stop();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollBarFadingTimer.start();
	}

	private void setDataToRowsTableModel(Vector<Event> events) {
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		Vector<Object> row;
		Event event;
		int numOfEvents = events.size();

		for (int i = 0; i < numOfEvents; ++i) {
			event = events.get(i);

			row = new Vector<Object>();
			row.add(event.getDisplayCode());
			row.add(new EventNameAndTagsCellData(event.getName(), event
					.getHashTagsAsString()));
			row.add(event.getStartTime());
			row.add(event.getEndTime());

			rows.add(row);
		}

		rowsTableModel.setDataVector(rows, RESULT_TABLE_HEADER_TEXTS);
	}

	private void setTableWidths(JTable table, int[] columnWidths) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn column;

		for (int i = 0; i < columnWidths.length; ++i) {
			column = columnModel.getColumn(i);
			column.setMinWidth(columnWidths[i]);
			column.setPreferredWidth(columnWidths[i]);
		}
	}

	private void adjustResultWindowHeight(int numOfResultRows) {
		if (numOfResultRows < MAX_NUM_OF_DISPLAYED_ROWS) {
			scrollPaneHeight = numOfResultRows * RESULT_TABLE_ROW_HEIGHT;
		} else {
			scrollPaneHeight = MAX_SCROLL_PANE_HEIGHT;
		}
		resultWindowHeight = 2 * RESULT_TABLE_BUFFERED_ZONE_WIDTH
				+ RESULT_TABLE_HEADER_HEIGHT + scrollPaneHeight;
		scrollPane.setSize(RESULT_TABLE_WIDTH, scrollPaneHeight);
		setSize(RESULT_WINDOW_WIDTH, resultWindowHeight);
	}

	// -------------------------------------------------------------------------
	// Timers & Actions constants.
	// -------------------------------------------------------------------------
	private final Timer scrollBarFadingTimer = new Timer(
			SCROLL_PANE_VERTICAL_SCROLL_BAR_DISPLAY_DURATION,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					scrollPane
							.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
				}
			});

	private final Action onTabFocusOnCommandBar = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			GUI.setFocusOnCommandBar();
		}
	};

	private final Action onArrowDownSelectNextRow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			selectRowByOffset(ARROW_DOWN_DISTANCE);
		}
	};

	private final Action onArrowUpSelectPreviousRow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			selectRowByOffset(ARROW_UP_DISTANCE);
		}
	};

	private final Action onPageDownSelect5thNextRow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			selectRowByOffset(PAGE_DOWN_DISTANCE);
		}
	};

	private final Action onPageUpSelect5thPreviousRow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			selectRowByOffset(PAGE_UP_DISTANCE);
		}
	};

	private final Action onEndSelectLastRow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			selectRow(rowsTableModel.getRowCount() - 1);
		}
	};

	private final Action onHomeSelectFirstRow = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			selectRow(0);
		}
	};

	private final Action onEnterEditEvent = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			int selectedEventID = currentSelectedRow + 1;
			String command = "" + COMMAND_TYPE.EDIT.toString().toLowerCase()
					+ " " + selectedEventID;

			Vector<?> row = (Vector<?>) rowsTableModel.getDataVector().get(
					currentSelectedRow);
			EventNameAndTagsCellData nameAndTags = (EventNameAndTagsCellData) row
					.get(NAME_TAGS_COLUMN_INDEX);
			String name = nameAndTags.getEventName();
			String hashTags = nameAndTags.getEventTags();
			DateTime startTime = (DateTime) row.get(START_TIME_COLUMN_INDEX);
			DateTime endTime = (DateTime) row.get(END_TIME_COLUMN_INDEX);

			if (row != null) {
				command += " " + name;

				if (startTime == null) {
					if (endTime != null) {
						command += " " + dateTimeFormatter.print(endTime);
					}
				} else {
					command += " from " + dateTimeFormatter.print(startTime)
							+ " to " + dateTimeFormatter.print(endTime);
				}

				if (hashTags.length() > 0) {
					command += " " + hashTags;
				}
			}

			GUI.setCommand(command);
			GUI.setFocusOnCommandBar();
		}
	};

	private final Action onDeleteDeleteEvent = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent eventAction) {
			int selectedEventID = currentSelectedRow + 1;
			String command = "" + COMMAND_TYPE.DELETE.toString() + " "
					+ selectedEventID;

			GUI.executeCommandAndDisplayOuput(command, false);
		}
	};

	// -------------------------------------------------------------------------
	// Private Class
	// -------------------------------------------------------------------------
	private class NonEditableTableModel extends DefaultTableModel {
		public NonEditableTableModel() {
			super();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}