/* M3UImportFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.filesystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import com.inzyme.filesystem.AbstractLocalImportFolder;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.util.Debug;

public class M3UImportFile extends AbstractLocalImportFolder
{
    private String myName;
    
    public M3UImportFile(File _file) throws IOException {
	super(_file, false);
	String name = _file.getName();
	if (name.toLowerCase().endsWith(".m3u"))
	    name = name.substring(0, name.length() - 4);
	myName = name;
    }
    
    public String getName() {
	return myName;
    }
    
    protected IImportFile[] getChildren0() throws IOException {
	File f = getFile();
	FileInputStream fis = new FileInputStream(f);
	InputStreamReader isr = new InputStreamReader(fis);
	BufferedReader br = new BufferedReader(isr);
	File path = new File(new File(getLocation()).getParent());
	Vector childrenVec = new Vector();
	while (br.ready()) {
	    String line = br.readLine();
	    try {
		if (!line.startsWith("#")) {
		    File newFile = new File(line);
		    if (!newFile.isAbsolute())
			newFile = new File(path.getParent(), line);
		    childrenVec.addElement(ImportFileFactory
					       .createImportFile(newFile));
		}
	    } catch (IOException e) {
		Debug.println(e);
	    }
	}
	IImportFile[] children = new IImportFile[childrenVec.size()];
	childrenVec.copyInto(children);
	return children;
    }
}
