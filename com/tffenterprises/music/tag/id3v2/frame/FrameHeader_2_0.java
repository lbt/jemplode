/* FrameHeader_2_0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.PaddingException;

public class FrameHeader_2_0 extends FrameHeader
    implements Serializable, Cloneable
{
    private String oldID = null;
    private static ResourceBundle TYPE_MAPPING_INFO;
    
    static {
	String packageName = "com.tffenterprises.music.tag.id3v2.frame.";
	String bundleName
	    = "com.tffenterprises.music.tag.id3v2.frame.2_TypeMappings";
	TYPE_MAPPING_INFO
	    = (ResourceBundle.getBundle
	       ("com.tffenterprises.music.tag.id3v2.frame.2_TypeMappings"));
    }
    
    public FrameHeader_2_0() {
	/* empty */
    }
    
    public FrameHeader_2_0(InputStream in)
	throws PaddingException, IOException, TagDataFormatException {
	this();
	DataInputStream dataInput = new DataInputStream(in);
	byte[] idBytes = new byte[3];
	dataInput.readFully(idBytes);
	oldID = new String(idBytes);
	if (NULL_ID.startsWith(oldID))
	    throw new PaddingException((long) oldID.length());
	if (isValidFrameID(oldID))
	    super.setFrameID(GetEquivalenceForID(oldID));
	else
	    throw new TagDataFormatException(oldID + " is not a valid Frame "
					     + "ID for ID3v2.2.x");
	int theLength = (256 * dataInput.readUnsignedByte()
			 + dataInput.readUnsignedShort());
	super.setFrameLength(theLength);
    }
    
    public static String GetEquivalenceForID(String id) {
	if (isValidFrameID(id)) {
	    try {
		return TYPE_MAPPING_INFO.getString(id);
	    } catch (MissingResourceException mre) {
		return "Z" + id;
	    }
	}
	return null;
    }
    
    public static boolean isValidFrameID(String id) {
	if (id == null || id.length() != 3)
	    return false;
	char[] charArray = id.toCharArray();
	for (int i = 0; i < charArray.length; i++) {
	    if ((charArray[i] < 'A' || charArray[i] > 'Z')
		&& (charArray[i] < '0' || charArray[i] > '9'))
		return false;
	}
	return true;
    }
    
    public void updateChecksum(DataOutputChecksum checksum) {
	super.updateChecksum(checksum);
	checksum.update(oldID.getBytes(), 0, oldID.length());
    }
    
    public Object clone() {
	FrameHeader_2_0 newHeader = (FrameHeader_2_0) super.clone();
	newHeader.oldID = oldID;
	return newHeader;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	return ((FrameHeader_2_0) other).oldID.equals(oldID);
    }
    
    public String toString() {
	return super.toString() + ", (old ID: " + oldID + ")";
    }
    
    public void setFrameID(String newID) {
	if (!newID.equals(super.getFrameID())) {
	    if (isValidFrameID(newID)) {
		oldID = newID;
		super.setFrameID(GetEquivalenceForID(newID));
	    } else if (FrameHeader.isValidFrameID(newID)) {
		oldID = "unknown";
		super.setFrameID(newID);
	    } else
		throw new IllegalArgumentException
			  ("The ID " + newID + " is an "
			   + "invalid frame ID for all known "
			   + "versions of the ID3v2 " + "specification.");
	}
    }
    
    public void writeTo(OutputStream os) throws IOException {
	os.write(oldID.getBytes());
	int frameLength = getFrameLength();
	if ((frameLength & ~0xffffff) == 0) {
	    os.write(frameLength & 0xff0000);
	    os.write(frameLength & 0xff00);
	    os.write(frameLength & 0xff);
	} else
	    throw new IOException
		      ("The frame length is too large for an ID3v2.2 tag: "
		       + frameLength);
    }
}
