package org.jempeg.tags.id3;

import java.io.IOException;

import com.inzyme.typeconv.BigEndianUtils;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT8;
import com.inzyme.util.ReflectionUtils;

public class ID3V2Tag {
	public static final int ID3V2_VERSION = 4;
	public static final int ID3V2_TEXT_ENCODING_ISO8859_1 = 0;
	public static final int ID3V2_TEXT_ENCODING_UNICODE = 1;
	public static final int ID3V2_TEXT_ENCODING_UNICODE_BE = 2; /* only in v2.4 */
	public static final int ID3V2_TEXT_ENCODING_UTF8 = 3; /* only in v2.4 */

	private CharArray myFileIdentifier = new CharArray(3);
	private UINT8 myMinorVersion = new UINT8();
	private UINT8 myRevision = new UINT8();
	private UINT8 myFlags = new UINT8();
	private UINT32 mySize = new UINT32(); // syncsafe

	/* Followed in the file (but not in memory) by optional
	 * extended header, then the tags, then an optional
	 * extended footer, then some padding.
	 */

	public ID3V2Tag() {
	}

	public CharArray getFileIdentifier() {
		return myFileIdentifier;
	}

	public UINT8 getMinorVersion() {
		return myMinorVersion;
	}

	public UINT8 getRevision() {
		return myRevision;
	}

	public UINT8 getFlags() {
		return myFlags;
	}

	public UINT32 getSize() {
		return mySize;
	}

	public boolean isID3V2Tag() {
		return "ID3".equals(myFileIdentifier.getStringValue("ISO-8859-1"));
	}

	public void read(LittleEndianInputStream _is) throws IOException {
		myFileIdentifier.read(_is);
		myMinorVersion.read(_is);
		myRevision.read(_is);
		myFlags.read(_is);
		byte[] x = new byte[4];
		_is.read(x);
		mySize.setValue(ss32_to_cpu(x));
	}

	public static String describeTextEncoding(int _textEncoding) {
		switch (_textEncoding) {
			case ID3V2_TEXT_ENCODING_ISO8859_1 :
				return "ISO-8859-1";
			case ID3V2_TEXT_ENCODING_UNICODE :
				return "Unicode";
		}

		return "-unknown-";
	}

	public static final long ss32_to_cpu(byte[] _buffer) {
		long la = ((long) _buffer[0]) & 0xFF;
		long lb = ((long) _buffer[1]) & 0xFF;
		long lc = ((long) _buffer[2]) & 0xFF;
		long ld = ((long) _buffer[3]) & 0xFF;
		long value = (((((la << 7) | lb) << 7) | lc) << 7) | ld;
		return value;
	}

	public static final int ss24_to_cpu(byte[] _buffer) {
		int la = ((int) _buffer[0]) & 0xFF;
		int lb = ((int) _buffer[1]) & 0xFF;
		int lc = ((int) _buffer[2]) & 0xFF;
		int value = (((la << 7) | lb) << 7) | lc;
		return value;
	}

	//         Convert a plain 32-bit integer
	public static final long p32_to_cpu(byte[] _buffer) {
		return BigEndianUtils.toUnsigned32(_buffer[0], _buffer[1], _buffer[2], _buffer[3]);
	}

	//         Convert a plain 24-bit integer
	public static final int p24_to_cpu(byte[] _buffer) {
		return BigEndianUtils.toUnsigned24(_buffer[0], _buffer[1], _buffer[2]);
	}

	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
