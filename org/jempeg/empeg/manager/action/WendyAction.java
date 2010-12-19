/* WendyAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.WendyDialog;
import org.jempeg.nodestore.SynchronizeException;

public class WendyAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public WendyAction(ApplicationContext _context) {
	super("Wendy");
	myContext = _context;
    }
    
    public void performAction() {
	try {
	    WendyDialog wendyDialog = new WendyDialog(myContext);
	    wendyDialog.setVisible(true);
	    wendyDialog.dispose();
	} catch (SynchronizeException e) {
	    Debug.handleError(myContext.getFrame(), e, true);
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
