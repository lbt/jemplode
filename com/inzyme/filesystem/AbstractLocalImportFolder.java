/* AbstractLocalImportFolder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.File;
import java.io.IOException;

import com.inzyme.util.Debug;

public abstract class AbstractLocalImportFolder
    extends AbstractLocalImportFile implements IImportFolder
{
    private IImportFile[] myChildrenCache;
    
    protected AbstractLocalImportFolder(File _file, boolean _folder) {
	super(_file, _folder);
    }
    
    public int getSize() {
	try {
	    IImportFile[] children = getChildren();
	    int size;
	    if (children == null)
		size = 0;
	    else
		size = children.length;
	    return size;
	} catch (IOException e) {
	    Debug.println(e);
	    return 0;
	}
    }
    
    public Object getValueAt(int _index) {
	try {
	    IImportFile[] children = getChildren();
	    Object value;
	    if (children == null)
		value = null;
	    else
		value = children[_index];
	    return value;
	} catch (IOException e) {
	    Debug.println(e);
	    return null;
	}
    }
    
    public synchronized IImportFile[] getChildren() throws IOException {
	if (myChildrenCache == null)
	    myChildrenCache = getChildren0();
	return myChildrenCache;
    }
    
    protected abstract IImportFile[] getChildren0() throws IOException;
}
