//@author A0059827N
package cookiejar.gui.tablecellrenderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Renderer for cells containing the displayed code.
 */
@SuppressWarnings("serial")
public class DisplayedCodeRenderer extends JLabel 
								   implements TableCellRenderer {
	private final String cssStyle = "<head><style type='text/css'>"
			+ "p.adding_event {color: #00961C;}"
			+ "p.deleting_event {color: #E61C29;}"
			+ "</style></head>";
	
	public DisplayedCodeRenderer() {
		super();
		
		setOpaque(true);
		setFont(new Font("HelveticaNeueLT Com 55 Roman", Font.PLAIN, 24));
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setHorizontalAlignment(CENTER);
	}
	
	@Override
	public Component getTableCellRendererComponent(
							JTable table, Object displayedCode,
							boolean isSelected, boolean hasFocus,
							int row, int column) {
		
		String newDisplayedCode = displayedCode.toString();
		String formattedText = "<html>" + cssStyle;
		
		if (newDisplayedCode.equals("-1")) {
			formattedText += "<p> ++ </p>";
		} else if (newDisplayedCode.equals("-2")) {
			formattedText += "<p> -- </p>";
		} else {
			formattedText += "<p>" + newDisplayedCode + "</p>";
		}
		formattedText += "</html>";
		setText(formattedText);
		
		return this;
	}
}