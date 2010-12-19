/* NewFileFormatter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.FilenameFilter;

import com.tffenterprises.tagfix.io.ShortSelector;

public class NewFileFormatter
{
    static int counter = 0;
    static FilenameFilter filter = new ShortSelector();
    static FileAction action = new NewFileAction();
    
    public static void main(String[] argv) {
	try {
	    for (int i = 0; i < argv.length; i++)
		Recurse(new File(argv[i]));
	} catch (Exception e) {
	    System.err.println(e + ": " + e.getMessage());
	    e.printStackTrace(System.err);
	}
	action.finish();
    }
    
    private static void Recurse(File f) throws Exception {
	if (f.isDirectory()) {
	    String[] list = null;
	    if (filter != null)
		list = f.list(filter);
	    else
		list = f.list();
	    if (list != null) {
		for (int i = 0; i < list.length; i++)
		    Recurse(new File(f, list[i]));
	    } else
		System.out.println("null list for " + f.getPath());
	} else if (f.isFile() && action.performAction(f))
	    counter++;
    }
}
