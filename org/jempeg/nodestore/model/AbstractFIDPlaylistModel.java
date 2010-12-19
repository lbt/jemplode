package org.jempeg.nodestore.model;

import java.util.Enumeration;
import java.util.Vector;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.event.IPlaylistListener;

import com.inzyme.container.IContainer;

/**
 * AbstractFIDPlaylistModel providse the implementation 
 * of common methods that are shared by different models that
 * provides views of playlists and their playlist children.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFIDPlaylistModel implements IContainer, IPlaylistListener, IFIDPlaylistWrapper {
  private int myPlaylistIndex;
  private FIDPlaylist myPlaylist;
  private Vector myChildren;

  /**
   * Constructs a new AbstractFIDPlaylistModel.
   * 
   * @param _playlist the FIDPlaylist
   * @param _playlistIndex the index of this node in it's parent
   */
  public AbstractFIDPlaylistModel(FIDPlaylist _playlist, int _playlistIndex) {
    myPlaylist = _playlist;
    myPlaylistIndex = _playlistIndex;
  }

  /**
   * @see javax.swing.tree.TreeNode#children()
   */
  public synchronized Enumeration children() {
    ensureChildrenLoaded();
    Enumeration enum = myChildren.elements();
    return enum;
  }

  public int getPlaylistIndexOf(Object _obj) {
    ensureChildrenLoaded();
    return myChildren.indexOf(_obj);
  }

  /**
   * @see javax.swing.tree.TreeNode#getAllowsChildren()
   */
  public boolean getAllowsChildren() {
    return true;
  }

  /**
   * @see javax.swing.tree.TreeNode#isLeaf()
   */
  public boolean isLeaf() {
    boolean isLeaf;
    if (myPlaylist.isTransient()) {
      isLeaf = false;
    }
    else {
      ensureChildrenLoaded();
      isLeaf = myChildren.size() == 0;
    }
    return isLeaf;
  }

  /**
   * Returns the FIDPlaylist that is backing this TreeNode.
   * 
   * @return the FIDPlaylist that is backing this TreeNode
   */
  public FIDPlaylist getPlaylist() {
    return myPlaylist;
  }

  /**
   * Returns the index of this node its parent playlist.
   * 
   * @return the index of this node its parent playlist
   */
  public int getPlaylistIndex() {
    return myPlaylistIndex;
  }

  /**
   * Should be called when the underlying FIDPlaylist changes.  This
   * will cause this node and its children to be recomputed.
   */
  public synchronized void playlistStructureChanged() {
    Vector newChildrenVec = new Vector();

    synchronized (myPlaylist) {
      int size = myPlaylist.size();
      for (int i = 0; i < size; i ++ ) {
        FIDPlaylist playlist = myPlaylist.getPlaylistAt(i);
        if (playlist != null) {
          AbstractFIDPlaylistModel childPlaylistModel = createChildModel(playlist, i);
          newChildrenVec.addElement(childPlaylistModel);
        }
      }
    }

    boolean initialLoad = (myChildren == null);

    if (myChildren != null) {
      int size = myChildren.size();
      for (int i = 0; i < size; i ++ ) {
        AbstractFIDPlaylistModel childNode = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
        childNode.removeAllListeners();
      }
    }

    myChildren = newChildrenVec;

    if (!initialLoad) {
      notifyStructureChanged();
    }
  }

  public synchronized void playlistNodeInserted(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index) {
    if (myChildren != null && _parentPlaylist == myPlaylist && _childNode instanceof FIDPlaylist) {
      FIDPlaylist childPlaylist = (FIDPlaylist) _childNode;
      int index = -1;
      int size = myChildren.size();
      for (int i = 0; index == -1 && i < size; i ++ ) {
        AbstractFIDPlaylistModel childNode = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
        if (childNode.myPlaylistIndex == _index) {
          index = i;
        }
      }
      if (index == -1) {
        index = size;
      }

      AbstractFIDPlaylistModel newChildModel = createChildModel(childPlaylist, _index);
      myChildren.insertElementAt(newChildModel, index);

      size ++;
      for (int i = index + 1; i < size; i ++ ) {
        AbstractFIDPlaylistModel childNode = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
        childNode.myPlaylistIndex ++;
      }

      notifyChildrenWereInserted(new int[] {
        index
      });
    }
  }

  public synchronized void playlistNodeRemoved(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index) {
    if (myChildren != null && _parentPlaylist == myPlaylist) {
      if (_childNode instanceof FIDPlaylist) {
        int index = -1;
        int size = myChildren.size();
        for (int i = 0; index == -1 && i < size; i ++ ) {
          AbstractFIDPlaylistModel childNode = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
          if (childNode.myPlaylistIndex == _index) {
            index = i;
          }
        }
        AbstractFIDPlaylistModel childPlaylistModel = (AbstractFIDPlaylistModel) myChildren.elementAt(index);
        childPlaylistModel.removeAllListeners();

        myChildren.removeElementAt(index);

        size --;
        for (int i = index; i < size; i ++ ) {
          AbstractFIDPlaylistModel childNode = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
          if (childNode.myPlaylistIndex > _index) {
            childNode.myPlaylistIndex --;
          }
        }

        notifyChildrenWereRemoved(new int[] {
          index
        }, new Object[] {
          childPlaylistModel
        });
      }
      else {
        int size = myChildren.size();
        for (int i = 0; i < size; i ++ ) {
          AbstractFIDPlaylistModel childModel = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
          if (childModel.myPlaylistIndex > _index) {
            childModel.myPlaylistIndex --;
          }
        }
      }
    }
  }

  public synchronized void playlistStructureChanged(FIDPlaylist _parentPlaylist) {
    if (myChildren != null && _parentPlaylist == myPlaylist) {
      playlistStructureChanged();
    }
  }

  public String toString() {
    String title = myPlaylist.getTitle();
    return title;
  }

  /**
   * Removes this playlist from all the objects it
   * is listening to.  Make sure you call this
   * whenever a node is removed, otherwise you're
   * talking memory leak city!!
   */
  public synchronized void removeAllListeners() {
    myPlaylist.removePlaylistListener(this);

    if (myChildren != null) {
      int size = myChildren.size();
      for (int i = 0; i < size; i ++ ) {
        AbstractFIDPlaylistModel childPlaylistModel = (AbstractFIDPlaylistModel) myChildren.elementAt(i);
        childPlaylistModel.removeAllListeners();
      }
    }
  }

  /**
   * Children of this tree node are lazily loaded, so this
   * method is called at the top of every tree method to make
   * sure that the children have been filled in.
   */
  protected void ensureChildrenLoaded() {
    if (myChildren == null) {
      playlistStructureChanged();
      myPlaylist.addPlaylistListener(this);
    }
  }

  /**
   * @see org.jempeg.model.IContainer#getName()
   */
  public String getName() {
    return myPlaylist.getName();
  }

  /**
   * @see org.jempeg.empeg.model.IContainer#getSize()
   */
  public int getSize() {
    ensureChildrenLoaded();
    int size = myChildren.size();
    return size;
  }

  /**
   * @see org.jempeg.empeg.model.IContainer#getValueAt(int)
   */
  public Object getValueAt(int _index) {
    ensureChildrenLoaded();
    AbstractFIDPlaylistModel childPlaylistModel = (AbstractFIDPlaylistModel) myChildren.elementAt(_index);
    return childPlaylistModel;
  }

  protected Vector getChildrenVector() {
    return myChildren;
  }

  protected abstract void notifyStructureChanged();

  protected abstract void notifyChildrenWereInserted(int[] _indexes);

  protected abstract void notifyChildrenWereRemoved(int[] _indexes, Object[] _childModels);

  protected abstract AbstractFIDPlaylistModel createChildModel(FIDPlaylist _playlist, int _index);
}