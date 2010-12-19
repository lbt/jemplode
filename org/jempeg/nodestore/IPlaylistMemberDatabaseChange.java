package org.jempeg.nodestore;

public interface IPlaylistMemberDatabaseChange extends INodeDatabaseChange {
	public PlaylistPair[] getPlaylistPairs();
	
	public void setPlaylistPairs(PlaylistPair[] _playlistPairs);
}
