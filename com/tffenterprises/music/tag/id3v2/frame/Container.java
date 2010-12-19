/* Container - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.tffenterprises.io.ByteArrayOutputStream;
import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.id3v2.Frame;

public abstract class Container extends Frame
    implements Serializable, Cloneable
{
    private String frameid = "";
    private transient boolean changed = false;
    
    public Container() {
	/* empty */
    }
    
    public Container(String id) throws IllegalArgumentException {
	if (id == null)
	    throw new IllegalArgumentException
		      ("Attempting to give a null id to Container(String id).");
	frameid = id;
    }
    
    public Object clone() {
	Container newContainer = null;
	try {
	    newContainer = (Container) this.getClass().newInstance();
	} catch (Exception e) {
	    return null;
	}
	newContainer.frameid = frameid;
	newContainer.changed = changed;
	Enumeration frames = frames();
	while (frames.hasMoreElements())
	    newContainer.addFrame((Frame) frames.nextElement());
	return newContainer;
    }
    
    public void updateChecksum(DataOutputChecksum checksum) {
	checksum.writeBytes(this.getClass().getName());
	checksum.writeBytes(frameid);
	checksum.writeBoolean(changed);
	Enumeration frames = frames();
	while (frames.hasMoreElements())
	    checksum.write(((Frame) frames.nextElement()).getRawData());
    }
    
    public Checksum getChecksum() {
	DataOutputChecksum checksum = new DataOutputChecksum(new CRC32());
	updateChecksum(checksum);
	return checksum;
    }
    
    public final int hashCode() {
	return (int) getChecksum().getValue();
    }
    
    public synchronized boolean equals(Object other) {
	if (other == null)
	    return false;
	if (!other.getClass().equals(this.getClass()))
	    return false;
	Container oc = (Container) other;
	if (!frameid.equals(oc.frameid) || changed != oc.changed)
	    return false;
	if (size() != oc.size())
	    return false;
	Enumeration frames = frames();
	while (frames.hasMoreElements()) {
	    if (!oc.contains((Frame) frames.nextElement()))
		return false;
	}
	return true;
    }
    
    public synchronized String toString() {
	StringBuffer sb = new StringBuffer();
	Enumeration frames = frames();
	while (frames.hasMoreElements())
	    sb.append(((Frame) frames.nextElement()).toString() + "\n");
	sb.setLength(sb.length() - 1);
	return sb.toString();
    }
    
    public String getFrameID() {
	return frameid;
    }
    
    public byte getStatusFlags() {
	return (byte) -1;
    }
    
    public void setStatusFlags(byte flags) {
	/* empty */
    }
    
    public byte getFormatFlags() {
	return (byte) -1;
    }
    
    public void setFormatFlags(byte flags) {
	/* empty */
    }
    
    public boolean isChanged() {
	return changed;
    }
    
    public void setChanged(boolean changed) {
	this.changed = changed;
    }
    
    public synchronized byte[] getRawData() {
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	Enumeration frames = frames();
	while (frames.hasMoreElements())
	    bytes.write(((Frame) frames.nextElement()).getRawData());
	return bytes.toByteArray();
    }
    
    public void setRawData(byte[] rawData) {
	/* empty */
    }
    
    public byte[] toByteArray() {
	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	try {
	    writeTo(byteStream);
	} catch (IOException ioe) {
	    byteStream.reset();
	}
	return byteStream.toByteArray();
    }
    
    public synchronized void writeTo(OutputStream outputStream)
	throws IOException, IllegalArgumentException {
	if (outputStream == null)
	    throw new IllegalArgumentException
		      ("null output stream passed to BaseFrame.write().");
	Enumeration frames = frames();
	while (frames.hasMoreElements()) {
	    Frame f = (Frame) frames.nextElement();
	    f.writeTo(outputStream);
	}
    }
    
    public boolean isOfRepeatableType() {
	return false;
    }
    
    protected FrameHeader getHeader() {
	return null;
    }
    
    public abstract Frame addFrame(Frame frame)
	throws IllegalArgumentException;
    
    public abstract Frame removeFrame(Frame frame);
    
    public abstract Enumeration frames();
    
    public abstract boolean contains(Frame frame);
    
    public abstract boolean isEmpty();
    
    public abstract int size();
}
