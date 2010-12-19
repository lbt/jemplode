/* ButtonListCellRenderer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ButtonListCellRenderer implements ListCellRenderer
{
    public Component getListCellRendererComponent
	(JList _list, Object _value, int _index, boolean _isSelected,
	 boolean _cellHasFocus) {
	AbstractButton checkBox = (AbstractButton) _value;
	if (_isSelected) {
	    checkBox.setBackground(_list.getSelectionBackground());
	    checkBox.setForeground(_list.getSelectionForeground());
	} else {
	    checkBox.setBackground(_list.getBackground());
	    checkBox.setForeground(_list.getForeground());
	}
	return (Component) _value;
    }
}
