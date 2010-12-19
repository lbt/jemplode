/* GrabScreenAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.logoedit.GrabScreenDialog;
import org.jempeg.empeg.manager.plugins.JEmplodePlugin;
import org.jempeg.protocol.ProtocolException;

public class GrabScreenAction extends AbstractAction implements JEmplodePlugin
{
    private ApplicationContext myContext;
    
    public GrabScreenAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    GrabScreenDialog grabScreenDialog
		= new GrabScreenDialog(myContext);
	    grabScreenDialog.setVisible(true);
	} catch (ProtocolException e) {
	    Debug.handleError(myContext.getFrame(), e, true);
	}
    }
}
