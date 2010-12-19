/* AbstractFIDNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.LongLinkedList;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.StringUtils;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.event.INodeTagListener;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.PropertiesTagExtractorListener;
import org.jempeg.tags.TagExtractorFactory;
import org.jempeg.tags.UnknownFileFormatException;

public abstract class AbstractFIDNode
    implements IFIDNode, INodeTagListener, Serializable
{
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
    
    public AbstractFIDNode(PlayerDatabase _db, long _fid) {
	this(_db, _fid, new NodeTags());
    }
    
    public AbstractFIDNode(PlayerDatabase _db, long _fid, NodeTags _tags) {
	myDB = _db;
	myFID = _fid;
	myTags = _tags;
    }
    
    void initializeGeneration() {
	getTags().setLongValue("fid_generation",
			       System.currentTimeMillis() / 1000L);
    }
    
    void addToDatabase(boolean _generateDatabaseChange) {
	getDB().addFID(getFID(), this, _generateDatabaseChange);
	getDB().getNodeMap().registerRid(this);
    }
    
    void copyAttributes(AbstractFIDNode _node) {
	myType = _node.myType;
	myParentPlaylistFIDs = _node.myParentPlaylistFIDs;
	myDirty = _node.myDirty;
	myColored = _node.myColored;
	myDeleted = _node.myDeleted;
    }
    
    public void nodeIdentified(IFIDNode _node) {
	/* empty */
    }
    
    public void beforeNodeTagModified(IFIDNode _node, String _tag,
				      String _oldValue, String _newValue) {
	if (myIdentified)
	    myDB.fireBeforeNodeTagsModified(this, _tag, _oldValue, _newValue);
    }
    
    public void afterNodeTagModified(IFIDNode _node, String _tag,
				     String _oldValue, String _newValue) {
	if (myIdentified) {
	    if (_tag != "dirty" && !isTransient())
		getDB().getSynchronizeQueue().enqueue
		    (new FileInfoDatabaseChange(this, _tag, _oldValue,
						_newValue));
	    myDB.fireAfterNodeTagsModified(this, _tag, _oldValue, _newValue);
	    if (myParentPlaylistFIDs != null && myParentPlaylistFIDs.size() > 0
		&& FIDPlaylist.isAggregateTag(_tag)) {
		Iterator parentPlaylistFIDIter
		    = myParentPlaylistFIDs.iterator();
		int i = 0;
		while (parentPlaylistFIDIter.hasNext()) {
		    LongLinkedList.ListItem listItem
			= ((LongLinkedList.ListItem)
			   parentPlaylistFIDIter.next());
		    FIDPlaylist parentPlaylist
			= myDB.getPlaylist(listItem.getValue());
		    NodeTag sortTag = parentPlaylist.getSortTag();
		    boolean resort
			= sortTag != null && sortTag.getName().equals(_tag);
		    parentPlaylist.refreshAggregateTags(this, resort);
		    i++;
		}
	    }
	}
    }
    
    public synchronized void setType(int _type) {
	int oldType = myType;
	if (oldType != _type) {
	    String oldTypeStr = String.valueOf(oldType);
	    String newTypeStr = String.valueOf(_type);
	    myDB.fireBeforeNodeTagsModified(this, "iconType", oldTypeStr,
					    newTypeStr);
	    myType = _type;
	    myDB.fireAfterNodeTagsModified(this, "iconType", oldTypeStr,
					   newTypeStr);
	}
    }
    
    public int getType() {
	return myType;
    }
    
    public long getFID() {
	return myFID;
    }
    
    public int getAttributes(boolean _recursive) {
	int attributes = 0;
	if (isDirty())
	    attributes |= 0x1;
	if (isColored())
	    attributes |= 0x4;
	if (getTags().getBooleanValue("marked"))
	    attributes |= 0x2;
	if (isTransient())
	    attributes |= 0x8;
	return attributes;
    }
    
    public boolean isIdentified() {
	return myIdentified;
    }
    
    public void setIdentified(boolean _identified) {
	myIdentified = _identified;
	if (myIdentified && isAddedToDatabase())
	    myDB.fireNodeIdentified(this);
	if (_identified)
	    myTags.setNodeTagListener(this);
    }
    
    public boolean isMarkedForDeletion() {
	return myDeleted;
    }
    
    public boolean isDirty() {
	if (!isTransient() && (myDirty || myTags.isDirty()))
	    return true;
	return false;
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
	    myDB.fireBeforeNodeTagsModified(this, "colored", oldColoredStr,
					    newColoredStr);
	    myColored = _colored;
	    myDB.fireAfterNodeTagsModified(this, "colored", oldColoredStr,
					   newColoredStr);
	}
    }
    
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
	return myTags.getIntValue("fid_generation", 0);
    }
    
    public String getTitle() {
	return myTags.getValue("title");
    }
    
    public int getLength() {
	return myTags.getIntValue("length", 0);
    }
    
    public boolean hasOption(int _optionNum) {
	int options = myTags.getHexValue("options");
	boolean hasOption = (options & _optionNum) != 0;
	return hasOption;
    }
    
    public void setOption(int _optionNum, boolean _value) {
	int options = myTags.getHexValue("options");
	if (_value)
	    options |= _optionNum;
	else
	    options &= _optionNum ^ 0xffffffff;
	myTags.setHexValue("options", options);
    }
    
    void deleteFromAllPlaylists() {
	if (getReferenceCount() > 0) {
	    FIDNodeMap nodeMap = getDB().getNodeMap();
	    Enumeration fidNodes = nodeMap.elements();
	    while (fidNodes.hasMoreElements()) {
		IFIDNode fidNode = (IFIDNode) fidNodes.nextElement();
		if ((!isTransient() || fidNode.isTransient())
		    && fidNode instanceof FIDPlaylist) {
		    FIDPlaylist playlist = (FIDPlaylist) fidNode;
		    playlist.removeNode(this);
		}
	    }
	}
    }
    
    public void delete() {
	SynchronizeQueue synchronizequeue;
	MONITORENTER (synchronizequeue = myDB.getSynchronizeQueue());
	MISSING MONITORENTER
	synchronized (synchronizequeue) {
	    if (!myDeleted) {
		deleteFromAllPlaylists();
		if (myDB.removeFID(myFID))
		    myDB.freeSpace(this);
		myDeleted = true;
	    }
	}
    }
    
    public int hashCode() {
	return (int) (myFID ^ myFID >>> 32);
    }
    
    public boolean equals(Object _obj) {
	boolean equals = _obj instanceof IFIDNode;
	if (equals) {
	    IFIDNode otherNode = (IFIDNode) _obj;
	    equals = myFID == otherNode.getFID();
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
	String name = this.getClass().getName();
	int lastDotIndex = name.lastIndexOf('.');
	return ("[" + name.substring(lastDotIndex + 1) + ": title = "
		+ getTitle() + "; fid = " + myFID + "]");
    }
    
    void setDB(PlayerDatabase _db) {
	myDB = _db;
    }
    
    protected void identifyFile
	(IImportFile _importFile, boolean _alreadyAddedToDatabase,
	 boolean _copyExistingTags)
	throws UnknownFileFormatException, FileNotFoundException, EOFException,
	       IOException {
	try {
	    PropertiesTagExtractorListener tagListener
		= new PropertiesTagExtractorListener();
	    try {
		boolean allowUnknownTypes
		    = PropertiesManager.getInstance()
			  .getBooleanProperty("jempeg.allowUnknownTypes");
		ITagExtractor tagExtractor
		    = TagExtractorFactory
			  .createTagExtractor(_importFile, allowUnknownTypes);
		tagExtractor.extractTags(_importFile, tagListener);
	    } catch (UnknownFileFormatException e) {
		throw e;
	    } catch (FileNotFoundException e) {
		throw e;
	    } catch (EOFException e) {
		throw e;
	    } catch (IOException e) {
		throw e;
	    }
	    String title = tagListener.getTagValue("title");
	    if (title == null || title.length() == 0) {
		String filename = _importFile.getName();
		if (filename == null || filename.length() == 0)
		    filename = "Unknown File " + System.currentTimeMillis();
		tagListener.tagExtracted("title", filename);
	    }
	    if (_alreadyAddedToDatabase)
		myDB.getNodeMap().unregisterRid(this);
	    myTags.setValue("type", tagListener.getTagValue("type"));
	    myTags.setValue("title", tagListener.getTagValue("title"));
	    myTags.setValue("artist", tagListener.getTagValue("artist"));
	    myTags.setValue("comment", tagListener.getTagValue("comment"));
	    myTags.setValue("genre", tagListener.getTagValue("genre"));
	    myTags.setValue("source", tagListener.getTagValue("source"));
	    myTags.setValue("year", tagListener.getTagValue("year"));
	    myTags.setValue("codec", tagListener.getTagValue("codec"));
	    myTags.setValue("bitrate", tagListener.getTagValue("bitrate"));
	    myTags.setValue("offset", tagListener.getTagValue("offset"));
	    myTags.setValue("samplerate",
			    tagListener.getTagValue("samplerate"));
	    myTags.setValue("duration", tagListener.getTagValue("duration"));
	    myTags.setValue("tracknr", tagListener.getTagValue("tracknr"));
	    myTags.setLongValue("ctime",
				Calendar.getInstance
				    (TimeZone.getTimeZone("GMT")).getTime
				    ().getTime() / 1000L);
	    myTags.setValue("rid", tagListener.getTagValue("rid"));
	    if (_copyExistingTags)
		copyTagsIfNotEmpty(_importFile);
	} catch (Object object) {
	    setIdentified(true);
	    if (_alreadyAddedToDatabase)
		myDB.getNodeMap().registerRid(this);
	    throw object;
	}
	setIdentified(true);
	if (_alreadyAddedToDatabase)
	    myDB.getNodeMap().registerRid(this);
    }
    
    public void copyTagsIfNotEmpty(IImportFile _importFile)
	throws IOException {
	Properties importFileTags = _importFile.getTags();
	if (importFileTags != null && !importFileTags.isEmpty()) {
	    Debug.println
		(4, ("AbstractFIDNode.copyTagsIfNotEmpty: Copying tags from "
		     + _importFile + " to " + getTitle()));
	    copyTagIfNotEmpty(importFileTags, "title");
	    copyTagIfNotEmpty(importFileTags, "artist");
	    copyTagIfNotEmpty(importFileTags, "comment");
	    copyTagIfNotEmpty(importFileTags, "genre");
	    copyTagIfNotEmpty(importFileTags, "source");
	    copyTagIfNotEmpty(importFileTags, "year");
	    copyTagIfNotEmpty(importFileTags, "tracknr");
	    copyTagIfNotEmpty(importFileTags, "ctime");
	    copyTagIfNotEmpty(importFileTags, "skip_count");
	    copyTagIfNotEmpty(importFileTags, "play_count");
	    copyTagIfNotEmpty(importFileTags, "pin");
	    copyTagIfNotEmpty(importFileTags, "wendy");
	}
    }
    
    private void copyTagIfNotEmpty(Properties _importFileTags,
				   String _tagName) {
	String tagValue = _importFileTags.getProperty(_tagName);
	if (tagValue != null && tagValue.length() > 0)
	    myTags.setValue(_tagName, tagValue);
    }
    
    protected abstract boolean isAddedToDatabase();
    
    public synchronized FIDPlaylist[] getParentPlaylists() {
	FIDPlaylist[] parentPlaylists;
	if (myParentPlaylistFIDs == null)
	    parentPlaylists = new FIDPlaylist[0];
	else {
	    parentPlaylists = new FIDPlaylist[myParentPlaylistFIDs.size()];
	    Iterator parentPlaylistFIDIter = myParentPlaylistFIDs.iterator();
	    int i = 0;
	    while (parentPlaylistFIDIter.hasNext()) {
		LongLinkedList.ListItem listItem
		    = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
		parentPlaylists[i] = myDB.getPlaylist(listItem.getValue());
		i++;
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
	    myDB.fireBeforeNodeTagsModified(this, "refs", oldValue, newValue);
	}
	long playlistFID = _playlist.getFID();
	if (myParentPlaylistFIDs == null)
	    myParentPlaylistFIDs = new LongLinkedList();
	myParentPlaylistFIDs.add(playlistFID);
	myCachedSouplessReferenceCount = -1;
	if (!isSoup)
	    myDB.fireAfterNodeTagsModified(this, "refs", oldValue, newValue);
    }
    
    public synchronized void nodeRemovedFromPlaylist(FIDPlaylist _playlist) {
	boolean isSoup = _playlist.isSoup();
	String oldValue = null;
	String newValue = null;
	if (!isSoup) {
	    int souplessReferenceCount = getSouplessReferenceCount();
	    oldValue = String.valueOf(souplessReferenceCount);
	    newValue = String.valueOf(souplessReferenceCount - 1);
	    myDB.fireBeforeNodeTagsModified(this, "refs", oldValue, newValue);
	}
	boolean removed;
	if (myParentPlaylistFIDs == null)
	    removed = false;
	else
	    removed = myParentPlaylistFIDs.remove(_playlist.getFID());
	if (!removed)
	    System.out.println
		("AbstractFIDNode.nodeRemovedFromPlaylist: YOU ASKED TO REMOVE "
		 + this + " FROM " + _playlist + ", BUT IT WASN'T IN THERE!");
	myCachedSouplessReferenceCount = -1;
	if (!isSoup)
	    myDB.fireAfterNodeTagsModified(this, "refs", oldValue, newValue);
    }
    
    public synchronized void clearParentPlaylists() {
	if (myParentPlaylistFIDs != null)
	    myParentPlaylistFIDs.clear();
	myCachedSouplessReferenceCount = -1;
    }
    
    public synchronized int getReferenceCount() {
	int referenceCount = 0;
	if (myFID == 256L)
	    referenceCount++;
	if (myParentPlaylistFIDs != null) {
	    boolean isTransient = isTransient();
	    Iterator parentPlaylistFIDIter = myParentPlaylistFIDs.iterator();
	    while (parentPlaylistFIDIter.hasNext()) {
		LongLinkedList.ListItem listItem
		    = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
		boolean shouldRefCount = isTransient;
		if (!shouldRefCount) {
		    FIDPlaylist playlist
			= myDB.getPlaylist(listItem.getValue());
		    shouldRefCount
			= playlist != null && !playlist.isTransient();
		}
		if (shouldRefCount)
		    referenceCount++;
	    }
	}
	return referenceCount;
    }
    
    public synchronized int getSouplessReferenceCount() {
	if (myCachedSouplessReferenceCount == -1) {
	    int souplessReferenceCount = 0;
	    if (myParentPlaylistFIDs != null) {
		Iterator parentPlaylistFIDIter
		    = myParentPlaylistFIDs.iterator();
		while (parentPlaylistFIDIter.hasNext()) {
		    LongLinkedList.ListItem listItem
			= ((LongLinkedList.ListItem)
			   parentPlaylistFIDIter.next());
		    FIDPlaylist playlist
			= myDB.getPlaylist(listItem.getValue());
		    if (playlist != null && !playlist.isSoup())
			souplessReferenceCount++;
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
	    for (int i = 0; i < parentPlaylists.length; i++)
		parentPlaylists[i].fillInAncestors(_ancestorsSet);
	}
    }
}
