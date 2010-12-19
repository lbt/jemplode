/* PlaylistAddedDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class PlaylistAddedDatabaseChange
    extends AbstractNodeAddedDatabaseChange
    implements IPlaylistMemberDatabaseChange
{
    private PlaylistPair[] myPlaylistPairs;
    
    public PlaylistAddedDatabaseChange(FIDPlaylist _playlist) {
	super((IFIDNode) _playlist);
	myPlaylistPairs = _playlist.getPlaylistPairs();
    }
    
    public PlaylistPair[] getPlaylistPairs() {
	return myPlaylistPairs;
    }
    
    public void setPlaylistPairs(PlaylistPair[] _playlistPairs) {
	myPlaylistPairs = _playlistPairs;
    }
    
    public String getDescription() {
	return ResourceBundleUtils.getUIString("databaseChange.playlistAdded",
					       (new Object[]
						{ getNode().getTitle() }));
    }
    
    public void synchronize
	(ISynchronizeClient _synchronizeClient,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	PlaylistMembersDatabaseChange.synchronizePlaylistTags
	    (this, _synchronizeClient, _protocolClient);
    }
}
