/* APICFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.tffenterprises.io.ByteArrayInputStream;
import com.tffenterprises.io.ByteArrayOutputStream;

public class APICFrame extends TextBasedFrame
    implements KeyedFrame, Serializable, Cloneable
{
    private static final String MY_ID = "APIC";
    private String imageDescription = "";
    private String imageMimeType = "";
    private byte imageType = 0;
    private byte[] imageData;
    
    public APICFrame() {
	imageData = new byte[0];
	super.getHeader().setFrameID("APIC");
    }
    
    public APICFrame(FrameHeader header) throws IllegalArgumentException {
	super(header);
	imageData = new byte[0];
	header.setFrameID("APIC");
    }
    
    public APICFrame(FrameHeader header, String description, byte[] image)
	throws IllegalArgumentException {
	this(header);
	if (description == null || image == null)
	    throw new IllegalArgumentException
		      ("A null argument was passed to the APICFrame constructor.");
	imageDescription = description;
	imageData = image;
    }
    
    public synchronized Object clone() {
	APICFrame newFrame = (APICFrame) super.clone();
	newFrame.imageDescription = imageDescription;
	newFrame.imageData = imageData;
	newFrame.imageType = imageType;
	newFrame.imageMimeType = imageMimeType;
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	APICFrame otherFrame = (APICFrame) other;
	if (otherFrame.imageDescription == null
	    || !otherFrame.imageDescription.equals(imageDescription)
	    || otherFrame.imageMimeType == null
	    || !otherFrame.imageMimeType.equals(imageMimeType)
	    || otherFrame.imageType != imageType
	    || otherFrame.imageData == null && imageData != null
	    || otherFrame.imageData != null && imageData == null)
	    return false;
	if (imageData != null) {
	    if (imageData.length != otherFrame.imageData.length)
		return false;
	    for (int i = 0; i < imageData.length; i++) {
		if (imageData[i] != otherFrame.imageData[i])
		    return false;
	    }
	}
	return true;
    }
    
    public String toString() {
	String superstring = getHeader().toString();
	return (superstring + "\n" + "  Description = " + imageDescription
		+ "\n" + "  MIME type   = " + imageMimeType + "\n"
		+ "  Picturetype = " + imageType + "\n" + "  Data length = "
		+ imageData.length);
    }
    
    public byte[] getRawData() {
	if (imageDescription.length() + imageData.length > 0) {
	    String encoding = "UnicodeBig";
	    if (is8859String(imageDescription))
		encoding = "ISO8859_1";
	    if (imageData == null)
		return new byte[0];
	    int len = 200 + imageData.length;
	    ByteArrayOutputStream bs = new ByteArrayOutputStream(len);
	    try {
		bs.write(getTextEncoding(encoding));
		bs.write(imageMimeType.getBytes());
		bs.write(0);
		bs.writeByte(imageType);
		bs.write(imageDescription.getBytes(encoding));
		bs.write(getNullBytes(encoding));
		bs.write(imageData);
	    } catch (UnsupportedEncodingException uee) {
		throw new RuntimeException
			  ("There is something quite wrong with your java VM: it does not support the "
			   + encoding + " encoding");
	    } catch (FrameDataFormatException fdfe) {
		fdfe.printStackTrace(System.err);
		return new byte[0];
	    }
	    return bs.toByteArray();
	}
	return null;
    }
    
    public void setRawData(byte[] rawData)
	throws FrameDataFormatException, IllegalArgumentException {
	ByteArrayInputStream raw = new ByteArrayInputStream(rawData);
	String encoding = getTextEncoding(raw);
	try {
	    StringBuffer sb = new StringBuffer();
	    do {
		int c = raw.read();
		if (c != -1)
		    sb.append((char) c);
		else
		    sb.append(0);
	    } while (sb.charAt(sb.length() - 1) != 0);
	    imageMimeType = sb.toString();
	} catch (Exception e) {
	    throw new FrameDataFormatException(e.toString());
	}
	int b = raw.read();
	if (b == -1)
	    throw new FrameDataFormatException("Premature end of stream");
	imageType = (byte) b;
	try {
	    int increment = 1;
	    if (encoding.startsWith("Unicode")) {
		increment = 2;
		raw.mark(rawData.length);
		char first = raw.readChar();
		if (first != '\ufeff' && first != '\ufffe')
		    encoding = "UnicodeBig";
		raw.reset();
	    }
	    byte[] bytes = new byte[64 * increment];
	    raw.mark(rawData.length);
	    int i = 0;
	    for (i = 0; i < 64 * increment; i++) {
		int c = raw.read();
		if (c != -1)
		    bytes[i] = (byte) c;
		else
		    bytes[i] = (byte) 0;
		if (bytes[i] == 0 && bytes[i - increment + 1] == 0)
		    break;
	    }
	    imageDescription = new String(bytes, 0, i, encoding);
	} catch (Exception e) {
	    throw new FrameDataFormatException(e.toString());
	}
	int imageLength = raw.available();
	imageData = new byte[imageLength];
	imageLength = raw.read(imageData);
	if (imageLength != imageData.length) {
	    if (imageLength < 1)
		imageData = null;
	    else {
		byte[] imageAsRead = imageData;
		imageData = new byte[imageLength];
		System.arraycopy(imageAsRead, 0, imageData, 0, imageLength);
	    }
	}
    }
    
    public boolean isOfRepeatableType() {
	return true;
    }
    
    public String getDescription() {
	return imageDescription;
    }
    
    public void setDescription(String frameDescription) {
	if (frameDescription == null)
	    throw new IllegalArgumentException
		      ("null text passed to APICFrame.setDescription()");
	imageDescription = frameDescription;
	setChanged(true);
    }
    
    public byte[] getImageData() {
	return imageData;
    }
    
    public void setImageData(byte[] imageBytes) {
	if (imageBytes == null)
	    throw new IllegalArgumentException
		      ("null byte array passed to APICFrame.setImageData()");
	imageData = imageBytes;
	setChanged(true);
    }
}
