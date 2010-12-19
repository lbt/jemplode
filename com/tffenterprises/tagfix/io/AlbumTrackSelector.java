/* AlbumTrackSelector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix.io;
import java.io.File;
import java.io.FilenameFilter;

public class AlbumTrackSelector implements FilenameFilter
{
    File f = null;
    
    public boolean accept(File dir, String name) {
	if (name.indexOf(".mp3") != -1) {
	    for (int i = 0; i < 3; i++) {
		char c = dir.getName().charAt(i);
		if (c < '0' || c > '9')
		    return false;
	    }
	    if (f.canRead() && f.canWrite())
		return true;
	    return false;
	}
	f = new File(dir, name);
	if (f.isDirectory()) {
	    if (name.charAt(0) != '!' && name.charAt(1) != '!')
		return f.canRead();
	    return false;
	}
	return false;
    }
}
