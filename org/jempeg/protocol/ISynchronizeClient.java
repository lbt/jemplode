package org.jempeg.protocol;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.PlaylistPair;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;

/**
 * Provides an abstraction over the highest level multistep protocol operations 
 * involving the PlayerDatabase (like downloading and synchronizing) and 
 * also provides an interface between ProtocolClient and
 * PlayerDatabase so that protocol details (like data storage formats) don't
 * creep into PlayerDatabase.
 * 
 * @author Mike Schrag
 */
public interface ISynchronizeClient {
	/**
	 * Adds a listener that will receive synchronize client events
	 * 
	 * @param _listener the listener to add 
	 */
	public void addSynchronizeClientListener(ISynchronizeClientListener _listener);
	
	/**
	 * Removes a listener so that it will no longer receive synchronize client events
	 * 
	 * @param _listener the listener to remove 
	 */
	public void removeSynchronizeClientListener(ISynchronizeClientListener _listener);
	
	/**
	 * Returns the ConnectionFactory that is associated with this SynchronizeClient.
	 * 
	 * @return the ConnectionFactory that is associated with this SynchronizeClient
	 */
	public IConnectionFactory getConnectionFactory();
	
	/**
	 * Returns the ProtocolClient that this SynchronizeClient communicates with.  Note
	 * that if you call getProtocolClient, you must .close() the protocol client when
	 * you are done.
	 * 
	 * @param _progressListener the progressListener to use
	 * @return IProtocolClient the ProtocolClient that this SynchronizeClient communicates with
	 */
	public IProtocolClient getProtocolClient(ISimpleProgressListener _progressListener);
	
	/**
	 * Called when a node needs to be deleted.
	 * 
	 * @param _node the node to delete
	 * @param _protocolClient the ProtocolClient to synchronize with
	 * @throws SynchronizeException if the node cannot be deleted
	 */
	public void synchronizeDelete(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException;
  
  /**
   * Called when the file info for a node has been changed and needs to be
   * synchronized.
   * 
   * @param _node the node that has changed
   * @param _protocolClient the ProtocolClient to synchronize with
   * @throws SynchronizeException if the tags cannot be written
   */
  public void synchronizeFile(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException;
	
	/**
	 * Called when the file info for a node has been changed and needs to be
	 * synchronized.
	 * 
	 * @param _node the node that has changed
	 * @param _protocolClient the ProtocolClient to synchronize with
	 * @throws SynchronizeException if the tags cannot be written
	 */
	public void synchronizeTags(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException;
	
	/**
	 * Called when a playlist needs to be synchronized.  This allows for different 
	 * implementation of the playlist synchronize protocol.
	 * 
	 * @param _playlist the playlist to synchronize
	 * @param _playlistPairs the playlist pairs to synchronize
	 * @param _protocolClient the ProtocolClient to synchronize with
	 * @throws SynchronizeException if the playlist cannot be synchronized
	 */
	public void synchronizePlaylistTags(FIDPlaylist _playlist, PlaylistPair[] _playlistPairs, IProtocolClient _protocolClient) throws SynchronizeException;

	/**
	 * Initiates a download of the PlayerDatabase from a Player.  After calling
	 * download and dealing with any potential errors, <i>make sure you call
	 * playerDatabase.checkForProblems(..)</i>.  CheckForProblems will check for
	 * any playlist errors as well as setting the reference counts to the proper
	 * values.
	 * 
	 * @param _playerDatabase the player database to download into
	 * @param _rebuildOnFailure should the player be rebuilt if there is an error?
	 * @param _progressListener the progress listener to report progress to
	 * @return Reason[] explanations of any warnings or errors that occurred
	 * during the download
	 * @throws SynchronizeException if the download fails in an unrecoverable way
	 */
	public Reason[] download(PlayerDatabase _playerDatabase, boolean _rebuildOnFailure, IProgressListener _progressListener) throws SynchronizeException;

	/**
	 * Synchronize this PlayerDatabase with your Empeg.  This will move all
	 * the changes that have been made to this database onto your Empeg.  After 
	 * calling synchronize and dealing with any potential errors, <i>make sure 
	 * you call checkForProblems(..)</i>.  CheckForProblems will check for 
	 * any playlist errors as well as setting the reference counts to the 
	 * proper values.
	 * 
	 * @param _playerDatabase the player database to synchronize
	 * @param _progressListener the progress listener to report progress to
	 * @return a set of reasons why the synchronize or redownload failed or had problems
	 * @throws InterruptedException if the user cancels the synchronization
	 * @throws SynchronizeException if the synchronize fails
	 */
	public Reason[] synchronize(PlayerDatabase _playerDatabase, IProgressListener _progressListener) throws InterruptedException, SynchronizeException;
	
}
