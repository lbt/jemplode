/* FrameTypes - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;

public interface FrameTypes
{
    public static final String[] textFrameTypes
	= { "TALB", "TBPM", "TCOM", "TCON", "TCOP", "TDAT", "TDLY", "TENC",
	    "TEXT", "TFLT", "TIME", "TIT1", "TIT2", "TIT3", "TKEY", "TLAN",
	    "TLEN", "TMED", "TOAL", "TOFN", "TOLY", "TOPE", "TORY", "TOWN",
	    "TPE1", "TPE2", "TPE3", "TPE4", "TPOS", "TPUB", "TRCK", "TRDA",
	    "TRSN", "TRSO", "TSIZ", "TSRC", "TSSE", "TYER" };
    public static final String[] urlFrameTypes
	= { "WCOM", "WCOP", "WOAF", "WOAR", "WOAS", "WORS", "WPAY", "WPUB" };
    public static final String AUDIO_ENCRYPTION = "AENC";
    public static final String ATTACHED_PICTURE = "APIC";
    public static final String COMMENT = "COMM";
    public static final String COMMERCIAL_FRAME = "COMR";
    public static final String ENCRYPTION_METHOD = "ENCR";
    public static final String EQUALIZATION = "EQUA";
    public static final String TIMING_CODES = "ETCO";
    public static final String ENCAPSULATED_OBJECT = "GEOB";
    public static final String GROUP_IDENTIFICATION = "GRID";
    public static final String INVOLVED_PEOPLE = "IPLS";
    public static final String LINKED_INFORMATION = "LINK";
    public static final String MUSIC_CD_IDENTIFIER = "MCDI";
    public static final String MPEG_LOCATION_LOOKUP_TABLE = "MLLT";
    public static final String OWNERSHIP_FRAME = "OWNE";
    public static final String PRIVATE_FRAME = "PRIV";
    public static final String PLAY_COUNTER = "PCNT";
    public static final String POPULARIMETER = "POPM";
    public static final String POSITION_SYNCHRONISATION_FRAME = "POSS";
    public static final String RECOMMENDED_BUFFER_SIZE = "RBUF";
    public static final String RELATIVE_VOLUME_ADJUSTMENT = "RVAD";
    public static final String REVERB = "RVRB";
    public static final String SYNCHRONIZED_TEXT = "SYLT";
    public static final String SYNCHRONIZED_TEMPO_CODES = "SYTC";
    public static final String ALBUM_TITLE = "TALB";
    public static final String ALBUM = "TALB";
    public static final String SET_TITLE = "TALB";
    public static final String BEATS_PER_MINUTE = "TBPM";
    public static final String COMPOSER = "TCOM";
    public static final String CONTENT_TYPE = "TCON";
    public static final String COPYRIGHT = "TCOP";
    public static final String DATE = "TDAT";
    public static final String PLAYLIST_DELAY = "TDLY";
    public static final String ENCODED_BY = "TENC";
    public static final String AUTHOR = "TEXT";
    public static final String LYRICIST = "TEXT";
    public static final String FILE_TYPE = "TFLT";
    public static final String TIME = "TIME";
    public static final String TIME_MINUTES_SECONDS = "TIME";
    public static final String SUPER_TITLE = "TIT1";
    public static final String TITLE = "TIT2";
    public static final String SUB_TITLE = "TIT3";
    public static final String INITIAL_KEY = "TKEY";
    public static final String LANGUAGE_LIST = "TLAN";
    public static final String LENGTH = "TLEN";
    public static final String TIME_MILLISECONDS = "TLEN";
    public static final String DURATION = "TLEN";
    public static final String MEDIA_TYPE = "TMED";
    public static final String ORIGINAL_ALBUM = "TOAL";
    public static final String ORIGINAL_FILENAME = "TOFN";
    public static final String ORIGINAL_AUTHOR = "TOLY";
    public static final String ORIGINAL_LYRICIST = "TOLY";
    public static final String ORIGINAL_ARTIST = "TOPE";
    public static final String ORIGINAL_PERFORMER = "TOPE";
    public static final String ORIGINAL_RELEASE_YEAR = "TORY";
    public static final String FILE_OWNER = "TOWN";
    public static final String LICENSEE = "TOWN";
    public static final String LEAD_PERFORMER = "TPE1";
    public static final String SOLOIST = "TPE1";
    public static final String ARTIST = "TPE1";
    public static final String BAND = "TPE2";
    public static final String ORCHESTRA = "TPE2";
    public static final String CONDUCTOR = "TPE3";
    public static final String REMIX = "TPE4";
    public static final String MODIFIED_BY = "TPE4";
    public static final String PART_OF_SET = "TPOS";
    public static final String PUBLISHER = "TPUB";
    public static final String TRACK_NUMBER = "TRCK";
    public static final String POSITION_IN_SET = "TRCK";
    public static final String RECORDING_DATES = "TRDA";
    public static final String INTERNET_RADIO_STATION_NAME = "TRSN";
    public static final String INTERNET_RADIO_STATION_OWNER = "TRSO";
    public static final String SIZE = "TSIZ";
    public static final String AUDIO_SIZE = "TSIZ";
    public static final String ISRC = "TSRC";
    public static final String INTERNATIONAL_STANDARD_RECORDING_CODE = "TSRC";
    public static final String ENCODER_SETTINGS = "TSSE";
    public static final String ENCODER_USED = "TSSE";
    public static final String YEAR = "TYER";
    public static final String USER_DEFINED_TEXT = "TXXX";
    public static final String UNIQUE_FILE_ID = "UFID";
    public static final String TERMS_OF_USE = "USER";
    public static final String UNSYNCHRONIZED_TEXT = "USLT";
    public static final String COMMERCIAL_INFORMATION = "WCOM";
    public static final String COPYRIGHT_INFORMATION = "WCOP";
    public static final String LEGAL_INFORMATION = "WCOP";
    public static final String OFFICIAL_FILE_PAGE = "WOAF";
    public static final String OFFICIAL_ARTIST_PAGE = "WOAR";
    public static final String OFFICIAL_AUDIO_SOURCE_PAGE = "WOAS";
    public static final String OFFICIAL_RADIO_STATION_PAGE = "WORS";
    public static final String PAYMENT = "WPAY";
    public static final String OFFICIAL_PUBLISHER_PAGE = "WPUB";
    public static final String USER_DEFINED_URL = "WXXX";
}
