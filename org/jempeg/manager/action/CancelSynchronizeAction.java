/* CancelSynchronizeAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.progress.IProgressListener;

import org.jempeg.ApplicationContext;

public class CancelSynchronizeAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public CancelSynchronizeAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	IProgressListener progressListener
	    = myContext.getSynchronizeProgressListener();
	progressListener.setStopRequested(true);
    }
}
