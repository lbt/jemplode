package org.jempeg.nodestore.soup;

import java.io.IOException;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.PlayerDatabase;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.util.Debug;

/**
 * SoupUtils are handy utility methods for managing 
 * soup containers.
 * 
 * @author Mike Schrag
 */
public class SoupUtils {
  public static final String SOUP_COUNT_PROPERTY = "jempeg.soup.num";

  /**
   * Creates and adds a new persistent (on-Empeg) soup playlist.  
   * 
   * @param _playerDatabase the PlayerDatabase to add into
   * @param _name the name of the soup playlist to add
   * @param _soupUpdater the soup updater to attach
   * @return the soup playlist
   */
  public static FIDPlaylist createPersistentSoupPlaylist(PlayerDatabase _playerDatabase, String _name, ISoupLayer[] _soupLayers, boolean _threaded) {
    FIDPlaylist soupPlaylist = SoupUtils.createSoupPlaylist(_playerDatabase, _name, false, _soupLayers, true);
    SoupUtils.attachSoup(soupPlaylist, _soupLayers, _threaded, null);
    FIDPlaylist rootPlaylist = _playerDatabase.getRootPlaylist();
    rootPlaylist.addNode(soupPlaylist, true, CollationKeyCache.createDefaultCache());
    return soupPlaylist;
  }

  /**
   * Creates and adds a new transient (in-jEmplode) soup playlist.
   *
   * @param _playerDatabase the PlayerDatabase to add into
   * @param _name the name of the soup playlist to add
   * @param _soupUpdater the soup updater to attach
   * @param _saveForLater only applicable for transient, but optionally save
   * this playlist in .jempegrc
   * @param _threaded should we attach in the background?
   */
  public static FIDPlaylist createTransientSoupPlaylist(PlayerDatabase _playerDatabase, String _name, ISoupLayer[] _soupLayers, boolean _saveForLater, boolean _temporary, boolean _threaded, ISoupListener _soupListener) {
    FIDPlaylist soupPlaylist = SoupUtils.createSoupPlaylist(_playerDatabase, _name, true, _soupLayers, !_temporary);

    if (_temporary) {
      soupPlaylist.getTags().setBooleanValue(DatabaseTags.JEMPLODE_TEMPORARY_TAG, true);
      soupPlaylist.setIdentified(true);
    }

    if (_saveForLater) {
      try {
        PropertiesManager propertiesManager = PropertiesManager.getInstance();
        int soupNum = propertiesManager.getIntProperty(SoupUtils.SOUP_COUNT_PROPERTY, 0);
        propertiesManager.setProperty("jempeg.soup." + soupNum + ".externalForm", soupPlaylist.getTags().getValue(DatabaseTags.SOUP_TAG));
        propertiesManager.setProperty("jempeg.soup." + soupNum + ".name", soupPlaylist.getTitle());
        propertiesManager.setIntProperty(SoupUtils.SOUP_COUNT_PROPERTY, soupNum + 1);
        propertiesManager.save();
      }
      catch (IOException e) {
        Debug.println(e);
      }
    }

    SoupUtils.attachSoup(soupPlaylist, _soupLayers, _threaded, _soupListener);

    return soupPlaylist;
  }

  /**
   * Removes the given transient soup playlist from .jempegrc.
   * 
   * @param _index the index of the soup playlist to remove
   */
  public static void removeTransientSoupPlaylist(int _index) {
    try {
      PropertiesManager propertiesManager = PropertiesManager.getInstance();
      int soupCount = propertiesManager.getIntProperty(SoupUtils.SOUP_COUNT_PROPERTY, 0);
      for (int i = 0; i < soupCount; i ++ ) {
        int oldIndex = i;
        if (oldIndex != _index) {
          int newIndex = (oldIndex > _index) ? (oldIndex - 1) : oldIndex;
          String oldExternalFormKey = "jempeg.soup." + oldIndex + ".externalForm";
          String newExternalFormKey = "jempeg.soup." + newIndex + ".externalForm";
          String oldNameKey = "jempeg.soup." + oldIndex + ".name";
          String newNameKey = "jempeg.soup." + newIndex + ".name";
          String externalForm = propertiesManager.getProperty(oldExternalFormKey, "");
          String name = propertiesManager.getProperty(oldNameKey, "");
          propertiesManager.setProperty(newExternalFormKey, externalForm);
          propertiesManager.setProperty(newNameKey, name);
        }
      }
      propertiesManager.setIntProperty(SoupUtils.SOUP_COUNT_PROPERTY, soupCount - 1);
      propertiesManager.save();
    }
    catch (IOException e) {
      Debug.println(e);
    }
  }

  /**
   * Loads the transient soup playlists from jempegrc and attaches then
   * to the tree.
   * 
   * @param _database the player database to use
   * @param _treeModel the tree model to attach to
   * @param _threaded should we attach in the background?
   */
  public static void loadTransientSoupPlaylists(PlayerDatabase _playerDatabase, boolean _threaded) {
    PropertiesManager propertiesManager = PropertiesManager.getInstance();
    int soupNum = propertiesManager.getIntProperty(SoupUtils.SOUP_COUNT_PROPERTY, -1);
    if (soupNum == -1) {
      try {
        SoupUtils.createTransientSoupPlaylist(_playerDatabase, "Artists", SoupLayerFactory.fromExternalForm("tag:artist"), true, false, true, null);
        SoupUtils.createTransientSoupPlaylist(_playerDatabase, "Albums", SoupLayerFactory.fromExternalForm("tag:source"), true, false, true, null);
        SoupUtils.createTransientSoupPlaylist(_playerDatabase, "Genres", SoupLayerFactory.fromExternalForm("tag:genre"), true, false, true, null);
        SoupUtils.createTransientSoupPlaylist(_playerDatabase, "Years", SoupLayerFactory.fromExternalForm("tag:year"), true, false, true, null);
        SoupUtils.createTransientSoupPlaylist(_playerDatabase, "All", SoupLayerFactory.fromExternalForm("search:type=tune"), true, false, true, null);
      }
      catch (Throwable t) {
        Debug.println(t);
      }
    }
    else {
      for (int i = 0; i < soupNum; i ++ ) {
        try {
          String query = propertiesManager.getProperty("jempeg.soup." + i + ".externalForm", null);
          if (query != null) {
            String name = propertiesManager.getProperty("jempeg.soup." + i + ".name", query);
            SoupUtils.createTransientSoupPlaylist(_playerDatabase, name, SoupLayerFactory.fromExternalForm(query), false, false, _threaded, null);
          }
        }
        catch (Throwable t) {
          Debug.println(t);
        }
      }
    }
  }

  /**
   * Creates and new soup playlist.
   * 
   * @param _playerDatabase the PlayerDatabase to add into
   * @param _name the name of the soup playlist to add
   * @param _transient is it transient or not
   * @param _soupExternalForm the external form of the soup updater
   */
  private static FIDPlaylist createSoupPlaylist(PlayerDatabase _playerDatabase, String _name, boolean _transient, ISoupLayer[] _soupLayers, boolean _identifyImmediately) {
    String soupExternalForm = SoupLayerFactory.toExternalForm(_soupLayers);
    FIDPlaylist soupPlaylist = new FIDPlaylist(_playerDatabase, _transient);
    if (_name == null || _name.trim().length() == 0) {
      _name = soupExternalForm;
    }
    soupPlaylist.getTags().setValue(DatabaseTags.TITLE_TAG, _name);
    soupPlaylist.getTags().setValue(DatabaseTags.SOUP_TAG, soupExternalForm);
    soupPlaylist.setSoup(true);
    soupPlaylist.setIdentified(_identifyImmediately);
    Debug.println(Debug.INFORMATIVE, "SoupUtils.createSoupPlaylist: Created soup playlist " + soupPlaylist + " in " + _playerDatabase);
    return soupPlaylist;
  }

  /**
   * Attach a soup updater and a container to a database so that 
   * changes to the database notify the soup to update.
   * 
   * @param _soupPlaylist the playlist to attach to
   * @param _soupUpdater the soup updater to attach
   * @param _playerDatabase the database to listen to
   * @param _threaded should we attach in the background?
   */
  public static void attachSoup(FIDPlaylist _soupPlaylist, ISoupLayer[] _soupLayers, boolean _threaded, ISoupListener _soupListener) {
    if (!PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.DISABLE_SOUP_UPDATING_PROPERTY)) {
      SoupUpdater soupUpdater = new SoupUpdater(_soupPlaylist, _soupLayers);
      SoupInitializeRunnable runnable = new SoupInitializeRunnable(soupUpdater, _soupPlaylist, _soupListener);
      if (_threaded) {
        Thread t = new Thread(runnable, "jEmplode: SoupUpdater for " + SoupLayerFactory.toExternalForm(_soupLayers));
        t.start();
      }
      else {
        runnable.run();
        if (_soupListener != null) {
          _soupListener.soupInitialized(soupUpdater, _soupPlaylist);
        }
      }
    }
    else {
      Debug.println(Debug.WARNING, "Soup updating is disabled for " + SoupLayerFactory.toExternalForm(_soupLayers));
    }
  }

  /**
   * SoupInitializeRunnable initializes a soup container
   * in the background since initialization can be a pretty
   * heavy process.
   */
  protected static class SoupInitializeRunnable implements Runnable {
    private SoupUpdater mySoupUpdater;
    private FIDPlaylist mySoupPlaylist;
    private ISoupListener mySoupListener;

    public SoupInitializeRunnable(SoupUpdater _soupUpdater, FIDPlaylist _soupPlaylist, ISoupListener _soupListener) {
      mySoupUpdater = _soupUpdater;
      mySoupPlaylist = _soupPlaylist;
      mySoupListener = _soupListener;
    }

    public void run() {
      long startTime = System.currentTimeMillis();
      mySoupUpdater.initialize();
      if (mySoupListener != null) {
        mySoupListener.soupInitialized(mySoupUpdater, mySoupPlaylist);
      }
      Debug.println(Debug.INFORMATIVE, "SoupUtils.SoupInitializeRunnable.run: Finished " + SoupLayerFactory.toExternalForm(mySoupUpdater.getSoupLayers()) + " in " + (System.currentTimeMillis() - startTime) + "ms");
    }
  }
}