package org.jempeg.protocol;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.PlaylistPair;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.SynchronizeQueue;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;

/**
 * NoSynchronizeClient is an implementation of SynchronizeClient that basically
 * just NoOps.  This is a stub for player databases that aren't downloaded. 
 * 
 * @author Mike Schrag
 */
public class NoSynchronizeClient implements ISynchronizeClient {
	private NoProtocolClient myProtocolClient;
	
	public NoSynchronizeClient() {
		myProtocolClient = new NoProtocolClient();
	}
	
	public void addSynchronizeClientListener(ISynchronizeClientListener _listener) {
	}
	
	public void removeSynchronizeClientListener(ISynchronizeClientListener _listener) {
	}
	
	public IConnectionFactory getConnectionFactory() {
		return null;
	}

	public IProtocolClient getProtocolClient(ISimpleProgressListener _progressListener) {
		return myProtocolClient;
	}
	
	public void synchronizeDelete(IFIDNode __node, IProtocolClient _client) throws SynchronizeException {
	}
	
	public void synchronizePlaylistTags(FIDPlaylist _playlist, PlaylistPair[] _playlistPairs, IProtocolClient _protocolClient) throws SynchronizeException {
	}
	
	public void synchronizeTags(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException {
	}
  
  public void synchronizeFile(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException {
  }

	public Reason[] download(PlayerDatabase _playerDatabase, boolean _rebuildOnFailure, IProgressListener _progressListener) throws SynchronizeException {
		return new Reason[0];
	}

	public Reason[] synchronize(PlayerDatabase _playerDatabase, IProgressListener _progressListener) throws InterruptedException, SynchronizeException {
		SynchronizeQueue synchronizeQueue = _playerDatabase.getSynchronizeQueue();
		synchronized (synchronizeQueue) {
			while (!synchronizeQueue.isEmpty()) {
				IDatabaseChange databaseChange = synchronizeQueue.dequeue();
				databaseChange.synchronize(this, myProtocolClient);
			}
			synchronizeQueue.clear();
		}
		return new Reason[0];
	}
}
