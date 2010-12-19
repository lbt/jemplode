/* IPlaylistListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.event;
import java.util.EventListener;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public interface IPlaylistListener extends EventListener
{
    public void playlistStructureChanged(FIDPlaylist fidplaylist);
    
    public void playlistNodeInserted(FIDPlaylist fidplaylist,
				     IFIDNode ifidnode, int i);
    
    public void playlistNodeRemoved(FIDPlaylist fidplaylist, IFIDNode ifidnode,
				    int i);
}
