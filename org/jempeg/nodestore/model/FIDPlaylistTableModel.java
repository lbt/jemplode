package org.jempeg.nodestore.model;

import javax.swing.event.TableModelListener;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.event.IPlaylistListener;

/**
 * FIDPlaylistTableModel is a table model that proxies an FIDPlaylist, 
 * presenting its children in a table format.
 *
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class FIDPlaylistTableModel extends AbstractPlaylistTableModel implements IFIDPlaylistWrapper, IPlaylistListener {
  private FIDPlaylist myPlaylist;
  private int myListenerCount;

  /**
   * Constructs a new FIDPlaylistTableModel.
   * 
   * @param _playlist the playlist to create a table view for
   */
  public FIDPlaylistTableModel(FIDPlaylist _playlist) {
    super(PlaylistTableUtils.getColumnTagNames(_playlist.getContainedType()));
    myPlaylist = _playlist;
  }

  /**
   * Constructs a new FIDPlaylistTableModel.
   * 
   * @param _playlist the playlist to create a table view for
   * @param _columnTagNames the tag names to use as columns
   */
  public FIDPlaylistTableModel(FIDPlaylist _playlist, String[] _columnTagNames) {
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

  public synchronized void addTableModelListener(TableModelListener _listener) {
    super.addTableModelListener(_listener);
    if (myListenerCount == 0) {
      myPlaylist.addPlaylistListener(this);
      //Debug.println(Debug.INFORMATIVE, "TableModel " + this + " is now listening to " + myPlaylist);
    }
    myListenerCount ++;
  }

  /**
   * @see javax.swing.table.TableModel#removeTableModelListener(TableModelListener)
   */
  public synchronized void removeTableModelListener(TableModelListener _listener) {
    super.removeTableModelListener(_listener);
    myListenerCount --;
    if (myListenerCount == 0) {
      myPlaylist.removePlaylistListener(this);
      //Debug.println(Debug.INFORMATIVE, "TableModel " + this + " is no longer listening to " + myPlaylist);
    }
  }

  public void playlistNodeInserted(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index) {
    if (_parentPlaylist == myPlaylist && _index != -1) {
      fireTableRowsInserted(_index, _index);
    }
  }

  public void playlistNodeRemoved(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index) {
    if (_parentPlaylist == myPlaylist && _index != -1) {
      fireTableRowsDeleted(_index, _index);
    }
  }

  public void playlistStructureChanged(FIDPlaylist _parentPlaylist) {
    if (_parentPlaylist == myPlaylist) {
      fireTableStructureChanged();
    }
  }
}