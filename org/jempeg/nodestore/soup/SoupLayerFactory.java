package org.jempeg.nodestore.soup;

import java.text.ParseException;
import java.util.Vector;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.exception.ChainedRuntimeException;

// Yes, this factory has some OO issues.
public class SoupLayerFactory {
	public static final String TAG_NAME = "tag";
	public static final String SEARCH_NAME = "search";
  public static final String SORT_NAME = "sort";
	public static final String TAGLETTER_NAME = "tagletter";
	public static final String TAGRANGE_NAME = "tagrange";

	public static final char NAME_SEPARATOR = ':';
	public static final char LAYER_SEPARATOR = '~';

	public static final int DEFAULT_CONTAINED_TYPE = IFIDNode.TYPE_TUNE;
	
	/**
	 * Returns an ISoupLayer array that corresponds to the given external
	 * form.  The external form is in the format:
	 * 
	 * tag:artist~tag:album~search:name like 'test'
	 * 
	 * @param _externalForm the external form to parse
	 * @return an ISoupLayer array that represents in the given external form
	 * @throws ParseException if the soup string cannot be parsed
	 */
	public static ISoupLayer[] fromExternalForm(String _externalForm) throws ParseException {
		int containedType = SoupLayerFactory.DEFAULT_CONTAINED_TYPE;
		Vector soupLayersVec = new Vector();
		int lastTildeIndex = _externalForm.length();
		while (lastTildeIndex > 0) {
			int tildeIndex = _externalForm.lastIndexOf(SoupLayerFactory.LAYER_SEPARATOR, lastTildeIndex - 1);
			String token = _externalForm.substring(tildeIndex + 1, lastTildeIndex);
			int colonIndex = token.indexOf(SoupLayerFactory.NAME_SEPARATOR);
      String sortTag;
			String type = token.substring(0, colonIndex);
      int commaIndex = type.indexOf(',');
      if (commaIndex != -1) {
        sortTag = type.substring(commaIndex + 1);
        type = type.substring(0, commaIndex);
      }
      else {
        sortTag = DatabaseTags.TITLE_TAG;
      }
      
			String value = token.substring(colonIndex + 1);

			ISoupLayer soupLayer;
			if (type.equalsIgnoreCase(SoupLayerFactory.TAG_NAME)) {
				soupLayer = new BasicTagPlaylistSoupLayer(value, containedType, sortTag);
			}
			else if (type.equalsIgnoreCase(SoupLayerFactory.SEARCH_NAME)) {
				soupLayer = new SearchSoupLayer(value, containedType, sortTag);
			}
      else if (type.equalsIgnoreCase(SoupLayerFactory.SORT_NAME)) {
        soupLayer = new SortSoupLayer(containedType, sortTag);
      }
			else if (type.equalsIgnoreCase(SoupLayerFactory.TAGLETTER_NAME)) {
				soupLayer = new FirstLetterTagPlaylistSoupLayer(value, containedType, sortTag);
			}
			else if (type.startsWith(SoupLayerFactory.TAGRANGE_NAME)) {
				soupLayer = new RangeTagPlaylistSoupLayer(value, containedType, sortTag);
			}
			else {
				throw new ParseException("Unknown soup layer type '" + type + "'.", 0);
			}
			containedType = soupLayer.getType();

			soupLayersVec.insertElementAt(soupLayer, 0);
			lastTildeIndex = tildeIndex;
		}
		ISoupLayer[] soupLayers = new ISoupLayer[soupLayersVec.size()];
		soupLayersVec.copyInto(soupLayers);
		if (soupLayers.length == 0) {
			soupLayers = null;
		}
		return soupLayers;
	}

	public static String toExternalForm(ISoupLayer[] _soupLayers) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _soupLayers.length; i++) {
			String layerName;
			if (_soupLayers[i] instanceof BasicTagPlaylistSoupLayer) {
				layerName = SoupLayerFactory.TAG_NAME;
			}
			else if (_soupLayers[i] instanceof SearchSoupLayer) {
				layerName = SoupLayerFactory.SEARCH_NAME;
			}
      else if (_soupLayers[i] instanceof SortSoupLayer) {
        layerName = SoupLayerFactory.SORT_NAME;
      }
			else if (_soupLayers[i] instanceof FirstLetterTagPlaylistSoupLayer) {
				layerName = SoupLayerFactory.TAGLETTER_NAME;
			}
			else if (_soupLayers[i] instanceof RangeTagPlaylistSoupLayer) {
				layerName = SoupLayerFactory.TAGRANGE_NAME;
			}
			else {
				throw new ChainedRuntimeException("Unknown soup layer type '" + _soupLayers[i].getClass().getName() + "'.");
			}
			appendExternalForm(sb, layerName, _soupLayers[i].getSortTag(), _soupLayers[i].toExternalForm());
		}
		return sb.toString();
	}

	public static void appendExternalForm(StringBuffer _sb, String _layerName, NodeTag _sortTag, String _externalForm) {
		if (_sb.length() > 0) {
			_sb.append(SoupLayerFactory.LAYER_SEPARATOR);
		}
		_sb.append(_layerName);
    _sb.append(",");
    _sb.append(_sortTag.getName());
		_sb.append(SoupLayerFactory.NAME_SEPARATOR);
		_sb.append(_externalForm);
	}
}
