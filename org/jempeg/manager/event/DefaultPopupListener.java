/* DefaultPopupListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class DefaultPopupListener extends MouseAdapter
{
    public JPopupMenu myPopup;
    
    public DefaultPopupListener(JPopupMenu _popup) {
	myPopup = _popup;
    }
    
    public void mousePressed(MouseEvent _event) {
	maybeShowPopup(_event);
    }
    
    public void mouseReleased(MouseEvent _event) {
	maybeShowPopup(_event);
    }
    
    protected void maybeShowPopup(MouseEvent _event) {
	if (_event.isPopupTrigger()) {
	    JComponent tableHeader = (JComponent) _event.getComponent();
	    myPopup.show(tableHeader, _event.getX(), _event.getY());
	    _event.consume();
	}
    }
}
