/* Recurse - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

public class Recurse
{
    static int counter = 0;
    static FilenameFilter filter = null;
    static List actions = new LinkedList();
    
    public static void main(String[] argv) {
	try {
	    ResourceBundle bundle = ResourceBundle.getBundle("recurse");
	    try {
		Class ff = Class.forName(bundle.getString("filter"));
		filter = (FilenameFilter) ff.newInstance();
	    } catch (Exception e) {
		filter = null;
	    }
	    java.util.SortedSet keySet = new TreeSet();
	    Enumeration enumeration = bundle.getKeys();
	    while (enumeration.hasMoreElements())
		keySet.add(enumeration.nextElement());
	    Iterator iterator = keySet.iterator();
	    while (iterator.hasNext()) {
		String key = (String) iterator.next();
		if (key.startsWith("action")) {
		    Class fa = Class.forName(bundle.getString(key));
		    actions.add((FileAction) fa.newInstance());
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    throw new RuntimeException(e.getMessage());
	}
	try {
	    for (int i = 0; i < argv.length; i++)
		Recurse(new File(argv[i]));
	} catch (Exception e) {
	    System.err.println(e + ": " + e.getMessage());
	    e.printStackTrace(System.err);
	}
	Iterator iterator = actions.iterator();
	while (iterator.hasNext()) {
	    FileAction action = (FileAction) iterator.next();
	    action.finish();
	}
    }
    
    static void Recurse(File f) throws Exception {
	if (f.isDirectory()) {
	    String[] list;
	    if (filter != null)
		list = f.list(filter);
	    else
		list = f.list();
	    if (list != null) {
		for (int i = 0; i < list.length; i++)
		    Recurse(new File(f, list[i]));
	    } else
		System.out.println("null list for " + f.getName());
	} else if (f.isFile()) {
	    Iterator iterator = actions.iterator();
	    while (iterator.hasNext()) {
		FileAction action = (FileAction) iterator.next();
		if (action.performAction(f))
		    counter++;
	    }
	}
    }
}
