/* SolidColorListCellRenderer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class SolidColorListCellRenderer extends JLabel
    implements ListCellRenderer
{
    private SolidColorIcon myIcon
	= new SolidColorIcon(this, Color.black, 50, 20);
    
    public SolidColorListCellRenderer() {
	setIcon(myIcon);
    }
    
    public Component getListCellRendererComponent
	(JList list, Object value, int index, boolean isSelected,
	 boolean cellHasFocus) {
	Color color = (Color) value;
	myIcon.setColor(color);
	return this;
    }
}
