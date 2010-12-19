package org.jempeg.nodestore;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

/**
 * An IDatabaseChange represents a single modification to the
 * PlayerDatabase that needs to be synced to a device.
 * 
 * @author Mike Schrag
 */
public interface IDatabaseChange {
	/**
	 * Returns the number of attempts that have been made to sync this change.
	 * 
	 * @return the number of sync attempts
	 */
	public int getAttempt();
	
	/**
	 * Sets the number of attempts that have been made to sync this change.
	 * 
	 * @param _attempt the number of attempts 
	 */
	public void setAttempt(int _attempt);
	
	/**
	 * Increment the number of sync attempts.
	 */
	public void incrementAttempt();
	
	/**
	 * Returns the name of this change.
	 */
	public String getName();
	
	/**
	 * Returns a description of this change.
	 */
	public String getDescription();
	
	/**
	 * Synchronize this database change.
	 * 
	 * @param _synchronizeClient the SynchronizeClient to synchronize with
	 * @param _protocolClient the ProtocolClient of the device to sync with
	 * @throws SynchronizeException if this change fails to synchronize
	 */
	public void synchronize(ISynchronizeClient _synchronizeClient, IProtocolClient _protocolClient) throws SynchronizeException;
	
	/**
	 * Returns the number of bytes that this change will transmit.
	 * 
	 * @return the number of bytes that this change will transmit
	 */
	public long getLength();
}
