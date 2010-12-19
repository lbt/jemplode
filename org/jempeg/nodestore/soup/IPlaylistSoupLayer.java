package org.jempeg.nodestore.soup;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public interface IPlaylistSoupLayer extends ISoupLayer {
	public FIDPlaylist[] getPlaylists(FIDPlaylist _soupPlaylist, IFIDNode _node, boolean _createIfMissing);
}
