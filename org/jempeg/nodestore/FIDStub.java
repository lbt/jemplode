/* FIDStub - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import javax.swing.tree.TreePath;

import com.inzyme.model.Reason;
import com.inzyme.text.StringUtils;

class FIDStub implements IFIDNode
{
    private FIDNodeMap myNodeMap;
    private long myFID;
    
    public FIDStub(FIDNodeMap _nodeMap, long _fid) {
	myNodeMap = _nodeMap;
	myFID = _fid;
    }
    
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
	/* empty */
    }
    
    public boolean isIdentified() {
	return false;
    }
    
    public void setIdentified(boolean _identified) {
	/* empty */
    }
    
    public boolean isDirty() {
	return false;
    }
    
    public void setDirty(boolean _dirty) {
	/* empty */
    }
    
    public boolean isMarkedForDeletion() {
	return false;
    }
    
    public PlayerDatabase getDB() {
	throw new IllegalStateException
		  ("Someone asked for the database for an FIDStub.");
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
	/* empty */
    }
    
    public Reason[] checkForProblems(boolean _repair, TreePath _path) {
	return Reason.NO_REASONS;
    }
    
    public void deleteFromAllPlaylists() {
	/* empty */
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
	/* empty */
    }
    
    public FIDPlaylist[] getParentPlaylists() {
	return new FIDPlaylist[0];
    }
    
    public void nodeAddedToPlaylist(FIDPlaylist _playlist) {
	/* empty */
    }
    
    public void nodeRemovedFromPlaylist(FIDPlaylist _playlist) {
	/* empty */
    }
    
    public String toString() {
	return "[FIDStub: fid = " + myFID + "]";
    }
}
