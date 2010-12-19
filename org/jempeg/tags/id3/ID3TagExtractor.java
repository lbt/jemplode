/* ID3TagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags.id3;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.LimitedInputStream;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.model.MultiValueHashtable;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.ReflectionUtils;

import org.jempeg.tags.FrameInfoExtract;
import org.jempeg.tags.Genre;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.ITagExtractorListener;
import org.jempeg.tags.RID;

public class ID3TagExtractor implements ITagExtractor, ID3V2FrameListener
{
    public static final boolean DEBUG = false;
    private static MultiValueHashtable FRAME_MAPPINGS
	= new MultiValueHashtable();
    private static FrameIDLookup[] FRAME_LOOKUP;
    private String myFilename;
    private CollectFrames myCollect;
    private ID3V2Tag myID3V2Tag;
    private ID3V1Tag myID3V1Tag;
    
    protected static class CollectFrames extends Properties
    {
	public void onTextFrame(SeekableInputStream _stream,
				ID3V2Frame _frameHeader) throws IOException {
	    String frameID
		= _frameHeader.getFrameID().getStringValue("ISO-8859-1");
	    long frameSize = _frameHeader.getSize();
	    if (frameSize < 1L)
		throw new IOException("Short frame!");
	    LimitedInputStream lis
		= new LimitedInputStream(_stream, frameSize);
	    int textEncoding = lis.read();
	    int available = lis.available();
	    if (textEncoding == 0) {
		String s = readString(lis, available,
				      getPreferredISO8859Encoding());
		if (s.length() > 0)
		    onTextFrame(frameID, s);
	    } else if (textEncoding == 1) {
		String s = readString(lis, available, "UTF-16LE");
		if (s.length() > 0)
		    onTextFrame(frameID, s);
	    } else if (textEncoding == 2) {
		String s = readString(lis, available, "UTF-16BE");
		if (s.length() > 0)
		    onTextFrame(frameID, s);
	    } else if (textEncoding == 3) {
		String s = readString(lis, available, "UTF8");
		if (s.length() > 0)
		    onTextFrame(frameID, s);
	    } else
		throw new IOException("Unknown Text Encoding!");
	}
	
	public void onTextFrame(String _frameID, String _utf8FrameData)
	    throws IOException {
	    do {
		if ("TCON".equals(_frameID)) {
		    String genre = Genre.getGenre(_utf8FrameData);
		    if (genre.length() > 0)
			put(_frameID, genre);
		} else {
		    if ("1YER".equals(_frameID)) {
			try {
			    int year = Integer.parseInt(_utf8FrameData);
			    if (year > 0)
				put(_frameID, _utf8FrameData);
			    break;
			} catch (NumberFormatException numberformatexception) {
			    break;
			}
		    }
		    put(_frameID, _utf8FrameData);
		}
	    } while (false);
	}
	
	public void onCommentFrame
	    (SeekableInputStream _stream, ID3V2Frame _frameHeader)
	    throws IOException {
	    String frameID
		= _frameHeader.getFrameID().getStringValue("ISO-8859-1");
	    long frameSize = _frameHeader.getSize();
	    if (frameSize < 5L)
		throw new IOException("Short frame!");
	    LimitedInputStream lis
		= new LimitedInputStream(_stream, frameSize);
	    int textEncoding = lis.read();
	    int language = 0;
	    language = lis.read();
	    language |= lis.read() << 8;
	    language |= lis.read() << 16;
	    while (lis.available() > 0) {
		int value = lis.read();
		if (value == 0)
		    break;
	    }
	    int available = lis.available();
	    if (available > 0) {
		String comment = readString(lis, available,
					    getPreferredISO8859Encoding());
		onTextFrame(frameID, comment);
	    }
	}
	
	protected String readString(InputStream _is, int _length,
				    String _encoding) throws IOException {
	    byte[] strBytes = new byte[_length];
	    for (int pos = 0; pos < _length;
		 pos += _is.read(strBytes, pos, _length - pos)) {
		/* empty */
	    }
	    String str = deUnsynchronize(strBytes, _encoding);
	    return str;
	}
	
	public void onUnknownFrame
	    (SeekableInputStream _stream, ID3V2Frame _frameHeader)
	    throws IOException {
	    String frameID
		= _frameHeader.getFrameID().getStringValue("ISO-8859-1");
	}
	
	public void onUnknownFrame(String _frameID, InputStream _data,
				   long _frameSize) throws IOException {
	    /* empty */
	}
    }
    
    public static class TagOffsets
    {
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
    
    public static class FrameIDLookup
    {
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_COMMENT = 2;
	public String myFrameID;
	public String myFrameThreeCC;
	public String myFrameDescription;
	public int myType;
	
	public FrameIDLookup(String _frameID, String _frameThreeCC,
			     String _frameDescription, int _type) {
	    myFrameID = _frameID;
	    myFrameThreeCC = _frameThreeCC;
	    myFrameDescription = _frameDescription;
	    myType = _type;
	}
	
	public void onFrame(SeekableInputStream _is, ID3V2Frame _frameHeader,
			    CollectFrames _collect) throws IOException {
	    switch (myType) {
	    case 0:
		_collect.onUnknownFrame(_is, _frameHeader);
		break;
	    case 1:
		_collect.onTextFrame(_is, _frameHeader);
		break;
	    case 2:
		_collect.onCommentFrame(_is, _frameHeader);
		break;
	    default:
		throw new IllegalArgumentException("Unknown type: " + myType
						   + ".");
	    }
	}
	
	public String toString() {
	    return ("[FrameIDLookup: id = " + myFrameID + "; threecc = "
		    + myFrameThreeCC + "; description = " + myFrameDescription
		    + "]");
	}
    }
    
    static {
	FRAME_MAPPINGS.putValues("title",
				 new String[] { "TIT2", "1TIT", "0TIT" });
	FRAME_MAPPINGS.putValues("artist",
				 new String[] { "TPE1", "TPE2", "TPE3", "TCOM",
						"1ART" });
	FRAME_MAPPINGS.putValues("source", new String[] { "TALB", "1ALB" });
	FRAME_MAPPINGS.putValues("year", new String[] { "TYER", "1YER" });
	FRAME_MAPPINGS.putValues("comment", new String[] { "COMM", "1COM" });
	FRAME_MAPPINGS.putValues("genre", new String[] { "TCON", "1CON" });
	FRAME_MAPPINGS.putValues("tracknr",
				 new String[] { "TRCK", "1TRK", "1TNR" });
	FRAME_MAPPINGS.putValues("file_id",
				 new String[] { "TRCK", "1TRK", "1TNR" });
	FRAME_MAPPINGS.putValues("subtitle", new String[] { "TIT3" });
	FRAME_MAPPINGS.putValues("composer", new String[] { "TCOM" });
	FRAME_MAPPINGS.putValues("conductor", new String[] { "TPE3" });
	FRAME_MAPPINGS.putValues("remixed", new String[] { "TPE4" });
	FRAME_MAPPINGS.putValues("partofset", new String[] { "TPOS" });
	FRAME_MAPPINGS.putValues("partname", new String[] { "TSST" });
	FRAME_MAPPINGS.putValues("isrc", new String[] { "TSRC" });
	FRAME_MAPPINGS.putValues("originalartist", new String[] { "TOPE" });
	FRAME_MAPPINGS.putValues("lyricist", new String[] { "TEXT" });
	FRAME_MAPPINGS.putValues("bpm", new String[] { "TBPM" });
	FRAME_MAPPINGS.putValues("key", new String[] { "TKEY" });
	FRAME_MAPPINGS.putValues("language", new String[] { "TLAN" });
	FRAME_MAPPINGS.putValues("sourcemedia", new String[] { "TMED" });
	FRAME_MAPPINGS.putValues("mood", new String[] { "TMOO" });
	FRAME_MAPPINGS.putValues("sourcesort", new String[] { "TSOA" });
	FRAME_MAPPINGS.putValues("artistsort", new String[] { "TSOP" });
	FRAME_MAPPINGS.putValues("titlesort", new String[] { "TSOT" });
	FRAME_LOOKUP
	    = (new FrameIDLookup[]
	       { new FrameIDLookup("AENC", "CRA", "Audio encryption", 0),
		 new FrameIDLookup("APIC", "PIC", "Attached picture", 0),
		 new FrameIDLookup("ASPI", "", "Audio seek point index", 0),
		 new FrameIDLookup("COMM", "COM", "Comments", 2),
		 new FrameIDLookup("COMR", "", "Commercial frame", 0),
		 new FrameIDLookup("ENCR", "",
				   "Encryption method registration", 0),
		 new FrameIDLookup("EQUA", "EQU", "Equalisation", 0),
		 new FrameIDLookup("EQU2", "", "Equalisation (2)", 0),
		 new FrameIDLookup("ETCO", "ETC", "Event timing codes", 0),
		 new FrameIDLookup("GEOB", "GEO",
				   "General encapsulated object", 0),
		 new FrameIDLookup("GRID", "",
				   "Group identification registration", 0),
		 new FrameIDLookup("IPLS", "IPL", "Involved people list", 0),
		 new FrameIDLookup("LINK", "LNK", "Linked information", 0),
		 new FrameIDLookup("MCDI", "MCI", "Music CD identifier", 0),
		 new FrameIDLookup("MLLT", "MLL", "MPEG location lookup table",
				   0),
		 new FrameIDLookup("OWNE", "", "Ownership frame", 0),
		 new FrameIDLookup("PRIV", "", "Private frame", 0),
		 new FrameIDLookup("PCNT", "CNT", "Play counter", 0),
		 new FrameIDLookup("POPM", "POP", "Popularimeter", 0),
		 new FrameIDLookup("POSS", "",
				   "Position synchronisation frame", 0),
		 new FrameIDLookup("RBUF", "BUF", "Recommended buffer size",
				   0),
		 new FrameIDLookup("RVAD", "RVA", "Relative volume adjustment",
				   0),
		 new FrameIDLookup("RVA2", "",
				   "Relative volume adjustment (2)", 0),
		 new FrameIDLookup("RVRB", "REV", "Reverb", 0),
		 new FrameIDLookup("SEEK", "", "Seek frame", 0),
		 new FrameIDLookup("SIGN", "", "Signature frame", 0),
		 new FrameIDLookup("SYLT", "SLT", "Synchronised lyric/text",
				   0),
		 new FrameIDLookup("SYTC", "STC", "Synchronised tempo codes",
				   0),
		 new FrameIDLookup("TALB", "TAL", "Album/Movie/Show title", 1),
		 new FrameIDLookup("TBPM", "TBP", "BPM (beats per minute)", 1),
		 new FrameIDLookup("TCOM", "TCM", "Composer", 1),
		 new FrameIDLookup("TCON", "TCO", "Content type", 1),
		 new FrameIDLookup("TCOP", "TCR", "Copyright message", 1),
		 new FrameIDLookup("TDAT", "", "Date", 1),
		 new FrameIDLookup("TDEN", "", "Encoding time", 1),
		 new FrameIDLookup("TDLY", "TDY", "Playlist delay", 1),
		 new FrameIDLookup("TDOR", "", "Original release time", 1),
		 new FrameIDLookup("TDRC", "", "Recording time", 1),
		 new FrameIDLookup("TDRL", "", "Release time", 1),
		 new FrameIDLookup("TDTG", "", "Tagging time", 1),
		 new FrameIDLookup("TENC", "TEN", "Encoded by", 1),
		 new FrameIDLookup("TEXT", "TXT", "Lyricist/Text writer", 1),
		 new FrameIDLookup("TFLT", "TFT", "File type", 1),
		 new FrameIDLookup("TIME", "TIM", "Time", 1),
		 new FrameIDLookup("TIPL", "", "Involved people list", 1),
		 new FrameIDLookup("TIT1", "TT1", "Content group description",
				   1),
		 new FrameIDLookup("TIT2", "TT2",
				   "Title/songname/content description", 1),
		 new FrameIDLookup("TIT3", "TT3",
				   "Subtitle/Description refinement", 1),
		 new FrameIDLookup("TKEY", "TKE", "Initial key", 1),
		 new FrameIDLookup("TLAN", "TLA", "Language(s)", 1),
		 new FrameIDLookup("TLEN", "TLE", "Length", 1),
		 new FrameIDLookup("TMCL", "", "Musician credits list", 1),
		 new FrameIDLookup("TMED", "TMT", "Media type", 1),
		 new FrameIDLookup("TMOO", "", "Mood", 1),
		 new FrameIDLookup("TOAL", "TOT",
				   "Original album/movie/show title", 1),
		 new FrameIDLookup("TOFN", "TOF", "Original filename", 1),
		 new FrameIDLookup("TOLY", "TOL",
				   "Original lyricist(s)/text writer(s)", 1),
		 new FrameIDLookup("TOPE", "TOA",
				   "Original artist(s)/performer(s)", 1),
		 new FrameIDLookup("TORY", "TOR", "Original release year", 1),
		 new FrameIDLookup("TOWN", "", "File owner/licensee", 1),
		 new FrameIDLookup("TPE1", "TP1",
				   "Lead performer(s)/Soloist(s)", 1),
		 new FrameIDLookup("TPE2", "TP2",
				   "Band/orchestra/accompaniment", 1),
		 new FrameIDLookup("TPE3", "TP3",
				   "Conductor/performer refinement", 1),
		 (new FrameIDLookup
		  ("TPE4", "TP4",
		   "Interpreted, remixed, or otherwise modified by", 1)),
		 new FrameIDLookup("TPOS", "TPA", "Part of a set", 1),
		 new FrameIDLookup("TPRO", "", "Produced notice", 1),
		 new FrameIDLookup("TPUB", "TPB", "Publisher", 1),
		 new FrameIDLookup("TRCK", "TRK",
				   "Track number/Position in set", 1),
		 new FrameIDLookup("TRDA", "TRD", "Recording dates", 1),
		 new FrameIDLookup("TRSN", "", "Internet radio station name",
				   1),
		 new FrameIDLookup("TRSO", "", "Internet radio station owner",
				   1),
		 new FrameIDLookup("TSIZ", "TSI", "Size", 1),
		 new FrameIDLookup("TSOA", "", "Album sort order", 1),
		 new FrameIDLookup("TSOP", "", "Performer sort order", 1),
		 new FrameIDLookup("TSOT", "", "Title sort order", 1),
		 (new FrameIDLookup
		  ("TSRC", "TRC",
		   "ISRC (international standard recording code)", 1)),
		 (new FrameIDLookup
		  ("TSSE", "TSS",
		   "Software/Hardware and settings used for encoding", 1)),
		 new FrameIDLookup("TSST", "", "Set subtitle", 1),
		 new FrameIDLookup("TXXX", "TXX",
				   "User defined text information frame", 1),
		 new FrameIDLookup("TYER", "TYE", "Year", 1),
		 new FrameIDLookup("UFID", "UFI", "Unique file identifier", 0),
		 new FrameIDLookup("USER", "", "Terms of use", 0),
		 new FrameIDLookup("USLT", "ULT",
				   "Unsynchronised lyric/text transcription",
				   0),
		 new FrameIDLookup("WCOM", "WCM", "Commercial information", 0),
		 new FrameIDLookup("WCOP", "WCP",
				   "Copyright/Legal information", 0),
		 new FrameIDLookup("WOAF", "WAF",
				   "Official audio file webpage", 0),
		 new FrameIDLookup("WOAR", "WAR",
				   "Official artist/performer webpage", 0),
		 new FrameIDLookup("WOAS", "WAS",
				   "Official audio source webpage", 0),
		 new FrameIDLookup("WORS", "",
				   "Official Internet radio station homepage",
				   0),
		 new FrameIDLookup("WPAY", "", "Payment", 0),
		 new FrameIDLookup("WPUB", "WPB",
				   "Publishers official webpage", 0),
		 new FrameIDLookup("WXXX", "WXX",
				   "User defined URL link frame", 0) });
    }
    
    public static boolean isID3Tag(byte[] _header) {
	if (_header[0] == 73 && _header[1] == 68 && _header[2] == 51)
	    return true;
	return false;
    }
    
    public int isTagExtractorFor(String _name, byte[] _header) {
	int isTagExtractor;
	if (isID3Tag(_header))
	    isTagExtractor = 0;
	else if ((_header[0] & 0xff) == 255 && (_header[1] & 0xf0) == 240) {
	    if ((_header[1] & 0x8) == 8) {
		if ((_header[1] & 0x2) == 2)
		    isTagExtractor = 1;
		else if ((_header[1] & 0x4) == 4)
		    isTagExtractor = 1;
		else
		    isTagExtractor = -1;
	    } else if ((_header[1] & 0x8) == 0)
		isTagExtractor = 1;
	    else
		isTagExtractor = -1;
	} else
	    isTagExtractor = -1;
	if (_name.toLowerCase().endsWith(".mp3"))
	    isTagExtractor = 1;
	return isTagExtractor;
    }
    
    public static FrameIDLookup getFrameLookupByFourCC(String _frameID) {
	for (int i = 0; i < FRAME_LOOKUP.length; i++) {
	    FrameIDLookup lookup = FRAME_LOOKUP[i];
	    if (_frameID.equals(lookup.myFrameID))
		return lookup;
	}
	return null;
    }
    
    public static FrameIDLookup getFrameLookupByThreeCC(String _frameID) {
	for (int i = 0; i < FRAME_LOOKUP.length; i++) {
	    FrameIDLookup lookup = FRAME_LOOKUP[i];
	    if (_frameID.equals(lookup.myFrameThreeCC))
		return lookup;
	}
	return null;
    }
    
    public ID3V1Tag getID3V1Tag() {
	return myID3V1Tag;
    }
    
    public ID3V2Tag getID3V2Tag() {
	return myID3V2Tag;
    }
    
    public void onFrame(SeekableInputStream _stream, ID3V2Frame _frame)
	throws IOException {
	String frameID = _frame.getFrameID().getStringValue("ISO-8859-1");
	FrameIDLookup lookup = getFrameLookupByFourCC(frameID);
	if (lookup != null)
	    lookup.onFrame(_stream, _frame, myCollect);
    }
    
    public void extractTags
	(IImportFile _file, ITagExtractorListener _listener)
	throws IOException {
	myFilename = _file.getName();
	SeekableInputStream is = _file.getSeekableInputStream();
	try {
	    extractTags(is, _listener);
	} catch (Object object) {
	    is.close();
	    throw object;
	}
	is.close();
    }
    
    public void extractTags
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	TagOffsets offsets = extractTagsWithoutFrameInfo(_is, _listener);
	FrameInfoExtract extract = new FrameInfoExtract(_is, _listener);
	extract.extract(offsets.getOffset());
    }
    
    public TagOffsets extractTagsWithoutFrameInfo
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	if (myFilename == null)
	    myFilename = "";
	_listener.tagExtracted("type", "tune");
	_listener.tagExtracted("codec", "mp3");
	myCollect = new CollectFrames();
	myCollect.onTextFrame("0TIT", myFilename);
	ID3V1Tag id3v1Tag = new ID3V1Tag();
	int trailer = extractV1Tags(_is, myCollect, id3v1Tag);
	if (trailer > 0)
	    myID3V1Tag = id3v1Tag;
	long length = _is.length();
	trailer = checkLyrics(_is, length, trailer);
	ID3V2Tag id3v2Tag = new ID3V2Tag();
	int offset = ID3V2TagParser.parse(_is, this, id3v2Tag);
	if (offset > 0)
	    myID3V2Tag = id3v2Tag;
	_listener.tagExtracted("offset", offset);
	_listener.tagExtracted("trailer", trailer);
	Enumeration tagNamesEnum = FRAME_MAPPINGS.keys();
	while (tagNamesEnum.hasMoreElements()) {
	    String tagName = (String) tagNamesEnum.nextElement();
	    Object id3NameObj = FRAME_MAPPINGS.get(tagName);
	    if (id3NameObj instanceof String)
		extractTag(_listener, tagName, (String) id3NameObj);
	    else if (id3NameObj != null) {
		boolean extracted = false;
		String id3Name;
		for (Enumeration id3NamesEnum
			 = ((Vector) id3NameObj).elements();
		     !extracted && id3NamesEnum.hasMoreElements();
		     extracted = extractTag(_listener, tagName, id3Name))
		    id3Name = (String) id3NamesEnum.nextElement();
	    }
	}
	String rid
	    = extractRidSpecific(_is, length, (long) offset, (long) trailer);
	_listener.tagExtracted("rid", rid);
	TagOffsets offsets = new TagOffsets(offset, trailer);
	return offsets;
    }
    
    private boolean extractTag(ITagExtractorListener _listener,
			       String _tagName, String _id3Name) {
	String value = myCollect.getProperty(_id3Name);
	if (value != null) {
	    _listener.tagExtracted(_tagName, value);
	    return true;
	}
	return false;
    }
    
    public String extractRidSpecific
	(SeekableInputStream _is, long _length, long _offset, long _trailer)
	throws IOException {
	return RID.calculateRid(_is, _offset, _length - _trailer);
    }
    
    public int extractV1Tags(ID3V1Tag _tag, CollectFrames _collect)
	throws IOException {
	if (!"TAG".equals(_tag.getSignature()
			      .getTrimmedStringValue("ISO-8859-1")))
	    return 0;
	int trailer = _tag.getLength();
	if (_collect == null)
	    return 0;
	String title = _tag.getTitle().getTrimmedStringValue("default");
	if (title.length() > 0)
	    _collect.onTextFrame("1TIT", title);
	String artist = _tag.getArtist().getTrimmedStringValue("default");
	if (artist.length() > 0)
	    _collect.onTextFrame("1ART", artist);
	String album = _tag.getAlbum().getTrimmedStringValue("default");
	if (album.length() > 0)
	    _collect.onTextFrame("1ALB", album);
	String year = _tag.getYear().getTrimmedStringValue("default");
	if (year.length() > 0)
	    _collect.onTextFrame("1YER", year);
	boolean skipTrackNumber = false;
	byte[] commentBytes = _tag.getComment().getValue();
	if (commentBytes[28] == 0 && commentBytes[29] != 0) {
	    _collect.onTextFrame("1TNR", String.valueOf(commentBytes[29]));
	    skipTrackNumber = true;
	}
	String comment
	    = _tag.getComment().getTrimmedStringValue("default",
						      skipTrackNumber ? 1 : 0);
	if (comment.length() > 0)
	    _collect.onTextFrame("1COM", comment);
	String genre = Genre.getGenre(_tag.getGenre().getValue());
	if (genre.length() > 0)
	    _collect.onTextFrame("1CON", genre);
	myID3V1Tag = _tag;
	return trailer;
    }
    
    public int extractV1Tags(SeekableInputStream _is, CollectFrames _collect,
			     ID3V1Tag _id3v1Tag) throws IOException {
	long fileSize = _is.length();
	if (fileSize < (long) _id3v1Tag.getLength())
	    throw new IOException
		      ("Stream is too short to contain an ID3V1 Tag.");
	_is.seek(fileSize - (long) _id3v1Tag.getLength());
	_id3v1Tag.read(new LittleEndianInputStream(_is));
	return extractV1Tags(_id3v1Tag, _collect);
    }
    
    public int checkLyrics
	(SeekableInputStream _is, long _length, int _trailer)
	throws IOException {
	if (_length - (long) _trailer < 15L)
	    return _trailer;
	_is.seek(_length - (long) _trailer - 15L);
	byte[] bytez = new byte[15];
	_is.readFully(bytez, 0, 15);
	if (!"LYRICS".equals(new String(bytez, 6, 6)))
	    return _trailer;
	int lyricsLength = Integer.parseInt(new String(bytez, 0, 6));
	if ((long) (_trailer + lyricsLength + 15) >= _length)
	    return _trailer;
	long newPos = _length - (long) _trailer - (long) lyricsLength - 15L;
	_is.seek(newPos);
	_is.readFully(bytez, 0, 11);
	if (!"LYRICSBEGIN".equals(new String(bytez, 0, 11)))
	    return _trailer;
	return _trailer + lyricsLength;
    }
    
    private static String getPreferredISO8859Encoding() {
	return (PropertiesManager.getInstance()
		    .getBooleanProperty("treatISOAsDefaultEncoding")
		? "default" : "ISO-8859-1");
    }
    
    public static String deUnsynchronize(byte[] _bytes, String _encoding)
	throws UnsupportedEncodingException {
	int readIndex = 0;
	int writeIndex = 0;
	while (readIndex < _bytes.length) {
	    _bytes[writeIndex++] = _bytes[readIndex];
	    if (_bytes[readIndex] == -1 && _bytes[readIndex + 1] == 0)
		readIndex += 2;
	    else
		readIndex++;
	}
	while (writeIndex < _bytes.length)
	    _bytes[writeIndex++] = (byte) 0;
	String str = StringUtils.parseString(_bytes, _encoding,
					     getPreferredISO8859Encoding());
	return str;
    }
}
