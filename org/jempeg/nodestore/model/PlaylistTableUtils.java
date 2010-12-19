package org.jempeg.nodestore.model;

import javax.swing.table.TableModel;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.table.SortedTableModel;

/**
 * PlaylistTableUtils provides a set of utilities for 
 * managing the playlist table view.
 * 
 * @author Mike Schrag
 */
public class PlaylistTableUtils {
	/**
	 * The default columns that are displayed in the playlist table
	 */
	public static final String[] DEFAULT_COLUMNS = new String[]{ DatabaseTags.TITLE_TAG, DatabaseTags.ARTIST_TAG, DatabaseTags.POS_TAG, DatabaseTags.SOURCE_TAG, DatabaseTags.GENRE_TAG, DatabaseTags.DURATION_TAG, DatabaseTags.YEAR_TAG };

	public static final String[] SIMPLE_COLUMNS = new String[]{ DatabaseTags.TITLE_TAG, DatabaseTags.ARTIST_TAG, DatabaseTags.SOURCE_TAG, DatabaseTags.TRACKNR_TAG, DatabaseTags.GENRE_TAG, DatabaseTags.LENGTH_TAG, DatabaseTags.DURATION_TAG };

	private static String PLAYLIST_COLUMNS_KEY = "jempeg.playlist.columnNames";

	public static void setPlaylistColumnsKey(String _playlistColumnsKey) {
		PLAYLIST_COLUMNS_KEY = _playlistColumnsKey;
	}

	/**
	 * Sets the tag names to use as columns for playlist tables.
	 * 
	 * @param _columnTagNames the tag names to use for columns
	 */
	public static void setColumnTagNames(String[] _columnTagNames) {
		PropertiesManager.getInstance().setStringArrayProperty(PlaylistTableUtils.PLAYLIST_COLUMNS_KEY, _columnTagNames);
	}

	/**
	 * Returns the tag names that are used as columns for playlist tables.
	 * 
	 * @return the tag names that are used as columns for playlist tables
	 */
	public static String[] getColumnTagNames() {
		String[] columnTagNames = PropertiesManager.getInstance().getStringArrayProperty(PlaylistTableUtils.PLAYLIST_COLUMNS_KEY, PlaylistTableUtils.DEFAULT_COLUMNS);
		return columnTagNames;
	}

	/**
	 * Sets the tag names to use as columns for playlist tables.
	 * 
	 * @param _columnTagNames the tag names to use for columns
	 */
	public static void setColumnTagNames(int _fidType, String[] _columnTagNames) {
		if (_fidType == -1) {
			setColumnTagNames(_columnTagNames);
		}
		else {
			PropertiesManager.getInstance().setStringArrayProperty(PlaylistTableUtils.PLAYLIST_COLUMNS_KEY + "." + _fidType, _columnTagNames);
		}
	}

	/**
	 * Returns the tag names that are used as columns for playlist tables.
	 * 
	 * @return the tag names that are used as columns for playlist tables
	 */
	public static String[] getColumnTagNames(int _fidType) {
		String[] columnTagNames = PropertiesManager.getInstance().getStringArrayProperty(PlaylistTableUtils.PLAYLIST_COLUMNS_KEY + "." + _fidType, PlaylistTableUtils.getColumnTagNames());
		return columnTagNames;
	}

	/**
	 * Loads the column width from the properties file for a specific column.
	 * 
	 * @param __tagName the tag name to restore the width of
	 * @return the width of the column
	 */
	public static int getColumnWidth(String _tagName) {
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		int width = propertiesManager.getIntProperty("jempeg.tag." + _tagName + ".width", -1);
		return width;
	}

	/**
	 * Saves the current column widths to the properties file.
	 * 
	 * @param _tagName the tag name to save the width of
	 * @param _width the width to save
	 */
	public static void setColumnWidth(String _tagName, int _width) {
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		propertiesManager.setIntProperty("jempeg.tag." + _tagName + ".width", _width);
	}

	/**
	 * This is just a handy method that checks to see if the given
	 * table model is an instanceof IPlaylistTableModel and returns it
	 * if it is, otherwise it returns null.  This is necessary because
	 * you can't perform a lot of operations on non-PlaylistTableModels.
	 * 
	 * @param _tableModel the table model
	 * @return the IPlaylistTableModel of this table model
	 */
	public static IPlaylistTableModel getPlaylistTableModel(TableModel _tableModel) {
		IPlaylistTableModel playlistTableModel;

		if (_tableModel instanceof IPlaylistTableModel) {
			playlistTableModel = (IPlaylistTableModel) _tableModel;
		}
		else if (_tableModel instanceof SortedTableModel) {
			TableModel proxiedTableModel = ((SortedTableModel) _tableModel).getModel();
			playlistTableModel = getPlaylistTableModel(proxiedTableModel);
		}
		else {
			playlistTableModel = null;
		}

		return playlistTableModel;
	}

	/**
	 * This is just a handy method that checks to see if the given
	 * table model is an instanceof IPlaylistTableModel and returns it
	 * if it is, otherwise it returns null.  This is necessary because
	 * you can't perform a lot of operations on non-PlaylistTableModels.
	 * 
	 * @param _tableModel the table model
	 * @return the IPlaylistTableModel of this table model
	 */
	public static IFIDPlaylistWrapper getPlaylistWrapper(TableModel _tableModel) {
		IFIDPlaylistWrapper playlistWrapper;

		if (_tableModel instanceof IFIDPlaylistWrapper) {
			playlistWrapper = (IFIDPlaylistWrapper) _tableModel;
		}
		else if (_tableModel instanceof SortedTableModel) {
			TableModel proxiedTableModel = ((SortedTableModel) _tableModel).getModel();
			playlistWrapper = getPlaylistWrapper(proxiedTableModel);
		}
		else {
			playlistWrapper = null;
		}

		return playlistWrapper;
	}
}
