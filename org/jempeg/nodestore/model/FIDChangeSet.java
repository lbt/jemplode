/* FIDChangeSet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.util.Vector;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.Reason;

import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.IFIDNode;

public class FIDChangeSet
{
    private Vector myAddedNodes = new Vector();
    private Vector myRemovedNodes = new Vector();
    private Vector myModifiedNodes = new Vector();
    private Vector mySkippedReasons = new Vector();
    private Vector myFailedReasons = new Vector();
    
    public void nodeAdded(IFIDNode _node) {
	myAddedNodes.addElement(_node);
    }
    
    public void nodeRemoved(IFIDNode _node) {
	myRemovedNodes.addElement(_node);
    }
    
    public void nodeModified(IFIDNode _node) {
	myModifiedNodes.addElement(_node);
    }
    
    public void fileSkipped(IImportFile _file, String _reason) {
	mySkippedReasons.addElement(new Reason(_file.getLocation(), _reason));
    }
    
    public void fileFailed(IImportFile _file, String _reason) {
	myFailedReasons.addElement(new Reason(_file.getLocation(), _reason));
    }
    
    public void fileFailed(IImportFile _file, Throwable _exception) {
	myFailedReasons.addElement(new Reason(_file.getName(), _exception));
    }
    
    public void addSkippedReasons(Reason[] _skippedReasons) {
	for (int i = 0; i < _skippedReasons.length; i++)
	    mySkippedReasons.addElement(_skippedReasons[i]);
    }
    
    public void addFailedReasons(Reason[] _failedReasons) {
	for (int i = 0; i < _failedReasons.length; i++)
	    myFailedReasons.addElement(_failedReasons[i]);
    }
    
    public int getSize() {
	int size = (myAddedNodes.size() + myRemovedNodes.size()
		    + myModifiedNodes.size() + mySkippedReasons.size()
		    + myFailedReasons.size());
	return size;
    }
    
    public IFIDNode[] getAddedNodes() {
	return getNodes(myAddedNodes);
    }
    
    public IFIDNode[] getRemovedNodes() {
	return getNodes(myRemovedNodes);
    }
    
    public IFIDNode[] getModifiedNodes() {
	return getNodes(myModifiedNodes);
    }
    
    public Reason[] getSkippedReasons() {
	return getChanges(mySkippedReasons);
    }
    
    public Reason[] getFailedReasons() {
	return getChanges(myFailedReasons);
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < myAddedNodes.size(); i++) {
	    IFIDNode addedNode = (IFIDNode) myAddedNodes.elementAt(i);
	    toString("A", addedNode, sb);
	}
	for (int i = 0; i < myModifiedNodes.size(); i++) {
	    IFIDNode modifiedNode = (IFIDNode) myModifiedNodes.elementAt(i);
	    toString("M", modifiedNode, sb);
	}
	for (int i = 0; i < myFailedReasons.size(); i++) {
	    Reason reason = (Reason) myFailedReasons.elementAt(i);
	    toString("F", reason, sb);
	}
	for (int i = 0; i < mySkippedReasons.size(); i++) {
	    Reason reason = (Reason) mySkippedReasons.elementAt(i);
	    toString("S", reason, sb);
	}
	for (int i = 0; i < myRemovedNodes.size(); i++) {
	    IFIDNode removedNode = (IFIDNode) myRemovedNodes.elementAt(i);
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
	_sb.append(_node.getTags().getValue("type"));
	_sb.append(" \"");
	if (_node instanceof FIDLocalFile)
	    _sb.append(((FIDLocalFile) _node).getFile());
	else
	    _sb.append(_node.getTags().getValue("title"));
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
