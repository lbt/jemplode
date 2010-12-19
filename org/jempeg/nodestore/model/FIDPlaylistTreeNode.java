package org.jempeg.nodestore.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jempeg.nodestore.FIDPlaylist;

import com.inzyme.tree.IContainerTreeNode;

/**
 * FIDPlaylistTreeNode is a TreeNode implementation on
 * top of the Playlists in the PlayerDatabase.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class FIDPlaylistTreeNode extends AbstractFIDPlaylistModel implements IContainerTreeNode {
	private DefaultTreeModel myTreeModel;
	private TreeNode myParent;
	
	/**
	 * Constructs a new FIDPlaylistTreeNode.
	 * 
	 * @param _treeModel the tree model that this node is in (for update notification)
	 * @param _playlist the FIDPlaylistTreeNode
	 */
	public FIDPlaylistTreeNode(DefaultTreeModel _treeModel, FIDPlaylist _playlist, int _playlistIndex) {
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

	/**
	 * Returns the TreeModel that this tree node is a member of.
	 * 
	 * @return the TreeModel that this tree node is a member of
	 */
	public DefaultTreeModel getTreeModel() {
		return myTreeModel;
	}

	/**
	 * @see javax.swing.tree.TreeNode#children()
	 */
	public synchronized Enumeration children() {
		ensureChildrenLoaded();
		Enumeration enum = getChildrenVector().elements();
		return enum;
	}
	
	/**
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	/**
	 * @see javax.swing.tree.TreeNode#getChildAt(int)
	 */
	public synchronized TreeNode getChildAt(int _childIndex) {
		ensureChildrenLoaded();
		FIDPlaylistTreeNode childNode = (FIDPlaylistTreeNode)getChildrenVector().elementAt(_childIndex);
		return childNode;
	}
	
	/**
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	public synchronized int getChildCount() {
		ensureChildrenLoaded();
		int childCount = getChildrenVector().size();
		return childCount;
	}
	
	/**
	 * @see javax.swing.tree.TreeNode#getIndex(TreeNode)
	 */
	public synchronized int getIndex(TreeNode _node) {
		ensureChildrenLoaded();
		int index = getChildrenVector().indexOf(_node);
		return index;
	}
	
	/**
	 * Sets the parent of this TreeNode.
	 * 
	 * @param _parent the parent of this TreeNode
	 */
	public void setParent(MutableTreeNode _parent) {
		myParent = _parent;
	}
	
	/**
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	public TreeNode getParent() {
		return myParent;
	}
	
	/**
	 * @see javax.swing.tree.MutableTreeNode#insert(MutableTreeNode, int)
	 */
	public void insert(MutableTreeNode _child, int _index) {
		throw new IllegalArgumentException("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#remove(int)
	 */
	public void remove(int _index) {
		throw new IllegalArgumentException("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#remove(MutableTreeNode)
	 */
	public void remove(MutableTreeNode _node) {
		throw new IllegalArgumentException("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#removeFromParent()
	 */
	public void removeFromParent() {
		((MutableTreeNode)getParent()).remove(this);
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#setUserObject(Object)
	 */
	public void setUserObject(Object _object) {
		throw new IllegalArgumentException("This method on FIDPlaylistTreeNode is not implemented.  Mutate this node through the FIDPlaylist instead.");
	}
	
	/**
	 * @see org.jempeg.empeg.model.AbstractFIDPlaylistModel#notifyStructureChanged()
	 */
	protected void notifyStructureChanged() {
		myTreeModel.nodeStructureChanged(this);
	}
	
	/**
	 * @see org.jempeg.empeg.model.AbstractFIDPlaylistModel#createChildModel(FIDPlaylist, int)
	 */
	protected AbstractFIDPlaylistModel createChildModel(FIDPlaylist _playlist, int _index) {
		FIDPlaylistTreeNode newChildNode = new FIDPlaylistTreeNode(myTreeModel, _playlist, _index);
		newChildNode.setParent(this);
		return newChildNode;
	}
	
	/**
	 * @see org.jempeg.empeg.model.AbstractFIDPlaylistModel#notifyChildrenWereInserted(int[])
	 */
	protected void notifyChildrenWereInserted(int[] _indexes) {
		myTreeModel.nodesWereInserted(this, _indexes);
	}
	
	/**
	 * @see org.jempeg.empeg.model.AbstractFIDPlaylistModel#notifyChildrenWereRemoved(int[], Object[])
	 */
	protected void notifyChildrenWereRemoved(int[] _indexes, Object[] _childModels) {
		myTreeModel.nodesWereRemoved(this, _indexes, _childModels);
	}
}
