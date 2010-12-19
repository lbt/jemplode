package org.jempeg.nodestore.model;

import java.util.Vector;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.IFIDNode;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.Reason;

/**
* FIDChangeSet is used as a single datastructure
* to encapsulate a series of changes to a Playlist.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class FIDChangeSet {
  private Vector myAddedNodes;
  private Vector myRemovedNodes;
  private Vector myModifiedNodes;
  private Vector mySkippedReasons;
  private Vector myFailedReasons;

  /**
  * Constructs a new FIDChangeSet
  */
  public FIDChangeSet() {
    myAddedNodes = new Vector();
    myRemovedNodes = new Vector();
    myModifiedNodes = new Vector();
    mySkippedReasons = new Vector();
    myFailedReasons = new Vector();
  }

  /**
  * Called when a node is added to a playlist.
  *
  * @param _node the node that was added
  */
  public void nodeAdded(IFIDNode _node) {
    myAddedNodes.addElement(_node);
  }

  /**
  * Called when a node is removed from a playlist.
  *
  * @param _node the node that was removed
  */
  public void nodeRemoved(IFIDNode _node) {
    myRemovedNodes.addElement(_node);
  }

  /**
  * Called when a node is modified in a playlist.
  *
  * @param _node the node that was modified
  */
  public void nodeModified(IFIDNode _node) {
    myModifiedNodes.addElement(_node);
  }

  /**
  * Called when a file is skipped
  *
  * @param _file the file that was skipped
  * @param _reason the reason the file was skipped
  */
  public void fileSkipped(IImportFile _file, String _reason) {
    mySkippedReasons.addElement(new Reason(_file.getLocation(), _reason));
  }

  /**
  * Called when a node fails
  *
  * @param _file the file that failed
  * @param _reason the reason for the failure
  */
  public void fileFailed(IImportFile _file, String _reason) {
    myFailedReasons.addElement(new Reason(_file.getLocation(), _reason));
  }

  /**
  * Called when a file import fails
  *
  * @param _file the file that failed
  * @param _exception the exception that caused the failure
  */
  public void fileFailed(IImportFile _file, Throwable _exception) {
    myFailedReasons.addElement(new Reason(_file.getName(), _exception));
  }

  /**
   * Adds a set of skipped reasons.
   *
   * @param _skippedReasons the skipped reasons to add
   */
  public void addSkippedReasons(Reason[] _skippedReasons) {
    for (int i = 0; i < _skippedReasons.length; i ++) {
      mySkippedReasons.addElement(_skippedReasons[i]);
    }
  }

  /**
   * Adds a set of failed reasons.
   *
   * @param _failedReasons the failed reasons to add
   */
  public void addFailedReasons(Reason[] _failedReasons) {
    for (int i = 0; i < _failedReasons.length; i ++) {
      myFailedReasons.addElement(_failedReasons[i]);
    }
  }

  /**
  * Returns the total number of changes that 
  * are encapsulated in this set.
  *
  * @returns the total number of changes
  */
  public int getSize() {
    int size = myAddedNodes.size() + myRemovedNodes.size() + myModifiedNodes.size() + mySkippedReasons.size() + myFailedReasons.size();
    return size;
  }

  /**
  * Returns the set of added nodes.
  *
  * @returns the set of added nodes
  */
  public IFIDNode[] getAddedNodes() {
    return getNodes(myAddedNodes);
  }

  /**
  * Returns the set of removed nodes.
  *
  * @returns the set of removed nodes
  */
  public IFIDNode[] getRemovedNodes() {
    return getNodes(myRemovedNodes);
  }

  /**
  * Returns the set of modified nodes.
  *
  * @returns the set of modified nodes
  */
  public IFIDNode[] getModifiedNodes() {
    return getNodes(myModifiedNodes);
  }

  /**
  * Returns the set of skipped reasons.
  *
  * @returns the set of skipped reasons
  */
  public Reason[] getSkippedReasons() {
    return getChanges(mySkippedReasons);
  }

  /**
  * Returns the set of failed reasons.
  *
  * @returns the set of failed reasons
  */
  public Reason[] getFailedReasons() {
    return getChanges(myFailedReasons);
  }
  
  public String toString() {
  	StringBuffer sb = new StringBuffer();
  	
		for (int i = 0; i < myAddedNodes.size(); i ++) {
			IFIDNode addedNode = (IFIDNode)myAddedNodes.elementAt(i);
			toString("A", addedNode, sb);
		}
		
		for (int i = 0; i < myModifiedNodes.size(); i ++) {
			IFIDNode modifiedNode = (IFIDNode)myModifiedNodes.elementAt(i);
			toString("M", modifiedNode, sb);
		}
		
		for (int i = 0; i < myFailedReasons.size(); i ++) {
			Reason reason = (Reason)myFailedReasons.elementAt(i);
			toString("F", reason, sb);
		}
		
		for (int i = 0; i < mySkippedReasons.size(); i ++) {
			Reason reason = (Reason)mySkippedReasons.elementAt(i);
			toString("S", reason, sb);
		}
		
		for (int i = 0; i < myRemovedNodes.size(); i ++) {
			IFIDNode removedNode = (IFIDNode)myRemovedNodes.elementAt(i);
			toString("R", removedNode, sb);
		}
		
		return sb.toString();
  }
  
	protected void toString(String _action, Reason _reason, StringBuffer _sb) {
		_sb.append(_action);
		_sb.append(" \"");
		_sb.append(_reason.getFileName());
		_sb.append("\" \"");
		_sb.append(_reason.getReason());
		_sb.append("\"");
		_sb.append("\n");
	}
	  
  protected void toString(String _action, IFIDNode _node, StringBuffer _sb) {
  	_sb.append(_action);
		_sb.append(" ");
		_sb.append(_node.getFID());
		_sb.append(" ");
		_sb.append(_node.getTags().getValue(DatabaseTags.TYPE_TAG));
		_sb.append(" \"");
		if (_node instanceof FIDLocalFile) {
			_sb.append(((FIDLocalFile)_node).getFile());
		} else {
			_sb.append(_node.getTags().getValue(DatabaseTags.TITLE_TAG));
		}
		_sb.append("\"");
		_sb.append("\n");
  }

  protected IFIDNode[] getNodes(Vector _vec) {
    IFIDNode[] nodes = new IFIDNode[_vec.size()];
    _vec.copyInto(nodes);
    return nodes;
  }

  protected Reason[] getChanges(Vector _vec) {
    Reason[] changes = new Reason[_vec.size()];
    _vec.copyInto(changes);
    return changes;
  }
}
