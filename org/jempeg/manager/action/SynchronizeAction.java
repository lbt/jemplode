/* SynchronizeAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.SynchronizeUI;

public class SynchronizeAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public SynchronizeAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	SynchronizeUI syncManager
	    = new SynchronizeUI(myContext.getPlayerDatabase(),
				myContext.getSynchronizeClient(),
				myContext.getFrame());
	syncManager.synchronizeInBackground
	    (myContext.getSynchronizeProgressListener());
    }
}
