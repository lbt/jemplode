package org.jempeg.empeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.progress.IProgressListener;
import com.inzyme.util.Debug;

/**
 * FsckAction forces an fsck on all drives.
 * 
 * @author Mike Schrag
 */
public class FsckAction extends AbstractAction {
	private ApplicationContext myContext;
	
	/**
	 * Constructor for FsckAction.
	 */
	public FsckAction(ApplicationContext _context) {
		myContext = _context;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent _event) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				ISynchronizeClient synchronizeClient = myContext.getSynchronizeClient();
				synchronized (synchronizeClient) {
					IProgressListener progressListener = myContext.getDefaultProgressListener();
					progressListener.progressStarted();
					progressListener.operationStarted("Checking player media");
					IProtocolClient client = synchronizeClient.getProtocolClient(progressListener);
					progressListener.operationUpdated(0, 1);
					try {
						((EmpegProtocolClient)client).checkMedia(true);
					}
					catch (Throwable e) {
						e.printStackTrace();
						Debug.println(Debug.ERROR, e);
					}
					finally {
						progressListener.operationUpdated(1, 1);
						progressListener.progressCompleted();
					}
				}
			}
		});
		t.start();
	}

}
