/* ButtonListMouseListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ButtonListMouseListener extends MouseAdapter
    implements KeyListener, ListSelectionListener
{
    private int myLastIndex = -1;
    private JList myList;
    private int myLastSelected = -1;
    
    public ButtonListMouseListener(JList _list) {
	myList = _list;
    }
    
    protected void click() {
	int index = myList.getSelectedIndex();
	if (index != -1) {
	    AbstractButton checkBox
		= (AbstractButton) myList.getModel().getElementAt(index);
	    checkBox.doClick();
	    myList.repaint();
	}
    }
    
    public void mousePressed(MouseEvent _event) {
	myLastIndex = myLastSelected;
    }
    
    public void mouseClicked(MouseEvent _event) {
	if (myLastSelected == myLastIndex)
	    click();
    }
    
    public void valueChanged(ListSelectionEvent _event) {
	if (!_event.getValueIsAdjusting()) {
	    int index = myList.getSelectedIndex();
	    myLastSelected = index;
	}
    }
    
    public void keyTyped(KeyEvent _event) {
	/* empty */
    }
    
    public void keyPressed(KeyEvent _event) {
	if (_event.getKeyChar() == ' ' || _event.getKeyCode() == 10)
	    click();
    }
    
    public void keyReleased(KeyEvent _event) {
	/* empty */
    }
}
