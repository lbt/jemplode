/* PluginClassLoader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.plugins;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

public class PluginClassLoader extends ClassLoader
{
    String path;
    Hashtable cache = new Hashtable();
    
    public PluginClassLoader(String _path) {
	path = _path;
    }
    
    protected synchronized Class loadClass(String name, boolean resolve)
	throws ClassNotFoundException {
	Class c = (Class) cache.get(name);
	if (c == null) {
	    try {
		return findSystemClass(name);
	    } catch (ClassNotFoundException classnotfoundexception) {
		c = loadIt(path, name);
		if (c == null)
		    c = loadFromSubdirectory(path, name);
		if (c == null) {
		    try {
			c = Class.forName(name);
		    } catch (Exception e) {
			c = null;
		    }
		}
		if (c == null)
		    throw new ClassNotFoundException(name);
	    }
	}
	if (c != null && resolve)
	    resolveClass(c);
	return c;
    }
    
    Class loadIt(String _path, String classname) {
	String filename = classname.replace('.', '/');
	filename += ".class";
	File fullname = new File(_path, filename);
	try {
	    java.io.InputStream is = new FileInputStream(fullname);
	    int bufsize = (int) fullname.length();
	    byte[] buf = new byte[bufsize];
	    is.read(buf, 0, bufsize);
	    is.close();
	    Class c = defineClass(classname, buf, 0, buf.length);
	    cache.put(classname, c);
	    return c;
	} catch (Exception e) {
	    return null;
	}
    }
    
    Class loadFromSubdirectory(String _path, String name) {
	File f = new File(_path);
	String[] list = f.list();
	if (list != null) {
	    for (int i = 0; i < list.length; i++) {
		f = new File(_path, list[i]);
		if (f.isDirectory()) {
		    Class c = loadIt(_path + list[i], name);
		    if (c != null)
			return c;
		}
	    }
	}
	return null;
    }
}
