/* SynchronizeUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.tree.TreePath;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ReasonsDialog;
import com.inzyme.util.Debug;

import org.jempeg.manager.dialog.ChangeSetDialog;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.model.FIDChangeSet;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.SynchronizeUtils;

public class SynchronizeUI
{
    private PlayerDatabase myPlayerDatabase;
    private ISynchronizeClient mySynchronizeClient;
    private JFrame myFrame;
    
    public SynchronizeUI(PlayerDatabase _playerDatabase,
			 ISynchronizeClient _synchronizeClient,
			 JFrame _frame) {
	myPlayerDatabase = _playerDatabase;
	mySynchronizeClient = _synchronizeClient;
	myFrame = _frame;
    }
    
    public Thread synchronizeInBackground
	(IProgressListener _progressListener) {
	return synchronizeInBackground(false, _progressListener);
    }
    
    public Thread synchronizeInBackground
	(final boolean _quitAfterSync,
	 final IProgressListener _progressListener) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    synchronize(_quitAfterSync, _progressListener);
		} catch (SynchronizeException e) {
		    Debug.handleError(myFrame, e, false);
		}
	    }
	}, "jEmplode: Synchronize");
	t.start();
	return t;
    }
    
    public void synchronize(IProgressListener _progressListener)
	throws SynchronizeException {
	synchronize(false, _progressListener);
    }
    
    public void synchronize
	(boolean _quitAfterSync, IProgressListener _progressListener)
	throws SynchronizeException {
	_progressListener.setStopRequested(false);
	boolean interrupted = false;
	Reason[] syncReasons = null;
	ISynchronizeClient isynchronizeclient;
	MONITORENTER (isynchronizeclient = mySynchronizeClient);
	boolean syncRequired;
	MISSING MONITORENTER
	synchronized (isynchronizeclient) {
	    if (_progressListener.isStopRequested())
		syncRequired = false;
	    else {
		syncRequired = myPlayerDatabase.isDirty();
		if (syncRequired) {
		    try {
			try {
			    _progressListener.progressStarted();
			    _progressListener.operationStarted
				(ResourceBundleUtils
				     .getUIString("synchronize.operation"));
			    try {
				syncReasons
				    = (mySynchronizeClient.synchronize
				       (myPlayerDatabase, _progressListener));
			    } catch (InterruptedException e) {
				interrupted = true;
			    }
			} catch (Exception e) {
			    throw new SynchronizeException
				      ((new ResourceBundleKey
					("errors", "synchronize.failed")),
				       e);
			}
		    } catch (Object object) {
			_progressListener.progressCompleted();
			throw object;
		    }
		    _progressListener.progressCompleted();
		}
	    }
	}
	if (syncRequired) {
	    if (syncReasons != null && syncReasons.length > 0) {
		ReasonsDialog dialog
		    = (new ReasonsDialog
		       (myFrame,
			ResourceBundleUtils
			    .getUIString("synchronize.failed.title"),
			ResourceBundleUtils
			    .getUIString("synchronize.failed.description"),
			ResourceBundleUtils.getUIString("ok"), null,
			syncReasons, false));
		dialog.setVisible(true);
	    }
	    if (!interrupted && !_quitAfterSync)
		Toolkit.getDefaultToolkit().beep();
	}
	if (!interrupted && _quitAfterSync)
	    System.exit(0);
    }
    
    public void downloadInBackground
	(final IProgressListener _progressListener) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    download(_progressListener);
		} catch (SynchronizeException e) {
		    Debug.handleError(myFrame, e, false);
		}
	    }
	}, "jEmplode: Download");
	t.start();
    }
    
    public void download(IProgressListener _progressListener)
	throws SynchronizeException {
	ISynchronizeClient isynchronizeclient;
	MONITORENTER (isynchronizeclient = mySynchronizeClient);
	MISSING MONITORENTER
	synchronized (isynchronizeclient) {
	    try {
		try {
		    _progressListener.progressStarted();
		    _progressListener.operationStarted
			(ResourceBundleUtils
			     .getUIString("download.operation"));
		    _progressListener.taskStarted
			(ResourceBundleUtils
			     .getUIString("download.operation"));
		    Reason[] downloadProblems
			= mySynchronizeClient.download(myPlayerDatabase, true,
						       _progressListener);
		    for (int i = 0; i < downloadProblems.length; i++) {
			Throwable t = downloadProblems[i].getException();
			if (t != null)
			    ExceptionUtils.printChainedStackTrace(t);
		    }
		    if (downloadProblems.length > 0
			&& _progressListener.isInteractive()) {
			ReasonsDialog dialog
			    = (new ReasonsDialog
			       (myFrame,
				(ResourceBundleUtils.getUIString
				 ("download.problems.frameTitle")),
				ResourceBundleUtils
				    .getUIString("download.problems.title"),
				ResourceBundleUtils.getUIString("ok"), null,
				downloadProblems, false));
			dialog.setVisible(true);
		    }
		    Debug.println(2, "Beep!");
		    if (_progressListener.isInteractive())
			Toolkit.getDefaultToolkit().beep();
		} catch (Exception e) {
		    throw new SynchronizeException
			      (new ResourceBundleKey("errors",
						     "download.failed"),
			       e);
		}
	    } catch (Object object) {
		_progressListener.progressCompleted();
		throw object;
	    }
	    _progressListener.progressCompleted();
	}
	Debug.println(2, "Download complete...");
    }
    
    public void downloadFiles
	(TreePath _path, IFIDNode[] _nodes, File _targetDir,
	 boolean _useHijack, IProgressListener _progressListener)
	throws IOException, ProtocolException {
	ISynchronizeClient isynchronizeclient;
	MONITORENTER (isynchronizeclient = mySynchronizeClient);
	MISSING MONITORENTER
	synchronized (isynchronizeclient) {
	    IProtocolClient protocolClient
		= mySynchronizeClient.getProtocolClient(_progressListener);
	    IConnection conn = protocolClient.getConnection();
	    int packetSize = conn.getPacketSize();
	    FIDChangeSet changeSet = new FIDChangeSet();
	    _progressListener.setStopRequested(false);
	    _progressListener.setStopEnabled(true);
	    _progressListener.progressStarted();
	    try {
		FIDPlaylist parentPlaylist
		    = (FIDPlaylist) _path.getLastPathComponent();
		for (int i = 0; i < _nodes.length; i++)
		    SynchronizeUtils.downloadFile(_path, _nodes[i],
						  parentPlaylist
						      .getIndexOf(_nodes[i]),
						  protocolClient, _targetDir,
						  changeSet, packetSize,
						  _useHijack,
						  _progressListener);
	    } catch (Object object) {
		_progressListener.progressCompleted();
		throw object;
	    }
	    _progressListener.progressCompleted();
	    ChangeSetDialog changeSetDialog
		= new ChangeSetDialog(myFrame,
				      (ResourceBundleUtils.getUIString
				       ("download.completion.frameTitle")),
				      "download", changeSet, false, true);
	    changeSetDialog.setVisible(true);
	    changeSetDialog.dispose();
	}
    }
    
    public void downloadFilesInBackground
	(final TreePath _path, final IFIDNode[] _nodes, final File _targetDir,
	 final boolean _useHijack, final IProgressListener _progressListener) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    downloadFiles(_path, _nodes, _targetDir, _useHijack,
				  _progressListener);
		} catch (Exception e) {
		    Debug.handleError(myFrame, e, false);
		}
	    }
	}, "jEmplode: Download Files");
	t.start();
    }
}
