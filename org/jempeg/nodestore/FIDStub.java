package org.jempeg.nodestore;

import javax.swing.tree.TreePath;

import com.inzyme.model.Reason;
import com.inzyme.text.StringUtils;

/**
 * FIDStub is a stub that gets put in the NodeStore
 * when a free FID is requested (to make sure that 
 * nobody else gets the FID before the actual node
 * is created)
 * 
 * @author Mike Schrag
 */
class FIDStub implements IFIDNode {
  private FIDNodeMap myNodeMap;
  private long myFID;

  /**
   * Constructor for FIDStub.
   */
  public FIDStub(FIDNodeMap _nodeMap, long _fid) {
    myNodeMap = _nodeMap;
    myFID = _fid;
  }

  /**
   * @see org.jempeg.empeg.nodestore.IFIDNode#getType()
   */
  public int getType() {
    return 0;
  }

  public long getFID() {
    return myFID;
  }

  public int getGeneration() {
    return 0;
  }

  public int getAttributes(boolean _recursive) {
    return 0;
  }

  public boolean isColored() {
    return false;
  }

  public boolean isTransient() {
    return true;
  }

  public void setColored(boolean _colored) {
  }

  public boolean isIdentified() {
    return false;
  }

  public void setIdentified(boolean _identified) {
  }

  public boolean isDirty() {
    return false;
  }

  public void setDirty(boolean _dirty) {
  }

  public boolean isMarkedForDeletion() {
    return false;
  }

  public PlayerDatabase getDB() {
    throw new IllegalStateException("Someone asked for the database for an FIDStub.");
  }

  public NodeTags getTags() {
    return new NodeTags();
  }

  public String getTitle() {
    return null;
  }

  public int getLength() {
    return 0;
  }

  public boolean hasOption(int _optionNum) {
    return false;
  }

  public void setOption(int _optionNum, boolean _value) {
  }

  public Reason[] checkForProblems(boolean _repair, TreePath _path) {
    return Reason.NO_REASONS;
  }

  public void deleteFromAllPlaylists() {
  }

  public void delete() {
    myNodeMap.removeNode(myFID);
  }

  public String toVerboseString(int _depth) {
    StringBuffer sb = new StringBuffer();
    StringUtils.repeat(sb, "  ", _depth);
    sb.append(toString());
    return sb.toString();
  }

  public int getReferenceCount() {
    return 0;
  }

  public int getSouplessReferenceCount() {
    return 0;
  }

  public void clearParentPlaylists() {
  }

  public FIDPlaylist[] getParentPlaylists() {
    return new FIDPlaylist[0];
  }

  public void nodeAddedToPlaylist(FIDPlaylist _playlist) {
  }

  public void nodeRemovedFromPlaylist(FIDPlaylist _playlist) {
  }

  public String toString() {
    return "[FIDStub: fid = " + myFID + "]";
  }
}