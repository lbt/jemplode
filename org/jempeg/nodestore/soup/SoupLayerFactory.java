/* SoupLayerFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.text.ParseException;
import java.util.Vector;

import com.inzyme.exception.ChainedRuntimeException;

import org.jempeg.nodestore.model.NodeTag;

public class SoupLayerFactory
{
    public static final String TAG_NAME = "tag";
    public static final String SEARCH_NAME = "search";
    public static final String SORT_NAME = "sort";
    public static final String TAGLETTER_NAME = "tagletter";
    public static final String TAGRANGE_NAME = "tagrange";
    public static final char NAME_SEPARATOR = ':';
    public static final char LAYER_SEPARATOR = '~';
    public static final int DEFAULT_CONTAINED_TYPE = 3;
    
    public static ISoupLayer[] fromExternalForm(String _externalForm)
	throws ParseException {
	int containedType = 3;
	Vector soupLayersVec = new Vector();
	int tildeIndex;
	for (int lastTildeIndex = _externalForm.length(); lastTildeIndex > 0;
	     lastTildeIndex = tildeIndex) {
	    tildeIndex = _externalForm.lastIndexOf('~', lastTildeIndex - 1);
	    String token
		= _externalForm.substring(tildeIndex + 1, lastTildeIndex);
	    int colonIndex = token.indexOf(':');
	    String type = token.substring(0, colonIndex);
	    int commaIndex = type.indexOf(',');
	    String sortTag;
	    if (commaIndex != -1) {
		sortTag = type.substring(commaIndex + 1);
		type = type.substring(0, commaIndex);
	    } else
		sortTag = "title";
	    String value = token.substring(colonIndex + 1);
	    ISoupLayer soupLayer;
	    if (type.equalsIgnoreCase("tag"))
		soupLayer = new BasicTagPlaylistSoupLayer(value, containedType,
							  sortTag);
	    else if (type.equalsIgnoreCase("search"))
		soupLayer = new SearchSoupLayer(value, containedType, sortTag);
	    else if (type.equalsIgnoreCase("sort"))
		soupLayer = new SortSoupLayer(containedType, sortTag);
	    else if (type.equalsIgnoreCase("tagletter"))
		soupLayer
		    = new FirstLetterTagPlaylistSoupLayer(value, containedType,
							  sortTag);
	    else if (type.startsWith("tagrange"))
		soupLayer = new RangeTagPlaylistSoupLayer(value, containedType,
							  sortTag);
	    else
		throw new ParseException(("Unknown soup layer type '" + type
					  + "'."),
					 0);
	    containedType = soupLayer.getType();
	    soupLayersVec.insertElementAt(soupLayer, 0);
	}
	ISoupLayer[] soupLayers = new ISoupLayer[soupLayersVec.size()];
	soupLayersVec.copyInto(soupLayers);
	if (soupLayers.length == 0)
	    soupLayers = null;
	return soupLayers;
    }
    
    public static String toExternalForm(ISoupLayer[] _soupLayers) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _soupLayers.length; i++) {
	    String layerName;
	    if (_soupLayers[i] instanceof BasicTagPlaylistSoupLayer)
		layerName = "tag";
	    else if (_soupLayers[i] instanceof SearchSoupLayer)
		layerName = "search";
	    else if (_soupLayers[i] instanceof SortSoupLayer)
		layerName = "sort";
	    else if (_soupLayers[i] instanceof FirstLetterTagPlaylistSoupLayer)
		layerName = "tagletter";
	    else if (_soupLayers[i] instanceof RangeTagPlaylistSoupLayer)
		layerName = "tagrange";
	    else
		throw new ChainedRuntimeException("Unknown soup layer type '"
						  + _soupLayers[i].getClass
							().getName()
						  + "'.");
	    appendExternalForm(sb, layerName, _soupLayers[i].getSortTag(),
			       _soupLayers[i].toExternalForm());
	}
	return sb.toString();
    }
    
    public static void appendExternalForm(StringBuffer _sb, String _layerName,
					  NodeTag _sortTag,
					  String _externalForm) {
	if (_sb.length() > 0)
	    _sb.append('~');
	_sb.append(_layerName);
	_sb.append(",");
	_sb.append(_sortTag.getName());
	_sb.append(':');
	_sb.append(_externalForm);
    }
}
