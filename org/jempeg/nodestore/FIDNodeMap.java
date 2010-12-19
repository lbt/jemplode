/* FIDNodeMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.model.MultiValueHashtable;

public class FIDNodeMap implements Serializable
{
    static final long serialVersionUID = 8314804521400449925L;
    private FIDNodeHashtable myFIDToNode;
    private Vector myFIDInOrder;
    private MultiValueHashtable myRidToNodes;
    private long myLastFreeFid;
    
    public FIDNodeMap() {
	reset();
    }
    
    public int size() {
	return myFIDInOrder.size();
    }
    
    public IFIDNode getNodeAt(int _index) {
	IFIDNode node = (IFIDNode) myFIDInOrder.elementAt(_index);
	return node;
    }
    
    public Enumeration keys() {
	return myFIDToNode.keys();
    }
    
    public Enumeration elements() {
	return myFIDInOrder.elements();
    }
    
    public int indexOf(IFIDNode _node) {
	return myFIDInOrder.indexOf(_node);
    }
    
    public IFIDNode getNode(long _fid) {
	return myFIDToNode.get(_fid);
    }
    
    void registerRid(IFIDNode _node) {
	String rid = _node.getTags().getValue("rid");
	if (rid.length() > 0)
	    myRidToNodes.put(rid, _node);
    }
    
    void unregisterRid(IFIDNode _node) {
	String rid = _node.getTags().getValue("rid");
	if (rid.length() > 0)
	    myRidToNodes.removeEqualEqual(rid, _node);
    }
    
    public Object getDuplicateNodes(IFIDNode _node) {
	Object matchingNodes = null;
	String rid = _node.getTags().getValue("rid");
	if (rid.length() > 0)
	    matchingNodes = getNodes(rid);
	return matchingNodes;
    }
    
    public Object getDuplicateNodesAsVector(IFIDNode _node) {
	Object obj = getDuplicateNodes(_node);
	Vector vec;
	if (obj instanceof Vector)
	    vec = (Vector) obj;
	else {
	    vec = new Vector();
	    vec.addElement(obj);
	}
	return vec;
    }
    
    public synchronized Object getNodes(String _rid) {
	return myRidToNodes.get(_rid);
    }
    
    public synchronized Vector getNodesAsVector(String _rid) {
	Object obj = getNodes(_rid);
	Vector vec;
	if (obj instanceof Vector)
	    vec = (Vector) obj;
	else {
	    vec = new Vector();
	    vec.addElement(obj);
	}
	return vec;
    }
    
    void addNode(long _fid, IFIDNode _node) {
	IFIDNode oldNode = myFIDToNode.put(_fid, _node);
	int oldIndex = -1;
	if (!(oldNode instanceof FIDStub))
	    oldIndex = myFIDInOrder.indexOf(_node);
	if (oldIndex == -1)
	    myFIDInOrder.addElement(_node);
	else {
	    oldNode = (IFIDNode) myFIDInOrder.elementAt(oldIndex);
	    unregisterRid(oldNode);
	    myFIDInOrder.setElementAt(_node, oldIndex);
	}
    }
    
    synchronized IFIDNode removeNode(long _fid) {
	IFIDNode node = myFIDToNode.remove(_fid);
	int size = size();
	int index = -1;
	for (int i = 0; index == -1 && i < size; i++) {
	    IFIDNode foundNode = getNodeAt(i);
	    if (foundNode == node)
		index = i;
	}
	if (index != -1)
	    myFIDInOrder.removeElementAt(index);
	if (node != null)
	    unregisterRid(node);
	if (_fid < myLastFreeFid)
	    myLastFreeFid = _fid;
	return node;
    }
    
    public synchronized long findFree() {
	int inc = 16;
	long fid;
	for (fid = myLastFreeFid; myFIDToNode.containsKey(fid);
	     fid += (long) inc) {
	    /* empty */
	}
	myLastFreeFid = fid + (long) inc;
	myFIDToNode.put(fid, new FIDStub(this, fid));
	return fid;
    }
    
    public synchronized long getMax() {
	long maxFid = 0L;
	Enumeration keys = myFIDToNode.keys();
	while (keys.hasMoreElements()) {
	    Long fidLong = (Long) keys.nextElement();
	    long fid = fidLong.longValue();
	    if (fid > maxFid)
		maxFid = fid;
	}
	return maxFid;
    }
    
    synchronized void clear() {
	reset();
    }
    
    public synchronized void reset() {
	myFIDToNode = new FIDNodeHashtable();
	myFIDInOrder = new Vector();
	myRidToNodes = new MultiValueHashtable();
	myLastFreeFid = 288L;
    }
}
