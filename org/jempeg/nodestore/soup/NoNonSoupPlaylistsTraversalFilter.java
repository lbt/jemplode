package org.jempeg.nodestore.soup;

import org.jempeg.nodestore.FIDPlaylist;

import com.inzyme.container.IContainer;
import com.inzyme.tree.ITraversalFilter;

public class NoNonSoupPlaylistsTraversalFilter implements ITraversalFilter {
	public NoNonSoupPlaylistsTraversalFilter() {
	}

	public boolean qualifies(IContainer _parentContainer, IContainer _childContainer, int _depth) {
		boolean qualifies = true;
		if (_childContainer instanceof FIDPlaylist) {
			FIDPlaylist childPlaylist = (FIDPlaylist)_childContainer;
			qualifies = childPlaylist.isSoup();
		}
		return qualifies;
	}

}
