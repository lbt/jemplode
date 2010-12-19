/* Flags_3_0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.io.UnsynchronizedInputStream;

class Flags_3_0 extends Flags implements Serializable, Cloneable
{
    public static final short CRC_EXTFLAG = -32768;
    public static final byte EXTENDED_HEADER_FLAG = 64;
    private short extendedFlags = 0;
    private transient int paddingLength = 0;
    private transient long tagCRC32 = 0L;
    
    protected Flags_3_0() {
	this((byte) 0, (short) 0);
    }
    
    protected Flags_3_0(byte flags) {
	this(flags, (short) 0);
    }
    
    protected Flags_3_0(byte flags, short extFlags) {
	super(flags);
	extendedFlags = extFlags;
    }
    
    public void updateChecksum(DataOutputChecksum checksum) {
	super.updateChecksum(checksum);
	checksum.writeShort(extendedFlags);
	checksum.writeShort(paddingLength);
	checksum.writeLong(tagCRC32);
    }
    
    public boolean equals(Object other) {
	if (super.equals(other)) {
	    Flags_3_0 of = (Flags_3_0) other;
	    if (extendedFlags == of.extendedFlags
		&& paddingLength == of.paddingLength
		&& tagCRC32 == of.tagCRC32)
		return true;
	}
	return false;
    }
    
    public String toString() {
	if (usesExtendedHeader()) {
	    char[] zeroes = { '0', '0', '0', '0', '0', '0', '0', '0' };
	    String highString
		= Integer.toBinaryString((extendedFlags & 0xff00) >> 8);
	    String lowString = Integer.toBinaryString(extendedFlags & 0xff);
	    String realString
		= ("Extended Flags: 0x"
		   + new String(zeroes, 0, 8 - highString.length())
		   + highString + " 0x"
		   + new String(zeroes, 0, 8 - lowString.length())
		   + lowString);
	    return super.toString() + ",\n" + realString;
	}
	return super.toString();
    }
    
    public short getExtFlags() {
	return extendedFlags;
    }
    
    public void setExtFlags(short newFlags) {
	if (extendedFlags != newFlags) {
	    extendedFlags = newFlags;
	    setChanged(true);
	}
    }
    
    public boolean checkExtFlagMask(short flagMask) {
	if ((flagMask & extendedFlags) == flagMask)
	    return true;
	return false;
    }
    
    public void setExtFlagMask(short flagMask) {
	setExtFlags((short) (extendedFlags | flagMask));
    }
    
    public void unsetExtFlagMask(short flagMask) {
	setExtFlags((short) (extendedFlags & (flagMask ^ 0xffffffff)));
    }
    
    public void checkVersion() throws TagDataFormatException {
	if ((getVersion() & 0xff00) != 768)
	    throw new TagDataFormatException
		      (this.getClass().getName() + " is "
		       + "not compatible with protocol version "
		       + Integer.toHexString(getVersion()));
    }
    
    public boolean usesExtendedHeader() {
	return check((byte) 64);
    }
    
    public boolean usesCRC32() {
	return checkExtFlagMask((short) -32768);
    }
    
    public InputStream processInput(InputStream in)
	throws IOException, TagDataFormatException {
	InputStream inStream = super.processInput(in);
	if (usesUnsynchronization())
	    inStream = new UnsynchronizedInputStream(inStream);
	if (usesExtendedHeader()) {
	    int remainingBytes = readExtendedHeader(inStream);
	    if (remainingBytes > 0)
		inStream.skip((long) remainingBytes);
	}
	inStream = processExtendedFlags(inStream);
	return inStream;
    }
    
    protected int readExtendedHeader(InputStream in)
	throws IOException, TagDataFormatException {
	DataInputStream bs = new DataInputStream(in);
	int remainInExtendedHdr = bs.readInt();
	short extendedFlags = bs.readShort();
	remainInExtendedHdr -= 2;
	int paddingLength = bs.readInt();
	remainInExtendedHdr -= 4;
	if (usesCRC32()) {
	    if (remainInExtendedHdr < 4)
		throw new TagDataFormatException
			  ("File Error: missing bytes in extended header structure.");
	    tagCRC32 = (long) (bs.readInt() & 0xffffffff);
	}
	return remainInExtendedHdr;
    }
    
    protected InputStream processExtendedFlags(InputStream in)
	throws IOException {
	InputStream inStream = in;
	if (usesCRC32())
	    inStream = processCRC32ExtFlag(inStream);
	return inStream;
    }
    
    protected InputStream processCRC32ExtFlag(final InputStream is) {
	class ID3v2CRC32ExtFlagChecker extends Flags.InputProcessingStream
	{
	    long crc;
	    
	    ID3v2CRC32ExtFlagChecker(long crc32) {
		super(new CheckedInputStream(is, new CRC32()));
		crc = crc32;
	    }
	    
	    public void endInputProcessing() {
		super.endInputProcessing();
		CheckedInputStream cis = (CheckedInputStream) in;
		if (crc != 0) {
		    /* empty */
		}
		cis.getChecksum().getValue();
	    }
	};
	return new ID3v2CRC32ExtFlagChecker(tagCRC32);
    }
    
    public OutputStream processOutput(OutputStream out) {
	OutputStream output = super.processOutput(out);
	unset((byte) 64);
	unset((byte) -128);
	return processUnsynchronization(output);
    }
}
