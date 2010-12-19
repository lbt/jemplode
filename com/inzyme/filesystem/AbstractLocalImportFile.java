/* AbstractLocalImportFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.io.SeekableInputStream;

public abstract class AbstractLocalImportFile
    implements IImportFile, Serializable
{
    private File myFile;
    
    protected AbstractLocalImportFile(File _file, boolean _folder) {
	myFile = _file;
    }
    
    public Properties getTags() {
	return new Properties();
    }
    
    public File getFile() {
	return myFile;
    }
    
    public Object getID() {
	return myFile;
    }
    
    public abstract String getName();
    
    public String getLocation() {
	String location = myFile.getAbsolutePath();
	return location;
    }
    
    public long getLength() {
	long length = myFile.length();
	return length;
    }
    
    public InputStream getInputStream() throws IOException {
	FileInputStream fis = new FileInputStream(myFile);
	return fis;
    }
    
    public SeekableInputStream getSeekableInputStream() throws IOException {
	FileSeekableInputStream fsis = new FileSeekableInputStream(myFile);
	return fsis;
    }
    
    public String toString() {
	String toString = myFile.toString();
	return toString;
    }
}
