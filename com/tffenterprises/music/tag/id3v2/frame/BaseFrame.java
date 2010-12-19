/* BaseFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.tffenterprises.io.ByteArrayOutputStream;
import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.id3v2.Frame;

public abstract class BaseFrame extends Frame
    implements Serializable, Cloneable
{
    private FrameHeader header = null;
    private transient boolean changed = false;
    
    protected BaseFrame() {
	header = FrameHeader.getNewInstance();
    }
    
    protected BaseFrame(FrameHeader header) throws IllegalArgumentException {
	if (header == null)
	    throw new IllegalArgumentException
		      ("Attempting to give a null header to BaseFrame(FrameHeader).");
	this.header = header;
    }
    
    public Object clone() {
	BaseFrame newframe = null;
	try {
	    newframe = (BaseFrame) this.getClass().newInstance();
	} catch (Exception e) {
	    return null;
	}
	newframe.header = (FrameHeader) header.clone();
	return newframe;
    }
    
    public void updateChecksum(DataOutputChecksum checksum) {
	header.updateChecksum(checksum);
	checksum.writeBytes(this.getClass().getName());
	checksum.write(getRawData());
    }
    
    public final Checksum getChecksum() {
	DataOutputChecksum checksum = new DataOutputChecksum(new CRC32());
	updateChecksum(checksum);
	return checksum.getChecksum();
    }
    
    public final int hashCode() {
	return (int) getChecksum().getValue();
    }
    
    public boolean equals(Object other) {
	if (other != null && other.getClass().equals(this.getClass())) {
	    BaseFrame of = (BaseFrame) other;
	    if (header.equals(of.header))
		return true;
	}
	return false;
    }
    
    public String toString() {
	return header.toString();
    }
    
    public String getFrameID() {
	return header.getFrameID();
    }
    
    public byte getStatusFlags() {
	return header.getStatusFlags();
    }
    
    public void setStatusFlags(byte flags) {
	header.setStatusFlags(flags);
    }
    
    public byte getFormatFlags() {
	return header.getFormatFlags();
    }
    
    public void setFormatFlags(byte flags) {
	header.setFormatFlags(flags);
    }
    
    public boolean isChanged() {
	return changed;
    }
    
    public void setChanged(boolean changed) {
	this.changed = changed;
    }
    
    public abstract byte[] getRawData();
    
    public abstract void setRawData(byte[] is)
	throws FrameDataFormatException, IllegalArgumentException;
    
    public byte[] toByteArray() {
	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	byte[] frameData = getBytes();
	try {
	    if (frameData != null && frameData.length > 0)
		writeFrameBytesToStream(byteStream, frameData);
	} catch (IOException ioe) {
	    byteStream.reset();
	}
	return byteStream.toByteArray();
    }
    
    public void writeTo(OutputStream outputStream)
	throws IOException, IllegalArgumentException {
	if (outputStream == null)
	    throw new IllegalArgumentException
		      ("null output stream passed to BaseFrame.write().");
	byte[] frameData = getBytes();
	if (frameData != null && frameData.length > 0)
	    writeFrameBytesToStream(outputStream, frameData);
    }
    
    private void writeFrameBytesToStream
	(OutputStream os, byte[] frameBytes) throws IOException {
	ByteArrayOutputStream bytes
	    = new ByteArrayOutputStream(2 * header.getFrameLength());
	OutputStream stream = header.processOutput(bytes);
	stream.write(frameBytes);
	stream.close();
	header.setFrameLength(bytes.size());
	header.writeTo(os);
	bytes.writeTo(os);
    }
    
    public boolean isOfRepeatableType() {
	return false;
    }
    
    protected final FrameHeader getHeader() {
	return header;
    }
    
    protected byte[] getBytes() {
	return getRawData();
    }
}
