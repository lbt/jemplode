/* RebuildAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.protocol.EmpegProtocolClient;

public class RebuildAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public RebuildAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    EmpegProtocolClient empegProtocolClient
			= ((EmpegProtocolClient)
			   (myContext.getSynchronizeClient().getProtocolClient
			    (new SilentProgressListener())));
		    empegProtocolClient.rebuildPlayerDatabase(0L);
		} catch (Exception e) {
		    Debug.println(e);
		}
	    }
	});
	t.start();
    }
}
