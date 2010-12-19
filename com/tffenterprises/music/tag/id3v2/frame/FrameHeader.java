/* FrameHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.tffenterprises.io.ByteArrayInputStream;
import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.PaddingException;

public abstract class FrameHeader implements Serializable, Cloneable
{
    private static ResourceBundle FRAMEHEADER_CLASS_INFO;
    private static Hashtable FRAMEHEADER_CLASSES = new Hashtable();
    private static final Class[] CONSTRUCTOR_PARAMS_TYPES_ARRAY = new Class[1];
    private static final String DEFAULT = "default";
    private String id = "";
    private transient int frameLength = 0;
    private transient int dataLength = -1;
    private transient boolean changed = false;
    protected static final String NULL_ID;
    /*synthetic*/ static Class class$0;
    
    static {
	String packageName = "com.tffenterprises.music.tag.id3v2.frame";
	String bundleName
	    = "com.tffenterprises.music.tag.id3v2.frame.FrameHeaderVersions";
	FRAMEHEADER_CLASS_INFO
	    = (ResourceBundle.getBundle
	       ("com.tffenterprises.music.tag.id3v2.frame.FrameHeaderVersions"));
	Class[] var_classes = CONSTRUCTOR_PARAMS_TYPES_ARRAY;
	int i = 0;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_ = Class.forName("java.io.InputStream");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	var_classes[i] = var_class;
	byte[] nulls = new byte[4];
	NULL_ID = new String(nulls);
    }
    
    public static boolean isValidFrameID(String frameID) {
	if (frameID == null || frameID.length() != 4)
	    return false;
	char[] charArray = frameID.toCharArray();
	for (int i = 0; i < 4; i++) {
	    if ((charArray[i] < 'A' || charArray[i] > 'Z')
		&& (charArray[i] < '0' || charArray[i] > '9'))
		return false;
	}
	return true;
    }
    
    public static FrameHeader getNewInstance() {
	try {
	    String versionString = FRAMEHEADER_CLASS_INFO.getString("default");
	    StringTokenizer st = new StringTokenizer(versionString, ".");
	    if (st.countTokens() != 2)
		throw new TagDataFormatException();
	    byte major = Byte.parseByte(st.nextToken());
	    byte minor = Byte.parseByte(st.nextToken());
	    short version = (short) (major << 8 | minor);
	    return getNewInstance(version);
	} catch (MissingResourceException mre) {
	    System.err.println
		("No default ID3v2 version in file FrameHeaderVersions.properties");
	    throw new RuntimeException(mre.toString());
	} catch (TagDataFormatException tdfe) {
	    System.err.println
		("Incorrect information in file FrameHeaderVersions.properties");
	    throw new RuntimeException(tdfe.toString());
	}
    }
    
    public static FrameHeader getNewInstance(short version)
	throws TagDataFormatException {
	try {
	    Class fhClass = GetFrameHeaderClass(version);
	    return (FrameHeader) fhClass.newInstance();
	} catch (IllegalAccessException iae) {
	    throw new RuntimeException(iae.toString());
	} catch (InstantiationException ie) {
	    throw new RuntimeException(ie.toString());
	}
    }
    
    public static FrameHeader getNewInstance(short version, String frameID)
	throws TagDataFormatException {
	FrameHeader header = getNewInstance(version);
	header.setFrameID(frameID);
	return header;
    }
    
    public static FrameHeader getNewInstance(short version, InputStream in)
	throws PaddingException, TagDataFormatException, IOException {
	Class fhClass = GetFrameHeaderClass(version);
	try {
	    Constructor constructor
		= fhClass.getConstructor(CONSTRUCTOR_PARAMS_TYPES_ARRAY);
	    Object[] params = new Object[1];
	    params[0] = in;
	    return (FrameHeader) constructor.newInstance(params);
	} catch (InvocationTargetException ite) {
	    Throwable te = ite.getTargetException();
	    if (te instanceof PaddingException)
		throw (PaddingException) te;
	    if (te instanceof IOException)
		throw (IOException) te;
	    if (te instanceof TagDataFormatException)
		throw (TagDataFormatException) te;
	    if (te instanceof RuntimeException)
		throw (RuntimeException) te;
	    te.printStackTrace();
	    throw new RuntimeException(te.toString());
	} catch (IllegalAccessException iae) {
	    throw new RuntimeException(iae.toString());
	} catch (InstantiationException ie) {
	    throw new RuntimeException(ie.toString());
	} catch (NoSuchMethodException nme) {
	    throw new RuntimeException(nme.toString());
	}
    }
    
    private static Class GetFrameHeaderClass(short version)
	throws TagDataFormatException {
	Class fhClass = (Class) FRAMEHEADER_CLASSES.get(new Short(version));
	int major = (version & 0xff00) >> 8;
	for (int minor = version & 0xff; fhClass == null && minor >= 0;
	     minor--) {
	    String versionString
		= new String(String.valueOf(major) + "." + minor);
	    fhClass = GetFrameHeaderClass(versionString);
	}
	if (fhClass == null) {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Unknown ID3v2 version: no result when looking up ");
	    sb.append("FrameHeader subclass for ID3v2 version ");
	    sb.append(String.valueOf((version & 0xff00) >> 8) + "."
		      + (version & 0xff));
	    throw new TagDataFormatException(sb.toString());
	}
	FRAMEHEADER_CLASSES.put(new Short(version), fhClass);
	return fhClass;
    }
    
    private static Class GetFrameHeaderClass(String version) {
	String subclassName = "";
	try {
	    subclassName = FRAMEHEADER_CLASS_INFO.getString(version);
	    return Class.forName(subclassName);
	} catch (ClassNotFoundException cnfe) {
	    System.err.println
		("Error in FrameHeaders configuration file: the class "
		 + subclassName + " was not found.");
	    throw new RuntimeException(cnfe.toString());
	} catch (MissingResourceException mre) {
	    return null;
	}
    }
    
    protected FrameHeader() {
	/* empty */
    }
    
    protected FrameHeader(InputStream in)
	throws PaddingException, TagDataFormatException, IOException {
	DataInputStream dataInput = new DataInputStream(in);
	byte[] idBytes = new byte[4];
	dataInput.readFully(idBytes);
	String newID = new String(idBytes);
	if (NULL_ID.equals(newID))
	    throw new PaddingException((long) newID.length());
	if (newID.length() > 3 && newID.charAt(3) == ' ')
	    newID = FrameHeader_2_0.GetEquivalenceForID(newID.substring(0, 3));
	if (isValidFrameID(newID))
	    id = newID;
	else
	    throw new TagDataFormatException("The string '" + newID + "'"
					     + "is not a valid Frame ID");
    }
    
    public void updateChecksum(DataOutputChecksum checksum) {
	checksum.writeBytes(this.getClass().getName());
	checksum.writeBytes(id);
	checksum.writeInt(frameLength);
	checksum.writeInt(dataLength);
	checksum.writeBoolean(changed);
    }
    
    public final Checksum getChecksum() {
	DataOutputChecksum checksum = new DataOutputChecksum(new CRC32());
	updateChecksum(checksum);
	return checksum;
    }
    
    public final int hashCode() {
	return (int) getChecksum().getValue();
    }
    
    public Object clone() {
	FrameHeader newHeader = null;
	try {
	    newHeader = (FrameHeader) this.getClass().newInstance();
	} catch (Exception e) {
	    String message
		= "Big problem in FrameHeader.clone(). This is either a RuntimeException or a programmer error.";
	    throw new RuntimeException(message + "\n" + e.toString());
	}
	newHeader.id = id;
	newHeader.frameLength = frameLength;
	newHeader.dataLength = dataLength;
	return newHeader;
    }
    
    public boolean equals(Object other) {
	if (other == null)
	    return false;
	if (!other.getClass().equals(this.getClass()))
	    return false;
	FrameHeader otherFH = (FrameHeader) other;
	if (!id.equals(otherFH.id) || frameLength != otherFH.frameLength
	    || dataLength != otherFH.dataLength)
	    return false;
	return true;
    }
    
    public String toString() {
	return "Frame ID: " + id;
    }
    
    public boolean isChanged() {
	return changed;
    }
    
    public void setChanged(boolean changed) {
	this.changed = changed;
    }
    
    public final String getFrameID() {
	return id;
    }
    
    public void setFrameID(String newID) {
	if (!isValidFrameID(newID))
	    throw new IllegalArgumentException(newID + " is a bad argument "
					       + "for " + this.getClass()
					       + "setFrameID()");
	if (!id.equals(newID)) {
	    id = newID;
	    setChanged(true);
	}
    }
    
    public int getFrameLength() {
	return frameLength;
    }
    
    protected final void setFrameLength(int theLength) {
	frameLength = theLength;
    }
    
    public int getDataLength() {
	return dataLength;
    }
    
    protected final void setDataLength(int theLength) {
	dataLength = theLength;
    }
    
    public byte getStatusFlags() {
	return (byte) 0;
    }
    
    public void setStatusFlags(byte flags) {
	/* empty */
    }
    
    public final boolean checkStatusMask(byte flagMask) {
	if ((flagMask & getStatusFlags()) == flagMask)
	    return true;
	return false;
    }
    
    public final void setStatusMask(byte flagMask) {
	setStatusFlags((byte) (getStatusFlags() | flagMask));
    }
    
    public final void unsetStatusMask(byte flagMask) {
	setStatusFlags((byte) (getStatusFlags() & (flagMask ^ 0xffffffff)));
    }
    
    public byte getFormatFlags() {
	return (byte) 0;
    }
    
    public void setFormatFlags(byte flags) {
	/* empty */
    }
    
    public final boolean checkFormatMask(byte flagMask) {
	if ((flagMask & getFormatFlags()) == flagMask)
	    return true;
	return false;
    }
    
    public final void setFormatMask(byte flagMask) {
	setFormatFlags((byte) (getFormatFlags() | flagMask));
    }
    
    public final void unsetFormatMask(byte flagMask) {
	setFormatFlags((byte) (getFormatFlags() & (flagMask ^ 0xffffffff)));
    }
    
    public boolean usesCompression() {
	return false;
    }
    
    public boolean usesGroupID() {
	return false;
    }
    
    public boolean usesEncryption() {
	return false;
    }
    
    public boolean usesUnsynchronization() {
	return false;
    }
    
    public boolean usesDataLengthIndicator() {
	return false;
    }
    
    public byte[] getFrameBytesFromStream
	(InputStream in, long availableBytes)
	throws IOException, TagDataFormatException, FrameDataFormatException {
	if ((long) frameLength > availableBytes)
	    throw new TagDataFormatException
		      ("Invalid number of bytes in stream");
	byte[] frameBytes = new byte[frameLength];
	int readBytes = in.read(frameBytes);
	ByteArrayInputStream bytes
	    = new ByteArrayInputStream(frameBytes, 0, readBytes);
	InputStream stream = processInput(bytes);
	if (stream == bytes)
	    return frameBytes;
	return decodeFrameBytesFromStream(stream);
    }
    
    private byte[] decodeFrameBytesFromStream(InputStream stream)
	throws FrameDataFormatException {
	byte[] frameBytes;
	try {
	    if (dataLength > -1) {
		frameBytes = new byte[dataLength];
		new DataInputStream(stream).readFully(frameBytes);
	    } else {
		frameBytes = new byte[frameLength];
		int readBytes = stream.read(frameBytes);
		if (readBytes < frameBytes.length) {
		    byte[] newBytes = new byte[readBytes];
		    System.arraycopy(frameBytes, 0, newBytes, 0,
				     newBytes.length);
		    frameBytes = newBytes;
		}
	    }
	} catch (IOException ioe) {
	    throw new FrameDataFormatException
		      ("An error occurred while decoding frame data: " + ioe);
	}
	return frameBytes;
    }
    
    public abstract void writeTo(OutputStream outputstream) throws IOException;
    
    protected InputStream processInput(InputStream in)
	throws FrameDataFormatException {
	return in;
    }
    
    protected OutputStream processOutput(OutputStream out) throws IOException {
	return out;
    }
}
