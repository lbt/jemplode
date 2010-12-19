/* MungedSelector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix.io;
import java.io.File;
import java.io.FilenameFilter;

public class MungedSelector implements FilenameFilter
{
    public boolean accept(File dir, String name) {
	if (Character.isDigit(name.charAt(0))
	    && Character.isDigit(name.charAt(1))
	    && Character.isWhitespace(name.charAt(2))) {
	    if (name.length() == 31 && name.indexOf(".mp3") == 27
		&& name.charAt(24) == '#')
		return true;
	    return false;
	}
	if (name.indexOf(".m3u") != -1)
	    return false;
	if (name.indexOf(".mp3") != -1)
	    return false;
	return true;
    }
}
