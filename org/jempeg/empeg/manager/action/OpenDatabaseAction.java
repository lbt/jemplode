/* OpenDatabaseAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.ConnectionSelectionDialog;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.ProtocolException;

public class OpenDatabaseAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public OpenDatabaseAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	boolean openDatabase = true;
	PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
	if (playerDatabase != null && playerDatabase.isDirty()) {
	    int confirm
		= (JOptionPane.showConfirmDialog
		   (myContext.getFrame(),
		    "You will lose your changes.  Are you sure you want to connect to another player?"));
	    if (confirm != 0)
		openDatabase = false;
	}
	if (openDatabase) {
	    try {
		ConnectionSelectionDialog
		    .showConnectionSelectionDialog(myContext);
	    } catch (ProtocolException e) {
		Debug.handleError(myContext.getFrame(), e, true);
	    }
	}
    }
}
