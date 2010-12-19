package org.jempeg.nodestore.event;

import java.util.EventListener;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;

/**
 * IDatabaseListener is an interface that is implemented by 
 * components that want to receive notifications about
 * when nodes are added, removed, or modified.
 * 
 * @author Mike Schrag
 */
public interface IDatabaseListener extends EventListener {
  /**
   * Fired when the free or total space of a database changes.
   * 
   * @param _totalSpace the total space in the database
   * @param _freeSpace the free space in the database
   */
  public void freeSpaceChanged(PlayerDatabase _playerDatabase, long _totalSpace, long _freeSpace);

  /**
   * Fired when a node is added to the database.
   * 
   * @param _node the node that was added
   */
  public void nodeAdded(IFIDNode _node);

  /**
   * Fired when a node is removed from the database.
   * 
   * @param _node the node that was removed
   */
  public void nodeRemoved(IFIDNode _node);

  public void databaseCleared(PlayerDatabase _playerDatabase);
}