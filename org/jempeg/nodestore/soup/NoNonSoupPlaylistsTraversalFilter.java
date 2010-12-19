/* NoNonSoupPlaylistsTraversalFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import com.inzyme.container.IContainer;
import com.inzyme.tree.ITraversalFilter;

import org.jempeg.nodestore.FIDPlaylist;

public class NoNonSoupPlaylistsTraversalFilter implements ITraversalFilter
{
    public boolean qualifies(IContainer _parentContainer,
			     IContainer _childContainer, int _depth) {
	boolean qualifies = true;
	if (_childContainer instanceof FIDPlaylist) {
	    FIDPlaylist childPlaylist = (FIDPlaylist) _childContainer;
	    qualifies = childPlaylist.isSoup();
	}
	return qualifies;
    }
}
