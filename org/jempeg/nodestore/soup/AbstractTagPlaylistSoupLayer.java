/* AbstractTagPlaylistSoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.ReflectionUtils;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public abstract class AbstractTagPlaylistSoupLayer
    implements IPlaylistSoupLayer
{
    private NodeTag myTag;
    private CollationKeyCache mySortCache;
    private NodeTag mySortTag;
    private int myContainedType;
    
    public AbstractTagPlaylistSoupLayer(String _tagName, int _containedType,
					String _sortTag) {
	this(NodeTag.getNodeTag(_tagName), _containedType,
	     NodeTag.getNodeTag(_sortTag));
    }
    
    public AbstractTagPlaylistSoupLayer(NodeTag _tag, int _containedType,
					NodeTag _sortTag) {
	myTag = _tag;
	myContainedType = _containedType;
	mySortTag = _sortTag;
	mySortCache = CollationKeyCache.createDefaultCache();
    }
    
    public int getType() {
	return myTag.getIconType();
    }
    
    public NodeTag getSortTag() {
	return mySortTag;
    }
    
    public CollationKeyCache getSortCache() {
	return mySortCache;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	return myTag.isDerivedFrom(_tag);
    }
    
    public FIDPlaylist[] getPlaylists
	(FIDPlaylist _soupPlaylist, IFIDNode _node, boolean _createIfMissing) {
	String playlistName = getPlaylistName(_node);
	int playlistIndex
	    = _soupPlaylist.getPlaylistIndex(playlistName, getType(),
					     myContainedType, _node, false,
					     _createIfMissing, mySortCache);
	FIDPlaylist[] playlists;
	if (playlistIndex < 0)
	    playlists = new FIDPlaylist[0];
	else {
	    FIDPlaylist newPlaylist
		= _soupPlaylist.getPlaylistAt(playlistIndex);
	    playlists = new FIDPlaylist[] { newPlaylist };
	}
	return playlists;
    }
    
    public boolean qualifies(IFIDNode _node) {
	boolean qualifies = (_node.isIdentified() && !_node.isTransient()
			     && !_node.isMarkedForDeletion()
			     && !(_node instanceof FIDPlaylist));
	return qualifies;
    }
    
    protected String getPlaylistName(IFIDNode _node) {
	String value = myTag.getStringValue(_node).trim();
	if (value.length() == 0)
	    value = ResourceBundleUtils.getUIString("soup.tagValueMissing");
	else
	    value = getPlaylistName(value);
	return value;
    }
    
    protected abstract String getPlaylistName(String string);
    
    public NodeTag getTag() {
	return myTag;
    }
    
    protected void setTag(NodeTag _tag) {
	myTag = _tag;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
