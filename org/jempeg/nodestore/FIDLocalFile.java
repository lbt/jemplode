/* FIDLocalFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.IOException;

import javax.swing.tree.TreePath;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.Reason;
import com.inzyme.util.Debug;

public class FIDLocalFile extends AbstractFIDNode
{
    private Object myID;
    private IImportFile myFile;
    private boolean myAddedToDatabase;
    
    public FIDLocalFile(PlayerDatabase _db, IImportFile _file,
			boolean _addToDatabase) throws IOException {
	this(_db, _db.getNodeMap().findFree(), createNodeTags(_file), _file,
	     _addToDatabase);
    }
    
    public FIDLocalFile(PlayerDatabase _db, long _fid, NodeTags _tags,
			IImportFile _file,
			boolean _addToDatabase) throws IOException {
	super(_db, _fid, _tags);
	myFile = _file;
	initializeGeneration();
	if (_addToDatabase)
	    addToDatabase(true);
	setType(3);
	setDirty(true);
    }
    
    public void setDirty(boolean _dirty) {
	super.setDirty(_dirty);
	try {
	    if (!_dirty)
		getDB().getFileToNodeMap().remove(getID());
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
    
    protected boolean isAddedToDatabase() {
	return myAddedToDatabase;
    }
    
    protected static NodeTags createNodeTags(IImportFile _file) {
	NodeTags tags = new LazyNodeTags(_file);
	tags.setValue("type", "tune");
	String name = _file.getName();
	tags.setValue("title", name);
	return tags;
    }
    
    protected synchronized Object getID() throws IOException {
	if (myID == null)
	    myID = myFile.getID();
	return myID;
    }
    
    public static FIDLocalFile createInstance
	(PlayerDatabase _db, IImportFile _file, boolean _identifyImmediately,
	 boolean _checkForLocalDuplicates)
	throws IOException {
	if (_checkForLocalDuplicates
	    && getExistingInstance(_db, _file) != null)
	    throw new IllegalArgumentException
		      ("There is already an instance created for " + _file
		       + ".  Use getExistingInstance() instead.");
	FIDLocalFile node = new FIDLocalFile(_db, _file, false);
	if (_identifyImmediately) {
	    try {
		node.identify();
		if (_checkForLocalDuplicates)
		    _db.getFileToNodeMap().put(_file.getID(), node);
	    } catch (IOException e) {
		node.delete();
		throw e;
	    } catch (Throwable e) {
		node.delete();
		throw new ChainedRuntimeException(("Unable to identify "
						   + _file + "."),
						  e);
	    }
	}
	return node;
    }
    
    public static FIDLocalFile getExistingInstance
	(PlayerDatabase _db, IImportFile _file) throws IOException {
	Object id = _file.getID();
	FIDLocalFile node = (FIDLocalFile) _db.getFileToNodeMap().get(id);
	return node;
    }
    
    public IImportFile getFile() {
	return myFile;
    }
    
    public void addToDatabase(boolean _generateDatabaseChange) {
	getDB().checkFreeSpace(this);
	super.addToDatabase(_generateDatabaseChange);
	getDB().consumeSpace(this);
	myAddedToDatabase = true;
	if (isIdentified())
	    getDB().fireNodeIdentified(this);
    }
    
    public void delete() {
	super.delete();
	try {
	    getDB().getFileToNodeMap().remove(getID());
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
    
    public Reason[] checkForProblems(boolean _doRepair, TreePath _path) {
	return Reason.NO_REASONS;
    }
    
    public void identify() throws IOException {
	identifyFile(myFile, myAddedToDatabase, true);
    }
}
