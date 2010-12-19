/* AbstractContainerModifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.util.List;
import java.util.Vector;

import com.inzyme.container.AllowAllFilter;
import com.inzyme.container.ContainerUtils;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.IImportFolder;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.sort.QuickSort;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.tree.AllowAllTraversalFilter;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;

public abstract class AbstractContainerModifier implements IContainerModifier
{
    private PlayerDatabase myPlayerDatabase;
    private IContainer myTargetContainer;
    
    public AbstractContainerModifier(PlayerDatabase _playerDatabase,
				     IContainer _targetContainer) {
	myPlayerDatabase = _playerDatabase;
	myTargetContainer = _targetContainer;
    }
    
    protected PlayerDatabase getPlayerDatabase() {
	return myPlayerDatabase;
    }
    
    public IContainer getTargetContainer() {
	return myTargetContainer;
    }
    
    public FIDChangeSet importFiles
	(IImportFile[] _sourceFiles,
	 IConfirmationListener _confirmationListener,
	 IProgressListener _progressListener, boolean _identifyImmediately) {
	boolean alreadyInProgress = false;
	org.jempeg.nodestore.SynchronizeQueue synchronizequeue;
	MONITORENTER (synchronizequeue
		      = myPlayerDatabase.getSynchronizeQueue());
	FIDChangeSet fidchangeset;
	MISSING MONITORENTER
	synchronized (synchronizequeue) {
	    FIDChangeSet changeSet = new FIDChangeSet();
	    _progressListener.setStopEnabled(true);
	    try {
		Vector importVec = new Vector();
		for (int i = 0; i < _sourceFiles.length; i++)
		    ContainerUtils.traverse(_sourceFiles[i], importVec,
					    new AllowAllFilter(),
					    new AllowAllTraversalFilter());
		int totalCount = importVec.size();
		alreadyInProgress = _progressListener.isInProgress();
		_progressListener.progressStarted();
		_progressListener.operationStarted
		    (ResourceBundleUtils.getUIString("import.operation"));
		_progressListener.operationUpdated(0L, (long) totalCount);
		importFiles(myTargetContainer, _sourceFiles, changeSet,
			    _progressListener, _identifyImmediately);
	    } catch (Object object) {
		if (!alreadyInProgress)
		    _progressListener.progressCompleted();
		throw object;
	    }
	    if (!alreadyInProgress)
		_progressListener.progressCompleted();
	    fidchangeset = changeSet;
	}
	return fidchangeset;
    }
    
    public void importFiles
	(IContainer _targetContainer, IImportFile[] _sourceFiles,
	 FIDChangeSet _changeSet, IProgressListener _progressListener,
	 boolean _identifyImmediately) {
	object = object_0_;
	break while_12_;
    }
    
    protected abstract boolean importFile
	(IContainer icontainer, IFIDNode ifidnode, FIDChangeSet fidchangeset,
	 CollationKeyCache collationkeycache);
    
    protected abstract IContainer importContainer
	(IContainer icontainer, IImportFolder iimportfolder,
	 FIDChangeSet fidchangeset, IProgressListener iprogresslistener);
}
