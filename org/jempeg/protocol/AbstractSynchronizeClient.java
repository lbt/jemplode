/* AbstractSynchronizeClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.util.Vector;

import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

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

public abstract class AbstractSynchronizeClient
    implements ISynchronizeClient, IPasswordManager
{
    private static final String PASSWORD_KEY = "password";
    private IConnectionFactory myConnectionFactory;
    private ISynchronizeClientListener[] myListeners
	= new ISynchronizeClientListener[0];
    private IDatabaseChange myCurrentDatabaseChange;
    private String myPassword;
    
    protected class SynchronizeProgressListener
	implements ISimpleProgressListener
    {
	private IProgressListener myProxiedProgressListener;
	
	public SynchronizeProgressListener
	    (IProgressListener _proxiedProgressListener) {
	    myProxiedProgressListener = _proxiedProgressListener;
	}
	
	public void progressReported(long _current, long _maximum) {
	    if (myCurrentDatabaseChange != null) {
		fireSynchronizeInProgress(myCurrentDatabaseChange, _current,
					  _maximum);
		myProxiedProgressListener
		    .taskStarted(myCurrentDatabaseChange.getDescription());
		myProxiedProgressListener.taskUpdated(_current, _maximum);
	    } else
		myProxiedProgressListener.taskUpdated(_current, _maximum);
	}
	
	public void progressReported(String _description, long _current,
				     long _maximum) {
	    if (myCurrentDatabaseChange == null)
		myProxiedProgressListener.taskStarted(_description);
	    progressReported(_current, _maximum);
	}
    }
    
    protected class DownloadProgressListener implements ISimpleProgressListener
    {
	private IProgressListener myProxiedProgressListener;
	
	public DownloadProgressListener
	    (IProgressListener _proxiedProgressListener) {
	    myProxiedProgressListener = _proxiedProgressListener;
	}
	
	public void progressReported(long _current, long _maximum) {
	    myProxiedProgressListener.taskUpdated(_current, _maximum);
	}
	
	public void progressReported(String _description, long _current,
				     long _maximum) {
	    myProxiedProgressListener.taskStarted(_description);
	    progressReported(_current, _maximum);
	}
    }
    
    protected AbstractSynchronizeClient
	(IConnectionFactory _connectionFactory) {
	myConnectionFactory = _connectionFactory;
	myPassword = PropertiesManager.getInstance().getProperty("password");
    }
    
    public synchronized void addSynchronizeClientListener
	(ISynchronizeClientListener _listener) {
	ISynchronizeClientListener[] listeners
	    = new ISynchronizeClientListener[myListeners.length + 1];
	System.arraycopy(myListeners, 0, listeners, 0, myListeners.length);
	listeners[listeners.length - 1] = _listener;
	myListeners = listeners;
    }
    
    public synchronized void removeSynchronizeClientListener
	(ISynchronizeClientListener _listener) {
	ISynchronizeClientListener[] listeners
	    = new ISynchronizeClientListener[myListeners.length - 1];
	int newIndex = 0;
	int i = 0;
	while (i < myListeners.length) {
	    if (myListeners[i] == _listener)
		newIndex--;
	    else
		listeners[newIndex] = myListeners[i];
	    i++;
	    newIndex++;
	}
	myListeners = listeners;
    }
    
    void fireDownloadStarted(PlayerDatabase _playerDatabase) {
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].downloadStarted(_playerDatabase);
    }
    
    void fireDownloadCompleted(PlayerDatabase _playerDatabase) {
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].downloadCompleted(_playerDatabase);
    }
    
    void fireSynchronizeStarted(PlayerDatabase _playerDatabase) {
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].synchronizeStarted(_playerDatabase);
    }
    
    void fireSynchronizeStarted(IDatabaseChange _databaseChange) {
	myCurrentDatabaseChange = _databaseChange;
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].synchronizeStarted(_databaseChange);
    }
    
    void fireSynchronizeInProgress(IDatabaseChange _databaseChange,
				   long _current, long _total) {
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].synchronizeInProgress(_databaseChange, _current,
						 _total);
    }
    
    void fireSynchronizeCompleted(IDatabaseChange _databaseChange,
				  boolean _successfully) {
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].synchronizeCompleted(_databaseChange,
						_successfully);
	myCurrentDatabaseChange = null;
    }
    
    void fireSynchronizeCompleted(PlayerDatabase _playerDatabase,
				  boolean _successfully) {
	for (int i = myListeners.length - 1; i >= 0; i--)
	    myListeners[i].synchronizeCompleted(_playerDatabase,
						_successfully);
    }
    
    public void setPassword(String _password, boolean _persistent) {
	myPassword = _password;
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	if (_persistent)
	    propertiesManager.setProperty("password", _password);
	else
	    propertiesManager.removeProperty("password");
	try {
	    propertiesManager.save();
	} catch (Throwable t) {
	    Debug.println(t);
	}
    }
    
    public String getPassword() {
	return myPassword;
    }
    
    public IConnectionFactory getConnectionFactory() {
	return myConnectionFactory;
    }
    
    public abstract void synchronizePlaylistTags
	(FIDPlaylist fidplaylist, PlaylistPair[] playlistpairs,
	 IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public abstract void synchronizeTags
	(IFIDNode ifidnode, IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public Reason[] download
	(PlayerDatabase _playerDatabase, boolean _rebuildOnFailure,
	 IProgressListener _progressListener)
	throws SynchronizeException {
	try {
	    fireDownloadStarted(_playerDatabase);
	    SynchronizeQueue synchronizeQueue
		= _playerDatabase.getSynchronizeQueue();
	    IProtocolClient protocolClient
		= (getProtocolClient
		   (new DownloadProgressListener(_progressListener)));
	    protocolClient.open();
	    Reason[] databaseProblems;
	    try {
		protocolClient.waitForDevice(10);
		protocolClient.readLock();
		_playerDatabase.clear();
		synchronizeQueue.clear();
		computeFreeSpace(_playerDatabase, protocolClient);
		Reason[] downloadProblems
		    = download0(_playerDatabase, protocolClient,
				_rebuildOnFailure, _progressListener);
		Reason[] databaseRepairProblems
		    = _playerDatabase.checkForProblems0(true);
		databaseProblems
		    = new Reason[(downloadProblems.length
				  + databaseRepairProblems.length)];
		System.arraycopy(downloadProblems, 0, databaseProblems, 0,
				 downloadProblems.length);
		System.arraycopy(databaseRepairProblems, 0, databaseProblems,
				 downloadProblems.length,
				 databaseRepairProblems.length);
		_playerDatabase
		    .setDeviceSettings(protocolClient.getDeviceSettings());
		NodeTag.resetNodeTags(_playerDatabase.getDatabaseTags());
	    } catch (Object object) {
		try {
		    protocolClient.unlock();
		} catch (Throwable t) {
		    Debug.println(t);
		}
		protocolClient.close();
		throw object;
	    }
	    try {
		protocolClient.unlock();
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	    protocolClient.close();
	    fireDownloadCompleted(_playerDatabase);
	    return databaseProblems;
	} catch (ProtocolException e) {
	    throw new SynchronizeException
		      ((new ResourceBundleKey
			("errors", "synchronize.databaseDownloadFailed")),
		       e);
	}
    }
    
    protected abstract Reason[] download0
	(PlayerDatabase playerdatabase, IProtocolClient iprotocolclient,
	 boolean bool, IProgressListener iprogresslistener)
	throws ProtocolException, SynchronizeException;
    
    public Reason[] synchronize
	(PlayerDatabase _playerDatabase, IProgressListener _progressListener)
	throws InterruptedException, SynchronizeException {
	object = object_3_;
	break;
    }
    
    protected void closeAndReopen(IProtocolClient _protocolClient)
	throws ProtocolException {
	try {
	    _protocolClient.close();
	} catch (Throwable ex) {
	    Debug.println(ex);
	}
	_protocolClient.open();
	_protocolClient.waitForDevice(10);
	_protocolClient.writeLock();
    }
    
    protected abstract void beforeSynchronize
	(PlayerDatabase playerdatabase, IProtocolClient iprotocolclient,
	 IProgressListener iprogresslistener)
	throws SynchronizeException, ProtocolException;
    
    protected abstract void afterSynchronize
	(PlayerDatabase playerdatabase, IProtocolClient iprotocolclient,
	 IProgressListener iprogresslistener)
	throws SynchronizeException, ProtocolException;
    
    protected void computeFreeSpace
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient)
	throws ProtocolException {
	StorageInfo storageInfo = _protocolClient.getStorageInfo(0);
	_playerDatabase.setFreeSpace(storageInfo.getSize(),
				     storageInfo.getFree());
    }
}
