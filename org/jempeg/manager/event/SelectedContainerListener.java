package org.jempeg.manager.event;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IContextListener;
import org.jempeg.nodestore.model.PlaylistTableModelFactory;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

public class SelectedContainerListener implements IContextListener {
	private JTable myTable;
	private ApplicationContext myContext;

	public SelectedContainerListener(ApplicationContext _context, JTable _table) {
		myContext = _context;
		myTable = _table;
	}
	
	public void databaseChanged(PlayerDatabase _oldPlayerDatabase, PlayerDatabase _newPlayerDatabase) {

	}
	
	public void selectedContainerChanged(IContainer _oldSelectedContainer, IContainer _newSelectedContainer) {
		if (myTable == myContext.getTable()) {
			TableModel tableModel = PlaylistTableModelFactory.getTableModel(_newSelectedContainer);
			myTable.setModel(tableModel);
		}
	}
	
	public void selectionChanged(Object _source, ContainerSelection _oldSelection, ContainerSelection _newSelection) {

	}
	
	public void synchronizeClientChanged(ISynchronizeClient _oldSynchronizeClient, ISynchronizeClient _newSynchronizeClient) {

	}
}
