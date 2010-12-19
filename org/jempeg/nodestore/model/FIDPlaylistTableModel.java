/* FIDPlaylistTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import javax.swing.event.TableModelListener;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.event.IPlaylistListener;

public class FIDPlaylistTableModel extends AbstractPlaylistTableModel
    implements IFIDPlaylistWrapper, IPlaylistListener
{
    private FIDPlaylist myPlaylist;
    private int myListenerCount;
    
    public FIDPlaylistTableModel(FIDPlaylist _playlist) {
	super(PlaylistTableUtils
		  .getColumnTagNames(_playlist.getContainedType()));
	myPlaylist = _playlist;
    }
    
    public FIDPlaylistTableModel(FIDPlaylist _playlist,
				 String[] _columnTagNames) {
	super(_columnTagNames);
	myPlaylist = _playlist;
    }
    
    public String getName() {
	return myPlaylist.getName();
    }
    
    public Object getValueAt(int _row) {
	IFIDNode rowNode = getNodeAt(_row);
	return rowNode;
    }
    
    public IFIDNode getNodeAt(int _row) {
	IFIDNode rowNode = myPlaylist.getNodeAt(_row);
	return rowNode;
    }
    
    public FIDPlaylist getPlaylist() {
	return myPlaylist;
    }
    
    public int getRowCount() {
	int rowCount = myPlaylist.size();
	return rowCount;
    }
    
    public synchronized void addTableModelListener
	(TableModelListener _listener) {
	super.addTableModelListener(_listener);
	if (myListenerCount == 0)
	    myPlaylist.addPlaylistListener(this);
	myListenerCount++;
    }
    
    public synchronized void removeTableModelListener
	(TableModelListener _listener) {
	super.removeTableModelListener(_listener);
	myListenerCount--;
	if (myListenerCount == 0)
	    myPlaylist.removePlaylistListener(this);
    }
    
    public void playlistNodeInserted(FIDPlaylist _parentPlaylist,
				     IFIDNode _childNode, int _index) {
	if (_parentPlaylist == myPlaylist && _index != -1)
	    fireTableRowsInserted(_index, _index);
    }
    
    public void playlistNodeRemoved(FIDPlaylist _parentPlaylist,
				    IFIDNode _childNode, int _index) {
	if (_parentPlaylist == myPlaylist && _index != -1)
	    fireTableRowsDeleted(_index, _index);
    }
    
    public void playlistStructureChanged(FIDPlaylist _parentPlaylist) {
	if (_parentPlaylist == myPlaylist)
	    fireTableStructureChanged();
    }
}
