/* PlaylistTableUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import javax.swing.table.TableModel;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.table.SortedTableModel;

import org.jempeg.nodestore.IFIDPlaylistWrapper;

public class PlaylistTableUtils
{
    public static final String[] DEFAULT_COLUMNS
	= { "title", "artist", "pos", "source", "genre", "duration", "year" };
    public static final String[] SIMPLE_COLUMNS
	= { "title", "artist", "source", "tracknr", "genre", "length",
	    "duration" };
    private static String PLAYLIST_COLUMNS_KEY = "jempeg.playlist.columnNames";
    
    public static void setPlaylistColumnsKey(String _playlistColumnsKey) {
	PLAYLIST_COLUMNS_KEY = _playlistColumnsKey;
    }
    
    public static void setColumnTagNames(String[] _columnTagNames) {
	PropertiesManager.getInstance()
	    .setStringArrayProperty(PLAYLIST_COLUMNS_KEY, _columnTagNames);
    }
    
    public static String[] getColumnTagNames() {
	String[] columnTagNames
	    = (PropertiesManager.getInstance().getStringArrayProperty
	       (PLAYLIST_COLUMNS_KEY, DEFAULT_COLUMNS));
	return columnTagNames;
    }
    
    public static void setColumnTagNames(int _fidType,
					 String[] _columnTagNames) {
	if (_fidType == -1)
	    setColumnTagNames(_columnTagNames);
	else
	    PropertiesManager.getInstance().setStringArrayProperty
		(PLAYLIST_COLUMNS_KEY + "." + _fidType, _columnTagNames);
    }
    
    public static String[] getColumnTagNames(int _fidType) {
	String[] columnTagNames
	    = (PropertiesManager.getInstance().getStringArrayProperty
	       (PLAYLIST_COLUMNS_KEY + "." + _fidType, getColumnTagNames()));
	return columnTagNames;
    }
    
    public static int getColumnWidth(String _tagName) {
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	int width
	    = propertiesManager
		  .getIntProperty("jempeg.tag." + _tagName + ".width", -1);
	return width;
    }
    
    public static void setColumnWidth(String _tagName, int _width) {
	PropertiesManager propertiesManager = PropertiesManager.getInstance();
	propertiesManager.setIntProperty("jempeg.tag." + _tagName + ".width",
					 _width);
    }
    
    public static IPlaylistTableModel getPlaylistTableModel
	(TableModel _tableModel) {
	IPlaylistTableModel playlistTableModel;
	if (_tableModel instanceof IPlaylistTableModel)
	    playlistTableModel = (IPlaylistTableModel) _tableModel;
	else if (_tableModel instanceof SortedTableModel) {
	    TableModel proxiedTableModel
		= ((SortedTableModel) _tableModel).getModel();
	    playlistTableModel = getPlaylistTableModel(proxiedTableModel);
	} else
	    playlistTableModel = null;
	return playlistTableModel;
    }
    
    public static IFIDPlaylistWrapper getPlaylistWrapper
	(TableModel _tableModel) {
	IFIDPlaylistWrapper playlistWrapper;
	if (_tableModel instanceof IFIDPlaylistWrapper)
	    playlistWrapper = (IFIDPlaylistWrapper) _tableModel;
	else if (_tableModel instanceof SortedTableModel) {
	    TableModel proxiedTableModel
		= ((SortedTableModel) _tableModel).getModel();
	    playlistWrapper = getPlaylistWrapper(proxiedTableModel);
	} else
	    playlistWrapper = null;
	return playlistWrapper;
    }
}
