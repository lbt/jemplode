/* FsckAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.progress.IProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class FsckAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public FsckAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		ISynchronizeClient synchronizeClient
		    = myContext.getSynchronizeClient();
		ISynchronizeClient isynchronizeclient;
		MONITORENTER (isynchronizeclient = synchronizeClient);
		MISSING MONITORENTER
		synchronized (isynchronizeclient) {
		    IProgressListener progressListener
			= myContext.getDefaultProgressListener();
		    progressListener.progressStarted();
		    progressListener.operationStarted("Checking player media");
		    org.jempeg.protocol.IProtocolClient client
			= synchronizeClient
			      .getProtocolClient(progressListener);
		    progressListener.operationUpdated(0L, 1L);
		    try {
			try {
			    ((EmpegProtocolClient) client).checkMedia(true);
			} catch (Throwable e) {
			    e.printStackTrace();
			    Debug.println(16, e);
			}
		    } catch (Object object) {
			progressListener.operationUpdated(1L, 1L);
			progressListener.progressCompleted();
			throw object;
		    }
		    progressListener.operationUpdated(1L, 1L);
		    progressListener.progressCompleted();
		}
	    }
	});
	t.start();
    }
}
