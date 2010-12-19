package org.jempeg.tags.id3;

import java.io.IOException;

import com.inzyme.io.SeekableInputStream;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.Debug;

public class ID3V2TagParser {
	private static final boolean DEBUG = false;

	// returns offset
	public static int parse(SeekableInputStream _is, ID3V2FrameListener _listener, ID3V2Tag _v2) throws IOException {
		int offset = ID3V2TagParser.parseHeader(_is, _v2);
		if (offset == 0) {
			return 0;
		}

		LittleEndianInputStream leis = new LittleEndianInputStream(_is);
		for (;;) {
			// Right, now go looking for ID3 frames in the file -- we can't
			// easily copy them into memory, because they're woefully unaligned.
			ID3V2Frame frame = new ID3V2Frame(_v2);
			frame.read(leis);

			// Skip padding.
			if (frame.isLastTag()) {
				if (DEBUG) {
					Debug.println(Debug.INFORMATIVE, "Found an 0 byte, end of tags.");
				}
				break;
			}

			/* We convert ID3v2.2 to 2.3 here because (a) it simplifies the
			 * upstream code and (b) we only write v2.3 anyway
			 */
			String frameID = frame.getFrameID().getStringValue(StringUtils.ISO_8859_1);
			ID3TagExtractor.FrameIDLookup lookup;
			if (frameID.length() == 3) {
				lookup = ID3TagExtractor.getFrameLookupByThreeCC(frameID);
			} else if (frameID.length() == 4) { 
				lookup = ID3TagExtractor.getFrameLookupByFourCC(frameID);
			} else {
				lookup = null;
			}
			
			if (lookup != null) {
				frameID = lookup.myFrameID;
				frame = new ID3V2Frame(frameID, frame.getSizeLength(), frame.getSize(), frame.getFlags());
			} else {
				if (DEBUG) {
					Debug.println(Debug.WARNING, "Unknown frame ID: " + frameID);
				}
			}

			// Save the seek position - processing the frame may move it...
			long pos = _is.tell();

			long frameSize = frame.getSize();
			if ((pos + frameSize) > offset || frameSize <= 0) {
				if (DEBUG) {
					Debug.println(Debug.INFORMATIVE, "Bogus frame size (" + pos + " + " + frameSize + " > " + offset + ")");
				}
				break;
			}

			if (DEBUG) {
				Debug.println(Debug.INFORMATIVE, "v2.%c Frame at " + (pos - frame.getLength()));
			}

			if (frameSize > 0) {
				_listener.onFrame(_is, frame);
			}

			// Figure out where the next frame is coming from...
			_is.seek(pos + frameSize);
			if (pos + frameSize == offset) {
				if (DEBUG) {
					Debug.println(Debug.INFORMATIVE, "Got to end of tags at " + offset + ".");
				}
				break;
			}
		}

		return offset;
	}

	public static int parseHeader(SeekableInputStream _is, ID3V2Tag _header) throws IOException {
		_is.seek(0);

		_header.read(new LittleEndianInputStream(_is));

		if (DEBUG) {
			Debug.println(Debug.INFORMATIVE, _header.toString());
		}
		int tagSize = (int) _header.getSize().getValue() + 10; // Size doesn't include 10-byte header

		if (!_header.isID3V2Tag()) {
			if (DEBUG) {
				Debug.println(Debug.INFORMATIVE, "Does not contain an ID3v2 tag.");
			}
			return 0;
		}

		if (_header.getMinorVersion().getValue() > ID3V2Tag.ID3V2_VERSION) {
			if (DEBUG) {
				Debug.println(Debug.INFORMATIVE, "Don't understand this version because it's too new.");
			}
			return 0;
		}

		return tagSize;
	}
}
