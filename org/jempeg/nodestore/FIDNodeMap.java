/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
 */
package org.jempeg.nodestore;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.model.MultiValueHashtable;

/**
 * FIDNodeMap maps FID Long values to
 * the IFIDNode that corresponds to
 * it.  This is used as the internal
 * datastructure of PlayerDatabase.
 *
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class FIDNodeMap implements Serializable {
  static final long serialVersionUID = 8314804521400449925L;

  private FIDNodeHashtable myFIDToNode;
  private Vector myFIDInOrder;
  private MultiValueHashtable myRidToNodes;
  private long myLastFreeFid;

  public FIDNodeMap() {
    reset();
  }

  /**
   * @return the number of nodes in this map.
   */
  public int size() {
    return myFIDInOrder.size();
  }

  /**
   * @return the node at the given index
   */
  public IFIDNode getNodeAt(int _index) {
    IFIDNode node = (IFIDNode) myFIDInOrder.elementAt(_index);
    return node;
  }

  /**
   * @return an Enumeration of Long's of all available FID numbers (in no particular order)
   */
  public Enumeration keys() {
    return myFIDToNode.keys();
  }

  /**
   * @return an Enumeration of all available IFIDNode's (in insertion order)
   */
  public Enumeration elements() {
    return myFIDInOrder.elements();
  }

  /**
   * @return the index of the specified node
   */
  public int indexOf(IFIDNode _node) {
    return myFIDInOrder.indexOf(_node);
  }

  /**
   * @return the node that has the given FID
   */
  public IFIDNode getNode(long _fid) {
    return myFIDToNode.get(_fid);
  }

  /**
   * Registers the given node's RID value for lookup.
   * 
   * @param _node the node to register
   */
  void registerRid(IFIDNode _node) {
    String rid = _node.getTags().getValue(DatabaseTags.RID_TAG);
    if (rid.length() > 0) {
      myRidToNodes.put(rid, _node);
    }
  }

  /**
   * Removes the lookup for the given node's RID value.
   *
   * @param _node the node to unregister
   */
  void unregisterRid(IFIDNode _node) {
    String rid = _node.getTags().getValue(DatabaseTags.RID_TAG);
    if (rid.length() > 0) {
      myRidToNodes.removeEqualEqual(rid, _node);
    }
  }

  /**
   * If there are no matches, this will return null, if there is only one 
   * match, this will return the IFIDNode, otherwise this will return a 
   * Vector of nodes.  This is not a very javaish thing, but it's here as 
   * an optimization since this can be called pretty frequently during an import.  
   * 
   * @return the nodes that have the given RID value
   */
  public Object getDuplicateNodes(IFIDNode _node) {
    Object matchingNodes = null;
    String rid = _node.getTags().getValue(DatabaseTags.RID_TAG);
    if (rid.length() > 0) {
      matchingNodes = getNodes(rid);
    }
    return matchingNodes;
  }

  public Object getDuplicateNodesAsVector(IFIDNode _node) {
    Object obj = getDuplicateNodes(_node);
    Vector vec;
    if (obj instanceof Vector) {
      vec = (Vector) obj;
    }
    else {
      vec = new Vector();
      vec.addElement(obj);
    }
    return vec;
  }

  /**
   * If there are no matches, this will return null, if there is only one 
   * match, this will return the IFIDNode, otherwise this will return a 
   * Vector of nodes.  This is not a very javaish thing, but it's here as 
   * an optimization since this can be called pretty frequently during an import.  
   * 
   * @return the nodes that have the given RID value
   */
  public synchronized Object getNodes(String _rid) {
    return myRidToNodes.get(_rid);
  }

  public synchronized Vector getNodesAsVector(String _rid) {
    Object obj = getNodes(_rid);
    Vector vec;
    if (obj instanceof Vector) {
      vec = (Vector) obj;
    }
    else {
      vec = new Vector();
      vec.addElement(obj);
    }
    return vec;
  }

  void addNode(long _fid, IFIDNode _node) {
    IFIDNode oldNode = (IFIDNode) myFIDToNode.put(_fid, _node);
    int oldIndex = -1;
    if (!(oldNode instanceof FIDStub)) {
      oldIndex = myFIDInOrder.indexOf(_node);
    }
    if (oldIndex == -1) {
      myFIDInOrder.addElement(_node);
    }
    else {
      oldNode = (IFIDNode) myFIDInOrder.elementAt(oldIndex);
      unregisterRid(oldNode);
      myFIDInOrder.setElementAt(_node, oldIndex);
    }
  }

  synchronized IFIDNode removeNode(long _fid) {
    IFIDNode node = myFIDToNode.remove(_fid);

    // Because two different nodes can be .equals(..), we
    // need to found the EXACT node and remove it.  Otherwise
    // we might inadvertantly delete our matching node.  This is
    // particularly an issue when you find a duplicate of yourself
    // and you need to be deleted.  You don't want to delete your
    // duplicate.
    int size = size();
    int index = -1;
    for (int i = 0; index == -1 && i < size; i ++ ) {
      IFIDNode foundNode = getNodeAt(i);
      if (foundNode == node) {
        index = i;
      }
    }
    if (index != -1) {
      myFIDInOrder.removeElementAt(index);
    }

    if (node != null) {
      unregisterRid(node);
    }

    if (_fid < myLastFreeFid) {
      myLastFreeFid = _fid;
    }
    return node;
  }

  /**
   * Returns the first free FID.
   * 
   * @return the first free FID
   */
  public synchronized long findFree() {
    int inc = 0x10;

    long fid = myLastFreeFid;
    do {
      if (!myFIDToNode.containsKey(fid)) {
        break;
      }
      fid += inc;
    }
    while (true);
    myLastFreeFid = fid + inc;

    // stub out the requested fid to be threadsafe
    myFIDToNode.put(fid, new FIDStub(this, fid));

    return fid;
  }

  /**
   * Returns the highest FID.
   * 
   * @return the highest FID
   */
  public synchronized long getMax() {
    long maxFid = 0;
    Enumeration keys = myFIDToNode.keys();
    while (keys.hasMoreElements()) {
      Long fidLong = (Long) keys.nextElement();
      long fid = fidLong.longValue();
      if (fid > maxFid) {
        maxFid = fid;
      }
    }
    return maxFid;
  }

  synchronized void clear() {
    //		Enumeration elements = myFIDToNode.elements();
    //		while (elements.hasMoreElements()) {
    //			IFIDNode node = (IFIDNode)elements.nextElement();
    //			// TODO: Is this necessary?  are we just generating tons of events?
    //			node.delete();
    //		}
    reset();
  }

  public synchronized void reset() {
    myFIDToNode = new FIDNodeHashtable();
    myFIDInOrder = new Vector();
    myRidToNodes = new MultiValueHashtable();
    myLastFreeFid = FIDConstants.FID_FIRSTNORMAL;
  }
}

