/* FIDPlaylistTreeNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.util.Enumeration;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import com.inzyme.tree.IContainerTreeNode;

import org.jempeg.nodestore.FIDPlaylist;

public class FIDPlaylistTreeNode extends AbstractFIDPlaylistModel
    implements IContainerTreeNode
{
    private DefaultTreeModel myTreeModel;
    private TreeNode myParent;
    
    public FIDPlaylistTreeNode(DefaultTreeModel _treeModel,
			       FIDPlaylist _playlist, int _playlistIndex) {
	super(_playlist, _playlistIndex);
	myTreeModel = _treeModel;
    }
    
    public int getType() {
	FIDPlaylist playlist = getPlaylist();
	return playlist.getType();
    }
    
    public void setType(int _type) {
	FIDPlaylist playlist = getPlaylist();
	playlist.setType(_type);
    }
    
    public int getContainedType() {
	FIDPlaylist playlist = getPlaylist();
	return playlist.getContainedType();
    }
    
    public void setContainedType(int _containedType) {
	FIDPlaylist playlist = getPlaylist();
	playlist.setContainedType(_containedType);
    }
    
    public DefaultTreeModel getTreeModel() {
	return myTreeModel;
    }
    
    public synchronized Enumeration children() {
	ensureChildrenLoaded();
	Enumeration enum = getChildrenVector().elements();
	return enum;
    }
    
    public boolean getAllowsChildren() {
	return true;
    }
    
    public synchronized TreeNode getChildAt(int _childIndex) {
	ensureChildrenLoaded();
	FIDPlaylistTreeNode childNode
	    = (FIDPlaylistTreeNode) getChildrenVector().elementAt(_childIndex);
	return childNode;
    }
    
    public synchronized int getChildCount() {
	ensureChildrenLoaded();
	int childCount = getChildrenVector().size();
	return childCount;
    }
    
    public synchronized int getIndex(TreeNode _node) {
	ensureChildrenLoaded();
	int index = getChildrenVector().indexOf(_node);
	return index;
    }
    
    public void setParent(MutableTreeNode _parent) {
	myParent = _parent;
    }
    
    public TreeNode getParent() {
	return myParent;
    }
    
    public void insert(MutableTreeNode _child, int _index) {
	throw new IllegalArgumentException
		  ("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
    }
    
    public void remove(int _index) {
	throw new IllegalArgumentException
		  ("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
    }
    
    public void remove(MutableTreeNode _node) {
	throw new IllegalArgumentException
		  ("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
    }
    
    public void removeFromParent() {
	((MutableTreeNode) getParent()).remove(this);
    }
    
    public void setUserObject(Object _object) {
	throw new IllegalArgumentException
		  ("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
    }
    
    protected void notifyStructureChanged() {
	myTreeModel.nodeStructureChanged(this);
    }
    
    protected AbstractFIDPlaylistModel createChildModel(FIDPlaylist _playlist,
							int _index) {
	FIDPlaylistTreeNode newChildNode
	    = new FIDPlaylistTreeNode(myTreeModel, _playlist, _index);
	newChildNode.setParent(this);
	return newChildNode;
    }
    
    protected void notifyChildrenWereInserted(int[] _indexes) {
	myTreeModel.nodesWereInserted(this, _indexes);
    }
    
    protected void notifyChildrenWereRemoved(int[] _indexes,
					     Object[] _childModels) {
	myTreeModel.nodesWereRemoved(this, _indexes, _childModels);
    }
}
