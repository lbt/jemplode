package org.jempeg.tags.id3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.tags.FrameInfoExtract;
import org.jempeg.tags.Genre;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.ITagExtractorListener;
import org.jempeg.tags.RID;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.LimitedInputStream;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.model.MultiValueHashtable;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.Debug;
import com.inzyme.util.ReflectionUtils;

public class ID3TagExtractor implements ITagExtractor, ID3V2FrameListener {
  public static final boolean DEBUG = false;

  private static MultiValueHashtable FRAME_MAPPINGS;
  private static FrameIDLookup[] FRAME_LOOKUP;

  /** This loop seems a little inside-out.  We should be scooting along the
   * tags we've found, looking for the equivalent empeg tag.
   *
   * However, that leaves us with broken priorities.  So we will do it this
   * way.
   *
   * Note: If you extend the list of possible ID3V2 tags, check that you've:
   * (1) got a NULL at the end.
   * (2) not got more than MAX_MAPPINGS (including the NULL) in the list.
   */
  static {
    FRAME_MAPPINGS = new MultiValueHashtable();
    FRAME_MAPPINGS.putValues("title", new String[] {
        "TIT2",
        "1TIT",
        "0TIT"
    });
    FRAME_MAPPINGS.putValues("artist", new String[] {
        "TPE1",
        "TPE2",
        "TPE3",
        "TCOM",
        "1ART"
    });
    FRAME_MAPPINGS.putValues("source", new String[] {
        "TALB",
        "1ALB"
    });
    FRAME_MAPPINGS.putValues("year", new String[] {
        "TYER",
        "1YER"
    }); /* deprecated -- should use TDRC */
    FRAME_MAPPINGS.putValues("comment", new String[] {
        "COMM",
        "1COM"
    });
    FRAME_MAPPINGS.putValues("genre", new String[] {
        "TCON",
        "1CON"
    });
    FRAME_MAPPINGS.putValues("tracknr", new String[] {
        "TRCK",
        "1TRK",
        "1TNR"
    });
    FRAME_MAPPINGS.putValues("file_id", new String[] {
        "TRCK",
        "1TRK",
        "1TNR"
    }); // Intentional duplicate - keep in line with "tracknr"
    FRAME_MAPPINGS.putValues("subtitle", new String[] {
      "TIT3"
    });
    FRAME_MAPPINGS.putValues("composer", new String[] {
      "TCOM"
    });
    FRAME_MAPPINGS.putValues("conductor", new String[] {
      "TPE3"
    });
    FRAME_MAPPINGS.putValues("remixed", new String[] {
      "TPE4"
    });
    FRAME_MAPPINGS.putValues("partofset", new String[] {
      "TPOS"
    });
    FRAME_MAPPINGS.putValues("partname", new String[] {
      "TSST"
    });
    FRAME_MAPPINGS.putValues("isrc", new String[] {
      "TSRC"
    });
    FRAME_MAPPINGS.putValues("originalartist", new String[] {
      "TOPE"
    });
    FRAME_MAPPINGS.putValues("lyricist", new String[] {
      "TEXT"
    });
    FRAME_MAPPINGS.putValues("bpm", new String[] {
      "TBPM"
    });
    FRAME_MAPPINGS.putValues("key", new String[] {
      "TKEY"
    });
    FRAME_MAPPINGS.putValues("language", new String[] {
      "TLAN"
    });
    FRAME_MAPPINGS.putValues("sourcemedia", new String[] {
      "TMED"
    });
    FRAME_MAPPINGS.putValues("mood", new String[] {
      "TMOO"
    });
    FRAME_MAPPINGS.putValues("sourcesort", new String[] {
      "TSOA"
    });
    FRAME_MAPPINGS.putValues("artistsort", new String[] {
      "TSOP"
    });
    FRAME_MAPPINGS.putValues("titlesort", new String[] {
      "TSOT"
    });

    FRAME_LOOKUP = new FrameIDLookup[] {
        new FrameIDLookup("AENC", "CRA", "Audio encryption", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("APIC", "PIC", "Attached picture", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("ASPI", "", "Audio seek point index", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("COMM", "COM", "Comments", FrameIDLookup.TYPE_COMMENT),
        new FrameIDLookup("COMR", "", "Commercial frame", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("ENCR", "", "Encryption method registration", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("EQUA", "EQU", "Equalisation", FrameIDLookup.TYPE_UNKNOWN), // (deprecated in ID3v2.4.0)
        new FrameIDLookup("EQU2", "", "Equalisation (2)", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("ETCO", "ETC", "Event timing codes", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("GEOB", "GEO", "General encapsulated object", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("GRID", "", "Group identification registration", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("IPLS", "IPL", "Involved people list", FrameIDLookup.TYPE_UNKNOWN), // (deprecated in ID3v2.4.0)

        new FrameIDLookup("LINK", "LNK", "Linked information", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("MCDI", "MCI", "Music CD identifier", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("MLLT", "MLL", "MPEG location lookup table", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("OWNE", "", "Ownership frame", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("PRIV", "", "Private frame", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("PCNT", "CNT", "Play counter", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("POPM", "POP", "Popularimeter", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("POSS", "", "Position synchronisation frame", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("RBUF", "BUF", "Recommended buffer size", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("RVAD", "RVA", "Relative volume adjustment", FrameIDLookup.TYPE_UNKNOWN), // (deprecated in ID3v2.4.0)
        new FrameIDLookup("RVA2", "", "Relative volume adjustment (2)", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("RVRB", "REV", "Reverb", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("SEEK", "", "Seek frame", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("SIGN", "", "Signature frame", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("SYLT", "SLT", "Synchronised lyric/text", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("SYTC", "STC", "Synchronised tempo codes", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("TALB", "TAL", "Album/Movie/Show title", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TBPM", "TBP", "BPM (beats per minute)", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TCOM", "TCM", "Composer", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TCON", "TCO", "Content type", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TCOP", "TCR", "Copyright message", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TDAT", "", "Date", FrameIDLookup.TYPE_TEXT), // (deprecated in ID3v2.4.0)
        new FrameIDLookup("TDEN", "", "Encoding time", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TDLY", "TDY", "Playlist delay", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TDOR", "", "Original release time", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TDRC", "", "Recording time", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TDRL", "", "Release time", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TDTG", "", "Tagging time", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TENC", "TEN", "Encoded by", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TEXT", "TXT", "Lyricist/Text writer", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TFLT", "TFT", "File type", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TIME", "TIM", "Time", FrameIDLookup.TYPE_TEXT), // (deprecated in ID3v2.4.0)
        new FrameIDLookup("TIPL", "", "Involved people list", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TIT1", "TT1", "Content group description", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TIT2", "TT2", "Title/songname/content description", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TIT3", "TT3", "Subtitle/Description refinement", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TKEY", "TKE", "Initial key", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TLAN", "TLA", "Language(s)", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TLEN", "TLE", "Length", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TMCL", "", "Musician credits list", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TMED", "TMT", "Media type", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TMOO", "", "Mood", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TOAL", "TOT", "Original album/movie/show title", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TOFN", "TOF", "Original filename", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TOLY", "TOL", "Original lyricist(s)/text writer(s)", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TOPE", "TOA", "Original artist(s)/performer(s)", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TORY", "TOR", "Original release year", FrameIDLookup.TYPE_TEXT),
        // (deprecated in ID3v2.4.0)
        new FrameIDLookup("TOWN", "", "File owner/licensee", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPE1", "TP1", "Lead performer(s)/Soloist(s)", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPE2", "TP2", "Band/orchestra/accompaniment", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPE3", "TP3", "Conductor/performer refinement", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPE4", "TP4", "Interpreted, remixed, or otherwise modified by", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPOS", "TPA", "Part of a set", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPRO", "", "Produced notice", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TPUB", "TPB", "Publisher", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TRCK", "TRK", "Track number/Position in set", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TRDA", "TRD", "Recording dates", FrameIDLookup.TYPE_TEXT), // (deprecated in ID3v2.4.0)
        new FrameIDLookup("TRSN", "", "Internet radio station name", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TRSO", "", "Internet radio station owner", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TSIZ", "TSI", "Size", FrameIDLookup.TYPE_TEXT), // (deprecated in ID3v2.4.0)
        new FrameIDLookup("TSOA", "", "Album sort order", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TSOP", "", "Performer sort order", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TSOT", "", "Title sort order", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TSRC", "TRC", "ISRC (international standard recording code)", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TSSE", "TSS", "Software/Hardware and settings used for encoding", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TSST", "", "Set subtitle", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TXXX", "TXX", "User defined text information frame", FrameIDLookup.TYPE_TEXT),
        new FrameIDLookup("TYER", "TYE", "Year", FrameIDLookup.TYPE_TEXT), // (deprecated in ID3v2.4.0)

        new FrameIDLookup("UFID", "UFI", "Unique file identifier", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("USER", "", "Terms of use", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("USLT", "ULT", "Unsynchronised lyric/text transcription", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WCOM", "WCM", "Commercial information", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WCOP", "WCP", "Copyright/Legal information", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WOAF", "WAF", "Official audio file webpage", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WOAR", "WAR", "Official artist/performer webpage", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WOAS", "WAS", "Official audio source webpage", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WORS", "", "Official Internet radio station homepage", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WPAY", "", "Payment", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WPUB", "WPB", "Publishers official webpage", FrameIDLookup.TYPE_UNKNOWN),
        new FrameIDLookup("WXXX", "WXX", "User defined URL link frame", FrameIDLookup.TYPE_UNKNOWN)
    };
  }

  private String myFilename;
  private CollectFrames myCollect;
  private ID3V2Tag myID3V2Tag;
  private ID3V1Tag myID3V1Tag;

  public ID3TagExtractor() {
  }

  public static boolean isID3Tag(byte[] _header) {
    return (_header[0] == 'I' && _header[1] == 'D' && _header[2] == '3');
  }

  public int isTagExtractorFor(String _name, byte[] _header) {
    int isTagExtractor;
    if (isID3Tag(_header)) {
      isTagExtractor = ITagExtractor.MAYBE;
    }
    else if ((_header[0] & 0xFF) == 0xFF && (_header[1] & 0xF0) == 0xF0) {
      if ((_header[1] & 0x08) == 0x08) {
        if ((_header[1] & 0x02) == 0x02) {
          isTagExtractor = ITagExtractor.YES;
        }
        else if ((_header[1] & 0x04) == 0x04) {
          isTagExtractor = ITagExtractor.YES;
        }
        else {
          isTagExtractor = ITagExtractor.NO;
        }
      }
      else if ((_header[1] & 0x08) == 0x00) {
        isTagExtractor = ITagExtractor.YES; // Is MPEG 2.0 the same as MP2, or is that MPEG 1 Layer 2?
      }
      else {
        isTagExtractor = ITagExtractor.NO;
      }
    }
    else {
      isTagExtractor = ITagExtractor.NO;
    }

    if (_name.toLowerCase().endsWith(".mp3")) {
      isTagExtractor = ITagExtractor.YES;
    }

    return isTagExtractor;
  }

  public static FrameIDLookup getFrameLookupByFourCC(String _frameID) {
    for (int i = 0; i < ID3TagExtractor.FRAME_LOOKUP.length; i ++ ) {
      FrameIDLookup lookup = ID3TagExtractor.FRAME_LOOKUP[i];
      if (_frameID.equals(lookup.myFrameID)) {
        return lookup;
      }
    }
    return null;
  }

  public static FrameIDLookup getFrameLookupByThreeCC(String _frameID) {
    for (int i = 0; i < ID3TagExtractor.FRAME_LOOKUP.length; i ++ ) {
      FrameIDLookup lookup = ID3TagExtractor.FRAME_LOOKUP[i];
      if (_frameID.equals(lookup.myFrameThreeCC)) {
        return lookup;
      }
    }
    return null;
  }

  public ID3V1Tag getID3V1Tag() {
    return myID3V1Tag;
  }

  public ID3V2Tag getID3V2Tag() {
    return myID3V2Tag;
  }

  public void onFrame(SeekableInputStream _stream, ID3V2Frame _frame) throws IOException {
    String frameID = _frame.getFrameID().getStringValue(StringUtils.ISO_8859_1);

    // Based on the frame ID, dispatch it to another function.
    FrameIDLookup lookup = ID3TagExtractor.getFrameLookupByFourCC(frameID);

    if (DEBUG) {
      Debug.println(Debug.INFORMATIVE, "Frame ID: " + frameID);
      if (lookup != null) {
        Debug.println(Debug.INFORMATIVE, "Frame Desc: " + lookup.myFrameDescription);
      }
      Debug.println(Debug.INFORMATIVE, "Frame Size: " + _frame.getSize());
      Debug.println(Debug.INFORMATIVE, "Frame Flags: " + _frame.getFlags());
      Debug.println(Debug.INFORMATIVE, "");
    }

    if (lookup != null) {
      lookup.onFrame(_stream, _frame, myCollect);
    }
  }

  public void extractTags(IImportFile _file, ITagExtractorListener _listener) throws IOException {
    myFilename = _file.getName();
    SeekableInputStream is = _file.getSeekableInputStream();
    try {
      extractTags(is, _listener);
    }
    finally {
      is.close();
    }
  }

  /** Create something that'll get kicked with each tag as it's found.
   * Once we've collected them all, we can sift through looking for the interesting ones.
   */
  public void extractTags(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException {
    TagOffsets offsets = extractTagsWithoutFrameInfo(_is, _listener);

    FrameInfoExtract extract = new FrameInfoExtract(_is, _listener);
    extract.extract(offsets.getOffset());
  }

  public TagOffsets extractTagsWithoutFrameInfo(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException {
    if (myFilename == null) {
      myFilename = "";
    }

    _listener.tagExtracted(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_TUNE);
    _listener.tagExtracted(DatabaseTags.CODEC_TAG, DatabaseTags.CODEC_MP3);

    myCollect = new CollectFrames();

    // Prime it with a default title...
    myCollect.onTextFrame("0TIT", myFilename);

    ID3V1Tag id3v1Tag = new ID3V1Tag();
    int trailer = extractV1Tags(_is, myCollect, id3v1Tag);
    if (trailer > 0) {
      myID3V1Tag = id3v1Tag;
    }

    long length = _is.length();
    // Even if we don't parse Lyrics frames we should exclude them from the rid
    trailer = checkLyrics(_is, length, trailer);

    ID3V2Tag id3v2Tag = new ID3V2Tag();
    int offset = ID3V2TagParser.parse(_is, this, id3v2Tag);
    if (offset > 0) {
      myID3V2Tag = id3v2Tag;
    }

    _listener.tagExtracted(DatabaseTags.OFFSET_TAG, offset);
    _listener.tagExtracted(DatabaseTags.TRAILER_TAG, trailer);

    Enumeration tagNamesEnum = ID3TagExtractor.FRAME_MAPPINGS.keys();
    while (tagNamesEnum.hasMoreElements()) {
      String tagName = (String) tagNamesEnum.nextElement();
      Object id3NameObj = ID3TagExtractor.FRAME_MAPPINGS.get(tagName);
      if (id3NameObj instanceof String) {
        extractTag(_listener, tagName, (String) id3NameObj);
      }
      else if (id3NameObj != null) {
        boolean extracted = false;
        Enumeration id3NamesEnum = ((Vector) id3NameObj).elements();
        while (!extracted && id3NamesEnum.hasMoreElements()) {
          String id3Name = (String) id3NamesEnum.nextElement();
          extracted = extractTag(_listener, tagName, id3Name);
        }
      }
    }

    // Now calculate the unique ID
    String rid = extractRidSpecific(_is, length, offset, trailer);
    _listener.tagExtracted(DatabaseTags.RID_TAG, rid);

    TagOffsets offsets = new TagOffsets(offset, trailer);
    return offsets;
  }

  private boolean extractTag(ITagExtractorListener _listener, String _tagName, String _id3Name) {
    String value = myCollect.getProperty(_id3Name);
    if (value != null) {
      _listener.tagExtracted(_tagName, value);
      return true;
    }
    else {
      return false;
    }
  }

  public String extractRidSpecific(SeekableInputStream _is, long _length, long _offset, long _trailer) throws IOException {
    return RID.calculateRid(_is, _offset, _length - _trailer);
  }

  public int extractV1Tags(ID3V1Tag _tag, CollectFrames _collect) throws IOException {
    if (!"TAG".equals(_tag.getSignature().getTrimmedStringValue(StringUtils.ISO_8859_1))) { // Doesn't look like ID3V1 tag
      if (DEBUG) {
        Debug.println(Debug.INFORMATIVE, "Tag does not contain ID3V1 data.");
      }
      return 0;
    }

    int trailer = _tag.getLength();

    // May only be interested in trailer value
    if (_collect == null) {
      return 0;
    }

    String title = _tag.getTitle().getTrimmedStringValue(StringUtils.DEFAULT_ENCODING);
    if (title.length() > 0) {
      _collect.onTextFrame("1TIT", title);
    }

    String artist = _tag.getArtist().getTrimmedStringValue(StringUtils.DEFAULT_ENCODING);
    if (artist.length() > 0) {
      _collect.onTextFrame("1ART", artist);
    }

    String album = _tag.getAlbum().getTrimmedStringValue(StringUtils.DEFAULT_ENCODING);
    if (album.length() > 0) {
      _collect.onTextFrame("1ALB", album);
    }

    String year = _tag.getYear().getTrimmedStringValue(StringUtils.DEFAULT_ENCODING);
    if (year.length() > 0) {
      _collect.onTextFrame("1YER", year);
    }

    boolean skipTrackNumber = false;
    /* ID3v1.1 states that comment[28] should be zero, and comment[29] should be the track number.
     * If comment[28] isn't zero, we'll assume that we don't have a ID3v1.1 tag.
     */
    byte[] commentBytes = _tag.getComment().getValue();
    if (commentBytes[28] == 0 && commentBytes[29] != 0) {
      _collect.onTextFrame("1TNR", String.valueOf(commentBytes[29]));
      skipTrackNumber = true;
    }

    String comment = _tag.getComment().getTrimmedStringValue(StringUtils.DEFAULT_ENCODING, (skipTrackNumber) ? 1 : 0);
    if (comment.length() > 0) {
      _collect.onTextFrame("1COM", comment);
    }

    String genre = Genre.getGenre(_tag.getGenre().getValue());
    if (genre.length() > 0) {
      _collect.onTextFrame("1CON", genre);
    }

    myID3V1Tag = _tag;

    return trailer;
  }

  public int extractV1Tags(SeekableInputStream _is, CollectFrames _collect, ID3V1Tag _id3v1Tag) throws IOException {
    // Go looking for ID3v1 tags...
    long fileSize = _is.length();

    if (fileSize < _id3v1Tag.getLength()) {
      throw new IOException("Stream is too short to contain an ID3V1 Tag.");
    }

    _is.seek(fileSize - _id3v1Tag.getLength());

    _id3v1Tag.read(new LittleEndianInputStream(_is));

    return extractV1Tags(_id3v1Tag, _collect);
  }

  public int checkLyrics(SeekableInputStream _is, long _length, int _trailer) throws IOException {
    if ((_length - _trailer) < 15) {
      return _trailer;
    }

    _is.seek(_length - _trailer - 15);
    byte[] bytez = new byte[15];
    _is.readFully(bytez, 0, 15);
    if (!"LYRICS".equals(new String(bytez, 6, 6))) {
      return _trailer;
    }

    if (DEBUG) {
      Debug.println(Debug.WARNING, "File contains (uninterpreted) Lyrics tag.");
    }
    int lyricsLength = Integer.parseInt(new String(bytez, 0, 6));

    if (_trailer + lyricsLength + 15 >= _length) {
      return _trailer;
    }

    long newPos = _length - _trailer - lyricsLength - 15;
    _is.seek(newPos);
    _is.readFully(bytez, 0, 11);
    if (!"LYRICSBEGIN".equals(new String(bytez, 0, 11))) {
      if (DEBUG) {
        Debug.println(Debug.WARNING, "Lyrics tag corrupt.");
      }
      return _trailer;
    }
    if (DEBUG) {
      Debug.println(Debug.INFORMATIVE, lyricsLength + " bytes of Lyrics3 confirmed.");
    }
    return (_trailer + lyricsLength);
  }

  private static String getPreferredISO8859Encoding() {
    return PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.TREAT_ISO_AS_DEFAULT_ENCODING_KEY) ? StringUtils.DEFAULT_ENCODING : StringUtils.ISO_8859_1;
  }

  protected static class CollectFrames extends Properties {
    public void onTextFrame(SeekableInputStream _stream, ID3V2Frame _frameHeader) throws IOException {
      String frameID = _frameHeader.getFrameID().getStringValue(StringUtils.ISO_8859_1);

      long frameSize = _frameHeader.getSize();

      // It's a text frame...
      if (frameSize < 1) {
        throw new IOException("Short frame!");
      }

      // It's a text frame, so we can treat it as a char array.
      LimitedInputStream lis = new LimitedInputStream(_stream, frameSize);
      int textEncoding = lis.read();

      if (DEBUG) {
        Debug.println(Debug.INFORMATIVE, "Text encoding: " + textEncoding + " (" + ID3V2Tag.describeTextEncoding(textEncoding) + ")");
      }
      
      // Decode the text frame, and add it to the list.
      int available = lis.available();
      if (textEncoding == ID3V2Tag.ID3V2_TEXT_ENCODING_ISO8859_1) {
        String s = readString(lis, available, ID3TagExtractor.getPreferredISO8859Encoding());
        if (s.length() > 0) {
          onTextFrame(frameID, s);
        }
      }
      else if (textEncoding == ID3V2Tag.ID3V2_TEXT_ENCODING_UNICODE) {
        String s = readString(lis, available, StringUtils.UNICODE_LITTLE);
        if (s.length() > 0) {
          onTextFrame(frameID, s);
        }
      }
      else if (textEncoding == ID3V2Tag.ID3V2_TEXT_ENCODING_UNICODE_BE) {
        String s = readString(lis, available, StringUtils.UNICODE_BIG);
        if (s.length() > 0) {
          onTextFrame(frameID, s);
        }
      }
      else if (textEncoding == ID3V2Tag.ID3V2_TEXT_ENCODING_UTF8) {
        String s = readString(lis, available, StringUtils.UTF8);
        if (s.length() > 0) {
          onTextFrame(frameID, s);
        }
      }
      else {
        throw new IOException("Unknown Text Encoding!");
      }
    }

    public void onTextFrame(String _frameID, String _utf8FrameData) throws IOException {
      //                      System.out.println(_frameID + " : " + _utf8FrameData);

      /// TODO: Fix this icky special-case for genre.
      if ("TCON".equals(_frameID)) {
        String genre = Genre.getGenre(_utf8FrameData);
        if (genre.length() > 0) {

          put(_frameID, genre);
        }
      }
      else if ("1YER".equals(_frameID)) { // We don't like zero years - so check before adding
        try {
          int year = Integer.parseInt(_utf8FrameData);
          if (year > 0) {
            put(_frameID, _utf8FrameData);
          }
        }
        catch (NumberFormatException e) {
          // that's ok .. ignore the year
        }
      }
      else {
        put(_frameID, _utf8FrameData);
      }
    }

    public void onCommentFrame(SeekableInputStream _stream, ID3V2Frame _frameHeader) throws IOException {
      String frameID = _frameHeader.getFrameID().getStringValue(StringUtils.ISO_8859_1);

      long frameSize = _frameHeader.getSize();

      if (frameSize < 5) {
        throw new IOException("Short frame!");
      }

      // It's a text frame, so we'll treat it as a char array.
      //                      byte[] buffer = new byte[_frameSize];
      //                      _stream.readFully(_buffer);

      LimitedInputStream lis = new LimitedInputStream(_stream, frameSize);
      int textEncoding = lis.read();

      if (DEBUG) {
        Debug.println(Debug.INFORMATIVE, "Text encoding: " + textEncoding + " (" + ID3V2Tag.describeTextEncoding(textEncoding) + ")");
      }

      /** @todo I've no idea what format the language-encoding is in.
       * This reads it as 3 little-endian bytes.
       */
      int language = 0;
      language = lis.read();
      language |= (lis.read()) << 8;
      language |= (lis.read()) << 16;

      // Immediately after that comes a "short content description".  We'll ignore it.
      // It's zero-terminated.
      while (lis.available() > 0) {
        int value = lis.read();
        if (value == 0) {
          break;
        }
      }

      // The rest of the data is the comment itself.
      int available = lis.available();
      if (available > 0) {
        String comment = readString(lis, available, ID3TagExtractor.getPreferredISO8859Encoding());
        onTextFrame(frameID, comment);
      }
    }

    protected String readString(InputStream _is, int _length, String _encoding) throws IOException {
      byte[] strBytes = new byte[_length];
      int pos = 0;
      while (pos < _length) {
        pos += _is.read(strBytes, pos, _length - pos);
      }
      String str = deUnsynchronize(strBytes, _encoding);
      return str;
    }

    public void onUnknownFrame(SeekableInputStream _stream, ID3V2Frame _frameHeader) throws IOException {
      String frameID = _frameHeader.getFrameID().getStringValue(StringUtils.ISO_8859_1);

      if (false) {
        long frameSize = _frameHeader.getSize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) frameSize); // TODO: Loss of precision
        LittleEndianOutputStream leos = new LittleEndianOutputStream(baos);
        // TODO: +1? const BYTE *data = reinterpret_cast<const BYTE *>(frameHeader + 1);
        _frameHeader.write(leos);
        onUnknownFrame(frameID, new ByteArrayInputStream(baos.toByteArray()), frameSize);
      }
    }

    public void onUnknownFrame(String _frameID, InputStream _data, long _frameSize) throws IOException {
      // System.out.println(_frameID + " : " + _frameSize);
      //                      Debug.printHex(_data);
    }
  }

  public static String deUnsynchronize(byte[] _bytes, String _encoding) throws UnsupportedEncodingException {
    int readIndex = 0;
    int writeIndex = 0;
    while (readIndex < _bytes.length) {
      _bytes[writeIndex ++] = _bytes[readIndex];
      if (_bytes[readIndex] == -1 && _bytes[readIndex + 1] == 0) {
        readIndex += 2;
      }
      else {
        readIndex ++;
      }
    }
    while (writeIndex < _bytes.length) {
      _bytes[writeIndex ++] = 0;
    }
    String str = StringUtils.parseString(_bytes, _encoding, ID3TagExtractor.getPreferredISO8859Encoding());
    return str;
  }

  public static class TagOffsets {
    private int myOffset;
    private int myTrailer;

    public TagOffsets(int _offset, int _trailer) {
      myOffset = _offset;
      myTrailer = _trailer;
    }

    public int getOffset() {
      return myOffset;
    }

    public int getTrailer() {
      return myTrailer;
    }

    public String toString() {
      return ReflectionUtils.toString(this);
    }
  }

  public static class FrameIDLookup {
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_COMMENT = 2;
    public String myFrameID;
    public String myFrameThreeCC;
    public String myFrameDescription;
    public int myType;

    public FrameIDLookup(String _frameID, String _frameThreeCC, String _frameDescription, int _type) {
      myFrameID = _frameID;
      myFrameThreeCC = _frameThreeCC;
      myFrameDescription = _frameDescription;
      myType = _type;
    }

    public void onFrame(SeekableInputStream _is, ID3V2Frame _frameHeader, CollectFrames _collect) throws IOException {
      switch (myType) {
        case FrameIDLookup.TYPE_UNKNOWN:
          _collect.onUnknownFrame(_is, _frameHeader);
          break;
        case FrameIDLookup.TYPE_TEXT:
          _collect.onTextFrame(_is, _frameHeader);
          break;
        case FrameIDLookup.TYPE_COMMENT:
          _collect.onCommentFrame(_is, _frameHeader);
          break;
        default:
          throw new IllegalArgumentException("Unknown type: " + myType + ".");
      }
    }

    public String toString() {
      return "[FrameIDLookup: id = " + myFrameID + "; threecc = " + myFrameThreeCC + "; description = " + myFrameDescription + "]";
    }
  }
}