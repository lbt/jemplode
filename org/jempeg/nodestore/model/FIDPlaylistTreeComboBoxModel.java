package org.jempeg.nodestore.model;

import javax.swing.tree.TreeNode;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.event.IPlaylistListener;

import com.inzyme.tree.TreeComboBoxModel;

public class FIDPlaylistTreeComboBoxModel extends TreeComboBoxModel implements IPlaylistListener, IFIDPlaylistWrapper {
  public FIDPlaylistTreeComboBoxModel(FIDPlaylistTreeNode _treeNode, boolean _rootVisible) {
    super(_treeNode, _rootVisible);
  }

  protected void nodeClosed(TreeNode _node) {
    if (_node instanceof IFIDPlaylistWrapper) {
      FIDPlaylist playlist = ((FIDPlaylistTreeNode) _node).getPlaylist();
      playlist.removePlaylistListener(this);
    }
  }

  protected void nodeOpened(TreeNode _node) {
    if (_node instanceof IFIDPlaylistWrapper) {
      FIDPlaylist playlist = ((FIDPlaylistTreeNode) _node).getPlaylist();
      playlist.addPlaylistListener(this);
    }
  }

  public void playlistNodeInserted(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index) {
    notifyStructureChanged();
  }

  public void playlistNodeRemoved(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index) {
    notifyStructureChanged();
  }

  public void playlistStructureChanged(FIDPlaylist _parentPlaylist) {
    notifyStructureChanged();
  }

  public FIDPlaylist getPlaylist() {
    return ((FIDPlaylistTreeNode) getRootNode()).getPlaylist();
  }

  /**
   * Removes this playlist from all the objects it
   * is listening to.  Make sure you call this
   * whenever a node is removed, otherwise you're
   * talking memory leak city!!
   */
  public synchronized void removeAllListeners() {
    FIDPlaylistTreeNode rootNode = (FIDPlaylistTreeNode) getRootNode();
    closeAll(false);
    rootNode.removeAllListeners();
  }

  public FIDPlaylist getSelectedPlaylist() {
    IFIDPlaylistWrapper wrapper = (IFIDPlaylistWrapper) getSelectedItem();
    FIDPlaylist playlist;
    if (wrapper != null) {
      playlist = wrapper.getPlaylist();
    }
    else {
      playlist = null;
    }
    return playlist;
  }
}