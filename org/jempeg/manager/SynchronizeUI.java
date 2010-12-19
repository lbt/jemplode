package org.jempeg.manager;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.tree.TreePath;

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

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ReasonsDialog;
import com.inzyme.util.Debug;

/**
* Contains the set of Emplode methods that control uploading
* and downloading.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class SynchronizeUI {
	private PlayerDatabase myPlayerDatabase;
	private ISynchronizeClient mySynchronizeClient;
	private JFrame myFrame;

	public SynchronizeUI(PlayerDatabase _playerDatabase, ISynchronizeClient _synchronizeClient, JFrame _frame) {
		myPlayerDatabase = _playerDatabase;
		mySynchronizeClient = _synchronizeClient;
		myFrame = _frame;
	}

	/**
	* Synchronizes all pending changes to the Empeg in the background
	*/
	public Thread synchronizeInBackground(IProgressListener _progressListener) {
		return synchronizeInBackground(false, _progressListener);
	}

	/**
	* Synchronizes all pending changes to the Empeg in the background, optionally 
	* quiiting after the synchronization is complete.
	* 
	* @param _quitAfterSync whether or not we should exit after sync.
	*/
	public Thread synchronizeInBackground(final boolean _quitAfterSync, final IProgressListener _progressListener) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					synchronize(_quitAfterSync, _progressListener);
				}
				catch (SynchronizeException e) {
					Debug.handleError(myFrame, e, false);
				}
			}
		}, "jEmplode: Synchronize");
		t.start();
		return t;
	}

	/**
	* Synchronizes all pending changes to the Empeg (without quitting)
	*
	* @throws IOException if the connection or protocol fails
	*/
	public void synchronize(IProgressListener _progressListener) throws SynchronizeException {
		synchronize(false, _progressListener);
	}

	/**
	* Synchronizes all pending changes to the Empeg and optionally quit on completion.
	*
	* @param _quitAfterSync should we quit jEmplode after completion?
	* @throws IOException if the connection or protocol fails
	*/
	public void synchronize(boolean _quitAfterSync, IProgressListener _progressListener) throws SynchronizeException {
		_progressListener.setStopRequested(false);
		
		boolean syncRequired;
		boolean interrupted = false;
		Reason[] syncReasons = null;

		synchronized (mySynchronizeClient) {
			if (_progressListener.isStopRequested()) {
				syncRequired = false;
			} else {
				syncRequired = myPlayerDatabase.isDirty();
				if (syncRequired) {
					try {
						_progressListener.progressStarted();
						_progressListener.operationStarted(ResourceBundleUtils.getUIString("synchronize.operation"));

						try {
							syncReasons = mySynchronizeClient.synchronize(myPlayerDatabase, _progressListener);
						}
						catch (InterruptedException e) {
							interrupted = true;
						}

						// TODO: Do we need to check for problems after a sync?  I don't think we do ...		
						//					Reason[] reasons = _synchronizeClient.getPlayerDatabase().checkForProblems(true);
						//					if (reasons.length > 0) {
						//						ReasonsDialog dialog = new ReasonsDialog(myContext.getFrame(), ResourceBundleUtils.getUIString("database.repairs.title"), ResourceBundleUtils.getUIString("database.repairs.description"), ResourceBundleUtils.getUIString("ok"), null, reasons, false);
						//						dialog.setVisible(true);
						//					}
					}
					catch (Exception e) {
						throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.failed"), e);
					}
					finally {
						_progressListener.progressCompleted();
					}
				}
			}
		}

		if (syncRequired) {
			if (syncReasons != null && syncReasons.length > 0) {
				ReasonsDialog dialog = new ReasonsDialog(myFrame, ResourceBundleUtils.getUIString("synchronize.failed.title"), ResourceBundleUtils.getUIString("synchronize.failed.description"), ResourceBundleUtils.getUIString("ok"), null, syncReasons, false);
				dialog.setVisible(true);
			}

			if (!interrupted && !_quitAfterSync) {
				Toolkit.getDefaultToolkit().beep();
			}
		}

		if (!interrupted && _quitAfterSync) {
			System.exit(0);
		}
	}

	/**
	* Downloads the database from the Empeg in the background 
	*/
	public void downloadInBackground(final IProgressListener _progressListener) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					download(_progressListener);
				}
				catch (SynchronizeException e) {
					Debug.handleError(myFrame, e, false);
				}
			}
		}, "jEmplode: Download");
		t.start();
	}

	/**
	* Downloads the current databases from the Empeg
	*
	* @throws IOException if the connection or protocol fails
	*/
	public void download(IProgressListener _progressListener) throws SynchronizeException {
		synchronized (mySynchronizeClient) {
			try {
				_progressListener.progressStarted();
				_progressListener.operationStarted(ResourceBundleUtils.getUIString("download.operation"));
				_progressListener.taskStarted(ResourceBundleUtils.getUIString("download.operation"));

				Reason[] downloadProblems = mySynchronizeClient.download(myPlayerDatabase, true, _progressListener);
				for (int i = 0; i < downloadProblems.length; i ++) {
					Throwable t = downloadProblems[i].getException();
					if (t != null) {
						ExceptionUtils.printChainedStackTrace(t);
					}
				}
				
				if (downloadProblems.length > 0) {
					if (_progressListener.isInteractive()) {
						ReasonsDialog dialog = new ReasonsDialog(myFrame, ResourceBundleUtils.getUIString("download.problems.frameTitle"), ResourceBundleUtils.getUIString("download.problems.title"), ResourceBundleUtils.getUIString("ok"), null, downloadProblems, false);
						dialog.setVisible(true);
					}
				}

				Debug.println(2, "Beep!");
				if (_progressListener.isInteractive()) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
			catch (Exception e) {
				throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "download.failed"), e);
			}
			finally {
				_progressListener.progressCompleted();
			}
		}
		Debug.println(2, "Download complete...");
	}

	/**
	* Downloads the given nodes from the Empeg onto your local machine using
	* a dialog-box confirmation
	*
	* @param _path the path to the starting container
	* @param _nodes the nodes to download
	* @param _targetDir the directory to write to
	* @throws IOException if the read files
	*/
	public void downloadFiles(TreePath _path, IFIDNode[] _nodes, File _targetDir, boolean _useHijack, IProgressListener _progressListener) throws IOException, ProtocolException {
		synchronized (mySynchronizeClient) {
			IProtocolClient protocolClient = mySynchronizeClient.getProtocolClient(_progressListener);
			IConnection conn = protocolClient.getConnection();
			int packetSize = conn.getPacketSize();

			FIDChangeSet changeSet = new FIDChangeSet();

			_progressListener.setStopRequested(false);
			_progressListener.setStopEnabled(true);
			_progressListener.progressStarted();
			try {
				FIDPlaylist parentPlaylist = (FIDPlaylist) _path.getLastPathComponent();
				for (int i = 0; i < _nodes.length; i++) {
					SynchronizeUtils.downloadFile(_path, _nodes[i], parentPlaylist.getIndexOf(_nodes[i]), protocolClient, _targetDir, changeSet, packetSize, _useHijack, _progressListener);
				}
			}
			finally {
				_progressListener.progressCompleted();
			}

			ChangeSetDialog changeSetDialog = new ChangeSetDialog(myFrame, ResourceBundleUtils.getUIString("download.completion.frameTitle"), "download", changeSet, false, true);
			changeSetDialog.setVisible(true);
			changeSetDialog.dispose();
		}
	}

	/**
	* Downloads files from the Empeg in the background 
	*/
	public void downloadFilesInBackground(final TreePath _path, final IFIDNode[] _nodes, final File _targetDir, final boolean _useHijack, final IProgressListener _progressListener) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					downloadFiles(_path, _nodes, _targetDir, _useHijack, _progressListener);
				}
				catch (Exception e) {
					Debug.handleError(myFrame, e, false);
				}
			}
		}, "jEmplode: Download Files");
		t.start();
	}
}
