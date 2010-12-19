/* FrameInfoExtract - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.IOException;

import com.inzyme.io.SeekableInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.Debug;

public class FrameInfoExtract
{
    private static final int FRAMES_FLAG = 1;
    private static final int BYTES_FLAG = 2;
    private static final int TOC_FLAG = 4;
    private static final int VBR_SCALE_FLAG = 8;
    private static final int FIND_SYNC_BEFORE_BYTES_COUNT = 196608;
    private SeekableInputStream myStream;
    private byte[] myBuffer = new byte[32768];
    private int myBufferStart;
    private int myBufferLength;
    private int myFirstHeader;
    private int mySeekPos;
    private int myLength;
    private int myShiftReg;
    private MP3Header myMP3Header;
    private VBRIHeader myVBRIHeader;
    private XingVBRHeader myXingHeader;
    private ITagExtractorListener myListener;
    
    protected class MP3Header
    {
	public int layerCode;
	public int mpegVersionCode;
	public int bitrateCode;
	public int samplerateCode;
	public int channelModeCode;
	public int layerNumber;
	public int padded;
	public int bitrate;
	public int samplerate;
	public int length;
	public int copyright;
	public int original;
    }
    
    protected class VBRIHeader
    {
	public int version;
	public int quality;
	public int bytes;
	public int frames;
	public int mpegAudioVersionID;
	public int sampleRateIndex;
	public int channelMode;
    }
    
    protected class XingVBRHeader
    {
	public int id;
	public int samprate;
	public int flags;
	public int frames;
	public int bytes;
	public int vbrScale;
	public char[] toc = new char[256];
    }
    
    public FrameInfoExtract
	(SeekableInputStream _stream, ITagExtractorListener _listener)
	throws IOException {
	myStream = _stream;
	myMP3Header = new MP3Header();
	myVBRIHeader = new VBRIHeader();
	myXingHeader = new XingVBRHeader();
	myListener = _listener;
    }
    
    public void extract(int _dataOffset) throws IOException {
	seek(0);
	myLength = (int) myStream.length();
	mySeekPos = _dataOffset;
	boolean gotVBRHeader = false;
	if (isXingHeader()) {
	    gotVBRHeader = true;
	    parseXingHeader();
	}
	mySeekPos = _dataOffset;
	if (!gotVBRHeader && isVBRIHeader()) {
	    gotVBRHeader = true;
	    parseVBRIHeader();
	}
	mySeekPos = _dataOffset;
	parseMP3Frames(gotVBRHeader);
	mySeekPos = myFirstHeader;
	if (!gotVBRHeader && isXingHeader())
	    parseXingHeader();
    }
    
    protected boolean isVBRIHeader() throws IOException {
	int shiftRegister = getWord();
	myVBRIHeader.mpegAudioVersionID
	    = (shiftRegister & 0x180000) >> 3 >> 16;
	myVBRIHeader.sampleRateIndex = (shiftRegister & 0xc00) >> 2 >> 8;
	myVBRIHeader.channelMode = (shiftRegister & 0xc0) >> 6;
	int layerDescription = (shiftRegister & 0x60000) >> 1 >> 16;
	int bitrateIndex = (shiftRegister & 0xf000) >> 4 >> 8;
	if (myVBRIHeader.channelMode != 0)
	    return false;
	int layerNumber = 4 - layerDescription;
	if (figureMP3Bitrate(myVBRIHeader.mpegAudioVersionID, layerNumber,
			     bitrateIndex)
	    != 160000)
	    return false;
	mySeekPos += 32;
	if (getByte() != 86)
	    return false;
	if (getByte() != 66)
	    return false;
	if (getByte() != 82)
	    return false;
	if (getByte() != 73)
	    return false;
	myVBRIHeader.version = getWord();
	myVBRIHeader.quality = getByte() << 8;
	myVBRIHeader.quality |= getByte();
	myVBRIHeader.bytes = getWord();
	myVBRIHeader.frames = getWord();
	return true;
    }
    
    protected void parseVBRIHeader() throws IOException {
	int lengthMs = 0;
	int sampleRate = figureMP3Samplerate(myVBRIHeader.mpegAudioVersionID,
					     myVBRIHeader.sampleRateIndex);
	if (sampleRate != 0)
	    lengthMs = (int) ((double) myVBRIHeader.frames
			      / ((double) sampleRate / 1000.0) * 1152.0);
	String bitrate = figureAverageBitrateFromVBRI(lengthMs);
	if (myListener != null) {
	    myListener.tagExtracted("duration", lengthMs);
	    myListener.tagExtracted("bitrate", bitrate);
	    myListener.tagExtracted("samplerate", sampleRate);
	    myListener.tagExtracted("stereo",
				    String.valueOf(myVBRIHeader.channelMode
						   != 3));
	}
    }
    
    protected boolean isXingHeader() throws IOException {
	if (!getMP3Sync(4))
	    return false;
	int id = myMP3Header.mpegVersionCode & 0x1;
	int mode = myMP3Header.channelModeCode;
	mySeekPos += figureXingHeaderOffset(id, mode);
	if (getByte() != 88)
	    return false;
	if (getByte() != 105)
	    return false;
	if (getByte() != 110)
	    return false;
	if (getByte() != 103)
	    return false;
	myXingHeader.id = id;
	myXingHeader.samprate = myMP3Header.samplerate;
	int headFlags = getWord();
	myXingHeader.flags = headFlags;
	if ((headFlags & 0x1) > 0)
	    myXingHeader.frames = getWord();
	if ((headFlags & 0x2) > 0)
	    myXingHeader.bytes = getWord();
	if ((headFlags & 0x4) > 0) {
	    for (int i = 0; i < 100; i++)
		myXingHeader.toc[i] = (char) getByte();
	}
	myXingHeader.vbrScale = 1;
	if ((headFlags & 0x8) > 0)
	    myXingHeader.vbrScale = getWord();
	return true;
    }
    
    protected int figureXingHeaderOffset(int _id, int _mode) {
	if (_id > 0) {
	    if (_mode != 3)
		return 32;
	    return 17;
	}
	if (_mode != 3)
	    return 17;
	return 9;
    }
    
    protected void parseXingHeader() throws IOException {
	int lengthMs;
	if (myXingHeader.samprate / 1000 != 0) {
	    int mpegVersion = myMP3Header.mpegVersionCode;
	    int layerNumber = myMP3Header.layerNumber;
	    int frameLen;
	    if (layerNumber == 1 || mpegVersion == 3)
		frameLen = 1152;
	    else
		frameLen = 576;
	    lengthMs = (int) ((double) frameLen
			      * ((double) myXingHeader.frames
				 / ((double) myXingHeader.samprate / 1000.0)));
	} else
	    lengthMs = 0;
	String bitrate = figureAverageBitrateFromXing(lengthMs);
	if (myListener != null) {
	    myListener.tagExtracted("duration", lengthMs);
	    myListener.tagExtracted("bitrate", bitrate);
	    myListener.tagExtracted("samplerate", myXingHeader.samprate);
	}
    }
    
    protected String figureAverageBitrateFromVBRI(int _lengthMs) {
	StringBuffer bitrate = new StringBuffer();
	bitrate.append('v');
	bitrate.append(myMP3Header.channelModeCode == 3 ? 'm' : 's');
	if (_lengthMs / 1000 != 0)
	    bitrate.append(myVBRIHeader.bytes * 8 / _lengthMs);
	else
	    bitrate.append(myMP3Header.bitrate / 1000);
	return bitrate.toString();
    }
    
    protected String figureAverageBitrateFromXing(int _lengthMs) {
	StringBuffer bitrate = new StringBuffer();
	bitrate.append('v');
	bitrate.append(myMP3Header.channelModeCode == 3 ? 'm' : 's');
	if (_lengthMs / 1000 != 0)
	    bitrate.append(myXingHeader.bytes * 8 / _lengthMs);
	else
	    bitrate.append(myMP3Header.bitrate / 1000);
	return bitrate.toString();
    }
    
    protected void parseMP3Frames(boolean _gotXingHeader) throws IOException {
	if (myLength < 4)
	    throw new IOException("Invalid MP3.");
	int startPos = tell();
	int endPos = startPos + 196608;
	if (endPos > myLength - 4)
	    endPos = myLength - 4;
	while (tell() < endPos) {
	    if (getMP3Sync(65536)) {
		int firstSamplerate = myMP3Header.samplerate;
		myFirstHeader = mySeekPos - 4;
		skip(myMP3Header.length - 4);
		if (getMP3Sync(4)) {
		    if (firstSamplerate == myMP3Header.samplerate) {
			StringBuffer bitrate = new StringBuffer();
			bitrate.append("f");
			bitrate.append(myMP3Header.channelModeCode == 3 ? 'm'
				       : 's');
			bitrate.append(myMP3Header.bitrate / 1000);
			int lengthMs = 0;
			if (myMP3Header.bitrate / 1000 != 0)
			    lengthMs = 8000 * ((myLength - myFirstHeader)
					       / myMP3Header.bitrate);
			if (myListener != null) {
			    if (!_gotXingHeader) {
				myListener.tagExtracted("bitrate",
							bitrate.toString());
				myListener.tagExtracted("samplerate",
							(myMP3Header
							 .samplerate));
				myListener.tagExtracted("duration", lengthMs);
				myListener.tagExtracted
				    ("stereo",
				     String.valueOf(myMP3Header.channelModeCode
						    != 3));
			    }
			    myListener.tagExtracted("offset", myFirstHeader);
			    if (myMP3Header.copyright > 0)
				myListener.tagExtracted("copyright", "yes");
			    else
				myListener.tagExtracted("copyright", "no");
			    if (myMP3Header.original > 0)
				myListener.tagExtracted("copy", "no");
			    else
				myListener.tagExtracted("copy", "yes");
			    return;
			}
		    }
		} else
		    seek(myFirstHeader + 1);
	    } else
		skip(-3);
	}
	throw new IOException("Invalid MP3.");
    }
    
    protected boolean getMP3Sync(int _distance) throws IOException {
	if (mySeekPos + _distance > myLength)
	    _distance = myLength - mySeekPos;
	int bound = mySeekPos + _distance;
	myShiftReg = 0;
	while (mySeekPos < bound) {
	    int curByte = getByte();
	    if (curByte < 0)
		return false;
	    myShiftReg = myShiftReg << 8 | curByte;
	    if ((myShiftReg & ~0x1fffff) == -2097152 && getMP3Header() > 0)
		return true;
	}
	return false;
    }
    
    protected void skip(int _howFar) {
	if (mySeekPos + _howFar < myLength) {
	    mySeekPos += _howFar;
	    myShiftReg = 0;
	}
    }
    
    protected void seek(int _pos) throws IOException {
	if (_pos > myLength)
	    _pos = myLength;
	mySeekPos = _pos;
	myShiftReg = 0;
    }
    
    protected int tell() {
	return mySeekPos;
    }
    
    protected boolean endOfFile() {
	boolean endOfFile = mySeekPos >= myLength;
	return endOfFile;
    }
    
    protected int getByte() throws IOException {
	if (mySeekPos < myBufferStart
	    || mySeekPos - myBufferStart >= myBufferLength) {
	    if (mySeekPos >= myLength) {
		mySeekPos++;
		return 0;
	    }
	    myBufferStart = mySeekPos;
	    myStream.seek((long) mySeekPos);
	    myBufferLength = myStream.read(myBuffer, 0, myBuffer.length);
	}
	byte b = myBuffer[mySeekPos++ - myBufferStart];
	return TypeConversionUtils.toUnsigned8(b);
    }
    
    protected int getMP3Header() throws IOException {
	myMP3Header.layerCode = (myShiftReg & 0x60000) >> 17;
	if (myMP3Header.layerCode == 0) {
	    Debug.println(4, "Invalid layerCode: " + myMP3Header.layerCode);
	    return 0;
	}
	myMP3Header.mpegVersionCode = (myShiftReg & 0x180000) >> 19;
	if (myMP3Header.mpegVersionCode == 1) {
	    Debug.println(4, ("Invalid mpegVersionCode: "
			      + myMP3Header.mpegVersionCode));
	    return 0;
	}
	myMP3Header.bitrateCode = (myShiftReg & 0xf000) >> 12;
	if (myMP3Header.bitrateCode == 0 || myMP3Header.bitrateCode == 15) {
	    Debug.println(4,
			  "Invalid bitrate code: " + myMP3Header.bitrateCode);
	    return 0;
	}
	myMP3Header.samplerateCode = (myShiftReg & 0xc00) >> 10;
	if (myMP3Header.samplerateCode == 3) {
	    Debug.println(4, ("Invalid samplerateCode: "
			      + myMP3Header.samplerateCode));
	    return 0;
	}
	myMP3Header.channelModeCode = (myShiftReg & 0xc0) >> 6;
	myMP3Header.layerNumber = 4 - myMP3Header.layerCode;
	myMP3Header.padded = (myShiftReg & 0x200) >> 9;
	myMP3Header.copyright = (myShiftReg & 0x80) >> 3;
	myMP3Header.original = (myShiftReg & 0x40) >> 2;
	myMP3Header.bitrate = figureMP3Bitrate(myMP3Header.mpegVersionCode,
					       myMP3Header.layerNumber,
					       myMP3Header.bitrateCode);
	myMP3Header.samplerate
	    = figureMP3Samplerate(myMP3Header.mpegVersionCode,
				  myMP3Header.samplerateCode);
	if (myMP3Header.layerNumber == 1)
	    myMP3Header.length
		= (12 * myMP3Header.bitrate / myMP3Header.samplerate
		   + myMP3Header.padded * 4);
	else if (myMP3Header.mpegVersionCode == 3)
	    myMP3Header.length
		= (144 * myMP3Header.bitrate / myMP3Header.samplerate
		   + myMP3Header.padded);
	else
	    myMP3Header.length
		= (72 * myMP3Header.bitrate / myMP3Header.samplerate
		   + myMP3Header.padded);
	return myMP3Header.length;
    }
    
    protected int figureMP3Bitrate(int _mpegVersionCode, int _layerNumber,
				   int _bitrateCode) {
	if (_layerNumber <= 0 || _layerNumber > 3) {
	    Debug.println(4, "Invalid layer number: " + _layerNumber);
	    return -1;
	}
	int[][][] bitrateTable = { { { 0, 32, 48, 56, 64, 80, 96, 112, 128,
				       144, 160, 176, 192, 224, 256, -1 },
				     { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80,
				       96, 112, 128, 144, 160, -1 },
				     { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80,
				       96, 112, 128, 144, 160, -1 } },
				   { { 0, 32, 64, 96, 128, 160, 192, 224, 256,
				       288, 320, 352, 384, 416, 448, -1 },
				     { 0, 32, 48, 56, 64, 80, 96, 112, 128,
				       160, 192, 224, 256, 320, 384, -1 },
				     { 0, 32, 40, 48, 56, 64, 80, 96, 112, 128,
				       160, 192, 224, 256, 320, -1 } } };
	return 1000 * (bitrateTable[_mpegVersionCode & 0x1][_layerNumber - 1]
		       [_bitrateCode]);
    }
    
    protected int figureMP3Samplerate(int _mpegVersionCode,
				      int _samplerateCode) {
	int[][] samplerateTable
	    = { { 11025, 12000, 8000, -1 }, { -1, -1, -1, -1 },
		{ 22050, 24000, 16000, -1 }, { 44100, 48000, 32000, -1 } };
	return samplerateTable[_mpegVersionCode][_samplerateCode];
    }
    
    protected int getWord() throws IOException {
	int w = getByte() << 24;
	w |= getByte() << 16;
	w |= getByte() << 8;
	w |= getByte();
	return w;
    }
}
