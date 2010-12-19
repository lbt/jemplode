/* RevertAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.PlayerDatabase;

public class RevertAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public RevertAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	boolean revert = true;
	PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
	if (playerDatabase != null) {
	    if (playerDatabase.isDirty()) {
		int confirm
		    = (JOptionPane.showConfirmDialog
		       (myContext.getFrame(),
			ResourceBundleUtils
			    .getUIString("revert.databaseDirtyConfirmation")));
		if (confirm != 0)
		    revert = false;
	    }
	    if (revert) {
		SynchronizeUI syncManager
		    = new SynchronizeUI(myContext.getPlayerDatabase(),
					myContext.getSynchronizeClient(),
					myContext.getFrame());
		syncManager.downloadInBackground
		    (myContext.getDownloadProgressListener());
	    }
	}
    }
}
