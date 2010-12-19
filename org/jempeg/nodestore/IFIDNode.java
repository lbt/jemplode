package org.jempeg.nodestore;

import javax.swing.tree.TreePath;

import com.inzyme.model.Reason;

/**
 * IFIDNode is the interface that is implemented by all nodes
 * on the device (playlists, tunes, local unsynced files, etc.)
 *
 * @author Mike Schrag
 * @version $Revision: 1.5 $
 */
public interface IFIDNode {
  public static final int TYPE_PLAYLIST = 2;
  public static final int TYPE_TUNE = 3;
  public static final int TYPE_TUNES = 4;
  public static final int TYPE_GENRE = 5;
  public static final int TYPE_GENRES = 6;
  public static final int TYPE_YEAR = 7;
  public static final int TYPE_YEARS = 8;
  public static final int TYPE_ALBUM = 9;
  public static final int TYPE_ALBUMS = 10;
  public static final int TYPE_ARTIST = 11;
  public static final int TYPE_ARTISTS = 12;
  public static final int TYPE_PLAYLISTS = 13;

  public static final int ATTRIBUTE_DIRTY = 1;
  public static final int ATTRIBUTE_MARKED = 2;
  public static final int ATTRIBUTE_COLORED = 4;
  public static final int ATTRIBUTE_TRANSIENT = 8;

  /**
   * Returns the type of this tree node.  This is
   * primarily used to determine the type of icon
   * that is displayed next to the node (one of the
   * IFIDNode.TYPE_xxx constants).
   * 
   * @return the type of this tree node
   */
  public int getType();

  /**
   * Returns the FID for this Node.
   * 
   * @return the FID for this Node
   */
  public long getFID();

  /**
   * Returns the Generation of this Node.  FID Generation is a tricky
   * concept, but essentially, if you find out that the generation number
   * of the node on the player is different than your own, then this one
   * is out of sync because of a remote change. 
   * 
   * @return the FID Generation for this Node
   */
  public int getGeneration();

  /**
   * Returns different attributes of this node (and its
   * children if the node is a playlist).  The attributes
   * are bitwise AND'd together.
   * 
   * @param _recursive should attributes be retrieved recursively
   * @return the attributes of this node (and its children)
   */
  public int getAttributes(boolean _recursive);

  /**
   * Returns whether or not this node has been "colored".
   * Coloring is a tag that can be applied and is similar
   * to the internal "marked" designation except that
   * coloring is not permanent.
   * 
   * @return whether or not this node has been "colored"
   */
  public boolean isColored();

  /**
   * Returns whether or not this is a transient
   * node.  A transient node is one that
   * will not ever be downloaded to the Empeg.
   * 
   * @return whether or not this playlist is transient
   */
  public boolean isTransient();

  /**
   * Sets the colored state of this node.
   * 
   * @param _colored the new colored state of this node
   */
  public void setColored(boolean _colored);

  /**
   * Returns whether or not this node has been parsed and its tags have been filled in.
   * 
   * @return whether or not this node has been identified
   */
  public boolean isIdentified();

  /**
   * Sets whether or not this node has been parsed and its tags have been filled in.
   * 
   * @param _dirty whether or not this node has been identified
   */
  public void setIdentified(boolean _identified);

  /**
   * Returns whether or not this node or its tags have been modified.
   * 
   * @return whether or not this node or its tags have been modified
   */
  public boolean isDirty();

  /**
   * Sets whether or not this node has been modified.
   * 
   * @param _dirty whether or not this node has been modified
   */
  public void setDirty(boolean _dirty);

  /**
   * Sets whether or not this node has been marked for deletion.
   * 
   * @return whether or not this node has been marked for deletion
   */
  public boolean isMarkedForDeletion();

  /**
   * Returns the Database that contains this node.
   * 
   * @return the Database that contains this node
   */
  public PlayerDatabase getDB();

  /**
   * Returns the tags for this node.
   * 
   * @return the tags for this node
   */
  public NodeTags getTags();

  /**
   * Returns the title of this node.
   * 
   * @return the title of this node
   */
  public String getTitle();

  /**
   * Returns the length of this node in bytes.
   * 
   * @return the length of this node in bytes
   */
  public int getLength();

  /**
   * Returns whether or not this node has the given option flag set.
   * 
   * @param _optionNum the option flag to check
   * @return whether or not this node has the given option flag set
   */
  public boolean hasOption(int _optionNum);

  /**
   * Sets the given option flag on this node.
   * 
   * @param _optionNum the option flag to set
   * @param _value whether the value is on or off
   */
  public void setOption(int _optionNum, boolean _value);

  /**
   * Checks for problems with this node and optionally repair them.
   * 
   * @param _repair should problems be automatically repaired?
   * @return a set of Reasons that explain the problems that were found (and fixed)
   */
  public Reason[] checkForProblems(boolean _repair, TreePath _path);

  /**
   * Delete this node and marks it for deletion in the 
   * next sync.
   */
  public void delete();

  /**
   * Returns .toString() with a lot more data in it.
   * 
   * @param _depth the depth to indent
   * @return a much bigger toString()
   */
  public String toVerboseString(int _depth);

  /**
   * Returns the number of references to this node.
   * 
   * @return the number of references to this node
   */
  public int getReferenceCount();

  public int getSouplessReferenceCount();

  public void nodeAddedToPlaylist(FIDPlaylist _playlist);

  public void nodeRemovedFromPlaylist(FIDPlaylist _playlist);

  public void clearParentPlaylists();

  public FIDPlaylist[] getParentPlaylists();
}