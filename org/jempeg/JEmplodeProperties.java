package org.jempeg;

import java.util.Locale;

import org.jempeg.nodestore.DatabaseTags;

import com.inzyme.properties.PropertiesManager;

/**
 * A placeholder for constants.
 * 
 * @author Mike Schrag
 */
public class JEmplodeProperties {
  public static final String LAST_OPEN_DIRECTORY_KEY = "lastOpenDirectory";

  public static final String SORT_TAG_KEY = "jempeg.playlist.sort.tag";

  public static final String SORT_DIRECTION_KEY = "jempeg.playlist.sort.direction";

  public static final String USE_HIJACK_KEY = "jempeg.useHijack";

  public static final String HIJACK_LOGIN_KEY = "jempeg.hijackLogin";

  public static final String HIJACK_PASSWORD_KEY = "jempeg.hijackPassword";

  /**
   * The property name for the serial port connection flag.
   **/
  public static final String SERIAL_FLAG_PROPERTY = "jempeg.connections.serialPort";

  /**
   * The property name for the USB connection flag.
   **/
  public static final String USB_FLAG_PROPERTY = "jempeg.connections.USB";

  /**
   * The property name for the Ethernet broadcast flag.
   **/
  public static final String ETHERNET_BROADCAST_FLAG_PROPERTY = "jempeg.connections.ethernetBroadcast";

  public static final String SPECIFIC_ADDRESS_FLAG_PROPERTY = "jempeg.connections.specificAddress";
  public static final String SPECIFIC_ADDRESS_PROPERTY = "jempeg.connections.inetAddress";

  public static final String KARMA_ADDRESS_FLAG_PROPERTY = "jempeg.connections.karmaSpecificAddress";
  public static final String KARMA_ADDRESS_PROPERTY = "jempeg.connections.karmaInetAddress";

  /**
   * The property name for the auto select flag.
   **/
  public static final String AUTO_SELECT_PROPERTY = "jempeg.connections.autoSelect";

  /**
   * The property name for the list of hosts to not broadcast to.
   **/
  public static final String DO_NOT_BROADCAST_TO_PROPERTY = "jempeg.connections.doNotBroadcastTo";

  public static final String FILENAME_FORMAT_BASE = "jempeg.filenameFormat.";

  public static final String FILENAME_FORMAT_TUNE_KEY = JEmplodeProperties.FILENAME_FORMAT_BASE + DatabaseTags.TYPE_TUNE;
  public static final String FILENAME_FORMAT_PLAYLIST_KEY = JEmplodeProperties.FILENAME_FORMAT_BASE + DatabaseTags.TYPE_PLAYLIST;
  public static final String FILENAME_FORMAT_TAXI_KEY = JEmplodeProperties.FILENAME_FORMAT_BASE + DatabaseTags.TYPE_TAXI;

  public static final String FILENAME_FORMAT_SOUP_BASE = "jempeg.soupFilenameFormat.";
  public static final String FILENAME_FORMAT_SOUP_TUNE_KEY = JEmplodeProperties.FILENAME_FORMAT_SOUP_BASE + DatabaseTags.TYPE_TUNE;
  public static final String FILENAME_FORMAT_SOUP_PLAYLIST_KEY = JEmplodeProperties.FILENAME_FORMAT_SOUP_BASE + DatabaseTags.TYPE_PLAYLIST;
  public static final String FILENAME_FORMAT_SOUP_TAXI_KEY = JEmplodeProperties.FILENAME_FORMAT_SOUP_BASE + DatabaseTags.TYPE_TAXI;

  /**
   * The property name of the directory to download into
   */
  public static final String DOWNLOAD_DIRECTORY_PROPERTY = "jempeg.downloadDir";

  /**
   * The property name of whether or not the full playlist path should be downloaded.
   */
  public static final String DOWNLOAD_FULL_PATH_PROPERTY = "jempeg.downloadFullPath";

  /**
   * The property name of whether or not fast connections should be used.
   */
  public static final String USE_FAST_CONNECTIONS_PROPERTY = "jempeg.useFastConnection";

  /**
   * The property name of whether or not unknown file types can be uploaded.
   */
  public static final String ALLOW_UNKNOWN_TYPES_PROPERTY = "jempeg.allowUnknownTypes";

  /**
   * The property name of whether or not to only show failed imports (i.e. don't show succeeded)
   */
  public static final String SHOW_ONLY_FAILED_IMPORTS_PROPERTY = "jempeg.onlyShowFailedImports";

  /**
   * The property name of the Look-and-Feel to use
   */
  public static final String LOOK_AND_FEEL_PROPERTY = "jempeg.lookAndFeel";

  /**
   * The value of the Look-and-Feel that specifies that the system look-and-feel should be used
   */
  public static final String LOOK_AND_FEEL_SYSTEM = "system";

  /**
   * The property name of the current download directory.
   */
  public static final String CURRENT_DOWNLOAD_DIRECTORY_PROPERTY = "jempeg.currentDownloadDirectory";

  /**
   * The property name of whether or not ID3v2 tags should be rewritten on download
   */
  public static final String WRITE_ID3v2_PROPERTY = "jempeg.writeID3v2";

  /**
   * The property name of whether or not non-Empeg ID3v2 tags should be preserved on export
   */
  public static final String PRESERVE_ID3v2_PROPERTY = "jempeg.preserveID3v2";

  /**
   * The property name of whether or not ID3v1 tags should be removed on download
   */
  public static final String REMOVE_ID3v1_PROPERTY = "jempeg.removeID3v1";

  /**
   * The property name of whether or not soup updating should be disabled.
   */
  public static final String DISABLE_SOUP_UPDATING_PROPERTY = "jempeg.disableSoupUpdating";

  /**
   * The property name of whether or not colors in playlists are computed recursively.
   */
  public static final String RECURSIVE_COLORS_PROPERTY = "jempeg.recursiveColors";

  /**
   * The property name of whether or not colors in soup playlists are computed recursively.
   */
  public static final String RECURSIVE_SOUP_COLORS_PROPERTY = "jempeg.recursiveSoupColors";

  /**
   * The property name of whether or not to globally dedupe tunes.
   */
  public static final String DEDUPLICATION_PROPERTY = "deduplication";

  public static final String PLAYER_DATABASE_PROPERTY = "playerDatabase";

  public static final String IMPORT_M3U_PROPERTY = "importM3U";

  public static final String TREAT_ISO_AS_DEFAULT_ENCODING_KEY = "treatISOAsDefaultEncoding";

  public static final String UPDATE_TAGS_ON_DUPLICATES = "updateTagsOnDuplicates";

  public static final String CACHE_DATABASE_KEY = "cacheDatabase";

  public static final String LAST_KNOWN_HOST_KEY = "lastKnownHost";

  public static final String REFRESH_AGGREGATE_TAGS_KEY = "refreshDependentTags";

  public static final String STRIP_ARTICLES_WHEN_COMPARING_KEY = "stripArticlesWhenComparing";

  public static final String TONY_SELECTIONS_KEY = "tonySelections";

  public static final String REBUILD_ON_PC_KEY = "rebuildOnPC";

  public static final String REBUILD_FROM_MEMORY_KEY = "rebuildFromMemory";

  public static final String SORT_PLAYLISTS_ABOVE_TUNES = "sortPlaylistsFirst";

  public static void initializeDefaults() {
    PropertiesManager defaults = PropertiesManager.getDefaults();
    defaults.setProperty(JEmplodeProperties.LAST_OPEN_DIRECTORY_KEY, System.getProperty("user.dir"));
    defaults.setProperty(JEmplodeProperties.CURRENT_DOWNLOAD_DIRECTORY_PROPERTY, System.getProperty("user.dir"));
    defaults.setProperty(JEmplodeProperties.SORT_TAG_KEY, DatabaseTags.TITLE_TAG);
    defaults.setBooleanProperty(JEmplodeProperties.USE_HIJACK_KEY, true);
    defaults.setProperty(JEmplodeProperties.HIJACK_LOGIN_KEY, "empeg");
    defaults.setProperty(JEmplodeProperties.HIJACK_PASSWORD_KEY, "empeg");
    defaults.setBooleanProperty(JEmplodeProperties.SORT_DIRECTION_KEY, true);
    defaults.setBooleanProperty(JEmplodeProperties.SERIAL_FLAG_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.USB_FLAG_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.ETHERNET_BROADCAST_FLAG_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.SPECIFIC_ADDRESS_FLAG_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.KARMA_ADDRESS_FLAG_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.AUTO_SELECT_PROPERTY, false);
    defaults.setProperty(JEmplodeProperties.DO_NOT_BROADCAST_TO_PROPERTY, "");
    defaults.setProperty(JEmplodeProperties.FILENAME_FORMAT_TUNE_KEY, "{artist}-{source}-{title}{ext}");
    defaults.setProperty(JEmplodeProperties.FILENAME_FORMAT_PLAYLIST_KEY, "{title}");
    defaults.setProperty(JEmplodeProperties.FILENAME_FORMAT_TAXI_KEY, "{title}");
    defaults.setProperty(JEmplodeProperties.FILENAME_FORMAT_SOUP_TUNE_KEY, "{artist}-{source}-{title}{ext}");
    defaults.setProperty(JEmplodeProperties.FILENAME_FORMAT_SOUP_PLAYLIST_KEY, "{title}");
    defaults.setProperty(JEmplodeProperties.FILENAME_FORMAT_SOUP_TAXI_KEY, "{title}");
    defaults.setBooleanProperty(JEmplodeProperties.DOWNLOAD_FULL_PATH_PROPERTY, false);
    defaults.setProperty(JEmplodeProperties.DOWNLOAD_DIRECTORY_PROPERTY, "");
    defaults.setBooleanProperty(JEmplodeProperties.USE_FAST_CONNECTIONS_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.ALLOW_UNKNOWN_TYPES_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.SHOW_ONLY_FAILED_IMPORTS_PROPERTY, true);
    defaults.setProperty(JEmplodeProperties.LOOK_AND_FEEL_PROPERTY, JEmplodeProperties.LOOK_AND_FEEL_SYSTEM);
    defaults.setBooleanProperty(JEmplodeProperties.WRITE_ID3v2_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.PRESERVE_ID3v2_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.REMOVE_ID3v1_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.DISABLE_SOUP_UPDATING_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.RECURSIVE_COLORS_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.RECURSIVE_SOUP_COLORS_PROPERTY, false);
    defaults.setBooleanProperty(JEmplodeProperties.DEDUPLICATION_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.IMPORT_M3U_PROPERTY, true);
    defaults.setBooleanProperty(JEmplodeProperties.TREAT_ISO_AS_DEFAULT_ENCODING_KEY, Locale.JAPAN.getLanguage().equals(Locale.getDefault().getLanguage()));
    defaults.setBooleanProperty(JEmplodeProperties.UPDATE_TAGS_ON_DUPLICATES, false);
    defaults.setBooleanProperty(JEmplodeProperties.CACHE_DATABASE_KEY, true);
    defaults.setBooleanProperty(JEmplodeProperties.REFRESH_AGGREGATE_TAGS_KEY, true);
    defaults.setBooleanProperty(JEmplodeProperties.STRIP_ARTICLES_WHEN_COMPARING_KEY, true);
    defaults.setBooleanProperty(JEmplodeProperties.TONY_SELECTIONS_KEY, false);
    defaults.setBooleanProperty(JEmplodeProperties.REBUILD_ON_PC_KEY, false);
    defaults.setBooleanProperty(JEmplodeProperties.REBUILD_FROM_MEMORY_KEY, false);
    defaults.setBooleanProperty(JEmplodeProperties.SORT_PLAYLISTS_ABOVE_TUNES, false);
  }
}