//@author A0059827N
package cookiejar.gui.tablecellrenderer;

import cookiejar.gui.EventNameAndTagsCellData;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Renderer for cells containing the event's name and tags. 
 */
@SuppressWarnings("serial")
public class EventNameRenderer extends JLabel 
							   implements TableCellRenderer {
	private final String cssStyle = "<head><style type='text/css'>"
			+ "p {padding-left: 7px; white-space: nowrap}"
			+ "p.name {font-family: HelveticaNeueLT Com 45 Lt; font-size: 15px;}"
			+ "p.tag {font-family: HelveticaNeueLT Com 45 Lt; font-size: 12px; color: orange;}"
			+ "</style></head>";
	
	public EventNameRenderer() {
		super();
		
		setOpaque(true);
		setBackground(Color.WHITE);
	}
	
	@Override
	public Component getTableCellRendererComponent(
							JTable table, Object eventNameAndTags,
							boolean isSelected, boolean hasFocus,
							int row, int column) {
		EventNameAndTagsCellData newEventNameAndTags = (EventNameAndTagsCellData) eventNameAndTags;
		String name = newEventNameAndTags.getEventName();
		String tags = newEventNameAndTags.getEventTags();
		
		String formattedText = "<html>" + cssStyle
				+ "<p class=\"name\">" + name + "</p>"
				+ "<p class=\"tag\">" + tags + "</p>"
				+ "</html>";
		setText(formattedText);
		
		return this;
	}
}