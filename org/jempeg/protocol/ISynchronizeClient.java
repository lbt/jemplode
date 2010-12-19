/* ISynchronizeClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.PlaylistPair;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

public interface ISynchronizeClient
{
    public void addSynchronizeClientListener
	(ISynchronizeClientListener isynchronizeclientlistener);
    
    public void removeSynchronizeClientListener
	(ISynchronizeClientListener isynchronizeclientlistener);
    
    public IConnectionFactory getConnectionFactory();
    
    public IProtocolClient getProtocolClient
	(ISimpleProgressListener isimpleprogresslistener);
    
    public void synchronizeDelete
	(IFIDNode ifidnode, IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public void synchronizeFile
	(IFIDNode ifidnode, IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public void synchronizeTags
	(IFIDNode ifidnode, IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public void synchronizePlaylistTags
	(FIDPlaylist fidplaylist, PlaylistPair[] playlistpairs,
	 IProtocolClient iprotocolclient)
	throws SynchronizeException;
    
    public Reason[] download
	(PlayerDatabase playerdatabase, boolean bool,
	 IProgressListener iprogresslistener)
	throws SynchronizeException;
    
    public Reason[] synchronize
	(PlayerDatabase playerdatabase, IProgressListener iprogresslistener)
	throws InterruptedException, SynchronizeException;
}
