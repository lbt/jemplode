/* FileUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.File;
import java.util.StringTokenizer;

public class FileUtils
{
    public static String cleanseFilename(String _name, boolean _removeDirs) {
	char replaceChar = '_';
	String cleansedName = _name;
	if (_removeDirs) {
	    cleansedName
		= cleansedName.replace(File.separatorChar, replaceChar);
	    cleansedName = cleansedName.replace('\\', replaceChar);
	    cleansedName = cleansedName.replace('/', replaceChar);
	}
	if (File.separatorChar == '\\') {
	    cleansedName = cleansedName.replace('/', replaceChar);
	    cleansedName = cleansedName.replace('*', replaceChar);
	    cleansedName = cleansedName.replace(':', replaceChar);
	    cleansedName = cleansedName.replace(';', replaceChar);
	    cleansedName = cleansedName.replace('?', replaceChar);
	    cleansedName = cleansedName.replace('\"', replaceChar);
	    cleansedName = cleansedName.replace('<', replaceChar);
	    cleansedName = cleansedName.replace('>', replaceChar);
	    cleansedName = cleansedName.replace('|', replaceChar);
	}
	return cleansedName;
    }
    
    public static File findInClasspath(String _name) {
	File foundFile = null;
	StringTokenizer tk
	    = new StringTokenizer(System.getProperty("java.class.path"),
				  File.pathSeparator);
	while (foundFile == null && tk.hasMoreElements()) {
	    String path = tk.nextToken();
	    File f = new File(path);
	    if (f.isDirectory()) {
		f = new File(f, _name);
		if (f.exists())
		    foundFile = f;
	    } else if (f.exists() && f.getName().equals(_name))
		foundFile = f;
	}
	return foundFile;
    }
}
