/* IDatabaseListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.event;
import java.util.EventListener;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;

public interface IDatabaseListener extends EventListener
{
    public void freeSpaceChanged(PlayerDatabase playerdatabase, long l,
				 long l_0_);
    
    public void nodeAdded(IFIDNode ifidnode);
    
    public void nodeRemoved(IFIDNode ifidnode);
    
    public void databaseCleared(PlayerDatabase playerdatabase);
}
