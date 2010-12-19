package org.jempeg.nodestore;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.text.ResourceBundleUtils;

public class PlaylistMembersDatabaseChange extends FileInfoDatabaseChange {
	public PlaylistMembersDatabaseChange(FIDPlaylist _playlist) {
		super(_playlist, DatabaseTags.PLAYLIST_TAG, null, null);
	}
	
	public String getDescription() {
		return ResourceBundleUtils.getUIString("databaseChange.playlistMembers", new Object[] { getNode().getTitle() });
	}
	
	public void synchronize(ISynchronizeClient _synchronizeClient, IProtocolClient _protocolClient) throws SynchronizeException {
		PlaylistMembersDatabaseChange.synchronizePlaylistTags(this, _synchronizeClient, _protocolClient);
	}

	public static void synchronizePlaylistTags(IPlaylistMemberDatabaseChange _databaseChange, ISynchronizeClient _synchronizeClient, IProtocolClient _protocolClient) throws SynchronizeException {
		FIDPlaylist playlist = (FIDPlaylist)_databaseChange.getNode();
		// set "length" to its proper value
		playlist.updateLength();
		_synchronizeClient.synchronizePlaylistTags(playlist, _databaseChange.getPlaylistPairs(), _protocolClient);
		playlist.setDirty(false); // clean, no need to sync now (phew!)
	}
}
