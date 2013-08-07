//@author A0059827N
package cookiejar.gui.tablecellrenderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer for cells containing the header.
 */
@SuppressWarnings("serial")
public class HeaderRenderer extends JLabel 
							implements TableCellRenderer {
	private final String cssStyle = "<head><style type='text/css'>"
			+ "p {padding-left: 7px;}"
			+ "</style></head>";
	
	public HeaderRenderer() {
		super();
		
		setOpaque(true);
		setFont(new Font("HelveticaNeueLT Com 65 Md", Font.PLAIN, 14));
		setBackground(Color.WHITE);
		setForeground(new Color(229, 123, 10));
	}
	
	@Override
	public Component getTableCellRendererComponent(
							JTable table, Object header,
							boolean isSelected, boolean hasFocus,
							int row, int column) {
		String formattedText = "<html>" + cssStyle
				+ "<p>" + header + "</p>"
				+ "</html>";
		
		setText(formattedText);
		
		return this;
	}
}