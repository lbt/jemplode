/* ID3V2TagParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags.id3;
import java.io.IOException;

import com.inzyme.io.SeekableInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;

public class ID3V2TagParser
{
    private static final boolean DEBUG = false;
    
    public static int parse
	(SeekableInputStream _is, ID3V2FrameListener _listener, ID3V2Tag _v2)
	throws IOException {
	int offset = parseHeader(_is, _v2);
	if (offset == 0)
	    return 0;
	LittleEndianInputStream leis = new LittleEndianInputStream(_is);
	long pos;
	long frameSize;
	do {
	    ID3V2Frame frame = new ID3V2Frame(_v2);
	    frame.read(leis);
	    if (frame.isLastTag())
		break;
	    String frameID = frame.getFrameID().getStringValue("ISO-8859-1");
	    ID3TagExtractor.FrameIDLookup lookup;
	    if (frameID.length() == 3)
		lookup = ID3TagExtractor.getFrameLookupByThreeCC(frameID);
	    else if (frameID.length() == 4)
		lookup = ID3TagExtractor.getFrameLookupByFourCC(frameID);
	    else
		lookup = null;
	    if (lookup != null) {
		frameID = lookup.myFrameID;
		frame = new ID3V2Frame(frameID, frame.getSizeLength(),
				       frame.getSize(), frame.getFlags());
	    }
	    pos = _is.tell();
	    frameSize = frame.getSize();
	    if (pos + frameSize > (long) offset || frameSize <= 0L)
		break;
	    if (frameSize > 0L)
		_listener.onFrame(_is, frame);
	    _is.seek(pos + frameSize);
	} while (pos + frameSize != (long) offset);
	return offset;
    }
    
    public static int parseHeader
	(SeekableInputStream _is, ID3V2Tag _header) throws IOException {
	_is.seek(0L);
	_header.read(new LittleEndianInputStream(_is));
	int tagSize = (int) _header.getSize().getValue() + 10;
	if (!_header.isID3V2Tag())
	    return 0;
	if (_header.getMinorVersion().getValue() > 4)
	    return 0;
	return tagSize;
    }
}
