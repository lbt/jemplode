package org.jempeg.nodestore.model;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.PlayerDatabase;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.tree.BasicContainerTreeNode;

/**
 * ContainerModifierFactory can create an IContainerModifier that
 * is able to handle modifying a particular ContainerSelection or Container.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class ContainerModifierFactory {
	/**
	 * Returns an IContainerModifier instance for the given selection.
	 * 
	 * @param _selection the selection to create a node modifier for
	 * @return an IContainerModifier instance for the given selection
	 */
	public static IContainerModifier getInstance(ContainerSelection _selection) {
		IContainer container = _selection.getContainer();
		IContainerModifier nodeModifier = getInstance(container);
		return nodeModifier;
	}

	/**
	 * Returns an IContainerModifier instance for the given IContainer.
	 * 
	 * @param _container the container to create a node modifier for
	 * @return an IContainerModifier instance for the given selection
	 */
	public static IContainerModifier getInstance(IContainer _container) {
		IContainerModifier nodeModifier;
		if (_container instanceof IFIDPlaylistWrapper) {
			FIDPlaylist playlist = ((IFIDPlaylistWrapper) _container).getPlaylist();
			if (playlist.isSoup()) {
				nodeModifier = new SoupPlaylistContainerModifier(playlist);
			}
			else {
				nodeModifier = new FIDPlaylistContainerModifier(playlist);
			}
		}
		else if (_container instanceof BasicContainerTreeNode) {
			nodeModifier = new BasicContainerModifier(_container);
		}
		else if (_container instanceof PlayerDatabase) {
			nodeModifier = new PlayerDatabaseContainerModifier((PlayerDatabase) _container);
		}
		else {
			throw new IllegalArgumentException("Unable to create a node modifier for a " + _container);
		}
		return nodeModifier;
	}
}
