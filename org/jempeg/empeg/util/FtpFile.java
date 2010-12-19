/* FtpFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.util;
import com.inzyme.util.ReflectionUtils;

public class FtpFile
{
    private String myPath;
    private String myName;
    private int mySize;
    
    public FtpFile(String _path, String _name, int _size) {
	myPath = _path;
	myName = _name;
	mySize = _size;
    }
    
    public String getPath() {
	return myPath;
    }
    
    public String getName() {
	return myName;
    }
    
    public String getFullPath() {
	return myPath + "/" + myName;
    }
    
    public int getSize() {
	return mySize;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
