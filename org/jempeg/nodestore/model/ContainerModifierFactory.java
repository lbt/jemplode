/* ContainerModifierFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.tree.BasicContainerTreeNode;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.PlayerDatabase;

public class ContainerModifierFactory
{
    public static IContainerModifier getInstance
	(ContainerSelection _selection) {
	IContainer container = _selection.getContainer();
	IContainerModifier nodeModifier = getInstance(container);
	return nodeModifier;
    }
    
    public static IContainerModifier getInstance(IContainer _container) {
	IContainerModifier nodeModifier;
	if (_container instanceof IFIDPlaylistWrapper) {
	    FIDPlaylist playlist
		= ((IFIDPlaylistWrapper) _container).getPlaylist();
	    if (playlist.isSoup())
		nodeModifier = new SoupPlaylistContainerModifier(playlist);
	    else
		nodeModifier = new FIDPlaylistContainerModifier(playlist);
	} else if (_container instanceof BasicContainerTreeNode)
	    nodeModifier = new BasicContainerModifier(_container);
	else if (_container instanceof PlayerDatabase)
	    nodeModifier = new PlayerDatabaseContainerModifier((PlayerDatabase)
							       _container);
	else
	    throw new IllegalArgumentException
		      ("Unable to create a node modifier for a " + _container);
	return nodeModifier;
    }
}
