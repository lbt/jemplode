/* ISynchronizeClientListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.event;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.PlayerDatabase;

public interface ISynchronizeClientListener
{
    public void downloadStarted(PlayerDatabase playerdatabase);
    
    public void downloadCompleted(PlayerDatabase playerdatabase);
    
    public void synchronizeStarted(PlayerDatabase playerdatabase);
    
    public void synchronizeStarted(IDatabaseChange idatabasechange);
    
    public void synchronizeInProgress(IDatabaseChange idatabasechange, long l,
				      long l_0_);
    
    public void synchronizeCompleted(IDatabaseChange idatabasechange,
				     boolean bool);
    
    public void synchronizeCompleted(PlayerDatabase playerdatabase,
				     boolean bool);
}
