/* TreePopupListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTree;

public class TreePopupListener extends MouseAdapter
{
    public JPopupMenu myPopup;
    
    public TreePopupListener(JPopupMenu _popup) {
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
	    JTree tree = (JTree) _event.getComponent();
	    int row = tree.getRowForLocation(_event.getX(), _event.getY());
	    if (row != -1)
		tree.setSelectionRow(row);
	    tree.requestFocus();
	    myPopup.show(tree, _event.getX(), _event.getY());
	    _event.consume();
	}
    }
}
