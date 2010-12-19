/* Flags - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.tffenterprises.io.DataOutputChecksum;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.io.UnsynchronizedOutputStream;

public abstract class Flags implements Serializable, Cloneable
{
    public static final byte UNSYNCHRONIZATION_FLAG = -128;
    public static final byte EXPERIMENTAL_FLAG = 32;
    public static final String DEFAULT = "default";
    private static ResourceBundle FLAGS_CLASS_INFO;
    private static final Hashtable FLAGS_CLASSES = new Hashtable();
    private short version = 0;
    private byte flags = 0;
    private transient boolean changed = false;
    
    public class InputProcessingStream extends FilterInputStream
    {
	public InputProcessingStream(InputStream is) {
	    super(is);
	}
	
	public void endInputProcessing() {
	    if (in instanceof InputProcessingStream)
		((InputProcessingStream) in).endInputProcessing();
	}
    }
    
    static {
	String packageName = "com.tffenterprises.music.tag.id3v2";
	String bundleName = "com.tffenterprises.music.tag.id3v2.FlagsVersions";
	FLAGS_CLASS_INFO
	    = (ResourceBundle.getBundle
	       ("com.tffenterprises.music.tag.id3v2.FlagsVersions"));
    }
    
    public static Flags getNewInstance() {
	try {
	    String versionString = FLAGS_CLASS_INFO.getString("default");
	    StringTokenizer st = new StringTokenizer(versionString, ".");
	    if (st.countTokens() != 2)
		throw new TagDataFormatException();
	    byte major = Byte.parseByte(st.nextToken());
	    byte minor = Byte.parseByte(st.nextToken());
	    short version = (short) (major << 8 | minor);
	    return getNewInstance(version);
	} catch (MissingResourceException mre) {
	    System.err.println
		("No default ID3v2 version in file FlagsVersions.properties");
	    throw new RuntimeException(mre.toString());
	} catch (TagDataFormatException tdfe) {
	    System.err.println
		("Incorrect information in file FlagsVersions.properties");
	    throw new RuntimeException(tdfe.toString());
	}
    }
    
    public static Flags getNewInstance(short version)
	throws TagDataFormatException {
	Class flagsClass = GetFlagsClass(version);
	try {
	    Flags newFlags = (Flags) flagsClass.newInstance();
	    newFlags.version = version;
	    newFlags.checkVersion();
	    return newFlags;
	} catch (IllegalAccessException iae) {
	    throw new RuntimeException(iae.toString());
	} catch (InstantiationException ie) {
	    throw new RuntimeException(ie.toString());
	}
    }
    
    public static Flags getNewInstance(short version, byte flagByte)
	throws TagDataFormatException {
	Flags newFlags = getNewInstance(version);
	newFlags.setFlags(flagByte);
	return newFlags;
    }
    
    private static Class GetFlagsClass(short version)
	throws TagDataFormatException {
	Class flagsClass = (Class) FLAGS_CLASSES.get(new Short(version));
	int major = (version & 0xff00) >> 8;
	for (int minor = version & 0xff; flagsClass == null && minor >= 0;
	     minor--) {
	    String versionString
		= new String(String.valueOf(major) + "." + minor);
	    flagsClass = GetFlagsClass(versionString);
	}
	if (flagsClass == null) {
	    StringBuffer sb = new StringBuffer();
	    sb.append("Unknown ID3v2 version: no result when looking up ");
	    sb.append("Flags subclass for ID3v2 version ");
	    sb.append(String.valueOf((version & 0xff00) >> 8) + "."
		      + (version & 0xff));
	    throw new TagDataFormatException(sb.toString());
	}
	FLAGS_CLASSES.put(new Short(version), flagsClass);
	return flagsClass;
    }
    
    private static Class GetFlagsClass(String versionString) {
	String subclassName = "<unknown_class>";
	try {
	    subclassName = FLAGS_CLASS_INFO.getString(versionString);
	    return Class.forName(subclassName);
	} catch (ClassNotFoundException cnfe) {
	    System.err.println("Error in Flags configuration file: the class "
			       + subclassName + " was not found.");
	    throw new RuntimeException(cnfe.toString());
	} catch (MissingResourceException mre) {
	    return null;
	}
    }
    
    protected Flags() {
	this((byte) 0);
    }
    
    protected Flags(byte flags) {
	this.flags = flags;
    }
    
    public void updateChecksum(Checksum checksum) {
	DataOutputChecksum doc = new DataOutputChecksum(checksum);
	doc.writeBytes(this.getClass().getName());
	doc.writeByte(flags);
    }
    
    public final Checksum getChecksum() {
	Checksum ck = new CRC32();
	updateChecksum(ck);
	return ck;
    }
    
    public final int hashCode() {
	return (int) getChecksum().getValue();
    }
    
    public Object clone() {
	Flags newFlags = null;
	try {
	    newFlags = (Flags) this.getClass().newInstance();
	} catch (Exception e) {
	    System.err.println
		("Big problem in Flags.clone(); this is either a RuntimeException or a programmer error.");
	    throw new RuntimeException(e.toString());
	}
	newFlags.flags = flags;
	newFlags.changed = changed;
	return newFlags;
    }
    
    public boolean equals(Object other) {
	if (other == null)
	    return false;
	if (!other.getClass().equals(this.getClass()))
	    return false;
	Flags otherFlags = (Flags) other;
	if (flags != otherFlags.flags)
	    return false;
	return true;
    }
    
    public String toString() {
	char[] zeroes = { '0', '0', '0', '0', '0', '0', '0', '0' };
	String flagString = Integer.toBinaryString(flags & 0xff);
	return ("Flags: 0x" + new String(zeroes, 0, 8 - flagString.length())
		+ flagString);
    }
    
    public short getVersion() {
	return version;
    }
    
    public abstract void checkVersion() throws TagDataFormatException;
    
    public boolean isChanged() {
	return changed;
    }
    
    public void setChanged(boolean changed) {
	this.changed = changed;
    }
    
    public byte getFlags() {
	return flags;
    }
    
    public void setFlags(byte newFlags) {
	if (flags != newFlags) {
	    flags = newFlags;
	    setChanged(true);
	}
    }
    
    public boolean check(byte flagMask) {
	if ((flagMask & flags) == flagMask)
	    return true;
	return false;
    }
    
    public void set(byte flagMask) {
	setFlags((byte) (flags | flagMask));
    }
    
    public void unset(byte flagMask) {
	setFlags((byte) (flags & (flagMask ^ 0xffffffff)));
    }
    
    public boolean isMarkedExperimental() {
	return check((byte) 32);
    }
    
    public boolean usesUnsynchronization() {
	return check((byte) -128);
    }
    
    public InputStream processInput(InputStream is)
	throws IOException, TagDataFormatException {
	InputStream inStream = is;
	return inStream;
    }
    
    public OutputStream processOutput(OutputStream out) {
	return out;
    }
    
    protected OutputStream processUnsynchronization(final OutputStream out) {
	class ID3v2TagUnsynker extends FilterOutputStream
	{
	    ID3v2TagUnsynker() {
		super(new UnsynchronizedOutputStream(out));
	    }
	    
	    public void close() throws IOException {
		out.close();
		if (((UnsynchronizedOutputStream) out).hasUnsynchronized())
		    set((byte) -128);
		else
		    unset((byte) -128);
	    }
	};
	return new ID3v2TagUnsynker();
    }
}
