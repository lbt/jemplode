/* ShortSelector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix.io;
import java.io.File;
import java.io.FilenameFilter;

public class ShortSelector implements FilenameFilter
{
    File f = null;
    
    public boolean accept(File dir, String name) {
	f = new File(dir, name);
	if (f.isDirectory())
	    return f.canRead();
	if (name.length() == 6 && Character.isDigit(name.charAt(0))
	    && Character.isDigit(name.charAt(1))
	    && name.indexOf(".mp3") != -1) {
	    if (f.canRead() && f.canWrite())
		return true;
	    return false;
	}
	return false;
    }
}
