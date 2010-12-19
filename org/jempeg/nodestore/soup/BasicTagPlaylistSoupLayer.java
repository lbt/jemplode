/* BasicTagPlaylistSoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import org.jempeg.nodestore.model.NodeTag;

public class BasicTagPlaylistSoupLayer extends AbstractTagPlaylistSoupLayer
{
    public BasicTagPlaylistSoupLayer(String _tagName, int _containedType,
				     String _sortTag) {
	super(_tagName, _containedType, _sortTag);
    }
    
    public BasicTagPlaylistSoupLayer(NodeTag _tag, int _containedType,
				     NodeTag _sortTag) {
	super(_tag, _containedType, _sortTag);
    }
    
    public String toExternalForm() {
	return getTag().getName();
    }
    
    public String getPlaylistName(String _value) {
	return _value;
    }
}
