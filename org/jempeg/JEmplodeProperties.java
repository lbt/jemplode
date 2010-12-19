/* JEmplodeProperties - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg;
import java.util.Locale;

import com.inzyme.properties.PropertiesManager;

public class JEmplodeProperties
{
    public static final String LAST_OPEN_DIRECTORY_KEY = "lastOpenDirectory";
    public static final String SORT_TAG_KEY = "jempeg.playlist.sort.tag";
    public static final String SORT_DIRECTION_KEY
	= "jempeg.playlist.sort.direction";
    public static final String USE_HIJACK_KEY = "jempeg.useHijack";
    public static final String HIJACK_LOGIN_KEY = "jempeg.hijackLogin";
    public static final String HIJACK_PASSWORD_KEY = "jempeg.hijackPassword";
    public static final String SERIAL_FLAG_PROPERTY
	= "jempeg.connections.serialPort";
    public static final String USB_FLAG_PROPERTY = "jempeg.connections.USB";
    public static final String ETHERNET_BROADCAST_FLAG_PROPERTY
	= "jempeg.connections.ethernetBroadcast";
    public static final String SPECIFIC_ADDRESS_FLAG_PROPERTY
	= "jempeg.connections.specificAddress";
    public static final String SPECIFIC_ADDRESS_PROPERTY
	= "jempeg.connections.inetAddress";
    public static final String KARMA_ADDRESS_FLAG_PROPERTY
	= "jempeg.connections.karmaSpecificAddress";
    public static final String KARMA_ADDRESS_PROPERTY
	= "jempeg.connections.karmaInetAddress";
    public static final String AUTO_SELECT_PROPERTY
	= "jempeg.connections.autoSelect";
    public static final String DO_NOT_BROADCAST_TO_PROPERTY
	= "jempeg.connections.doNotBroadcastTo";
    public static final String FILENAME_FORMAT_BASE = "jempeg.filenameFormat.";
    public static final String FILENAME_FORMAT_TUNE_KEY
	= "jempeg.filenameFormat.tune";
    public static final String FILENAME_FORMAT_PLAYLIST_KEY
	= "jempeg.filenameFormat.playlist";
    public static final String FILENAME_FORMAT_TAXI_KEY
	= "jempeg.filenameFormat.taxi";
    public static final String FILENAME_FORMAT_SOUP_BASE
	= "jempeg.soupFilenameFormat.";
    public static final String FILENAME_FORMAT_SOUP_TUNE_KEY
	= "jempeg.soupFilenameFormat.tune";
    public static final String FILENAME_FORMAT_SOUP_PLAYLIST_KEY
	= "jempeg.soupFilenameFormat.playlist";
    public static final String FILENAME_FORMAT_SOUP_TAXI_KEY
	= "jempeg.soupFilenameFormat.taxi";
    public static final String DOWNLOAD_DIRECTORY_PROPERTY
	= "jempeg.downloadDir";
    public static final String DOWNLOAD_FULL_PATH_PROPERTY
	= "jempeg.downloadFullPath";
    public static final String USE_FAST_CONNECTIONS_PROPERTY
	= "jempeg.useFastConnection";
    public static final String ALLOW_UNKNOWN_TYPES_PROPERTY
	= "jempeg.allowUnknownTypes";
    public static final String SHOW_ONLY_FAILED_IMPORTS_PROPERTY
	= "jempeg.onlyShowFailedImports";
    public static final String LOOK_AND_FEEL_PROPERTY = "jempeg.lookAndFeel";
    public static final String LOOK_AND_FEEL_SYSTEM = "system";
    public static final String CURRENT_DOWNLOAD_DIRECTORY_PROPERTY
	= "jempeg.currentDownloadDirectory";
    public static final String WRITE_ID3v2_PROPERTY = "jempeg.writeID3v2";
    public static final String PRESERVE_ID3v2_PROPERTY
	= "jempeg.preserveID3v2";
    public static final String REMOVE_ID3v1_PROPERTY = "jempeg.removeID3v1";
    public static final String DISABLE_SOUP_UPDATING_PROPERTY
	= "jempeg.disableSoupUpdating";
    public static final String RECURSIVE_COLORS_PROPERTY
	= "jempeg.recursiveColors";
    public static final String RECURSIVE_SOUP_COLORS_PROPERTY
	= "jempeg.recursiveSoupColors";
    public static final String DEDUPLICATION_PROPERTY = "deduplication";
    public static final String PLAYER_DATABASE_PROPERTY = "playerDatabase";
    public static final String IMPORT_M3U_PROPERTY = "importM3U";
    public static final String TREAT_ISO_AS_DEFAULT_ENCODING_KEY
	= "treatISOAsDefaultEncoding";
    public static final String UPDATE_TAGS_ON_DUPLICATES
	= "updateTagsOnDuplicates";
    public static final String CACHE_DATABASE_KEY = "cacheDatabase";
    public static final String LAST_KNOWN_HOST_KEY = "lastKnownHost";
    public static final String REFRESH_AGGREGATE_TAGS_KEY
	= "refreshDependentTags";
    public static final String STRIP_ARTICLES_WHEN_COMPARING_KEY
	= "stripArticlesWhenComparing";
    public static final String TONY_SELECTIONS_KEY = "tonySelections";
    public static final String REBUILD_ON_PC_KEY = "rebuildOnPC";
    public static final String REBUILD_FROM_MEMORY_KEY = "rebuildFromMemory";
    public static final String SORT_PLAYLISTS_ABOVE_TUNES
	= "sortPlaylistsFirst";
    
    public static void initializeDefaults() {
	PropertiesManager defaults = PropertiesManager.getDefaults();
	defaults.setProperty("lastOpenDirectory",
			     System.getProperty("user.dir"));
	defaults.setProperty("jempeg.currentDownloadDirectory",
			     System.getProperty("user.dir"));
	defaults.setProperty("jempeg.playlist.sort.tag", "title");
	defaults.setBooleanProperty("jempeg.useHijack", true);
	defaults.setProperty("jempeg.hijackLogin", "empeg");
	defaults.setProperty("jempeg.hijackPassword", "empeg");
	defaults.setBooleanProperty("jempeg.playlist.sort.direction", true);
	defaults.setBooleanProperty("jempeg.connections.serialPort", true);
	defaults.setBooleanProperty("jempeg.connections.USB", true);
	defaults.setBooleanProperty("jempeg.connections.ethernetBroadcast",
				    true);
	defaults.setBooleanProperty("jempeg.connections.specificAddress",
				    true);
	defaults.setBooleanProperty("jempeg.connections.karmaSpecificAddress",
				    false);
	defaults.setBooleanProperty("jempeg.connections.autoSelect", false);
	defaults.setProperty("jempeg.connections.doNotBroadcastTo", "");
	defaults.setProperty("jempeg.filenameFormat.tune",
			     "{artist}-{source}-{title}{ext}");
	defaults.setProperty("jempeg.filenameFormat.playlist", "{title}");
	defaults.setProperty("jempeg.filenameFormat.taxi", "{title}");
	defaults.setProperty("jempeg.soupFilenameFormat.tune",
			     "{artist}-{source}-{title}{ext}");
	defaults.setProperty("jempeg.soupFilenameFormat.playlist", "{title}");
	defaults.setProperty("jempeg.soupFilenameFormat.taxi", "{title}");
	defaults.setBooleanProperty("jempeg.downloadFullPath", false);
	defaults.setProperty("jempeg.downloadDir", "");
	defaults.setBooleanProperty("jempeg.useFastConnection", true);
	defaults.setBooleanProperty("jempeg.allowUnknownTypes", false);
	defaults.setBooleanProperty("jempeg.onlyShowFailedImports", true);
	defaults.setProperty("jempeg.lookAndFeel", "system");
	defaults.setBooleanProperty("jempeg.writeID3v2", false);
	defaults.setBooleanProperty("jempeg.preserveID3v2", false);
	defaults.setBooleanProperty("jempeg.removeID3v1", false);
	defaults.setBooleanProperty("jempeg.disableSoupUpdating", false);
	defaults.setBooleanProperty("jempeg.recursiveColors", true);
	defaults.setBooleanProperty("jempeg.recursiveSoupColors", false);
	defaults.setBooleanProperty("deduplication", true);
	defaults.setBooleanProperty("importM3U", true);
	defaults.setBooleanProperty("treatISOAsDefaultEncoding",
				    (Locale.JAPAN.getLanguage().equals
				     (Locale.getDefault().getLanguage())));
	defaults.setBooleanProperty("updateTagsOnDuplicates", false);
	defaults.setBooleanProperty("cacheDatabase", true);
	defaults.setBooleanProperty("refreshDependentTags", true);
	defaults.setBooleanProperty("stripArticlesWhenComparing", true);
	defaults.setBooleanProperty("tonySelections", false);
	defaults.setBooleanProperty("rebuildOnPC", false);
	defaults.setBooleanProperty("rebuildFromMemory", false);
	defaults.setBooleanProperty("sortPlaylistsFirst", false);
    }
}
