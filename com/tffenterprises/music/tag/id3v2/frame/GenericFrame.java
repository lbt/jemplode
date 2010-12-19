/* GenericFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;

public class GenericFrame extends BaseFrame implements Serializable, Cloneable
{
    private byte[] frameData;
    
    protected GenericFrame() {
	frameData = new byte[0];
	super.getHeader().setFrameID("XXXX");
    }
    
    public GenericFrame(FrameHeader header) {
	super(header);
	frameData = new byte[0];
    }
    
    public GenericFrame(FrameHeader header, byte[] frameData)
	throws IllegalArgumentException {
	super(header);
	this.frameData = new byte[0];
	if (frameData == null)
	    throw new IllegalArgumentException
		      ("Null argument passed to GenericFrame constructor.");
	this.frameData = frameData;
    }
    
    public synchronized Object clone() {
	GenericFrame newFrame = (GenericFrame) super.clone();
	newFrame.frameData = new byte[frameData.length];
	System.arraycopy(frameData, 0, newFrame.frameData, 0,
			 frameData.length);
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	GenericFrame otherFrame = (GenericFrame) other;
	if (otherFrame.frameData == null
	    || otherFrame.frameData.length != frameData.length)
	    return false;
	for (int i = 0; i < frameData.length; i++) {
	    if (otherFrame.frameData[i] != frameData[i])
		return false;
	}
	return true;
    }
    
    public byte[] getRawData() {
	if (frameData.length > 0) {
	    byte[] frameDataCopy = new byte[frameData.length];
	    System.arraycopy(frameData, 0, frameDataCopy, 0, frameData.length);
	    return frameDataCopy;
	}
	return null;
    }
    
    public void setRawData(byte[] rawData) throws IllegalArgumentException {
	if (rawData == null)
	    throw new IllegalArgumentException
		      ("null byte array passed to GenericFrame.setRawData().");
	byte[] newData = new byte[rawData.length];
	System.arraycopy(rawData, 0, newData, 0, rawData.length);
	frameData = newData;
    }
    
    public String toString() {
	return super.toString() + ": " + frameData.length + " bytes.";
    }
    
    public boolean isOfRepeatableType() {
	return true;
    }
    
    protected byte[] getBytes() {
	return frameData;
    }
}
