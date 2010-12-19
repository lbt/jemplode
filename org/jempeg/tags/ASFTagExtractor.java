/* ASFTagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.IOException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.model.GUID;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;
import com.inzyme.util.Debug;

public class ASFTagExtractor implements ITagExtractor
{
    private static final long CONVERT_100NS_TO_MS = 10000L;
    private static final GUID WMAHeaderGUID
	= new GUID(1974609456L, 26254, 4559, 166, 217, 0, 170, 0, 98, 206,
		   108);
    private static final GUID WMAContentDescriptionGUID
	= new GUID(1974609459L, 26254, 4559, 166, 217, 0, 170, 0, 98, 206,
		   108);
    private static final GUID WMAPropertiesGUID
	= new GUID(2360073377L, 43335, 4559, 142, 228, 0, 192, 12, 32, 83,
		   101);
    private static final GUID WMAUnknownDRM1GUID
	= new GUID(519772720L, 2914, 4560, 163, 155, 0, 160, 201, 3, 72, 246);
    private static final GUID WMATextTagsGUID
	= new GUID(3536888896L, 58119, 4562, 151, 240, 0, 160, 201, 94, 168,
		   80);
    private static final GUID WMAStreamPropertiesGUID
	= new GUID(3084650385L, 43447, 4559, 142, 230, 0, 192, 12, 32, 83,
		   101);
    private static final GUID WMAStreamTypeAudioGUID
	= new GUID(4167671360L, 23373, 4559, 168, 253, 0, 128, 95, 92, 68, 43);
    private static final GUID WMADataSectionGUID
	= new GUID(1974609462L, 26254, 4559, 166, 217, 0, 170, 0, 98, 206,
		   108);
    
    protected static class WMAObject
    {
	private GUID myGuid = new GUID();
	private UINT64 mySize = new UINT64();
	
	public WMAObject() {
	    /* empty */
	}
	
	public void read(LittleEndianInputStream _is) throws IOException {
	    myGuid.read(_is);
	    mySize.read(_is);
	}
	
	public GUID getGuid() {
	    return myGuid;
	}
	
	public UINT64 getSize() {
	    return mySize;
	}
    }
    
    protected static class WMAHeader
    {
	public WMAObject myObj = new WMAObject();
	public UINT32 myNumberHeaders = new UINT32();
	public byte myReserved1;
	public byte myReserved2;
	
	public void read(LittleEndianInputStream _is) throws IOException {
	    myObj.read(_is);
	    myNumberHeaders.read(_is);
	    myReserved1 = (byte) _is.read();
	    myReserved2 = (byte) _is.read();
	}
    }
    
    protected static class WMAContentDescription
    {
	private String myTitle;
	private String myAuthor;
	private String myCopyright;
	private String myDescription;
	private String myRating;
	
	public WMAContentDescription(WMAObject _obj) {
	    /* empty */
	}
	
	public String getTitle() {
	    return myTitle;
	}
	
	public String getAuthor() {
	    return myAuthor;
	}
	
	public String getCopyright() {
	    return myCopyright;
	}
	
	public String getDescription() {
	    return myDescription;
	}
	
	public String getRating() {
	    return myRating;
	}
	
	public void read(LittleEndianInputStream _is) throws IOException {
	    UINT16 titleLen = new UINT16();
	    titleLen.read(_is);
	    UINT16 authorLen = new UINT16();
	    authorLen.read(_is);
	    UINT16 copyrightLen = new UINT16();
	    copyrightLen.read(_is);
	    UINT16 descriptionLen = new UINT16();
	    descriptionLen.read(_is);
	    UINT16 ratingLen = new UINT16();
	    ratingLen.read(_is);
	    myTitle = _is.readUTF16LE(titleLen.getValue());
	    myAuthor = _is.readUTF16LE(authorLen.getValue());
	    myCopyright = _is.readUTF16LE(copyrightLen.getValue());
	    myDescription = _is.readUTF16LE(descriptionLen.getValue());
	    myRating = _is.readUTF16LE(ratingLen.getValue());
	}
    }
    
    protected static class WMAProperties
    {
	private GUID myMultimediaStreamGuid = new GUID();
	private UINT64 myTotalSize = new UINT64();
	private UINT64 myCreated = new UINT64();
	private UINT64 myNumInterleavePackets = new UINT64();
	private UINT64 myPlayDuration100ns = new UINT64();
	private UINT64 mySendDuration100ns = new UINT64();
	private UINT64 myPrerollMs = new UINT64();
	private UINT32 myFlags = new UINT32();
	private UINT32 myMinInterleavePacketSize = new UINT32();
	private UINT32 myMaxInterleavePacketSize = new UINT32();
	private UINT32 myMaximumBitRate = new UINT32();
	
	public WMAProperties(WMAObject _obj) {
	    /* empty */
	}
	
	public UINT64 getPlayDuration100ns() {
	    return myPlayDuration100ns;
	}
	
	public UINT64 getPrerollMs() {
	    return myPrerollMs;
	}
	
	public UINT32 getMaximumBitRate() {
	    return myMaximumBitRate;
	}
	
	public void read(LittleEndianInputStream _is) throws IOException {
	    myMultimediaStreamGuid.read(_is);
	    myTotalSize.read(_is);
	    myCreated.read(_is);
	    myNumInterleavePackets.read(_is);
	    myPlayDuration100ns.read(_is);
	    mySendDuration100ns.read(_is);
	    myPrerollMs.read(_is);
	    myFlags.read(_is);
	    myMinInterleavePacketSize.read(_is);
	    myMaxInterleavePacketSize.read(_is);
	    myMaximumBitRate.read(_is);
	}
    }
    
    protected static class WMAStreamProperties
    {
	private GUID myStreamType = new GUID();
	private GUID myErrorCorrectionType = new GUID();
	private UINT64 myOffset = new UINT64();
	private UINT32 myTypeSpecificLen = new UINT32();
	private UINT32 myErrorCorrectionDataLen = new UINT32();
	private UINT16 myStreamNumber = new UINT16();
	private UINT32 myReserved = new UINT32();
	
	public WMAStreamProperties(WMAObject _obj) {
	    /* empty */
	}
	
	public GUID getStreamType() {
	    return myStreamType;
	}
	
	public void read(LittleEndianInputStream _is) throws IOException {
	    myStreamType.read(_is);
	    myErrorCorrectionType.read(_is);
	    myOffset.read(_is);
	    myTypeSpecificLen.read(_is);
	    myErrorCorrectionDataLen.read(_is);
	    myStreamNumber.read(_is);
	    myReserved.read(_is);
	}
    }
    
    protected static class WaveFormtEx
    {
	private UINT16 myWaveFormatTag = new UINT16();
	private UINT16 myNumChannels = new UINT16();
	private UINT32 myNumSamplesPerSec = new UINT32();
	private UINT32 myNumAvgBytesPerSec = new UINT32();
	private UINT16 myNumBlockAlign = new UINT16();
	private UINT16 myWaveBitsPerSample = new UINT16();
	private UINT16 myCBSize = new UINT16();
	
	public UINT32 getNumSamplesPerSec() {
	    return myNumSamplesPerSec;
	}
	
	public void read(LittleEndianInputStream _eis) throws IOException {
	    myWaveFormatTag.read(_eis);
	    myNumChannels.read(_eis);
	    myNumSamplesPerSec.read(_eis);
	    myNumAvgBytesPerSec.read(_eis);
	    myNumBlockAlign.read(_eis);
	    myWaveBitsPerSample.read(_eis);
	    myCBSize.read(_eis);
	}
    }
    
    public int isTagExtractorFor(String _name, byte[] _header) {
	int isTagExtractor;
	if (TypeConversionUtils.toUnsigned8(_header[0]) == 48
	    && TypeConversionUtils.toUnsigned8(_header[1]) == 38
	    && TypeConversionUtils.toUnsigned8(_header[2]) == 178
	    && TypeConversionUtils.toUnsigned8(_header[3]) == 117)
	    isTagExtractor = 1;
	else if (_header[8] == 87 && _header[9] == 65 && _header[10] == 86
		 && _header[11] == 69)
	    isTagExtractor = 1;
	else
	    isTagExtractor = -1;
	return isTagExtractor;
    }
    
    public void extractTags
	(IImportFile _file, ITagExtractorListener _listener)
	throws IOException {
	SeekableInputStream stream = _file.getSeekableInputStream();
	try {
	    extractTags(stream, _listener);
	} catch (Object object) {
	    stream.close();
	    throw object;
	}
	stream.close();
    }
    
    public void extractTags
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	_listener.tagExtracted("type", "tune");
	_listener.tagExtracted("codec", "wma");
	_is.seek(0L);
	LittleEndianInputStream eis = new LittleEndianInputStream(_is);
	WMAHeader header = new WMAHeader();
	header.read(eis);
	if (!header.myObj.myGuid.equals(WMAHeaderGUID)
	    || header.myReserved1 != 1 || header.myReserved2 != 2)
	    throw new IOException("Stream does not contain ASF data.");
	boolean foundProperties = false;
	long dataStartOffset = 0L;
	long numberHeaders = header.myNumberHeaders.getValue();
	for (int i = 0; (long) i <= numberHeaders; i++) {
	    long pos = _is.tell();
	    WMAObject obj = new WMAObject();
	    obj.read(eis);
	    GUID guid = obj.getGuid();
	    if (guid.equals(WMAContentDescriptionGUID))
		extractContentDescription(obj, eis, _listener);
	    else if (guid.equals(WMAPropertiesGUID)) {
		extractProperties(obj, eis, _listener);
		foundProperties = true;
	    } else if (guid.equals(WMAUnknownDRM1GUID))
		extractUnknownDRM(obj, eis, _listener);
	    else if (guid.equals(WMATextTagsGUID))
		extractTextTags(obj, eis, _listener);
	    else if (guid.equals(WMAStreamPropertiesGUID))
		extractStreamProperties(obj, eis, _listener);
	    else if (guid.equals(WMADataSectionGUID))
		dataStartOffset = pos;
	    else
		Debug.println(4, "Unrecognized: " + guid);
	    pos += obj.getSize().getValue();
	    _is.seek(pos);
	}
	if (!foundProperties)
	    throw new IOException
		      ("Unable to find a valid WMA properties header.");
	long dataEndOffset = _is.length();
	_is.seek(0L);
	String rid = RID.calculateRid(_is, dataStartOffset, dataEndOffset);
	_listener.tagExtracted("rid", rid);
	_listener.tagExtracted("offset", 0);
    }
    
    protected void extractContentDescription
	(WMAObject _obj, LittleEndianInputStream _eis,
	 ITagExtractorListener _listener)
	throws IOException {
	WMAContentDescription contentDescription
	    = new WMAContentDescription(_obj);
	contentDescription.read(_eis);
	tagExtracted(_listener, "title", contentDescription.getTitle());
	tagExtracted(_listener, "artist", contentDescription.getAuthor());
	tagExtracted(_listener, "comment",
		     contentDescription.getDescription());
    }
    
    protected void extractProperties
	(WMAObject _obj, LittleEndianInputStream _eis,
	 ITagExtractorListener _listener)
	throws IOException {
	WMAProperties properties = new WMAProperties(_obj);
	properties.read(_eis);
	long durationMs
	    = convert100nsToMilliseconds(properties.getPlayDuration100ns());
	long adjustedDurationMs
	    = durationMs - properties.getPrerollMs().getValue();
	_listener.tagExtracted("duration", adjustedDurationMs);
	EmpegBitrate eb = new EmpegBitrate();
	eb.setVBR(false);
	eb.setChannels(2);
	eb.setBitsPerSecond((int) properties.getMaximumBitRate().getValue());
	_listener.tagExtracted("bitrate", eb.toString());
    }
    
    protected void extractUnknownDRM
	(WMAObject _obj, LittleEndianInputStream _eis,
	 ITagExtractorListener _listener)
	throws IOException {
	throw new IOException("ASF stream has DRM");
    }
    
    protected void extractTextTags
	(WMAObject _obj, LittleEndianInputStream _eis,
	 ITagExtractorListener _listener)
	throws IOException {
	UINT16 val = new UINT16();
	val.read(_eis);
	int numTags = val.getValue();
	for (int i = 0; i < numTags; i++) {
	    val.read(_eis);
	    String name = _eis.readUTF16LE(val.getValue());
	    val.read(_eis);
	    val.read(_eis);
	    String value = _eis.readUTF16LE(val.getValue());
	    if (name.equals("WM/AlbumTitle"))
		tagExtracted(_listener, "source", value);
	    else if (name.equals("WM/Genre"))
		tagExtracted(_listener, "genre", value);
	    else if (name.equals("WM/Year"))
		tagExtracted(_listener, "year", value);
	    else if (name.equals("WM/Track")) {
		long trackNumber;
		try {
		    trackNumber = (long) (Integer.parseInt(value) + 1);
		} catch (NumberFormatException e) {
		    trackNumber = 1L;
		}
		tagExtracted(_listener, "tracknr",
			     String.valueOf(trackNumber));
		tagExtracted(_listener, "file_id",
			     String.valueOf(trackNumber));
	    }
	}
    }
    
    protected void extractStreamProperties
	(WMAObject _obj, LittleEndianInputStream _eis,
	 ITagExtractorListener _listener)
	throws IOException {
	WMAStreamProperties streamProperties = new WMAStreamProperties(_obj);
	streamProperties.read(_eis);
	if (streamProperties.getStreamType().equals(WMAStreamTypeAudioGUID)) {
	    WaveFormtEx waveFormat = new WaveFormtEx();
	    waveFormat.read(_eis);
	    long samplerate = waveFormat.getNumSamplesPerSec().getValue();
	    _listener.tagExtracted("samplerate", samplerate);
	}
    }
    
    protected void tagExtracted(ITagExtractorListener _listener, String _name,
				String _value) throws IOException {
	_listener.tagExtracted(_name, _value);
    }
    
    protected long convert100nsToMilliseconds(UINT64 _value) {
	return _value.getValue() / 10000L;
    }
}
