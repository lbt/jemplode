/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
 */
package org.jempeg.nodestore;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.event.INodeTagListener;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.PropertiesTagExtractorListener;
import org.jempeg.tags.TagExtractorFactory;
import org.jempeg.tags.UnknownFileFormatException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.LongLinkedList;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.StringUtils;
import com.inzyme.util.Debug;

/**
 * All nodes in the Empeg database extend
 * AbstractFIDNode (playlists, MP3 files, etc.)
 *
 * @author Mike Schrag
 * @version $Revision: 1.11 $
 */
public abstract class AbstractFIDNode implements IFIDNode, INodeTagListener, Serializable {
  private transient PlayerDatabase myDB;

  private int myType;
  private long myFID;
  private NodeTags myTags;

  private boolean myDeleted;
  private boolean myDirty;
  private boolean myColored;
  private boolean myIdentified;

  private LongLinkedList myParentPlaylistFIDs;
  private int myCachedSouplessReferenceCount = -1;

  /**
   * Creates a new AbstractFIDNode.
   * 
   * @param _db the PlayerDatabase to create this node in
   * @param _fid the FID of the node
   */
  public AbstractFIDNode(PlayerDatabase _db, long _fid) {
    this(_db, _fid, new NodeTags());
  }

  /**
   * Creates a new AbstractFIDNode.
   * 
   * @param _db the PlayerDatabase to create this node in
   * @param _fid the FID of the node
   * @param _tags the tags for this node
   * @param _cloneTags whether or not _tags should be cloned
   */
  public AbstractFIDNode(PlayerDatabase _db, long _fid, NodeTags _tags) {
    myDB = _db;
    myFID = _fid;
    myTags = _tags;
  }

  /**
   * Initializes the FID generation to the current time.
   */
  void initializeGeneration() {
    getTags().setLongValue(DatabaseTags.GENERATION_TAG, System.currentTimeMillis() / 1000);
  }

  void addToDatabase(boolean _generateDatabaseChange) {
    getDB().addFID(getFID(), this, _generateDatabaseChange);
    getDB().getNodeMap().registerRid(this);
  }

  /**
   * This secretly copies attributes from another node -- this is
   * to be used during runtime replacement of a node.
   */
  void copyAttributes(AbstractFIDNode _node) {
    myType = _node.myType;
    myParentPlaylistFIDs = _node.myParentPlaylistFIDs;
    myDirty = _node.myDirty;
    myColored = _node.myColored;
    myDeleted = _node.myDeleted;
  }

  public void nodeIdentified(IFIDNode _node) {
  }

  public void beforeNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
    if (myIdentified) {
      myDB.fireBeforeNodeTagsModified(this, _tag, _oldValue, _newValue);
    }
  }

  public void afterNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
    if (myIdentified) {
      if (_tag != DatabaseTags.DIRTY_TAG && !isTransient()) {
        getDB().getSynchronizeQueue().enqueue(new FileInfoDatabaseChange(this, _tag, _oldValue, _newValue));
      }
      myDB.fireAfterNodeTagsModified(this, _tag, _oldValue, _newValue);

      if (myParentPlaylistFIDs != null && myParentPlaylistFIDs.size() > 0 && FIDPlaylist.isAggregateTag(_tag)) {
        Iterator parentPlaylistFIDIter = myParentPlaylistFIDs.iterator();
        for (int i = 0; parentPlaylistFIDIter.hasNext(); i ++ ) {
          LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
          FIDPlaylist parentPlaylist = myDB.getPlaylist(listItem.getValue());
          // System.out.println("AbstractFIDNode.afterNodeTagModified: " + this + " is refreshing " + parentPlaylist + " because of " + _tag);
          NodeTag sortTag = parentPlaylist.getSortTag();
          boolean resort = (sortTag != null && sortTag.getName().equals(_tag));
          parentPlaylist.refreshAggregateTags(this, resort);
        }
      }
    }
  }

  /**
   * Sets the type of this IFIDNode (one of the
   * IFIDNode.TYPE_xxx constants).
   * 
   * @param _type the type of this node
   */
  public synchronized void setType(int _type) {
    int oldType = myType;
    if (oldType != _type) {
      String oldTypeStr = String.valueOf(oldType);
      String newTypeStr = String.valueOf(_type);
      myDB.fireBeforeNodeTagsModified(this, DatabaseTags.ICON_TYPE_TAG, oldTypeStr, newTypeStr);
      myType = _type;
      myDB.fireAfterNodeTagsModified(this, DatabaseTags.ICON_TYPE_TAG, oldTypeStr, newTypeStr);
    }
  }

  public int getType() {
    return myType;
  }

  public long getFID() {
    return myFID;
  }

  /**
   * @see org.jempeg.empeg.model.IFIDNode#getAttributes(boolean)
   */
  public int getAttributes(boolean _recursive) {
    int attributes = 0;
    if (isDirty()) {
      attributes |= IFIDNode.ATTRIBUTE_DIRTY;
    }
    if (isColored()) {
      attributes |= IFIDNode.ATTRIBUTE_COLORED;
    }
    if (getTags().getBooleanValue(DatabaseTags.MARKED_TAG)) {
      attributes |= IFIDNode.ATTRIBUTE_MARKED;
    }
    if (isTransient()) {
      attributes |= IFIDNode.ATTRIBUTE_TRANSIENT;
    }
    return attributes;
  }

  public boolean isIdentified() {
    return myIdentified;
  }

  public void setIdentified(boolean _identified) {
    myIdentified = _identified;
    if (myIdentified && isAddedToDatabase()) {
      myDB.fireNodeIdentified(this);
    }
    if (_identified) {
      myTags.setNodeTagListener(this);
    }
  }

  public boolean isMarkedForDeletion() {
    return myDeleted;
  }

  public boolean isDirty() {
    return !isTransient() && (myDirty || myTags.isDirty());
  }

  public void setDirty(boolean _dirty) {
    myDirty = _dirty;
    myTags.setDirty(_dirty);
  }

  public boolean isColored() {
    return myColored;
  }

  public synchronized void setColored(boolean _colored) {
    boolean oldColored = myColored;
    if (oldColored != _colored) {
      String oldColoredStr = String.valueOf(oldColored);
      String newColoredStr = String.valueOf(_colored);
      myDB.fireBeforeNodeTagsModified(this, DatabaseTags.COLORED_TAG, oldColoredStr, newColoredStr);
      myColored = _colored;
      myDB.fireAfterNodeTagsModified(this, DatabaseTags.COLORED_TAG, oldColoredStr, newColoredStr);
    }
  }

  /**
   * @see org.jempeg.empeg.nodestore.IFIDNode#isTransient()
   */
  public boolean isTransient() {
    return false;
  }

  public PlayerDatabase getDB() {
    return myDB;
  }

  public NodeTags getTags() {
    return myTags;
  }

  protected void setTags(NodeTags _tags) {
    myTags = _tags;
  }

  public int getGeneration() {
    return myTags.getIntValue(DatabaseTags.GENERATION_TAG, 0);
  }

  public String getTitle() {
    return myTags.getValue(DatabaseTags.TITLE_TAG);
  }

  public int getLength() {
    return myTags.getIntValue(DatabaseTags.LENGTH_TAG, 0);
  }

  public boolean hasOption(int _optionNum) {
    int options = myTags.getHexValue(DatabaseTags.OPTIONS_TAG);
    boolean hasOption = ((options & _optionNum) != 0);
    return hasOption;
  }

  public void setOption(int _optionNum, boolean _value) {
    int options = myTags.getHexValue(DatabaseTags.OPTIONS_TAG);
    if (_value) {
      options |= _optionNum;
    }
    else {
      options &= ~_optionNum;
    }
    myTags.setHexValue(DatabaseTags.OPTIONS_TAG, options);
  }

  /**
   * Deletes this node from all playlists that reference it.
   */
  void deleteFromAllPlaylists() {
    if (getReferenceCount() > 0) {
      FIDNodeMap nodeMap = getDB().getNodeMap();
      Enumeration fidNodes = nodeMap.elements();
      while (fidNodes.hasMoreElements()) {
        IFIDNode fidNode = (IFIDNode) fidNodes.nextElement();
        // transient nodes can only be in other transient nodes, so don't even
        // both attempting to delete a transient from a non-transient
        if ((!isTransient() || fidNode.isTransient()) && fidNode instanceof FIDPlaylist) {
          FIDPlaylist playlist = (FIDPlaylist) fidNode;
          playlist.removeNode(this);
        }
      }
    }
  }

  public void delete() {
    synchronized (myDB.getSynchronizeQueue()) {
      if (!myDeleted) {
        deleteFromAllPlaylists();
        if (myDB.removeFID(myFID)) {
          myDB.freeSpace(this);
        }
        myDeleted = true;
      }
    }
  }

  /**
   * Hashcode is based on the FID of the node only.
   */
  public int hashCode() {
    return (int) (myFID ^ (myFID >>> 32));
  }

  /**
   * Two AbstractFIDNodes are equal if their FID's match.
   */
  public boolean equals(Object _obj) {
    boolean equals = (_obj instanceof IFIDNode);
    if (equals) {
      IFIDNode otherNode = (IFIDNode) _obj;
      equals = (myFID == otherNode.getFID());
    }
    return equals;
  }

  public String toVerboseString(int _depth) {
    StringBuffer sb = new StringBuffer();
    StringUtils.repeat(sb, "  ", _depth);
    sb.append(toString());
    return sb.toString();
  }

  public String toString() {
    String name = getClass().getName();
    int lastDotIndex = name.lastIndexOf('.');
    return "[" + name.substring(lastDotIndex + 1) + ": title = " + getTitle() + "; fid = " + myFID + "]";
  }

  /**
   * Sets the PlayerDatabase for this node.
   * 
   * @param _db the PlayerDatabase
   */
  void setDB(PlayerDatabase _db) {
    myDB = _db;
  }

  protected void identifyFile(IImportFile _importFile, boolean _alreadyAddedToDatabase, boolean _copyExistingTags) throws UnknownFileFormatException, FileNotFoundException, EOFException, IOException {
    try {
      PropertiesTagExtractorListener tagListener = new PropertiesTagExtractorListener();
      try {
        boolean allowUnknownTypes = PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.ALLOW_UNKNOWN_TYPES_PROPERTY);
        ITagExtractor tagExtractor = TagExtractorFactory.createTagExtractor(_importFile, allowUnknownTypes);
        tagExtractor.extractTags(_importFile, tagListener);
      }
      catch (UnknownFileFormatException e) {
        throw e;
      }
      catch (FileNotFoundException e) {
        throw e;
      }
      catch (EOFException e) {
        throw e;
      }
      catch (IOException e) {
        throw e;
        //Debug.println(e);
      }

      // We don't like things that have an empty title.
      String title = tagListener.getTagValue(DatabaseTags.TITLE_TAG);
      if (title == null || title.length() == 0) {
        String filename = _importFile.getName();
        if (filename == null || filename.length() == 0) {
          filename = "Unknown File " + System.currentTimeMillis();
        }
        tagListener.tagExtracted(DatabaseTags.TITLE_TAG, filename);
      }

      if (_alreadyAddedToDatabase) {
        myDB.getNodeMap().unregisterRid(this);
      }

      myTags.setValue(DatabaseTags.TYPE_TAG, tagListener.getTagValue(DatabaseTags.TYPE_TAG));
      myTags.setValue(DatabaseTags.TITLE_TAG, tagListener.getTagValue(DatabaseTags.TITLE_TAG));
      myTags.setValue(DatabaseTags.ARTIST_TAG, tagListener.getTagValue(DatabaseTags.ARTIST_TAG));
      myTags.setValue(DatabaseTags.COMMENT_TAG, tagListener.getTagValue(DatabaseTags.COMMENT_TAG));
      myTags.setValue(DatabaseTags.GENRE_TAG, tagListener.getTagValue(DatabaseTags.GENRE_TAG));
      myTags.setValue(DatabaseTags.SOURCE_TAG, tagListener.getTagValue(DatabaseTags.SOURCE_TAG));
      myTags.setValue(DatabaseTags.YEAR_TAG, tagListener.getTagValue(DatabaseTags.YEAR_TAG));
      myTags.setValue(DatabaseTags.CODEC_TAG, tagListener.getTagValue(DatabaseTags.CODEC_TAG));
      myTags.setValue(DatabaseTags.BITRATE_TAG, tagListener.getTagValue(DatabaseTags.BITRATE_TAG));
      myTags.setValue(DatabaseTags.OFFSET_TAG, tagListener.getTagValue(DatabaseTags.OFFSET_TAG));
      myTags.setValue(DatabaseTags.SAMPLERATE_TAG, tagListener.getTagValue(DatabaseTags.SAMPLERATE_TAG));
      myTags.setValue(DatabaseTags.DURATION_TAG, tagListener.getTagValue(DatabaseTags.DURATION_TAG));
      myTags.setValue(DatabaseTags.TRACKNR_TAG, tagListener.getTagValue(DatabaseTags.TRACKNR_TAG));
      myTags.setLongValue(DatabaseTags.CTIME_TAG, Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime().getTime() / 1000);
      myTags.setValue(DatabaseTags.RID_TAG, tagListener.getTagValue(DatabaseTags.RID_TAG));

      if (_copyExistingTags) {
        copyTagsIfNotEmpty(_importFile);
      }
    }
    finally {
      setIdentified(true);
      if (_alreadyAddedToDatabase) {
        myDB.getNodeMap().registerRid(this);
      }
    }
  }

  public void copyTagsIfNotEmpty(IImportFile _importFile) throws IOException {
    Properties importFileTags = _importFile.getTags();
    if (importFileTags != null && !importFileTags.isEmpty()) {
      Debug.println(Debug.INFORMATIVE, "AbstractFIDNode.copyTagsIfNotEmpty: Copying tags from " + _importFile + " to " + this.getTitle());
      copyTagIfNotEmpty(importFileTags, DatabaseTags.TITLE_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.ARTIST_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.COMMENT_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.GENRE_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.SOURCE_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.YEAR_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.TRACKNR_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.CTIME_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.SKIP_COUNT_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.PLAY_COUNT_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.PIN_TAG);
      copyTagIfNotEmpty(importFileTags, DatabaseTags.WENDY_TAG);
    }
  }

  private void copyTagIfNotEmpty(Properties _importFileTags, String _tagName) {
    String tagValue = _importFileTags.getProperty(_tagName);
    if (tagValue != null && tagValue.length() > 0) {
      myTags.setValue(_tagName, tagValue);
    }
  }

  protected abstract boolean isAddedToDatabase();

  public synchronized FIDPlaylist[] getParentPlaylists() {
    FIDPlaylist[] parentPlaylists;
    if (myParentPlaylistFIDs == null) {
      parentPlaylists = new FIDPlaylist[0];
    }
    else {
      parentPlaylists = new FIDPlaylist[myParentPlaylistFIDs.size()];
      Iterator parentPlaylistFIDIter = myParentPlaylistFIDs.iterator();
      for (int i = 0; parentPlaylistFIDIter.hasNext(); i ++ ) {
        LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
        parentPlaylists[i] = myDB.getPlaylist(listItem.getValue());
      }
    }
    return parentPlaylists;
  }

  public synchronized void nodeAddedToPlaylist(FIDPlaylist _playlist) {
    boolean isSoup = _playlist.isSoup();
    String oldValue = null;
    String newValue = null;
    if (!isSoup) {
      int souplessReferenceCount = getSouplessReferenceCount();
      oldValue = String.valueOf(souplessReferenceCount);
      newValue = String.valueOf(souplessReferenceCount + 1);
      myDB.fireBeforeNodeTagsModified(this, DatabaseTags.REFS_TAG, oldValue, newValue);
    }

    // System.out.println("AbstractFIDNode.nodeAddedToPlaylist: Adding " + this + " to " + _playlist);
    long playlistFID = _playlist.getFID();
    if (myParentPlaylistFIDs == null) {
      myParentPlaylistFIDs = new LongLinkedList();
    }
    myParentPlaylistFIDs.add(playlistFID);
    myCachedSouplessReferenceCount = -1;

    if (!isSoup) {
      myDB.fireAfterNodeTagsModified(this, DatabaseTags.REFS_TAG, oldValue, newValue);
    }
  }

  public synchronized void nodeRemovedFromPlaylist(FIDPlaylist _playlist) {
    boolean isSoup = _playlist.isSoup();
    String oldValue = null;
    String newValue = null;
    if (!isSoup) {
      int souplessReferenceCount = getSouplessReferenceCount();
      oldValue = String.valueOf(souplessReferenceCount);
      newValue = String.valueOf(souplessReferenceCount - 1);
      myDB.fireBeforeNodeTagsModified(this, DatabaseTags.REFS_TAG, oldValue, newValue);
    }

    // System.out.println("AbstractFIDNode.nodeRemovedFromPlaylist: removing " + this + " from " + _playlist);
    boolean removed;
    if (myParentPlaylistFIDs == null) {
      removed = false;
    }
    else {
      removed = myParentPlaylistFIDs.remove(_playlist.getFID());
    }
    if (!removed) {
      System.out.println("AbstractFIDNode.nodeRemovedFromPlaylist: YOU ASKED TO REMOVE " + this + " FROM " + _playlist + ", BUT IT WASN'T IN THERE!");
    }
    myCachedSouplessReferenceCount = -1;

    if (!isSoup) {
      myDB.fireAfterNodeTagsModified(this, DatabaseTags.REFS_TAG, oldValue, newValue);
    }
  }

  public synchronized void clearParentPlaylists() {
    if (myParentPlaylistFIDs != null) {
      myParentPlaylistFIDs.clear();
    }
    myCachedSouplessReferenceCount = -1;
  }

  public synchronized int getReferenceCount() {
    int referenceCount = 0;

    if (myFID == FIDConstants.FID_ROOTPLAYLIST) {
      referenceCount ++;
    }

    if (myParentPlaylistFIDs != null) {
      boolean isTransient = isTransient();
      Iterator parentPlaylistFIDIter = myParentPlaylistFIDs.iterator();
      while (parentPlaylistFIDIter.hasNext()) {
        LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
        boolean shouldRefCount = isTransient;
        if (!shouldRefCount) {
          FIDPlaylist playlist = (FIDPlaylist) myDB.getPlaylist(listItem.getValue());
          shouldRefCount = playlist != null && !playlist.isTransient();
        }
        if (shouldRefCount) {
          referenceCount ++;
        }
      }
    }

    return referenceCount;
  }

  /**
   * If a node is in a soup, you don't really want that to count as a 
   * reference count.  Soupless reference count returns the real end-user
   * perceived reference count.
   * 
   * @return the number of references that aren't in a soup
   */
  public synchronized int getSouplessReferenceCount() {
    if (myCachedSouplessReferenceCount == -1) {
      int souplessReferenceCount = 0;

      if (myParentPlaylistFIDs != null) {
        Iterator parentPlaylistFIDIter = myParentPlaylistFIDs.iterator();
        while (parentPlaylistFIDIter.hasNext()) {
          LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
          FIDPlaylist playlist = (FIDPlaylist) myDB.getPlaylist(listItem.getValue());
          if (playlist != null && !playlist.isSoup()) {
            souplessReferenceCount ++;
          }
        }
      }

      myCachedSouplessReferenceCount = souplessReferenceCount;
    }
    return myCachedSouplessReferenceCount;
  }

  public Set getAncestors() {
    Set ancestorsSet = new HashSet();
    fillInAncestors(ancestorsSet);
    return ancestorsSet;
  }

  protected void fillInAncestors(Set _ancestorsSet) {
    if (_ancestorsSet.add(this)) {
      FIDPlaylist[] parentPlaylists = getParentPlaylists();
      for (int i = 0; i < parentPlaylists.length; i ++ ) {
        parentPlaylists[i].fillInAncestors(_ancestorsSet);
      }
    }
  }
}