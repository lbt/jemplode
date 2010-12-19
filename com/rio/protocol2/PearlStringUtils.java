/* PearlStringUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.io.RefByteArrayOutputStream;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.LittleEndianByteToUINT32Buffer;
import com.inzyme.typeconv.LittleEndianOutputStream;

import org.jempeg.nodestore.PlaylistPair;

public class PearlStringUtils
{
    public static Properties NAME_TO_ENCODING = new Properties();
    
    static {
	NAME_TO_ENCODING.setProperty("playlist", "ISO-8859-1");
	NAME_TO_ENCODING.setProperty("profile", "ISO-8859-1");
    }
    
    public static String encodeString(byte[] _bytes) {
	try {
	    ByteArrayOutputStream result
		= new ByteArrayOutputStream(_bytes.length);
	    PrintStream ps = new PrintStream(result);
	    for (int i = 0; i < _bytes.length; i++) {
		byte b = _bytes[i];
		if (b == 10)
		    ps.print("\\n");
		else if (b < 0)
		    ps.write(b);
		else if (b < 32 || b == 127) {
		    ps.print("\\x");
		    StringUtils.padAppend(ps, (long) b, 2, 16);
		} else if (b == 92)
		    ps.print("\\\\");
		else
		    ps.write(b);
	    }
	    ps.close();
	    String str = new String(result.toByteArray(), "ISO-8859-1");
	    return str;
	} catch (UnsupportedEncodingException e) {
	    throw new ChainedRuntimeException("This should never happen.", e);
	}
    }
    
    protected static final int toXDigit(char _c) {
	if (_c >= '0' && _c <= '9')
	    return _c - '0';
	if (_c >= 'a' && _c <= 'f')
	    return _c - 'a' + '\n';
	if (_c >= 'A' && _c <= 'F')
	    return _c - 'A' + '\n';
	return 0;
    }
    
    protected static final boolean isXDigit(char _c) {
	if ((_c < '0' || _c > '9') && (_c < 'a' || _c > 'f')
	    && (_c < 'A' || _c > 'F'))
	    return false;
	return true;
    }
    
    public static byte[] decodeString(String _str) {
	int size = _str.length();
	ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
	for (int i = 0; i < size; i++) {
	    char c = _str.charAt(i);
	    if (c != '\\')
		baos.write(c);
	    else {
		char d = _str.charAt(i + 1);
		switch (d) {
		case 'n':
		    baos.write(10);
		    i++;
		    break;
		case '\\':
		    baos.write(92);
		    i++;
		    break;
		case 'x': {
		    char e = _str.charAt(i + 2);
		    char f = _str.charAt(i + 3);
		    if (isXDigit(e) && isXDigit(f)) {
			int c2 = toXDigit(e) * 16 + toXDigit(f);
			baos.write(c2);
			i += 3;
		    }
		    break;
		}
		}
	    }
	}
	return baos.toByteArray();
    }
    
    public static PlaylistPair[] getPlaylistPairs(String _playlistStr) {
	byte[] playlistBytes = decodeString(_playlistStr);
	LittleEndianByteToUINT32Buffer playlistBuffer
	    = new LittleEndianByteToUINT32Buffer(playlistBytes);
	int length = playlistBuffer.getUINT32Size();
	PlaylistPair[] pairs;
	if (length > 0 && (playlistBuffer.getUINT32(0) & ~0xfffffffL) == 0L) {
	    pairs = new PlaylistPair[(length - 1) / 2];
	    int fidNum = 0;
	    for (int i = 1; i < length; i += 2)
		pairs[fidNum++]
		    = new PlaylistPair(playlistBuffer.getUINT32(i),
				       playlistBuffer.getINT32(i + 1));
	} else {
	    pairs = new PlaylistPair[length];
	    for (int i = 0; i < length; i++)
		pairs[i] = new PlaylistPair(playlistBuffer.getUINT32(i), 0);
	}
	return pairs;
    }
    
    public static String getPlaylistString(PlaylistPair[] _playlistPairs) {
	try {
	    int childCount = _playlistPairs.length;
	    RefByteArrayOutputStream baos
		= new RefByteArrayOutputStream(4 * (1 + childCount * 2));
	    LittleEndianOutputStream eos = new LittleEndianOutputStream(baos);
	    eos.writeUnsigned32(767L);
	    for (int i = 0; i < childCount; i++) {
		eos.writeUnsigned32(_playlistPairs[i].getFID());
		eos.writeSigned32(_playlistPairs[i].getGeneration());
	    }
	    byte[] playlistBytes = baos.getByteArray();
	    String playlistStr = encodeString(playlistBytes);
	    return playlistStr;
	} catch (IOException e) {
	    throw new ChainedRuntimeException("This should never happen.", e);
	}
    }
}
