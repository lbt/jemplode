package org.jempeg.tags;

import java.io.IOException;

import org.jempeg.nodestore.DatabaseTags;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.model.GUID;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;
import com.inzyme.util.Debug;

public class ASFTagExtractor implements ITagExtractor {
	public int isTagExtractorFor(String _name, byte[] _header) {
		int isTagExtractor;
		// ASF
		if (TypeConversionUtils.toUnsigned8(_header[0]) == 0x30 && TypeConversionUtils.toUnsigned8(_header[1]) == 0x26 && TypeConversionUtils.toUnsigned8(_header[2]) == 0xB2 && TypeConversionUtils.toUnsigned8(_header[3]) == 0x75) {
			isTagExtractor = ITagExtractor.YES;

			// WAV
		}
		else if (_header[8] == 'W' && _header[9] == 'A' && _header[10] == 'V' && _header[11] == 'E') {
			isTagExtractor = ITagExtractor.YES;
		}
		else {
			isTagExtractor = ITagExtractor.NO;
		}
		return isTagExtractor;
	}

	public void extractTags(IImportFile _file, ITagExtractorListener _listener) throws IOException {
		SeekableInputStream stream = _file.getSeekableInputStream();
		try {
			extractTags(stream, _listener);
		}
		finally {
			stream.close();
		}
	}

	public void extractTags(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException {
		_listener.tagExtracted(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_TUNE);
		_listener.tagExtracted(DatabaseTags.CODEC_TAG, DatabaseTags.CODEC_WMA);

		_is.seek(0);

		LittleEndianInputStream eis = new LittleEndianInputStream(_is);
		WMAHeader header = new WMAHeader();
		header.read(eis);

		if (!header.myObj.myGuid.equals(WMAHeaderGUID) || (header.myReserved1 != 0x01) || (header.myReserved2 != 0x02)) {
			throw new IOException("Stream does not contain ASF data.");
		}

		boolean foundProperties = false;
		long dataStartOffset = 0;

		long numberHeaders = header.myNumberHeaders.getValue();
		for (int i = 0; i <= numberHeaders; i++) {
			long pos = _is.tell();

			WMAObject obj = new WMAObject();
			obj.read(eis);

			// Dispatch based on the GUID....
			GUID guid = obj.getGuid();
			if (guid.equals(WMAContentDescriptionGUID)) {
				extractContentDescription(obj, eis, _listener);
			}
			else if (guid.equals(WMAPropertiesGUID)) {
				extractProperties(obj, eis, _listener);
				foundProperties = true;
			}
			else if (guid.equals(WMAUnknownDRM1GUID)) {
				extractUnknownDRM(obj, eis, _listener);
			}
			else if (guid.equals(WMATextTagsGUID)) {
				extractTextTags(obj, eis, _listener);
			}
			else if (guid.equals(WMAStreamPropertiesGUID)) {
				extractStreamProperties(obj, eis, _listener);
			}
			else if (guid.equals(WMADataSectionGUID)) {
				dataStartOffset = pos;
			}
			else {
				Debug.println(Debug.INFORMATIVE, "Unrecognized: " + guid);
			}

			pos += obj.getSize().getValue();
			_is.seek(pos);
		}

		if (!foundProperties) {
			throw new IOException("Unable to find a valid WMA properties header.");
		}

		long dataEndOffset = _is.length();
		_is.seek(0);
		String rid = RID.calculateRid(_is, dataStartOffset, dataEndOffset);
		_listener.tagExtracted(DatabaseTags.RID_TAG, rid);
		_listener.tagExtracted(DatabaseTags.OFFSET_TAG, 0);
	}

	protected void extractContentDescription(WMAObject _obj, LittleEndianInputStream _eis, ITagExtractorListener _listener) throws IOException {
		WMAContentDescription contentDescription = new WMAContentDescription(_obj);
		contentDescription.read(_eis);

		tagExtracted(_listener, DatabaseTags.TITLE_TAG, contentDescription.getTitle());
		tagExtracted(_listener, DatabaseTags.ARTIST_TAG, contentDescription.getAuthor());
		tagExtracted(_listener, DatabaseTags.COMMENT_TAG, contentDescription.getDescription());
	}

	protected void extractProperties(WMAObject _obj, LittleEndianInputStream _eis, ITagExtractorListener _listener) throws IOException {
		WMAProperties properties = new WMAProperties(_obj);
		properties.read(_eis);

		// We want duration in ms
		long durationMs = convert100nsToMilliseconds(properties.getPlayDuration100ns());

		// According to some piece of documentation I found, we have to
		// adjust the duration by 'preroll'.  Which, in true MS fashion is in
		// completely different units from the rest.  Fortunately, we _want_
		// the result in milliseconds.
		long adjustedDurationMs = durationMs - properties.getPrerollMs().getValue();
		_listener.tagExtracted(DatabaseTags.DURATION_TAG, adjustedDurationMs);

		/// We want bitrate, as xyNNN where x = f or v, y = s or m, and NNN is in Kbps.
		EmpegBitrate eb = new EmpegBitrate();
		eb.setVBR(false);
		eb.setChannels(2);
		eb.setBitsPerSecond((int) properties.getMaximumBitRate().getValue());
		_listener.tagExtracted(DatabaseTags.BITRATE_TAG, eb.toString());
	}

	protected void extractUnknownDRM(WMAObject _obj, LittleEndianInputStream _eis, ITagExtractorListener _listener) throws IOException {
		throw new IOException("ASF stream has DRM");
	}

	protected void extractTextTags(WMAObject _obj, LittleEndianInputStream _eis, ITagExtractorListener _listener) throws IOException {
		UINT16 val = new UINT16();
		val.read(_eis);

		int numTags = val.getValue();
		for (int i = 0; i < numTags; i++) {
			val.read(_eis);
			String name = _eis.readUTF16LE(val.getValue());

			val.read(_eis);
			// unknown

			val.read(_eis);
			String value = _eis.readUTF16LE(val.getValue());

			// Map from the WMA tag to the one we care about...
			if (name.equals("WM/AlbumTitle")) {
				tagExtracted(_listener, DatabaseTags.SOURCE_TAG, value);
			}
			else if (name.equals("WM/Genre")) {
				tagExtracted(_listener, DatabaseTags.GENRE_TAG, value);
			}
			else if (name.equals("WM/Year")) {
				tagExtracted(_listener, DatabaseTags.YEAR_TAG, value);
			}
			else if (name.equals("WM/Track")) {
				long trackNumber;
				try {
					trackNumber = Integer.parseInt(value) + 1;
				}
				catch (NumberFormatException e) {
					trackNumber = 1;
				}
				tagExtracted(_listener, DatabaseTags.TRACKNR_TAG, String.valueOf(trackNumber));
				tagExtracted(_listener, "file_id", String.valueOf(trackNumber));
			}
		}
	}

	protected void extractStreamProperties(WMAObject _obj, LittleEndianInputStream _eis, ITagExtractorListener _listener) throws IOException {
		WMAStreamProperties streamProperties = new WMAStreamProperties(_obj);
		streamProperties.read(_eis);
		if (streamProperties.getStreamType().equals(WMAStreamTypeAudioGUID)) {
			// Then it's followed by a WAVEFORMATEX, go poking around in it.
			WaveFormtEx waveFormat = new WaveFormtEx();
			waveFormat.read(_eis);
			long samplerate = waveFormat.getNumSamplesPerSec().getValue();
			_listener.tagExtracted(DatabaseTags.SAMPLERATE_TAG, samplerate);
		}
	}

	protected void tagExtracted(ITagExtractorListener _listener, String _name, String _value) throws IOException {
		_listener.tagExtracted(_name, _value);
	}

	protected long convert100nsToMilliseconds(UINT64 _value) {
		return (_value.getValue() / CONVERT_100NS_TO_MS);
	}

	private static final long CONVERT_100NS_TO_MS = 10000L;
	//private static final long FILETIME_SECOND     = 10000000L;
	//private static final long FILETIME_MINUTE     = (60 * FILETIME_SECOND);
	//private static final long FILETIME_HOUR       = (60 * FILETIME_MINUTE);
	//private static final long FILETIME_DAY        = (24 * FILETIME_HOUR);

	private static final GUID WMAHeaderGUID = new GUID(0x75b22630L, 0x668e, 0x11cf, 0xa6, 0xd9, 0x0, 0xaa, 0x0, 0x62, 0xce, 0x6c);
	private static final GUID WMAContentDescriptionGUID = new GUID(0x75b22633L, 0x668e, 0x11cf, 0xa6, 0xd9, 0x0, 0xaa, 0x0, 0x62, 0xce, 0x6c);
	private static final GUID WMAPropertiesGUID = new GUID(0x8cabdca1L, 0xa947, 0x11cf, 0x8e, 0xe4, 0x0, 0xc0, 0xc, 0x20, 0x53, 0x65);
	private static final GUID WMAUnknownDRM1GUID = new GUID(0x1EFB1A30L, 0x0B62, 0x11D0, 0xA3, 0x9B, 0x00, 0xA0, 0xC9, 0x03, 0x48, 0xF6);
	private static final GUID WMATextTagsGUID = new GUID(0xD2D0A440L, 0xE307, 0x11D2, 0x97, 0xF0, 0x00, 0xA0, 0xC9, 0x5E, 0xA8, 0x50);
	private static final GUID WMAStreamPropertiesGUID = new GUID(0xb7dc0791L, 0xa9b7, 0x11cf, 0x8e, 0xe6, 0x00, 0xc0, 0x0c, 0x20, 0x53, 0x65);
	private static final GUID WMAStreamTypeAudioGUID = new GUID(0xf8699e40L, 0x5b4d, 0x11cf, 0xa8, 0xfd, 0x00, 0x80, 0x5f, 0x5c, 0x44, 0x2b);
	private static final GUID WMADataSectionGUID = new GUID(0x75b22636, 0x668e, 0x11cf, 0xa6, 0xd9, 0x0, 0xaa, 0x0, 0x62, 0xce, 0x6c);

	protected static class WMAObject {
		private GUID myGuid = new GUID();
		private UINT64 mySize = new UINT64();

		public WMAObject() {
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

	protected static class WMAHeader {
		public WMAObject myObj = new WMAObject();
		public UINT32 myNumberHeaders = new UINT32();
		public byte myReserved1; // should be 1
		public byte myReserved2; // should be 2

		public void read(LittleEndianInputStream _is) throws IOException {
			myObj.read(_is);
			myNumberHeaders.read(_is);
			myReserved1 = (byte) _is.read();
			myReserved2 = (byte) _is.read();
		}
	}

	protected static class WMAContentDescription {
		private String myTitle;
		private String myAuthor;
		private String myCopyright;
		private String myDescription;
		private String myRating;

		public WMAContentDescription(WMAObject _obj) {
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

	protected static class WMAProperties {
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

	protected static class WMAStreamProperties {
		private GUID myStreamType = new GUID();
		private GUID myErrorCorrectionType = new GUID();
		private UINT64 myOffset = new UINT64();
		private UINT32 myTypeSpecificLen = new UINT32();
		private UINT32 myErrorCorrectionDataLen = new UINT32();
		private UINT16 myStreamNumber = new UINT16();
		private UINT32 myReserved = new UINT32();

		public WMAStreamProperties(WMAObject _obj) {
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

	protected static class WaveFormtEx {
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
}
