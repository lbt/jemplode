package org.jempeg.empeg.manager.event;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.INodeTagListener;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;

import com.inzyme.tree.BasicContainerTreeNode;

public class TreeSoupAttacher implements INodeTagListener, IDatabaseListener {
	private JTree myTree;

	public TreeSoupAttacher(JTree _tree) {
		myTree = _tree;
	}

	public void initialize(PlayerDatabase _playerDatabase) {
		Enumeration nodesEnum = _playerDatabase.getNodeMap().elements();
		while (nodesEnum.hasMoreElements()) {
			IFIDNode node = (IFIDNode) nodesEnum.nextElement();
			nodeIdentified(node);
		}
	}

	public void nodeIdentified(IFIDNode _node) {
		if (shouldAttach(_node)) {
			FIDPlaylist playlist = (FIDPlaylist) _node;
			DefaultTreeModel treeModel = (DefaultTreeModel) myTree.getModel();
			BasicContainerTreeNode rootNode = (BasicContainerTreeNode) treeModel.getRoot();
			int index = rootNode.getSize();
			FIDPlaylistTreeNode transientSoupPlaylistNode = new FIDPlaylistTreeNode(treeModel, playlist, index);
			rootNode.add(transientSoupPlaylistNode);
			treeModel.nodesWereInserted(rootNode, new int[] { index });
		}
	}

	public void freeSpaceChanged(PlayerDatabase _playerDatabase, long _totalSpace, long _freeSpace) {
	}
  
  public void databaseCleared(PlayerDatabase _playerDatabase) {
  }

	public void nodeAdded(IFIDNode _node) {
	}

	public void nodeRemoved(IFIDNode _node) {
		if (shouldAttach(_node)) {
			DefaultTreeModel treeModel = (DefaultTreeModel) myTree.getModel();
			BasicContainerTreeNode rootNode = (BasicContainerTreeNode) treeModel.getRoot();
			int matchingIndex = -1;
			for (int i = 0; matchingIndex == -1 && i < rootNode.getChildCount(); i ++) {
				FIDPlaylistTreeNode childTreeNode = (FIDPlaylistTreeNode) rootNode.getChildAt(i);
				FIDPlaylist childPlaylist = childTreeNode.getPlaylist();
				if (childPlaylist == _node) {
					matchingIndex = i;
				}
			}
			if (matchingIndex != -1) {
				TreeNode matchingTreeNode = rootNode.getChildAt(matchingIndex);
				rootNode.remove(matchingIndex);
				treeModel.nodesWereRemoved(rootNode, new int[] { matchingIndex }, new Object[] { matchingTreeNode });
			}
		}
	}

	public void afterNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
	}

	public void beforeNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
	}

	private boolean shouldAttach(IFIDNode _node) {
		boolean shouldAttach = false;
		if (_node instanceof FIDPlaylist) {
			FIDPlaylist playlist = (FIDPlaylist) _node;
			if (playlist.isTransient()) {
				NodeTags tags = playlist.getTags();
				if (tags.getValue(DatabaseTags.SOUP_TAG).length() > 0 && tags.getValue(DatabaseTags.JEMPLODE_TEMPORARY_TAG).length() == 0) {
					shouldAttach = true;
				}
			}
		}
		return shouldAttach;
	}

}
