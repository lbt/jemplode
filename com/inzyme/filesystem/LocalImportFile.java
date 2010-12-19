/* LocalImportFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.File;

public class LocalImportFile extends AbstractLocalImportFile
{
    private String myName;
    
    public LocalImportFile(File _file) {
	this(_file.getName(), _file);
    }
    
    public LocalImportFile(String _name, File _file) {
	super(_file, false);
	myName = _name;
    }
    
    public String getName() {
	return myName;
    }
}
