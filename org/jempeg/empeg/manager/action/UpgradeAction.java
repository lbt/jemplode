/* UpgradeAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.UpgraderDialog;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class UpgradeAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public UpgradeAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    ISynchronizeClient synchronizeClient
		= myContext.getSynchronizeClient();
	    UpgraderDialog upgradeDialog;
	    if (synchronizeClient == null)
		upgradeDialog = new UpgraderDialog(myContext.getFrame());
	    else {
		IProtocolClient protocolClient
		    = synchronizeClient
			  .getProtocolClient(new SilentProgressListener());
		try {
		    IConnection conn = protocolClient.getConnection();
		    upgradeDialog
			= new UpgraderDialog(myContext.getFrame(), conn);
		} catch (Object object) {
		    protocolClient.close();
		    throw object;
		}
		protocolClient.close();
	    }
	    upgradeDialog.setVisible(true);
	} catch (Throwable t) {
	    Debug.handleError(myContext.getFrame(), t, true);
	}
    }
}
