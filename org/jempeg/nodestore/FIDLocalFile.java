/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.nodestore;

import java.io.IOException;

import javax.swing.tree.TreePath;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.Reason;
import com.inzyme.util.Debug;

/**;
* FIDLocalFile is used to represent a file that is queued up locally to be
* added to the database and transferred to the player, but has not yet been.
*
* @author Mike Schrag
*/
public class FIDLocalFile extends AbstractFIDNode {
	private Object myID;
	private IImportFile myFile;
	private boolean myAddedToDatabase;

	/**
	 * Creates an FIDLocalFile.
	 * 
	 * @param _db the database to potentially add the node into
	 * @param _file the file that this node represents
	 * @param _addToDatabase whether or not the file should be auto-added to the database
	 */
	public FIDLocalFile(PlayerDatabase _db, IImportFile _file, boolean _addToDatabase) throws IOException {
		this(_db, _db.getNodeMap().findFree(), createNodeTags(_file), _file, _addToDatabase);
	}

	/**
	 * Creates an FIDLocalFile.
	 * 
	 * @param _db the database to potentially add the node into
	 * @param _fid the FID of this node
	 * @param _tags the tags for this node
	 * @param _file the file that this node represents
	 * @param _addToDatabase whether or not the file should be auto-added to the database
	 */
	public FIDLocalFile(PlayerDatabase _db, long _fid, NodeTags _tags, IImportFile _file, boolean _addToDatabase) throws IOException {
		super(_db, _fid, _tags);
		myFile = _file;

		initializeGeneration();

		if (_addToDatabase) {
			addToDatabase(true);
		}

		setType(IFIDNode.TYPE_TUNE);
		setDirty(true);
	}

	public void setDirty(boolean _dirty) {
		super.setDirty(_dirty);
		try {
			if (!_dirty) {
				getDB().getFileToNodeMap().remove(getID());
			}
		}
		catch (IOException e) {
			Debug.println(e);
		}
	}
	
	protected boolean isAddedToDatabase() {
		return myAddedToDatabase;
	}

	protected static NodeTags createNodeTags(IImportFile _file) {
		// set 'type' 'title' 'length' tags
		NodeTags tags = new LazyNodeTags(_file);

		tags.setValue(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_TUNE);

		String name = _file.getName();
		tags.setValue(DatabaseTags.TITLE_TAG, name);

		return tags;
	}

	protected synchronized Object getID() throws IOException {
		if (myID == null) {
			myID = myFile.getID();
		}
		return myID;
	}

	/**
	 * A factory method for creating FIDLocalFiles.  This exists because if you import the same file twice before syncing, you
	 * want them to be the same node internally.
	 * 
	 * @param _db the database to add into
	 * @param _file the file that this node represents
	 * @param _identifyImmediately if true, the node will be parsed now
	 * @param _checkForLocalDuplicates if true, then if a node already exists, IllegalArgumentException will be thrown
	 */
	public static FIDLocalFile createInstance(PlayerDatabase _db, IImportFile _file, boolean _identifyImmediately, boolean _checkForLocalDuplicates) throws IOException {
		if (_checkForLocalDuplicates) {
			if (getExistingInstance(_db, _file) != null) {
				throw new IllegalArgumentException("There is already an instance created for " + _file + ".  Use getExistingInstance() instead.");
			}
		}

		FIDLocalFile node = new FIDLocalFile(_db, _file, false);

		if (_identifyImmediately) {
			try {
				node.identify();
				if (_checkForLocalDuplicates) {
					_db.getFileToNodeMap().put(_file.getID(), node);
				}
			}
			catch (IOException e) {
				node.delete();
				throw e;
			}
			catch (Throwable e) {
				node.delete();
				throw new ChainedRuntimeException("Unable to identify " + _file + ".", e);
			}
		}

		return node;
	}

	/**
	 * Returns the FIDLocalFile that has already been created or null if one doesn't match.  Why is this separate
	 * from createInstance?  That's a good damn question.
	 * 
	 * @param _db the database to add into
	 * @param _file the file that this node represents
	 */
	public static FIDLocalFile getExistingInstance(PlayerDatabase _db, IImportFile _file) throws IOException {
		Object id = _file.getID();
		FIDLocalFile node = (FIDLocalFile) _db.getFileToNodeMap().get(id);
		return node;
	}

	/**
	 * Returns the File that this FIDLocalFile represents.
	 * 
	 * @return the File that this FIDLocalFile represents
	 */
	public IImportFile getFile() {
		return myFile;
	}

	/**
	 * Adding to the database causes free space to appear to be consumed.  FIDLocalFiles are
	 * not automatically added to the database because after being identified, it may turn out
	 * that they are duplicates.
	 * 
	 * @param _generateDatabaseChange whether or not to generate a database change for this addition
	 */
	public void addToDatabase(boolean _generateDatabaseChange) {
		getDB().checkFreeSpace(this);
		super.addToDatabase(_generateDatabaseChange);
		getDB().consumeSpace(this);
		myAddedToDatabase = true;
		if (isIdentified()) {
			getDB().fireNodeIdentified(this);
		}
	}

	public void delete() {
		super.delete();

		try {
			getDB().getFileToNodeMap().remove(getID());
		}
		catch (IOException e) {
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
