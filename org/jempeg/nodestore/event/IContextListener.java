package org.jempeg.nodestore.event;

import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

public interface IContextListener {
	public void databaseChanged(PlayerDatabase _oldPlayerDatabase, PlayerDatabase _newPlayerDatabase);
	
	public void synchronizeClientChanged(ISynchronizeClient _oldSynchronizeClient, ISynchronizeClient _newSynchronizeClient);
	
	public void selectedContainerChanged(IContainer _oldSelectedContainer, IContainer _newSelectedContainer);
	
	public void selectionChanged(Object _source, ContainerSelection _oldSelection, ContainerSelection _newSelection);
}
