package org.jempeg.protocol;

import java.util.Vector;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeRemovedDatabaseChange;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.PlaylistPair;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.SynchronizeQueue;
import org.jempeg.nodestore.event.ISynchronizeClientListener;
import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

/**
 * Synchronizing and downloading is very similar between various Rio devices, so this
 * class provides the shared functionality.
 * 
 * @author Mike Schrag
 */
public abstract class AbstractSynchronizeClient implements ISynchronizeClient, IPasswordManager {
	private static final String PASSWORD_KEY = "password";

	private IConnectionFactory myConnectionFactory;
	private ISynchronizeClientListener[] myListeners;
	private IDatabaseChange myCurrentDatabaseChange;
	private String myPassword;

	protected AbstractSynchronizeClient(IConnectionFactory _connectionFactory) {
		myListeners = new ISynchronizeClientListener[0];
		myConnectionFactory = _connectionFactory;
		myPassword = PropertiesManager.getInstance().getProperty(AbstractSynchronizeClient.PASSWORD_KEY);
	}

	public synchronized void addSynchronizeClientListener(ISynchronizeClientListener _listener) {
		ISynchronizeClientListener[] listeners = new ISynchronizeClientListener[myListeners.length + 1];
		System.arraycopy(myListeners, 0, listeners, 0, myListeners.length);
		listeners[listeners.length - 1] = _listener;
		myListeners = listeners;
	}

	public synchronized void removeSynchronizeClientListener(ISynchronizeClientListener _listener) {
		ISynchronizeClientListener[] listeners = new ISynchronizeClientListener[myListeners.length - 1];
		int newIndex = 0;
		for (int i = 0; i < myListeners.length; i ++, newIndex ++) {
			if (myListeners[i] == _listener) {
				newIndex --;
			}
			else {
				listeners[newIndex] = myListeners[i];
			}
		}
		myListeners = listeners;
	}

	void fireDownloadStarted(PlayerDatabase _playerDatabase) {
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].downloadStarted(_playerDatabase);
		}
	}

	void fireDownloadCompleted(PlayerDatabase _playerDatabase) {
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].downloadCompleted(_playerDatabase);
		}
	}

	void fireSynchronizeStarted(PlayerDatabase _playerDatabase) {
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].synchronizeStarted(_playerDatabase);
		}
	}

	void fireSynchronizeStarted(IDatabaseChange _databaseChange) {
		myCurrentDatabaseChange = _databaseChange;
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].synchronizeStarted(_databaseChange);
		}
	}

	void fireSynchronizeInProgress(IDatabaseChange _databaseChange, long _current, long _total) {
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].synchronizeInProgress(_databaseChange, _current, _total);
		}
	}

	void fireSynchronizeCompleted(IDatabaseChange _databaseChange, boolean _successfully) {
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].synchronizeCompleted(_databaseChange, _successfully);
		}
		myCurrentDatabaseChange = null;
	}

	void fireSynchronizeCompleted(PlayerDatabase _playerDatabase, boolean _successfully) {
		for (int i = myListeners.length - 1; i >= 0; i --) {
			myListeners[i].synchronizeCompleted(_playerDatabase, _successfully);
		}
	}

	/**
	 * Sets a new password to store for future authentication attempts within this session.
	 * 
	 * @param _password the new password to authenticate with
	 */
	public void setPassword(String _password, boolean _persistent) {
		myPassword = _password;

		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		if (_persistent) {
			propertiesManager.setProperty(AbstractSynchronizeClient.PASSWORD_KEY, _password);
		}
		else {
			propertiesManager.removeProperty(AbstractSynchronizeClient.PASSWORD_KEY);
		}
		try {
			propertiesManager.save();
		}
		catch (Throwable t) {
			Debug.println(t);
		}
	}

	/**
	 * Returns the last successful password that was used during this
	 * session.
	 * 
	 * @return password 
	 */
	public String getPassword() {
		return myPassword;
	}

	public IConnectionFactory getConnectionFactory() {
		return myConnectionFactory;
	}

	public abstract void synchronizePlaylistTags(FIDPlaylist _playlist, PlaylistPair[] _playlistPairs, IProtocolClient _protocolClient) throws SynchronizeException;

	public abstract void synchronizeTags(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException;

	public Reason[] download(PlayerDatabase _playerDatabase, boolean _rebuildOnFailure, IProgressListener _progressListener) throws SynchronizeException {
		try {
			fireDownloadStarted(_playerDatabase);
			
			Reason[] databaseProblems;

			SynchronizeQueue synchronizeQueue = _playerDatabase.getSynchronizeQueue();
			IProtocolClient protocolClient = getProtocolClient(new DownloadProgressListener(_progressListener));
			protocolClient.open();
			try {
				protocolClient.waitForDevice(10);

				protocolClient.readLock();

				_playerDatabase.clear();
				synchronizeQueue.clear();

				computeFreeSpace(_playerDatabase, protocolClient);

				Reason[] downloadProblems = download0(_playerDatabase, protocolClient, _rebuildOnFailure, _progressListener);
				Reason[] databaseRepairProblems = _playerDatabase.checkForProblems0(true);

				databaseProblems = new Reason[downloadProblems.length + databaseRepairProblems.length];
				System.arraycopy(downloadProblems, 0, databaseProblems, 0, downloadProblems.length);
				System.arraycopy(databaseRepairProblems, 0, databaseProblems, downloadProblems.length, databaseRepairProblems.length);
				
				_playerDatabase.setDeviceSettings(protocolClient.getDeviceSettings());
				NodeTag.resetNodeTags(_playerDatabase.getDatabaseTags());
			}
			finally {
				try {
					protocolClient.unlock();
				}
				catch (Throwable t) {
					Debug.println(t);
				}
				protocolClient.close();
			}
			
			fireDownloadCompleted(_playerDatabase);
			return databaseProblems;
		}
		catch (ProtocolException e) {
			throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.databaseDownloadFailed"), e);
		}
	}

	protected abstract Reason[] download0(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, boolean _rebuildOnFailure, IProgressListener _progressListener) throws ProtocolException, SynchronizeException;

	public Reason[] synchronize(PlayerDatabase _playerDatabase, IProgressListener _progressListener) throws InterruptedException, SynchronizeException {
		SynchronizeQueue synchronizeQueue = _playerDatabase.getSynchronizeQueue();

		if (!_playerDatabase.isDirty()) {
			return new Reason[0];
		}

		boolean successful = false;
		fireSynchronizeStarted(_playerDatabase);

		try {
      Debug.println(Debug.VERBOSE, "AbstractSynchronizeClient.synchronize: Starting on Thread #" + Thread.currentThread().hashCode());

			Vector reasonsVec = new Vector();

			IProtocolClient protocolClient = getProtocolClient(new SynchronizeProgressListener(_progressListener));
			protocolClient.open();
			try {
				protocolClient.waitForDevice(10);

				beforeSynchronize(_playerDatabase, protocolClient, _progressListener);

				protocolClient.writeLock();

				IDeviceSettings currentDeviceSettings = _playerDatabase.getDeviceSettings();
				IDeviceSettings newDeviceSettings = protocolClient.getDeviceSettings();
				if (currentDeviceSettings.getSerialNumber() == newDeviceSettings.getSerialNumber() && currentDeviceSettings.getDeviceGeneration() != newDeviceSettings.getDeviceGeneration()) {
					throw new ProtocolException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.deviceModified"));
				}

				if (currentDeviceSettings.isDirty()) {
					protocolClient.changeDeviceSettings(currentDeviceSettings);
					_playerDatabase.setDeviceSettings(newDeviceSettings);
				}

				int maxQueueSize = 0;
				_progressListener.setStopEnabled(true);
				_progressListener.setStopRequested(false);
				_progressListener.operationStarted(ResourceBundleUtils.getUIString("synchronize.operation"));
				try {
					boolean recycleConnectionBeforeDelete = PropertiesManager.getInstance().getBooleanProperty("recycleConnectionBeforeDelete", true);
					synchronizeQueue.setSynchronizingNow(true);
					IDatabaseChange databaseChange = null;
					while (!_progressListener.isStopRequested() && (databaseChange = synchronizeQueue.dequeue()) != null) {
						int currentQueueSize = synchronizeQueue.getSize();
						maxQueueSize = Math.max(maxQueueSize, currentQueueSize);

						fireSynchronizeStarted(databaseChange);
						try {
              Debug.println(Debug.VERBOSE, "AbstractSynchronizeClient.synchronize: Start " + databaseChange);
							databaseChange.incrementAttempt();

							// bug in the karma -- kill the connection and restart prior to deleting
							if (recycleConnectionBeforeDelete && databaseChange instanceof NodeRemovedDatabaseChange) {
								closeAndReopen(protocolClient);
							}

							databaseChange.synchronize(this, protocolClient);
							fireSynchronizeCompleted(databaseChange, true);
							Debug.println(Debug.VERBOSE, "AbstractSynchronizeClient.synchronize: Finished " + databaseChange);
						}
						catch (Throwable t) {
              Debug.println(Debug.VERBOSE, "AbstractSynchronizeClient.synchronize: Failed " + databaseChange + "; requeuing");

							fireSynchronizeCompleted(databaseChange, false);
							synchronizeQueue.requeue(databaseChange);
							if (databaseChange.getAttempt() >= 3) {
								databaseChange.setAttempt(0);
								throw t;
							}

							Debug.println(t);
							closeAndReopen(protocolClient);
						}
						finally {
							_progressListener.operationUpdated((maxQueueSize - currentQueueSize), maxQueueSize);
						}
					}

					_playerDatabase.setDeviceSettings(protocolClient.getDeviceSettings());

					afterSynchronize(_playerDatabase, protocolClient, _progressListener);
				}
				catch (Throwable t) {
					Debug.println(t);
					reasonsVec.addElement(new Reason(t));
				}
				finally {
					synchronizeQueue.setSynchronizingNow(false);
					if (_progressListener.isStopRequested()) {
						reasonsVec.addElement(new Reason(ResourceBundleUtils.getErrorString("synchronize.synchronizeCancelled")));
						//						_progressListener.setStopRequested(false);
						//						_progressListener.setStopEnabled(false);
					}
				}
			}
			finally {
				try {
					protocolClient.unlock();
				}
				catch (Throwable t) {
					Debug.println(t);
				}
				protocolClient.close();
				Debug.println(Debug.VERBOSE, "AbstractSynchronizeClient.synchronize: Finished on Thread #" + Thread.currentThread().hashCode());
			}

			if (reasonsVec.size() == 0) {
				computeFreeSpace(_playerDatabase, protocolClient);
			}

			successful = true;

			return Reason.toArray(reasonsVec);
		}
		catch (ProtocolException e) {
			throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.synchronizeFailed"), e);
		}
		finally {
			fireSynchronizeCompleted(_playerDatabase, successful);
		}
	}

	protected void closeAndReopen(IProtocolClient _protocolClient) throws ProtocolException {
		// Kill the connection and restart it ... Just in case something really
		// screwy happened (like a crash, etc)
		try {
			_protocolClient.close();
		}
		catch (Throwable ex) {
			Debug.println(ex);
		}

		_protocolClient.open();
		_protocolClient.waitForDevice(10);
		_protocolClient.writeLock();
	}

	protected abstract void beforeSynchronize(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, IProgressListener _progressListener) throws SynchronizeException, ProtocolException;

	protected abstract void afterSynchronize(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, IProgressListener _progressListener) throws SynchronizeException, ProtocolException;

	protected void computeFreeSpace(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient) throws ProtocolException {
		StorageInfo storageInfo = _protocolClient.getStorageInfo(IProtocolClient.STORAGE_ZERO);
		_playerDatabase.setFreeSpace(storageInfo.getSize(), storageInfo.getFree());
	}

	protected class SynchronizeProgressListener implements ISimpleProgressListener {
		private IProgressListener myProxiedProgressListener;

		public SynchronizeProgressListener(IProgressListener _proxiedProgressListener) {
			myProxiedProgressListener = _proxiedProgressListener;
		}

		public void progressReported(long _current, long _maximum) {
			if (myCurrentDatabaseChange != null) {
				fireSynchronizeInProgress(myCurrentDatabaseChange, _current, _maximum);
				myProxiedProgressListener.taskStarted(myCurrentDatabaseChange.getDescription());
				myProxiedProgressListener.taskUpdated(_current, _maximum);
			}
			else {
				myProxiedProgressListener.taskUpdated(_current, _maximum);
			}
		}

		public void progressReported(String _description, long _current, long _maximum) {
			if (myCurrentDatabaseChange == null) {
				myProxiedProgressListener.taskStarted(_description);
			}
			progressReported(_current, _maximum);
		}
	}

	protected class DownloadProgressListener implements ISimpleProgressListener {
		private IProgressListener myProxiedProgressListener;

		public DownloadProgressListener(IProgressListener _proxiedProgressListener) {
			myProxiedProgressListener = _proxiedProgressListener;
		}

		public void progressReported(long _current, long _maximum) {
			myProxiedProgressListener.taskUpdated(_current, _maximum);
		}

		public void progressReported(String _description, long _current, long _maximum) {
			myProxiedProgressListener.taskStarted(_description);
			progressReported(_current, _maximum);
		}
	}
}
