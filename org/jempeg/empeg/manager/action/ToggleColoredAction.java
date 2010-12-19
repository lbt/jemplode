/* ToggleColoredAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.IFIDNode;

public class ToggleColoredAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public ToggleColoredAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	ContainerSelection selection = myContext.getSelection();
	if (selection != null) {
	    Object[] values = selection.getSelectedValues();
	    int newColored = -1;
	    for (int i = 0; i < values.length; i++) {
		if (values[i] instanceof IFIDNode) {
		    IFIDNode node = (IFIDNode) values[i];
		    if (newColored == -1) {
			if (node.isColored())
			    newColored = -1;
			else
			    newColored = 1;
		    }
		    if (newColored == 1)
			node.setColored(true);
		    else
			node.setColored(false);
		}
	    }
	}
    }
}
