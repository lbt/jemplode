/* PlayAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.protocol.IProtocolClient;

public class PlayAction extends AbstractAction
{
    public static final int PLAYMODE_INSERT = 0;
    public static final int PLAYMODE_APPEND = 1;
    public static final int PLAYMODE_REPLACE = 2;
    private ApplicationContext myContext;
    private int myMode;
    
    public PlayAction(ApplicationContext _context, int _mode) {
	myContext = _context;
	myMode = _mode;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    ContainerSelection selection = myContext.getSelection();
	    if (selection != null) {
		IProtocolClient client
		    = myContext.getSynchronizeClient()
			  .getProtocolClient(new SilentProgressListener());
		Object[] selectedValues = selection.getSelectedValues();
		for (int i = 0; i < selectedValues.length; i++) {
		    if (selectedValues[i] instanceof IFIDNode) {
			IFIDNode node = (IFIDNode) selectedValues[i];
			if (myMode == 1)
			    client.playAppend(node.getFID());
			else if (myMode == 2)
			    client.playReplace(node.getFID());
			else if (myMode == 0)
			    client.playInsert(node.getFID());
		    }
		}
	    }
	} catch (Exception e) {
	    Debug.println(e);
	}
    }
}
