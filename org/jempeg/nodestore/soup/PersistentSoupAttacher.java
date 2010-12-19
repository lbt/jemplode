package org.jempeg.nodestore.soup;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.Vector;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

import com.inzyme.util.Debug;

/**
 * PersistentSoupAttacher is responsible for 
 * listening for database changes and hooking up all the 
 * on-Empeg soup nodes with their corresponding updaters.
 * 
 * @author Mike Schrag
 */
public class PersistentSoupAttacher implements ISynchronizeClientListener {
  private boolean myThreaded;

  /**
   * Constructor for PersistentSoupAttacher.
   * 
   * @param _threaded should the soup attachment be threaded?
   */
  public PersistentSoupAttacher(boolean _threaded) {
    super();
    myThreaded = _threaded;
  }

  public void downloadCompleted(PlayerDatabase _playerDatabase) {
    Vector soupPlaylistsVec = new Vector();

    FIDNodeMap nodeMap = _playerDatabase.getNodeMap();
    Enumeration nodesEnum = nodeMap.elements();
    while (nodesEnum.hasMoreElements()) {
      IFIDNode node = (IFIDNode) nodesEnum.nextElement();
      if (node instanceof FIDPlaylist) {
        FIDPlaylist playlist = (FIDPlaylist) node;
        if (!playlist.isTransient() && playlist.getTags().getValue(DatabaseTags.SOUP_TAG).length() > 0) {
          playlist.makeSoupy();
          soupPlaylistsVec.addElement(playlist);
        }
      }
    }

    Enumeration soupPlaylistsEnum = soupPlaylistsVec.elements();
    while (soupPlaylistsEnum.hasMoreElements()) {
      try {
        FIDPlaylist soupPlaylist = (FIDPlaylist) soupPlaylistsEnum.nextElement();
        String soupExternalForm = soupPlaylist.getTags().getValue(DatabaseTags.SOUP_TAG);
        ISoupLayer[] soupLayers = SoupLayerFactory.fromExternalForm(soupExternalForm);
        SoupUtils.attachSoup(soupPlaylist, soupLayers, myThreaded, null);
      }
      catch (ParseException e) {
        Debug.println(e);
      }
    }
  }

  public void downloadStarted(PlayerDatabase _playerDatabase) {
  }

  public void synchronizeCompleted(IDatabaseChange _databaseChange, boolean _successfully) {
  }

  public void synchronizeCompleted(PlayerDatabase _playerDatabase, boolean _succesfully) {
  }

  public void synchronizeInProgress(IDatabaseChange _databaseChange, long _current, long _total) {
  }

  public void synchronizeStarted(IDatabaseChange _databaseChange) {
  }

  public void synchronizeStarted(PlayerDatabase _playerDatabase) {
  }
}