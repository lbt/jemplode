package org.jempeg.tags;

import java.io.IOException;

import org.jempeg.nodestore.DatabaseTags;

import com.inzyme.io.SeekableInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.Debug;

/**
* FrameInfoExtract is used to parse MP3 frame info
* (sample rate, bitrate, VBR headers, etc.) from an
* MP3.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class FrameInfoExtract {
	private static final int FRAMES_FLAG = 0x0001;
	private static final int BYTES_FLAG = 0x0002;
	private static final int TOC_FLAG = 0x0004;
	private static final int VBR_SCALE_FLAG = 0x0008;

	//      private static final int FRAMES_AND_BYTES = (FRAMES_FLAG | BYTES_FLAG);

	private static final int FIND_SYNC_BEFORE_BYTES_COUNT = (192 * 1024); /* 192K */

	private SeekableInputStream myStream;

	private byte[] myBuffer = new byte[32768];
	private int myBufferStart;
	private int myBufferLength;
	private int myFirstHeader;

	private int mySeekPos;
	private int myLength;
	private int myShiftReg;

	// TODO: move these out of the class...
	private MP3Header myMP3Header;
	private VBRIHeader myVBRIHeader;
	private XingVBRHeader myXingHeader;

	private ITagExtractorListener myListener;

	public FrameInfoExtract(SeekableInputStream _stream, ITagExtractorListener _listener) throws IOException {
		myStream = _stream;
		myMP3Header = new MP3Header();
		myVBRIHeader = new VBRIHeader();
		myXingHeader = new XingVBRHeader();
		myListener = _listener;
	}

	public void extract(int _dataOffset) throws IOException {
		/* pdh 18-Sep-00: changed to check for CBR first, as only the CBR check
		 * searches for the header (i.e. skips ID3v2).
		 */
		seek(0);
		//mySeekPos = 0;
		myLength = (int) myStream.length();

		/* ral 17-Dec-01: We've seen some files, created with Musicmatch, and then "fixed"
		* with MP3 Tag Studio, which have the Xing VBR header at the start, followed by a
		* couple of bogus frames.  This causes us to skip the VBR header.
		*
		* We'll have a quick stab at reading it from immediately after the ID3v2 tag.
		*/
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

		/* pdh 31-Aug-01: just in case there was any junk after the id3v2 tag,
		 * don't believe the dataOffset passed in -- instead, use the position
		 * where the CBR search found sync.
		 */
		mySeekPos = myFirstHeader;

		// Try for a VBR header...
		if (!gotVBRHeader && isXingHeader()) {
			parseXingHeader();
		}
	}

	// get Xing header data
	protected boolean isVBRIHeader() throws IOException {
		int shiftRegister = getWord();

		myVBRIHeader.mpegAudioVersionID = ((shiftRegister & 0x00180000) >> 3) >> 16;
		myVBRIHeader.sampleRateIndex = ((shiftRegister & 0x00000c00) >> 2) >> 8;
		myVBRIHeader.channelMode = ((shiftRegister & 0x000000c0) >> 6);

		int layerDescription = ((shiftRegister & 0x00060000) >> 1) >> 16;
		int bitrateIndex = ((shiftRegister & 0x0000f000) >> 4) >> 8;

		if (myVBRIHeader.channelMode != 0) {
			return false;
		}

		int layerNumber = 4 - layerDescription;
		if (figureMP3Bitrate(myVBRIHeader.mpegAudioVersionID, layerNumber, bitrateIndex) != 160000) {
			return false;
		}

		mySeekPos += 0x20;

		if (getByte() != 'V')
			return false;
		if (getByte() != 'B')
			return false;
		if (getByte() != 'R')
			return false;
		if (getByte() != 'I')
			return false;

		myVBRIHeader.version = getWord();
		myVBRIHeader.quality = getByte() << 8;
		myVBRIHeader.quality |= getByte();
		myVBRIHeader.bytes = getWord();
		myVBRIHeader.frames = getWord();

		return true;
	}

	/*
	 * @bug The "1152" is wrong for MPEG-2 and -2.5 layer 3 -- does anyone
	 * generate VBRI headers on such (ultra-low-bitrate) files? Fix by copying
	 * what's done for Xing headers.
	 */
	protected void parseVBRIHeader() throws IOException {
		// No divide by 0's!
		int lengthMs = 0;
		int sampleRate = figureMP3Samplerate(myVBRIHeader.mpegAudioVersionID, myVBRIHeader.sampleRateIndex);

		if (sampleRate != 0) {
			lengthMs = (int) (((double) myVBRIHeader.frames / ((double) sampleRate / 1000.0)) * 1152.0);
		}

		String bitrate = figureAverageBitrateFromVBRI(lengthMs);
		if (myListener != null) {
			myListener.tagExtracted(DatabaseTags.DURATION_TAG, lengthMs);
			myListener.tagExtracted(DatabaseTags.BITRATE_TAG, bitrate);
			myListener.tagExtracted(DatabaseTags.SAMPLERATE_TAG, sampleRate);
			myListener.tagExtracted(DatabaseTags.STEREO_TAG, String.valueOf(myVBRIHeader.channelMode != 3));
		}
	}

	// get Xing header data
	protected boolean isXingHeader() throws IOException {
		/* pdh 17-Oct-02: we must get the whole header, as we need to know the
		 * frame size (and thus the mpeg version and layer numbers), see
		 * ParseXingHeader().
		 */
		if (!getMP3Sync(4)) {
			return false;
		}

		int id = myMP3Header.mpegVersionCode & 1;
		//int srIndex = myMP3Header.samplerateCode;
		int mode = myMP3Header.channelModeCode;

		mySeekPos += figureXingHeaderOffset(id, mode);

		if (getByte() != 'X')
			return false; // fail
		if (getByte() != 'i')
			return false; // header not found
		if (getByte() != 'n')
			return false;
		if (getByte() != 'g')
			return false;

		myXingHeader.id = id;

		myXingHeader.samprate = myMP3Header.samplerate;

		// get flags
		int headFlags = getWord();
		myXingHeader.flags = headFlags;

		if ((headFlags & FRAMES_FLAG) > 0) {
			myXingHeader.frames = getWord();
		}

		if ((headFlags & BYTES_FLAG) > 0) {
			myXingHeader.bytes = getWord();
		}

		if ((headFlags & TOC_FLAG) > 0) {
			for (int i = 0; i < 100; i++) {
				myXingHeader.toc[i] = (char) getByte();
			}
		}

		myXingHeader.vbrScale = 1;
		if ((headFlags & VBR_SCALE_FLAG) > 0) {
			myXingHeader.vbrScale = getWord();
		}

		return true; // success
	}

	protected int figureXingHeaderOffset(int _id, int _mode) {
		if (_id > 0) { // mpeg1
			if (_mode != 3) {
				return 32;
			}
			else {
				return 17;
			}
		}
		else {
			if (_mode != 3) {
				return 17;
			}
			else {
				return 9;
			}
		}
	}

	protected void parseXingHeader() throws IOException {
		// No divide by 0's!
		int lengthMs;
		if ((myXingHeader.samprate / 1000) != 0) {
			int mpegVersion = myMP3Header.mpegVersionCode;
			int layerNumber = myMP3Header.layerNumber;
			int frameLen;

			if (layerNumber == 1 || mpegVersion == 3) {
				frameLen = 1152;
			}
			else {
				// Half-size frames in MPEG2 & 2.5, layers 2 & 3
				frameLen = 576;
			}

			lengthMs = (int)((double)frameLen * ((double)myXingHeader.frames / ((double)myXingHeader.samprate / 1000.0)));
		}
		else {
			lengthMs = 0;
		}

		// Note the average bitrate

		String bitrate = figureAverageBitrateFromXing(lengthMs);
		if (myListener != null) {
			myListener.tagExtracted(DatabaseTags.DURATION_TAG, lengthMs);
			myListener.tagExtracted(DatabaseTags.BITRATE_TAG, bitrate);
			myListener.tagExtracted(DatabaseTags.SAMPLERATE_TAG, myXingHeader.samprate);
		}
	}

	protected String figureAverageBitrateFromVBRI(int _lengthMs) {
		StringBuffer bitrate = new StringBuffer();
		// Note the average bitrate
		bitrate.append('v');
		bitrate.append((myMP3Header.channelModeCode == 3) ? 'm' : 's');
		if ((_lengthMs / 1000) != 0) {
			bitrate.append((myVBRIHeader.bytes * 8) / _lengthMs);
		}
		else {
			// Unknown bitrate: use bitrate of first header
			bitrate.append(myMP3Header.bitrate / 1000);
		}
		return bitrate.toString();
	}

	protected String figureAverageBitrateFromXing(int _lengthMs) {
		StringBuffer bitrate = new StringBuffer();
		// Note the average bitrate
		bitrate.append('v');
		bitrate.append((myMP3Header.channelModeCode == 3) ? 'm' : 's');
		if ((_lengthMs / 1000) != 0) {
			bitrate.append((myXingHeader.bytes * 8) / _lengthMs);
		}
		else {
			// Unknown bitrate: use bitrate of first header
			bitrate.append(myMP3Header.bitrate / 1000);
		}
		return bitrate.toString();
	}

	protected void parseMP3Frames(boolean _gotXingHeader) throws IOException {
		if (myLength < 4) {
			throw new IOException("Invalid MP3.");
		}

		// It's a standard MP3 file.  Look for sync in the first 192Kb (3 lots of 64).
		int startPos = tell();
		int endPos = startPos + FIND_SYNC_BEFORE_BYTES_COUNT;

		if (endPos > (myLength - 4)) {
			endPos = (myLength - 4);
		}

		while (tell() < endPos) {
			if (getMP3Sync(65536)) {
				// We got sync
				int firstSamplerate = myMP3Header.samplerate;
				myFirstHeader = mySeekPos - 4;

				// To be valid, the next frame should be the right distance away.
				skip(myMP3Header.length - 4);
				if (getMP3Sync(4)) {
					// Looks good so far.  Do the sample rates match?
					if (firstSamplerate == myMP3Header.samplerate) {
						// They do.  There's a good chance that this is valid data.
						// Note down the bitrate and sample rate.
						StringBuffer bitrate = new StringBuffer();
						bitrate.append("f");
						bitrate.append((myMP3Header.channelModeCode == 3) ? 'm' : 's');
						bitrate.append(myMP3Header.bitrate / 1000);

						int lengthMs = 0;
						if ((myMP3Header.bitrate / 1000) != 0) {
							lengthMs = 8000 * ((myLength - myFirstHeader) / myMP3Header.bitrate);
						}

						if (myListener != null) {
							if (!_gotXingHeader) {
								myListener.tagExtracted(DatabaseTags.BITRATE_TAG, bitrate.toString());
								myListener.tagExtracted(DatabaseTags.SAMPLERATE_TAG, myMP3Header.samplerate);
								myListener.tagExtracted(DatabaseTags.DURATION_TAG, lengthMs);
								myListener.tagExtracted(DatabaseTags.STEREO_TAG, String.valueOf(myMP3Header.channelModeCode != 3));
							}

							// As this is the first frame, this is really where we
							// should be streaming from
							myListener.tagExtracted(DatabaseTags.OFFSET_TAG, myFirstHeader);

							if (myMP3Header.copyright > 0) {
								myListener.tagExtracted(DatabaseTags.COPYRIGHT_TAG, "yes");
							}
							else {
								myListener.tagExtracted(DatabaseTags.COPYRIGHT_TAG, "no");
							}

							if (myMP3Header.original > 0) {
								myListener.tagExtracted(DatabaseTags.COPY_TAG, "no");
							}
							else {
								myListener.tagExtracted(DatabaseTags.COPY_TAG, "yes");
							}

							return;
						}
					}
				}
				else {
					// Go back to before but skip a byte so we don't revisit the same sync.
					seek(myFirstHeader + 1);
				}
			}
			else {
				// Back up a bit, just in case we managed to straddle the sync...
				skip(-3);
			}
		}

		throw new IOException("Invalid MP3.");
	}

	protected boolean getMP3Sync(int _distance) throws IOException {
		// Search until end of file or distance bytes
		if ((mySeekPos + _distance) > myLength) {
			_distance = (myLength - mySeekPos);
		}
		int bound = mySeekPos + _distance;

		// Nothing in the shift register yet
		myShiftReg = 0;

		// On an empty drive, you can seek forever...
		while (mySeekPos < bound) {
			int curByte = getByte();
			if (curByte < 0) {
				return false;
			}

			myShiftReg = (myShiftReg << 8) | curByte;

			// Got sync yet?
			if ((myShiftReg & 0xffe00000) == 0xffe00000) {
				if (getMP3Header() > 0) {
					return true;
				}
			}
		}

		return false;
	}

	protected void skip(int _howFar) {
		if ((mySeekPos + _howFar) < myLength) {
			mySeekPos += _howFar;

			// The shift register will be junk
			myShiftReg = 0;
		}
	}

	protected void seek(int _pos) throws IOException {
		if (_pos > myLength) {
			_pos = myLength;
		}
		mySeekPos = _pos;

		// The shift register will be junk
		myShiftReg = 0;
	}

	protected int tell() {
		return mySeekPos;
	}

	protected boolean endOfFile() {
		boolean endOfFile = (mySeekPos >= myLength);
		return endOfFile;
	}

	protected int getByte() throws IOException {
		// Fill buffer as necessary
		if (mySeekPos < myBufferStart || (mySeekPos - myBufferStart) >= myBufferLength) {
			// Valid?
			if (mySeekPos >= myLength) {
				// Simulate reading zeros beyond the end
				mySeekPos++;
				return 0;
			}

			// Refill it
			myBufferStart = mySeekPos;
			myStream.seek(mySeekPos);
			myBufferLength = myStream.read(myBuffer, 0, myBuffer.length);
		}

		byte b = myBuffer[(mySeekPos++ -myBufferStart)];
		return TypeConversionUtils.toUnsigned8(b);
	}

	// See: http://www.dv.co.yu/mpgscript/mpeghdr.htm
	protected int getMP3Header() throws IOException {
		myMP3Header.layerCode = (myShiftReg & 0x60000) >> 17;

		// Invalid layer?
		if (myMP3Header.layerCode == 0) {
			//			throw new IOException("Invalid layerCode: " + myMP3Header.layerCode);
			Debug.println(Debug.INFORMATIVE, "Invalid layerCode: " + myMP3Header.layerCode);
			return 0;
		}

		myMP3Header.mpegVersionCode = (myShiftReg & 0x180000) >> 19;

		// Reserved version code
		if (myMP3Header.mpegVersionCode == 1) {
			//	    throw new IOException("Invalid mpegVersionCode: " + myMP3Header.mpegVersionCode);
			Debug.println(Debug.INFORMATIVE, "Invalid mpegVersionCode: " + myMP3Header.mpegVersionCode);
			return 0;
		}

		myMP3Header.bitrateCode = (myShiftReg & 0xf000) >> 12;

		// We don't do free format or invalid bitrates
		if (myMP3Header.bitrateCode == 0 || myMP3Header.bitrateCode == 0xf) {
			//			throw new IOException("Invalid bitrate code: " + myMP3Header.bitrateCode);
			Debug.println(Debug.INFORMATIVE, "Invalid bitrate code: " + myMP3Header.bitrateCode);
			return 0;
		}

		myMP3Header.samplerateCode = (myShiftReg & 0xc00) >> 10;

		// We don't do bad sample rates
		if (myMP3Header.samplerateCode == 0x3) {
			//			throw new IOException("Invalid samplerateCode: " + myMP3Header.samplerateCode);
			Debug.println(Debug.INFORMATIVE, "Invalid samplerateCode: " + myMP3Header.samplerateCode);
			return 0;
		}

		/* 0=stereo, 1=js, 2=dual channel, 3=mono */
		myMP3Header.channelModeCode = (myShiftReg & 0xc0) >> 6;

		myMP3Header.layerNumber = 4 - myMP3Header.layerCode;
		myMP3Header.padded = (myShiftReg & 0x200) >> 9;

		myMP3Header.copyright = (myShiftReg & 0x80) >> 3;
		myMP3Header.original = (myShiftReg & 0x40) >> 2;

		myMP3Header.bitrate = figureMP3Bitrate(myMP3Header.mpegVersionCode, myMP3Header.layerNumber, myMP3Header.bitrateCode);
		myMP3Header.samplerate = figureMP3Samplerate(myMP3Header.mpegVersionCode, myMP3Header.samplerateCode);

		if (myMP3Header.layerNumber == 1) {
			// MPEG 1, 2 & 2.5 layer 1
			myMP3Header.length = (12 * myMP3Header.bitrate) / myMP3Header.samplerate + myMP3Header.padded * 4;
		}
		else if (myMP3Header.mpegVersionCode == 3) {
			// MPEG 1 layers 2 & 3
			myMP3Header.length = (144 * myMP3Header.bitrate) / myMP3Header.samplerate + myMP3Header.padded;
		}
		else {
			// Frames are smaller here...
			myMP3Header.length = (72 * myMP3Header.bitrate) / myMP3Header.samplerate + myMP3Header.padded;
		}

		return myMP3Header.length;
	}

	protected int figureMP3Bitrate(int _mpegVersionCode, int _layerNumber, int _bitrateCode) {
		if (_layerNumber <= 0 || _layerNumber > 3) {
			Debug.println(Debug.INFORMATIVE, "Invalid layer number: " + _layerNumber);
			return -1;
		}

		int[][][] bitrateTable = new int[][][] {
			// MPEG2 & 2.5
			new int[][] {
				// Layer 1
				new int[] { 0, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256, -1 },
				// Layer 2
				new int[] { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, -1 },
				// Layer 3
				new int[] { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, -1 }
			},
			// MPEG1
			new int[][] {
				// Layer 1
				new int[] { 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, -1 },
				// Layer 2
				new int[] { 0, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384, -1 },
				// Layer 3
				new int[] { 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, -1 }
			}
		};

		return 1000 * bitrateTable[_mpegVersionCode & 1][_layerNumber - 1][_bitrateCode];
	}

	protected int figureMP3Samplerate(int _mpegVersionCode, int _samplerateCode) {
		int[][] samplerateTable = new int[][] {
			// MPEG Version 2.5
			new int[] { 11025, 12000, 8000, -1 },
			// Reserved
			new int[] { -1, -1, -1, -1 },
			// MPEG Version 2
			new int[] { 22050, 24000, 16000, -1 },
			// MPEG Version 1
			new int[] { 44100, 48000, 32000, -1 }
		};

		return samplerateTable[_mpegVersionCode][_samplerateCode];
	}

	protected int getWord() throws IOException {
		int w;

		w = getByte() << 24;
		w |= getByte() << 16;
		w |= getByte() << 8;
		w |= getByte();

		return w;
	}

	protected class MP3Header {
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

	protected class VBRIHeader {
		public int version;
		public int quality;
		public int bytes;
		public int frames;

		public int mpegAudioVersionID;
		public int sampleRateIndex;
		public int channelMode;
	}

	// A Xing header may be present in the ancillary
	// data field of the first frame of an mp3 bitstream
	// The Xing header (optionally) contains
	//      frames      total number of audio frames in the bitstream
	//      bytes       total number of bytes in the bitstream
	//      toc         table of contents

	// toc (table of contents) gives seek points
	// for random access
	// the ith entry determines the seek point for
	// i-percent duration
	// seek point in bytes = (toc[i]/256.0) * total_bitstream_bytes
	// e.g. half duration seek point = (toc[50]/256.0) * total_bitstream_bytes
	protected class XingVBRHeader {
		public int id; // from MPEG header, 0=MPEG2, 1=MPEG1
		public int samprate; // determined from MPEG header
		public int flags; // from Xing header data
		public int frames; // total bit stream frames from Xing header data
		public int bytes; // total bit stream bytes from Xing header data
		public int vbrScale; // encoded vbr scale from Xing header data
		public char[] toc = new char[256]; // table of contents
	}
}
