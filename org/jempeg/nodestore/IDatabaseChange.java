/* IDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public interface IDatabaseChange
{
    public int getAttempt();
    
    public void setAttempt(int i);
    
    public void incrementAttempt();
    
    public String getName();
    
    public String getDescription();
    
    public void synchronize
	(ISynchronizeClient isynchronizeclient,
	 IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public long getLength();
}
