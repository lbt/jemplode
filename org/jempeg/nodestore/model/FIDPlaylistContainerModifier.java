/* FIDPlaylistContainerModifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.container.AllowAllFilter;
import com.inzyme.container.ContainerSelection;
import com.inzyme.container.ContainerUtils;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFolder;
import com.inzyme.model.IntVector;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.sort.IntQuickSort;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.tree.AllowAllTraversalFilter;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public class FIDPlaylistContainerModifier
    extends AbstractPlaylistContainerModifier
{
    public FIDPlaylistContainerModifier(FIDPlaylist _targetPlaylist) {
	super(_targetPlaylist);
    }
    
    public void delete(ContainerSelection _selection,
		       IConfirmationListener _confirmationListener) {
	int size = _selection.getSize();
	int confirmation;
	if (_confirmationListener == null)
	    confirmation = 0;
	else {
	    String title = null;
	    if (size == 1) {
		IFIDNode fidNode = (IFIDNode) _selection.getValueAt(0);
		title = fidNode.getTitle();
	    }
	    String message = (ResourceBundleUtils.getUIString
			      ("deleteConfirmation.playlistMessage",
			       new Object[] { new Integer(size), title }));
	    String checkboxMessage
		= (ResourceBundleUtils.getUIString
		   ("deleteConfirmation.playlistCheckboxMessage"));
	    confirmation
		= _confirmationListener.confirm(message, checkboxMessage,
						null);
	}
	if (confirmation == 0) {
	    int[] selectedIndexes = _selection.getSelectedIndexes();
	    IntQuickSort quickSort = new IntQuickSort();
	    quickSort.sort(selectedIndexes);
	    for (int i = size - 1; i >= 0; i--) {
		int index = _selection.getIndexAt(i);
		getTargetPlaylist().removeNodeAt(index);
	    }
	} else if (confirmation == 4) {
	    Vector deleteVec = new Vector();
	    for (int i = 0; i < size; i++)
		ContainerUtils.traverse(_selection.getValueAt(i), deleteVec,
					new AllowAllFilter(),
					new AllowAllTraversalFilter());
	    Enumeration deletedNodesEnum = deleteVec.elements();
	    while (deletedNodesEnum.hasMoreElements()) {
		IFIDNode fidNode = (IFIDNode) deletedNodesEnum.nextElement();
		fidNode.delete();
	    }
	}
    }
    
    protected boolean importFile(IContainer _targetContainer, IFIDNode _node,
				 FIDChangeSet _changeSet,
				 CollationKeyCache _cache) {
	FIDPlaylist targetPlaylist = (FIDPlaylist) _targetContainer;
	boolean alreadyExists = targetPlaylist.contains(_node);
	if (!alreadyExists) {
	    Debug.println(2, "Adding node... " + _node);
	    targetPlaylist.addNode(_node, true, _cache);
	}
	return !alreadyExists;
    }
    
    protected IContainer importContainer
	(IContainer _targetContainer, IImportFolder _sourceFile,
	 FIDChangeSet _changeSet, IProgressListener _progressListener) {
	FIDPlaylist targetPlaylist = (FIDPlaylist) _targetContainer;
	FIDPlaylist parentPlaylist;
	if (getPlayerDatabase().isNestedPlaylistAllowed()) {
	    System.out.println
		("FIDPlaylistContainerModifier.importContainer: sourceFile = "
		 + _sourceFile);
	    String sourceFileName = _sourceFile.getName();
	    System.out.println
		("FIDPlaylistContainerModifier.importContainer: targetContainer = "
		 + _targetContainer);
	    int childPlaylistIndex
		= targetPlaylist.getPlaylistIndex(sourceFileName, false, true,
						  CollationKeyCache
						      .createDefaultCache());
	    System.out.println
		("FIDPlaylistContainerModifier.importContainer: childPlaylistIndex of "
		 + sourceFileName + " in " + targetPlaylist + " = "
		 + childPlaylistIndex);
	    parentPlaylist = targetPlaylist.getPlaylistAt(childPlaylistIndex);
	    System.out.println
		("FIDPlaylistContainerModifier.importContainer: parentPlaylist = "
		 + parentPlaylist);
	    _changeSet.nodeAdded(parentPlaylist);
	} else
	    parentPlaylist = targetPlaylist;
	return parentPlaylist;
    }
    
    public int[] copyTo(ContainerSelection _sourceSelection,
			IConfirmationListener _confirmationListener,
			boolean _deepCopy,
			IProgressListener _progressListener) {
	IntVector indexesVec = new IntVector();
	Object[] selectedValues = _sourceSelection.getSelectedValues();
	int selectionSize = _sourceSelection.getSize();
	FIDPlaylist targetPlaylist = getTargetPlaylist();
	CollationKeyCache cache = CollationKeyCache.createDefaultCache();
	for (int i = 0; ((_progressListener == null
			  || !_progressListener.isStopRequested())
			 && i < selectionSize); i++) {
	    int index = this.copyTo(_sourceSelection.getContext(),
				    targetPlaylist, selectedValues[i],
				    _deepCopy, _progressListener, cache);
	    if (index != -1)
		indexesVec.addElement(index);
	}
	return indexesVec.toArray();
    }
    
    public void moveFrom(ContainerSelection _selection) {
	delete(_selection, null);
    }
    
    public int[] moveTo(ContainerSelection _sourceSelection,
			IConfirmationListener _confirmationListener,
			IProgressListener _progressListener) {
	int[] indexes = copyTo(_sourceSelection, _confirmationListener, false,
			       _progressListener);
	IContainerModifier nodeModifier
	    = ContainerModifierFactory.getInstance(_sourceSelection);
	nodeModifier.moveFrom(_sourceSelection);
	return indexes;
    }
}
