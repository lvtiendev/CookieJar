//@author A0059827N
package cookiejar.gui.tablecellrenderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import org.joda.time.DateTime;
import org.joda.time.format.*;

/**
 * Renderer for cells containing the datetime. 
 */
@SuppressWarnings("serial")
public class DateTimeRenderer extends JLabel 
							  implements TableCellRenderer {
	private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd MMM YY");
	private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
	private static final String cssStyle = "<head><style type='text/css'>"
			+ "p {padding-left: 7px;}"
			+ "p.date {font-family: HelveticaNeueLT Com 55 Roman; font-size: 15px;}"
			+ "p.empty_date {font-family: HelveticaNeueLT Com 55 Roman; font-size: 15px; color: gray; padding-bottom: 12px;}"
			+ "p.time {font-family: HelveticaNeueLT Com 45 Lt; font-size: 12px;}"
			+ "</style></head>";
	
	public DateTimeRenderer() {
		super();

		setOpaque(true);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
	}
	
	@Override
	public Component getTableCellRendererComponent(
							JTable table, Object dateTime,
							boolean isSelected, boolean hasFocus,
							int row, int column) {
		DateTime newDateTime = (DateTime)dateTime; 
		String formattedText;
		
		if (newDateTime != null) {
			String date = dateFormatter.print(newDateTime);
			String time = timeFormatter.print(newDateTime);
			formattedText = "<html>" + cssStyle
					+ "<p class=\"date\">" + date + "</p>"
					+ "<p class=\"time\">" + time + "</p>"
					+ "</html>";
		} else {
			formattedText = "<html>" + cssStyle
					+ "<p class=\"empty_date\"> _ </p>"
					+ "</html>";
		}
		setText(formattedText);
		
		return this;
	}
}