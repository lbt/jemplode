/* BasicContainerModifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import javax.swing.tree.DefaultTreeModel;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.tree.BasicContainerTreeNode;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.soup.SoupUtils;

public class BasicContainerModifier implements IContainerModifier
{
    private IContainer myTargetContainer;
    
    public BasicContainerModifier(IContainer _targetContainer) {
	myTargetContainer = _targetContainer;
    }
    
    public IContainer getTargetContainer() {
	return myTargetContainer;
    }
    
    public void delete(ContainerSelection _selection,
		       IConfirmationListener _confirmationListener) {
	if (_selection.getSize() == 1) {
	    Object obj = _selection.getValueAt(0);
	    if (obj instanceof FIDPlaylistTreeNode) {
		FIDPlaylistTreeNode playlistTreeNode
		    = (FIDPlaylistTreeNode) obj;
		FIDPlaylist playlist = playlistTreeNode.getPlaylist();
		int confirmation = (_confirmationListener.confirm
				    (("Are you sure want to remove the "
				      + playlist.getTitle() + " soup?"),
				     playlist));
		if (confirmation == 0) {
		    int index = _selection.getIndexAt(0);
		    BasicContainerTreeNode containerTreeNode
			= (BasicContainerTreeNode) _selection.getContainer();
		    containerTreeNode.remove(index);
		    if (playlist.getReferenceCount() == 0)
			playlist.delete();
		    DefaultTreeModel treeModel
			= playlistTreeNode.getTreeModel();
		    treeModel.nodesWereRemoved(containerTreeNode,
					       new int[] { index },
					       (new Object[]
						{ playlistTreeNode }));
		    SoupUtils.removeTransientSoupPlaylist(index - 1);
		}
	    }
	}
    }
    
    public FIDChangeSet importFiles
	(IImportFile[] _sourceFiles,
	 IConfirmationListener _confirmationListener,
	 IProgressListener _progressListener, boolean _identifyImmediately) {
	return new FIDChangeSet();
    }
    
    public void importFiles
	(IContainer _targetContainer, IImportFile[] _sourceFiles,
	 FIDChangeSet _changeSet, IProgressListener _progressListener,
	 boolean _identifyImmediately) {
	/* empty */
    }
    
    public void moveFrom(ContainerSelection _selection) {
	/* empty */
    }
    
    public int[] moveTo(ContainerSelection _sourceSelection,
			IConfirmationListener _confirmationListener,
			IProgressListener _progressListener) {
	return new int[0];
    }
    
    public int[] copyTo(ContainerSelection _sourceSelection,
			IConfirmationListener _confirmationListener,
			boolean _deepCopy,
			IProgressListener _progressListener) {
	return new int[0];
    }
}
