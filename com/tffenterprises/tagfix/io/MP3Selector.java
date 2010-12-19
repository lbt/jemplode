/* MP3Selector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix.io;
import java.io.File;
import java.io.FilenameFilter;

public class MP3Selector implements FilenameFilter
{
    File f = null;
    
    public boolean accept(File dir, String name) {
	f = new File(dir, name);
	if (f.isDirectory())
	    return f.canRead();
	if (f.isFile() && name.indexOf(".mp3") != -1)
	    return f.canRead();
	return false;
    }
}
