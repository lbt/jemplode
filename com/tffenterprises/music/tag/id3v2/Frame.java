/* Frame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.Checksum;

import com.tffenterprises.io.ByteArrayInputStream;
import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.frame.FrameDataFormatException;
import com.tffenterprises.music.tag.id3v2.frame.FrameHeader;

public abstract class Frame implements Serializable, Cloneable
{
    private static final Class[] CONSTRUCTOR_TYPES;
    private static final Hashtable FRAME_CLASSES = new Hashtable();
    private static final Class FRAME_CLASS;
    private static final Class GENERIC_FRAME_CLASS;
    private static final Class TEXT_FRAME_CLASS;
    private static ResourceBundle FRAME_TYPE_CLASS_MAPPINGS;
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    
    static {
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_
		    = Class
			  .forName("com.tffenterprises.music.tag.id3v2.Frame");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	FRAME_CLASS = var_class;
	try {
	    String pname = FRAME_CLASS.getName();
	    pname = pname.substring(0, pname.lastIndexOf('.')) + ".frame";
	    GENERIC_FRAME_CLASS = Class.forName(pname + '.' + "GenericFrame");
	    TEXT_FRAME_CLASS = Class.forName(pname + '.' + "TextFrame");
	} catch (Exception e) {
	    throw new RuntimeException
		      ("Problem in the static initializer for Frame. Execution can not continue. The exception was: "
		       + e);
	}
	CONSTRUCTOR_TYPES = new Class[1];
	Class[] var_classes = CONSTRUCTOR_TYPES;
	int i = 0;
	Class var_class_1_ = class$1;
	if (var_class_1_ == null) {
	    Class var_class_2_;
	    try {
		var_class_2_
		    = (Class.forName
		       ("com.tffenterprises.music.tag.id3v2.frame.FrameHeader"));
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_1_ = class$1 = var_class_2_;
	}
	var_classes[i] = var_class_1_;
	String packageName = "com.tffenterprises.music.tag.id3v2.frame";
	String bundleName
	    = "com.tffenterprises.music.tag.id3v2.frame.FrameTypeToClassMappings";
	FRAME_TYPE_CLASS_MAPPINGS
	    = (ResourceBundle.getBundle
	       ("com.tffenterprises.music.tag.id3v2.frame.FrameTypeToClassMappings"));
    }
    
    public static Frame FromInputStream
	(short version, InputStream inputStream, long availableBytes)
	throws PaddingException, TagDataFormatException, IOException {
	Frame newFrame = null;
	try {
	    FrameHeader header
		= FrameHeader.getNewInstance(version, inputStream);
	    byte[] dataBytes
		= header.getFrameBytesFromStream(inputStream, availableBytes);
	    Class frameClass = GetFrameClassForID(header.getFrameID());
	    Constructor constructor
		= frameClass.getConstructor(CONSTRUCTOR_TYPES);
	    Object[] params = new Object[1];
	    params[0] = header;
	    newFrame = (Frame) constructor.newInstance(params);
	    newFrame.setRawData(dataBytes);
	} catch (NoSuchMethodException nsme) {
	    nsme.printStackTrace(System.err);
	    newFrame = null;
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace(System.err);
	    newFrame = null;
	} catch (InstantiationException ie) {
	    ie.printStackTrace(System.err);
	    newFrame = null;
	} catch (InvocationTargetException ite) {
	    Throwable te = ite.getTargetException();
	    te.printStackTrace(System.err);
	    newFrame = null;
	} catch (FrameDataFormatException fdfe) {
	    newFrame = null;
	} catch (IllegalArgumentException iae) {
	    iae.printStackTrace(System.err);
	    newFrame = null;
	}
	return newFrame;
    }
    
    public static Frame FromByteArray(short version, byte[] byteArray)
	throws TagDataFormatException, PaddingException {
	return FromByteArray(version, byteArray, 0);
    }
    
    public static Frame FromByteArray
	(short version, byte[] byteArray, int offset)
	throws TagDataFormatException, PaddingException {
	ByteArrayInputStream byteStream
	    = new ByteArrayInputStream(byteArray, offset,
				       byteArray.length - offset);
	try {
	    return FromInputStream(version, byteStream,
				   (long) byteStream.available());
	} catch (IOException e) {
	    return null;
	}
    }
    
    public static Frame getNewInstance(FrameHeader header, String theID) {
	header.setFrameID(theID);
	return getNewInstance(header);
    }
    
    public static Frame getNewInstance(String theID) {
	FrameHeader header = FrameHeader.getNewInstance();
	return getNewInstance(header, theID);
    }
    
    public static Frame getNewInstance(FrameHeader header) {
	try {
	    Class frameClass = GetFrameClassForID(header.getFrameID());
	    Constructor constructor
		= frameClass.getConstructor(CONSTRUCTOR_TYPES);
	    Object[] params = new Object[1];
	    params[0] = header;
	    return (Frame) constructor.newInstance(params);
	} catch (NoSuchMethodException nsme) {
	    nsme.printStackTrace(System.err);
	    throw new RuntimeException();
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace(System.err);
	    throw new RuntimeException();
	} catch (InstantiationException ie) {
	    ie.printStackTrace(System.err);
	    throw new RuntimeException();
	} catch (InvocationTargetException ite) {
	    Throwable te = ite.getTargetException();
	    te.printStackTrace(System.err);
	    return null;
	}
    }
    
    public static Class RegisterFrameClass
	(String frameID, Class frameClass) throws IllegalArgumentException {
	if (!FrameHeader.isValidFrameID(frameID))
	    throw new IllegalArgumentException("Invalid Frame ID: " + frameID);
	if (frameClass == null || !FRAME_CLASS.isAssignableFrom(frameClass))
	    throw new IllegalArgumentException("Invalid class (" + frameClass
					       + ") for ID: " + frameID);
	return (Class) FRAME_CLASSES.put(frameID, frameClass);
    }
    
    public static Class UnregisterFrameClass(String frameID) {
	return (Class) FRAME_CLASSES.remove(frameID);
    }
    
    public static Class GetFrameClassForID(String frameID) {
	Class frameClass = null;
	frameClass = (Class) FRAME_CLASSES.get(frameID);
	if (frameClass != null)
	    return frameClass;
	if (!FrameHeader.isValidFrameID(frameID))
	    throw new IllegalArgumentException("Invalid frame ID: " + frameID);
	try {
	    String classname = FRAME_TYPE_CLASS_MAPPINGS.getString(frameID);
	    frameClass = Class.forName(classname);
	    if (frameClass != null) {
		RegisterFrameClass(frameID, frameClass);
		return frameClass;
	    }
	} catch (MissingResourceException missingresourceexception) {
	    /* empty */
	} catch (ClassNotFoundException classnotfoundexception) {
	    /* empty */
	}
	try {
	    String packageName = GENERIC_FRAME_CLASS.getName();
	    packageName
		= packageName.substring(0, packageName.lastIndexOf('.'));
	    frameClass = Class.forName(packageName + '.' + frameID + "Frame");
	    if (frameClass != null) {
		RegisterFrameClass(frameID, frameClass);
		return frameClass;
	    }
	} catch (ClassNotFoundException classnotfoundexception) {
	    /* empty */
	}
	if (frameID.charAt(0) == 'T')
	    frameClass = TEXT_FRAME_CLASS;
	else if (frameID.charAt(0) == 'W')
	    frameClass = GENERIC_FRAME_CLASS;
	else
	    frameClass = GENERIC_FRAME_CLASS;
	RegisterFrameClass(frameID, frameClass);
	return frameClass;
    }
    
    public static int MinimumLength(short version) {
	int major = (version & 0xff00) >> 8;
	if (major >= 3)
	    return 11;
	if (major == 2)
	    return 7;
	throw new IllegalArgumentException("Invalid or unknown ID3v2 version");
    }
    
    public abstract Object clone();
    
    public abstract void updateChecksum(DataOutputChecksum dataoutputchecksum);
    
    public abstract Checksum getChecksum();
    
    public abstract int hashCode();
    
    public abstract boolean equals(Object object);
    
    public abstract String getFrameID();
    
    public abstract byte getFormatFlags();
    
    public abstract void setFormatFlags(byte i);
    
    public abstract byte getStatusFlags();
    
    public abstract void setStatusFlags(byte i);
    
    public abstract boolean isChanged();
    
    public abstract void setChanged(boolean bool);
    
    public abstract byte[] getRawData();
    
    public abstract void setRawData(byte[] is)
	throws FrameDataFormatException, IllegalArgumentException;
    
    public abstract String toString();
    
    public abstract byte[] toByteArray();
    
    public abstract void writeTo(OutputStream outputstream)
	throws IOException, IllegalArgumentException;
    
    public abstract boolean isOfRepeatableType();
    
    protected abstract FrameHeader getHeader();
}
