/* LocalImportFolder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.File;
import java.io.IOException;

public class LocalImportFolder extends AbstractLocalImportFolder
{
    private String myName;
    
    public LocalImportFolder(File _file) {
	this(_file.getName(), _file);
    }
    
    public LocalImportFolder(String _name, File _file) {
	super(_file, true);
	myName = _name;
    }
    
    public String getName() {
	return myName;
    }
    
    protected IImportFile[] getChildren0() throws IOException {
	File f = getFile();
	String[] list = f.list();
	IImportFile[] children;
	if (list == null)
	    children = null;
	else {
	    children = new IImportFile[list.length];
	    for (int i = 0; i < list.length; i++) {
		File childFile = new File(f, list[i]);
		children[i] = ImportFileFactory.createImportFile(childFile);
	    }
	}
	return children;
    }
}
