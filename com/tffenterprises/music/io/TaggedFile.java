/* TaggedFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.io;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import com.tffenterprises.music.tag.ID3Tag;
import com.tffenterprises.music.tag.ID3v1;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.util.Buffer;

public class TaggedFile
{
    public static String ReadOnly = "r";
    public static String ReadWrite = "rw";
    private RandomAccessFile raFile = null;
    private boolean checkedID3v1 = false;
    private boolean tagID3v1present = false;
    private boolean checkedID3v2 = false;
    private boolean tagID3v2present = false;
    /*synthetic*/ static Class class$0;
    
    public TaggedFile(File theFile, String openmode)
	throws SecurityException, IOException {
	if (theFile.isFile())
	    raFile = new RandomAccessFile(theFile, openmode);
	else
	    throw new FileNotFoundException(theFile.getPath()
					    + " cannot be opened");
    }
    
    public TaggedFile(String pathname, String openmode)
	throws SecurityException, IOException {
	this(new File(pathname), openmode);
    }
    
    public RandomAccessFile getRAFile() {
	return raFile;
    }
    
    public void close() {
	try {
	    raFile.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
	raFile = null;
    }
    
    public long length() throws IOException {
	return raFile.length();
    }
    
    public boolean hasID3v1() throws IOException {
	if (!checkedID3v1) {
	    tagID3v1present = ID3v1.CheckFileForTag(raFile);
	    checkedID3v1 = true;
	}
	return tagID3v1present;
    }
    
    public boolean hasID3v2() throws IOException {
	if (!checkedID3v2) {
	    tagID3v2present = ID3v2.CheckFileForTag(raFile);
	    checkedID3v2 = true;
	}
	return tagID3v2present;
    }
    
    public boolean hasTag() throws IOException {
	if (!hasID3v2() && !hasID3v1())
	    return false;
	return true;
    }
    
    public void writeID3v1(ID3v1 tag) throws IOException {
	if (tag != null) {
	    if (hasID3v1())
		raFile.seek(ID3v1.GetTagOffset(raFile));
	    else {
		raFile.seek(raFile.length());
		raFile.write("TAG".getBytes());
	    }
	    raFile.write(tag.getBytes());
	    tagID3v1present = true;
	    tag.setChanged(false);
	}
    }
    
    public void removeID3v1() throws IOException {
	while (hasID3v1()) {
	    try {
		Class[] args = { Long.TYPE };
		java.lang.reflect.Method m
		    = raFile.getClass().getMethod("setLength", args);
	    } catch (NoSuchMethodException nsme) {
		throw new IOException("Cannot shorten a file with this JDK!");
	    }
	    raFile
		.setLength(ID3v1.GetTagOffset(raFile) - (long) "TAG".length());
	    tagID3v1present = false;
	    checkedID3v1 = false;
	}
    }
    
    public void writeID3v2(ID3v2 tag) throws IOException {
	if (tag != null) {
	    if (hasID3v2()) {
		int length
		    = Math.max(ID3v2.GetTagLength(raFile) + "ID3".length(),
			       tag.getMinimalLength());
		writeID3v2(tag, length);
	    } else
		writeID3v2(tag, -1);
	}
    }
    
    public void writeID3v2(ID3v2 tag, int newLengthInFile) throws IOException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	tag.writeTo(bos, newLengthInFile);
	writeID3v2Bytes(bos.toByteArray());
	tag.setChanged(false);
    }
    
    public void writeID3v2Bytes(byte[] tagBytes) throws IOException {
	int curLength = 0;
	if (hasID3v2())
	    curLength = ID3v2.GetTagLength(raFile);
	if (hasID3v2() && tagBytes.length == curLength) {
	    raFile.seek(0L);
	    raFile.write("ID3".getBytes());
	    raFile.write(tagBytes);
	} else {
	    if (hasID3v2() && tagBytes.length < curLength) {
		try {
		    Class[] args = { Long.TYPE };
		    java.lang.reflect.Method m
			= raFile.getClass().getMethod("setLength", args);
		} catch (NoSuchMethodException nsme) {
		    throw new IOException
			      ("Cannot shorten a file with this JDK!");
		}
	    }
	    adjustFileLength(tagBytes);
	}
	tagID3v2present = true;
    }
    
    private void adjustFileLength(byte[] tagBytes) throws IOException {
	boolean debug = false;
	int buflen = 524288;
	if (buflen < tagBytes.length + "ID3".length())
	    buflen = tagBytes.length + "ID3".length() + 1;
	Vector buffers = new Vector(2);
	Buffer first = new Buffer(buflen);
	Buffer second = new Buffer(buflen);
	Buffer bbb = new Buffer(buflen);
	byte[] buf = new byte[buflen];
	long writeloc = 0L;
	long readloc = 0L;
	if (hasID3v2())
	    readloc = (long) ("ID3".length() + ID3v2.GetTagLength(raFile));
	raFile.seek(readloc);
	int read = raFile.read(buf);
	if (read > 0) {
	    first.setData(buf, read);
	    buffers.addElement(first);
	    readloc = raFile.getFilePointer();
	    read = raFile.read(buf);
	    if (read > 0) {
		second.setData(buf, read);
		buffers.addElement(second);
	    }
	}
	readloc = raFile.getFilePointer();
	raFile.seek(writeloc);
	raFile.write("ID3".getBytes());
	raFile.write(tagBytes);
	while (read > 0) {
	    writeloc = raFile.getFilePointer();
	    raFile.seek(readloc);
	    read = raFile.read(buf);
	    if (read > 0) {
		bbb.setData(buf, read);
		buffers.addElement(bbb);
	    }
	    readloc = raFile.getFilePointer();
	    bbb = (Buffer) buffers.elementAt(0);
	    buffers.removeElementAt(0);
	    raFile.seek(writeloc);
	    raFile.write(bbb.getData(), 0, bbb.getDataLength());
	}
	while (buffers.size() > 0) {
	    bbb = (Buffer) buffers.elementAt(0);
	    buffers.removeElementAt(0);
	    raFile.write(bbb.getData(), 0, bbb.getDataLength());
	}
	try {
	    raFile.setLength(raFile.getFilePointer());
	} catch (NoSuchMethodError nosuchmethoderror) {
	    /* empty */
	}
	buffers = null;
    }
    
    public void writeTag(ID3Tag tag) throws IOException {
	if (tag instanceof ID3v1)
	    writeID3v1((ID3v1) tag);
	else if (tag instanceof ID3v2)
	    writeID3v2((ID3v2) tag);
	else
	    throw new IllegalArgumentException
		      ("Unknown subclass of com.tffenterprises.music.Tag TaggedFile.writeTag()");
    }
    
    public ID3v1 getID3v1() throws IOException {
	if (!hasID3v1())
	    return null;
	byte[] tagArray = new byte[ID3v1.GetTagLength(raFile)];
	raFile.seek(ID3v1.GetTagOffset(raFile));
	raFile.read(tagArray);
	return new ID3v1(tagArray);
    }
    
    public byte[] getID3v2Bytes() throws IOException {
	if (!hasID3v2())
	    return null;
	byte[] tagArray = new byte[ID3v2.GetTagLength(raFile)];
	raFile.seek(ID3v2.GetTagOffset(raFile));
	raFile.read(tagArray);
	return tagArray;
    }
    
    public ID3v2 getID3v2() throws IOException {
	byte[] tagArray = getID3v2Bytes();
	if (tagArray != null) {
	    try {
		return new ID3v2(tagArray);
	    } catch (TagDataFormatException tdfe) {
		return null;
	    }
	}
	return null;
    }
    
    public ID3Tag getTag() throws IOException {
	if (hasID3v2())
	    return getID3v2();
	if (hasID3v1())
	    return getID3v1();
	return null;
    }
    
    private void findMpegAudioStart() throws IOException {
	if (ID3v2.CheckFileForTag(raFile))
	    raFile.seek((long) ID3v2.GetTagLength(raFile));
	else
	    raFile.seek(0L);
	int last = raFile.readUnsignedByte();
	while (raFile.getFilePointer() < raFile.length()) {
	    int cur = raFile.readUnsignedByte();
	    if (last == 255 && (cur & 0xf0) == 240)
		break;
	    last = cur;
	}
	raFile.seek(raFile.getFilePointer() - 2L);
    }
}
