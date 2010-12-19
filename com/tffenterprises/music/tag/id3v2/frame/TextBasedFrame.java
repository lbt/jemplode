/* TextBasedFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import com.tffenterprises.io.ByteArrayInputStream;

public abstract class TextBasedFrame extends BaseFrame
    implements Serializable, Cloneable
{
    public static final String ISO_8859_1 = "ISO8859_1";
    public static final String UNICODE = "Unicode";
    public static final String UNICODE_BIG = "UnicodeBig";
    public static final String UNICODE_LITTLE = "UnicodeLittle";
    public static final String UTF8 = "UTF8";
    public static final char NULL_CHAR = '\0';
    public static final char BOM_CHAR = '\ufeff';
    public static final String NULL_CHAR_STRING;
    public static final String BOM_CHAR_STRING;
    public static final String NULL_BOM_STRING;
    public static final String[] ENCODING_STRINGS = { "ISO8859_1", "Unicode" };
    private static final Hashtable ENCODING_BYTES = new Hashtable();
    
    static {
	char[] NULL_CHAR_ARRAY = new char[1];
	char[] BOM_CHAR_ARRAY = { '\ufeff' };
	char[] NULL_BOM_ARRAY = { '\0', '\ufeff' };
	NULL_CHAR_STRING = new String(NULL_CHAR_ARRAY);
	BOM_CHAR_STRING = new String(BOM_CHAR_ARRAY);
	NULL_BOM_STRING = new String(NULL_BOM_ARRAY);
	for (byte b = 0; b < ENCODING_STRINGS.length; b++)
	    ENCODING_BYTES.put(ENCODING_STRINGS[b], new Byte(b));
	ENCODING_BYTES.put("UnicodeBig", ENCODING_BYTES.get("Unicode"));
	ENCODING_BYTES.put("UnicodeLittle", ENCODING_BYTES.get("Unicode"));
    }
    
    protected TextBasedFrame() {
	/* empty */
    }
    
    protected TextBasedFrame(FrameHeader header)
	throws IllegalArgumentException {
	super(header);
    }
    
    public boolean is8859String(String string) {
	if (string == null)
	    return false;
	char[] charArray = string.toCharArray();
	for (int i = 0; i < charArray.length; i++) {
	    if (charArray[i] > '\u00ff')
		return false;
	}
	return true;
    }
    
    public String getTextEncoding(ByteArrayInputStream in)
	throws FrameDataFormatException {
	return getTextEncoding((byte) in.read());
    }
    
    public String getTextEncoding(byte b) throws FrameDataFormatException {
	try {
	    return ENCODING_STRINGS[b];
	} catch (ArrayIndexOutOfBoundsException aoobe) {
	    throw new FrameDataFormatException
		      ("The encoding byte of the data passed to TextFrame.getEncoding() is invalid.");
	}
    }
    
    public byte[] getTextEncoding(String encoding)
	throws FrameDataFormatException {
	Byte b = (Byte) ENCODING_BYTES.get(encoding);
	if (b != null) {
	    byte[] enc = new byte[1];
	    enc[0] = b.byteValue();
	    return enc;
	}
	throw new FrameDataFormatException
		  ("The encoding byte of the data passed to TextFrame.getEncoding() is invalid.");
    }
    
    public byte[] getNullBytes(String encoding)
	throws FrameDataFormatException {
	if (encoding.equals("ISO8859_1"))
	    return new byte[1];
	if (encoding.startsWith("Unicode"))
	    return new byte[2];
	throw new FrameDataFormatException
		  ("The encoding byte of the data passed to TextFrame.getEncoding() is invalid.");
    }
    
    public String read8859String(ByteArrayInputStream in)
	throws FrameDataFormatException {
	return readString(in, "ISO8859_1");
    }
    
    public char[] read8859Characters(ByteArrayInputStream in)
	throws FrameDataFormatException {
	return readCharacters(in, "ISO8859_1");
    }
    
    public String readUnicodeString(ByteArrayInputStream in)
	throws FrameDataFormatException {
	return readString(in, "Unicode");
    }
    
    public char[] readUnicodeCharacters(ByteArrayInputStream in)
	throws FrameDataFormatException {
	return readCharacters(in, "Unicode");
    }
    
    protected String readString(ByteArrayInputStream in, String encoding)
	throws FrameDataFormatException {
	StringBuffer buffer = new StringBuffer();
	buffer.append(readCharacters(in, encoding));
	while (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == 0)
	    buffer.deleteCharAt(buffer.length() - 1);
	return buffer.toString();
    }
    
    protected char[] readCharacters
	(ByteArrayInputStream in, String encoding)
	throws FrameDataFormatException {
	char[] characters = new char[in.available()];
	try {
	    InputStreamReader isr = new InputStreamReader(in, encoding);
	    int read = isr.read(characters);
	    if (read != characters.length && read > 0) {
		char[] readCharacters = characters;
		characters = new char[read];
		System.arraycopy(readCharacters, 0, characters, 0,
				 characters.length);
	    }
	} catch (UnsupportedEncodingException uee) {
	    throw new FrameDataFormatException
		      ("Oddly, your VM seems to not fully support the "
		       + encoding + " encoding.");
	} catch (IOException ioe) {
	    throw new FrameDataFormatException
		      ("This is probably caused by a malformed " + encoding
		       + " " + "encoding in TextBasedFrame."
		       + "readCharacters(): " + ioe);
	}
	return characters;
    }
}
