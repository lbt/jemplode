/* SortedPlaylistTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.io.IOException;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.table.ISortableTableModel;
import com.inzyme.table.SortedTableModel;

import org.jempeg.nodestore.IFIDNode;

public class SortedPlaylistTableModel extends SortedTableModel
    implements IPlaylistTableModel
{
    public SortedPlaylistTableModel() {
	/* empty */
    }
    
    public SortedPlaylistTableModel(IPlaylistTableModel _model) {
	super((ISortableTableModel) _model);
    }
    
    public void setModel(ISortableTableModel _model) {
	if (!(_model instanceof IPlaylistTableModel))
	    throw new IllegalArgumentException
		      ("You can only use a SortedPlaylistTableModel to sort an IPlaylistTableModel.");
	super.setModel(_model);
    }
    
    protected void init() {
	super.init();
	restoreSortSettings();
    }
    
    protected void saveSortSettings(int _column, boolean _ascending) {
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	int sortingColumn = _column;
	NodeTag tag = getNodeTag(sortingColumn);
	propertiesManager.setProperty("jempeg.playlist.sort.tag",
				      tag.getName());
	propertiesManager.setBooleanProperty("jempeg.playlist.sort.direction",
					     _ascending);
	try {
	    propertiesManager.save();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
    
    protected void restoreSortSettings() {
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	String sortTag
	    = propertiesManager.getProperty("jempeg.playlist.sort.tag");
	boolean direction
	    = propertiesManager
		  .getBooleanProperty("jempeg.playlist.sort.direction");
	int sortColumn = -1;
	int columnCount = getColumnCount();
	for (int i = 0; sortColumn == -1 && i < columnCount; i++) {
	    NodeTag tag = getNodeTag(i);
	    if (tag.getName().equals(sortTag))
		sortColumn = i;
	}
	if (sortColumn == -1)
	    sortColumn = 0;
	sortByColumn(sortColumn, direction);
    }
    
    public NodeTag getNodeTag(int _columnNum) {
	IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) model;
	return playlistTableModel.getNodeTag(_columnNum);
    }
    
    public IFIDNode getNodeAt(int _row) {
	IFIDNode node = (IFIDNode) getValueAt(_row);
	return node;
    }
    
    public String[] getColumnTagNames() {
	IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) model;
	return playlistTableModel.getColumnTagNames();
    }
    
    public void setColumnTagNames(String[] _columnTagNames) {
	IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) model;
	playlistTableModel.setColumnTagNames(_columnTagNames);
    }
    
    public void sortByColumn(int _column, boolean _ascending) {
	saveSortSettings(_column, _ascending);
	super.sortByColumn(_column, _ascending);
    }
    
    public NodeTag getSortingNodeTag() {
	int sortingColumn = getSortingColumn();
	NodeTag sortingNodeTag = getNodeTag(sortingColumn);
	return sortingNodeTag;
    }
    
    protected void resort() {
	restoreSortSettings();
	super.resort();
    }
}
