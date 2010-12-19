/* FrameHeader_3_0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.tffenterprises.io.ByteArrayOutputStream;
import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.PaddingException;

public class FrameHeader_3_0 extends FrameHeader
    implements Serializable, Cloneable
{
    public static final byte TAG_ALTER_DISPOSE = -128;
    public static final byte FILE_ALTER_DISPOSE = 64;
    public static final byte READ_ONLY = 32;
    public static final byte COMPRESSED_FRAME = -128;
    public static final byte ENCRYPTED_FRAME = 64;
    public static final byte GROUPED_FRAME = 32;
    private byte statusFlags;
    private byte formatFlags;
    
    public FrameHeader_3_0() {
	/* empty */
    }
    
    public FrameHeader_3_0(InputStream in)
	throws IOException, PaddingException, TagDataFormatException {
	super(in);
	DataInputStream dataInput = new DataInputStream(in);
	super.setFrameLength(dataInput.readInt());
	statusFlags = (byte) (in.read() & 0xff);
	formatFlags = (byte) (in.read() & 0xff);
    }
    
    public void updateChecksum(DataOutputChecksum checksum) {
	super.updateChecksum(checksum);
	checksum.update(statusFlags);
	checksum.update(formatFlags);
    }
    
    public Object clone() {
	FrameHeader_3_0 o = (FrameHeader_3_0) super.clone();
	o.statusFlags = statusFlags;
	o.formatFlags = formatFlags;
	return o;
    }
    
    public boolean equals(Object other) {
	if (super.equals(other)) {
	    FrameHeader_3_0 of = (FrameHeader_3_0) other;
	    if (statusFlags == of.statusFlags && formatFlags == of.formatFlags)
		return true;
	    return false;
	}
	return false;
    }
    
    public String toString() {
	String basis = super.toString();
	if (statusFlags != 0 || formatFlags != 0) {
	    char[] zeroes = { '0', '0', '0', '0', '0', '0', '0', '0' };
	    String status = Integer.toBinaryString(statusFlags & 0xff);
	    String format = Integer.toBinaryString(formatFlags & 0xff);
	    return (basis + " (flags: Ox"
		    + new String(zeroes, 0, 8 - status.length()) + status
		    + " 0x" + new String(zeroes, 0, 8 - format.length())
		    + format + ")");
	}
	return basis;
    }
    
    public byte getStatusFlags() {
	return statusFlags;
    }
    
    public void setStatusFlags(byte flags) {
	statusFlags = flags;
    }
    
    public byte getFormatFlags() {
	return formatFlags;
    }
    
    public void setFormatFlags(byte flags) {
	formatFlags = flags;
    }
    
    public boolean usesCompression() {
	return checkFormatMask((byte) -128);
    }
    
    public void writeTo(OutputStream os) throws IOException {
	os.write(getFrameID().getBytes());
	new DataOutputStream(os).writeInt(getFrameLength());
	os.write(statusFlags);
	os.write(formatFlags);
    }
    
    protected InputStream processInput(InputStream in)
	throws FrameDataFormatException {
	InputStream input = super.processInput(in);
	byte SUPPORTED_FLAGS = -128;
	if ((formatFlags & (formatFlags ^ SUPPORTED_FLAGS)) != 0)
	    throw new FrameDataFormatException
		      ("An unknown or unhandled flag graces the format byte of this frame header. ("
		       + getFrameID() + ": "
		       + Integer.toBinaryString(formatFlags) + ")");
	try {
	    if (usesCompression())
		input = processDecompression(input);
	} catch (IOException ioe) {
	    throw new FrameDataFormatException
		      ("An error occurred while processing frame data: "
		       + ioe);
	}
	return input;
    }
    
    private InputStream processDecompression(final InputStream in)
	throws IOException {
	class ID3v230FrameInflater extends InflaterInputStream
	{
	    ID3v230FrameInflater() throws IOException {
		super(in);
		FrameHeader_3_0.this
		    .setDataLength(new DataInputStream(in).readInt());
	    }
	};
	return new ID3v230FrameInflater();
    }
    
    protected OutputStream processOutput(OutputStream out) throws IOException {
	OutputStream output = super.processOutput(out);
	if (usesCompression())
	    output = processCompression(output);
	formatFlags = (byte) (formatFlags & ~0x7f);
	return output;
    }
    
    private OutputStream processCompression(final OutputStream out) {
	class ID3v230FrameDeflater extends FilterOutputStream
	{
	    OutputStream real = null;
	    ByteArrayOutputStream data = null;
	    
	    ID3v230FrameDeflater() {
		super(new ByteArrayOutputStream());
		data = (ByteArrayOutputStream) out;
		real = out;
	    }
	    
	    public void flush() throws IOException {
		if (out != real) {
		    out = real;
		    data.close();
		    ByteArrayOutputStream compressed
			= new ByteArrayOutputStream(data.size());
		    DeflaterOutputStream deflater
			= new DeflaterOutputStream(compressed);
		    deflater.write(data.toByteArray());
		    deflater.finish();
		    deflater.close();
		    if (usesCompression() || ((double) (compressed.size() + 4)
					      < 0.8 * (double) data.size())) {
			new DataOutputStream(real).writeInt(data.size());
			real.write(compressed.toByteArray());
			FrameHeader_3_0.this.setFormatMask((byte) -128);
		    } else {
			real.write(data.toByteArray());
			FrameHeader_3_0.this.unsetFormatMask((byte) -128);
		    }
		}
		super.flush();
	    }
	};
	return new ID3v230FrameDeflater();
    }
}
