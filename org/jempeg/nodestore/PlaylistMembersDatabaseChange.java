/* PlaylistMembersDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class PlaylistMembersDatabaseChange extends FileInfoDatabaseChange
{
    public PlaylistMembersDatabaseChange(FIDPlaylist _playlist) {
	super(_playlist, "playlist", null, null);
    }
    
    public String getDescription() {
	return (ResourceBundleUtils.getUIString
		("databaseChange.playlistMembers",
		 new Object[] { getNode().getTitle() }));
    }
    
    public void synchronize
	(ISynchronizeClient _synchronizeClient,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	synchronizePlaylistTags(this, _synchronizeClient, _protocolClient);
    }
    
    public static void synchronizePlaylistTags
	(IPlaylistMemberDatabaseChange _databaseChange,
	 ISynchronizeClient _synchronizeClient,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	FIDPlaylist playlist = (FIDPlaylist) _databaseChange.getNode();
	playlist.updateLength();
	_synchronizeClient.synchronizePlaylistTags(playlist,
						   _databaseChange
						       .getPlaylistPairs(),
						   _protocolClient);
	playlist.setDirty(false);
    }
}
