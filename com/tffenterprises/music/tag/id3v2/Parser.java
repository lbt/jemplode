/* Parser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.tffenterprises.music.tag.id3v2.io.UnsynchronizedInputStream;

public abstract class Parser
{
    public static final String PREFIX = "ID3";
    private static final int HEADERLENGTH = 10;
    
    public static void ParseBytes(byte[] array) throws IOException {
	ParseStream(new ByteArrayInputStream(array));
    }
    
    public static void ParseStream(InputStream in) throws IOException {
	DataInputStream bs = null;
	if (in instanceof DataInputStream)
	    bs = (DataInputStream) in;
	else
	    bs = new DataInputStream(in);
	int fileLength = bs.available();
	if (bs.read() != 3 || bs.read() != 0)
	    throw new IllegalArgumentException
		      ("The byte array does not have a valid header, or the ID3v2 version number is not 3.0");
	byte flags = bs.readByte();
	int dataLength = 0;
	dataLength += bs.readUnsignedByte() * 128 * 128 * 128;
	dataLength += bs.readUnsignedByte() * 128 * 128;
	dataLength += bs.readUnsignedByte() * 128;
	dataLength += bs.readUnsignedByte();
	int padLength = 0;
	int afterHeader = bs.available();
	System.out.println("Tag flags are " + (flags & 0xff));
	if ((flags & 0x80) == 128)
	    bs = new DataInputStream(new UnsynchronizedInputStream(in));
	if ((flags & 0x40) == 64) {
	    int extendedHeaderLength = bs.readInt();
	    System.out.println("Extended header is " + extendedHeaderLength
			       + " bytes.");
	    short extFlags = bs.readShort();
	    padLength = bs.readInt();
	    if ((extFlags & 0x8000) == 32768) {
		System.out.println("This tag is checksummed");
		int i = bs.readInt();
	    }
	}
	if (padLength > 0)
	    dataLength -= padLength;
	System.out.println("Lengths: " + fileLength + ", " + "Tag Data: "
			   + dataLength + ", " + "Padding: " + padLength);
	while (afterHeader - dataLength < bs.available() - 10) {
	    System.out.println("Available: " + bs.available());
	    byte[] idBuf = new byte[4];
	    bs.readFully(idBuf);
	    if (idBuf[0] == 0 && idBuf[1] == 0 && idBuf[2] == 0
		&& idBuf[3] == 0) {
		System.out.println("We have reached padding");
		padLength = dataLength - (afterHeader
					  - (bs.available() + idBuf.length));
		dataLength -= padLength;
	    } else {
		String frameID = new String(idBuf);
		int frameLen = bs.readInt();
		int rawFrameLen = frameLen;
		System.out.println(frameID + ": " + frameLen + " bytes");
		short FrameFlags = bs.readShort();
		DataInputStream frameStream = bs;
		if ((FrameFlags & 0x80) == 128) {
		    System.out.println("Frame " + frameID + " is compressed");
		    int uncompressedLength = bs.readInt();
		    System.out.println("Should decompress from "
				       + (frameLen - 4) + " to "
				       + uncompressedLength + " bytes.");
		    System.out.println("Available: " + bs.available());
		    frameLen = uncompressedLength - 4;
		    rawFrameLen -= 4;
		    frameStream
			= new DataInputStream(new InflaterInputStream(bs));
		}
		if (frameID.charAt(0) == 'T') {
		    byte encoding = frameStream.readByte();
		    if (!frameID.equals("TXXX")) {
			if (encoding == 0) {
			    byte[] stringBytes = new byte[frameLen - 1];
			    frameStream.readFully(stringBytes);
			    System.out.println(new String(stringBytes,
							  "ISO8859_1"));
			} else if (encoding == 1) {
			    int BOM = frameStream.readUnsignedShort();
			    byte[] stringBytes = new byte[frameLen - 3];
			    frameStream.readFully(stringBytes);
			    if (BOM == 65279) {
				System.out.println("UnicodeBig");
				System.out.println(new String(stringBytes,
							      "UnicodeBig"));
			    } else if (BOM == 65534) {
				System.out.println("UnicodeLittle");
				System.out.println
				    (new String(stringBytes, "UnicodeLittle"));
			    }
			}
		    } else {
			String desc = "";
			String value = "";
			if (encoding == 0) {
			    byte[] stringBytes = new byte[frameLen - 1];
			    frameStream.readFully(stringBytes);
			    String fullField
				= new String(stringBytes, "ISO8859_1");
			    int separatorIndex = fullField.indexOf('\0');
			    if (separatorIndex == -1)
				desc = fullField;
			    else {
				desc = fullField.substring(0, separatorIndex);
				value
				    = fullField.substring(separatorIndex + 1);
			    }
			} else if (encoding == 1) {
			    int BOM = frameStream.readUnsignedShort();
			    String theEncoding = "Unicode";
			    byte[] stringBytes = new byte[frameLen - 3];
			    frameStream.readFully(stringBytes);
			    if (BOM == 65534)
				theEncoding = "UnicodeBig";
			    else if (BOM == 65279)
				theEncoding = "UnicodeLittle";
			    desc = new String(stringBytes, theEncoding);
			    int offset = 2 * desc.length() + 2;
			    value = new String(stringBytes, offset,
					       stringBytes.length - offset,
					       theEncoding);
			}
			System.out.println(desc + ": " + value);
		    }
		} else {
		    byte[] skipArray = new byte[rawFrameLen];
		    long skipped = (long) bs.read(skipArray);
		    System.out.println(String.valueOf(skipped)
				       + " bytes were skipped.");
		    System.out.println(String.valueOf(bs.available())
				       + " bytes remain in byte array.");
		}
	    }
	}
	System.out.println("Lengths: " + fileLength + ", " + "Tag Data: "
			   + dataLength + ", " + "Padding: " + padLength);
    }
    
    public static void main(String[] argv) throws IOException {
	FileInputStream fis = new FileInputStream(argv[0]);
	byte[] tagtag = new byte[3];
	fis.read(tagtag);
	if ("ID3".equals(new String(tagtag)))
	    ParseStream(fis);
    }
}
