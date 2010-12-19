/* ContainerModifierUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.exception.MethodNotImplementedException;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.ChangeSetDialog;
import org.jempeg.nodestore.model.FIDChangeSet;
import org.jempeg.nodestore.model.IContainerModifier;

public class ContainerModifierUI implements IContainerModifier
{
    private ApplicationContext myContext;
    private IContainerModifier myNodeModifier;
    
    public ContainerModifierUI(ApplicationContext _context,
			       IContainerModifier _nodeModifier) {
	myContext = _context;
	myNodeModifier = _nodeModifier;
    }
    
    public IContainer getTargetContainer() {
	return myNodeModifier.getTargetContainer();
    }
    
    public void delete(final ContainerSelection _selection,
		       final IConfirmationListener _confirmationListener) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		myNodeModifier.delete(_selection, _confirmationListener);
	    }
	});
	t.start();
    }
    
    public FIDChangeSet importFiles
	(final IImportFile[] _sourceFiles,
	 final IConfirmationListener _confirmationListener,
	 final IProgressListener _progressListener,
	 final boolean _identifyImmediately) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		FIDChangeSet changeSet
		    = myNodeModifier.importFiles(_sourceFiles,
						 _confirmationListener,
						 _progressListener,
						 _identifyImmediately);
		boolean onlyShowFailedImports
		    = PropertiesManager.getInstance()
			  .getBooleanProperty("jempeg.onlyShowFailedImports");
		if (!onlyShowFailedImports
		    || changeSet.getSkippedReasons().length > 0
		    || changeSet.getFailedReasons().length > 0) {
		    ChangeSetDialog changeSetDialog
			= (new ChangeSetDialog
			   (myContext.getFrame(),
			    ResourceBundleUtils
				.getUIString("import.completion.frameTitle"),
			    "import", changeSet, !onlyShowFailedImports,
			    true));
		    changeSetDialog.setVisible(true);
		    changeSetDialog.dispose();
		}
	    }
	});
	t.start();
	return new FIDChangeSet();
    }
    
    public void importFiles
	(IContainer _targetContainer, IImportFile[] _sourceFiles,
	 FIDChangeSet _changeSet, IProgressListener _progressListener,
	 boolean _identifyImmediately) {
	throw new MethodNotImplementedException
		  ("This should never get called.");
    }
    
    public void moveFrom(ContainerSelection _sourceSelection) {
	/* empty */
    }
    
    public int[] moveTo(final ContainerSelection _sourceSelection,
			final IConfirmationListener _confirmationListener,
			final IProgressListener _progressListener) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		int[] indexes = myNodeModifier.moveTo(_sourceSelection,
						      _confirmationListener,
						      _progressListener);
		IContainer targetContainer
		    = myNodeModifier.getTargetContainer();
		myContext.setSelection
		    (this,
		     new ContainerSelection(_sourceSelection.getContext(),
					    targetContainer, indexes));
	    }
	});
	t.start();
	return new int[0];
    }
    
    public int[] copyTo(final ContainerSelection _sourceSelection,
			final IConfirmationListener _confirmationListener,
			final boolean _deepCopy,
			final IProgressListener _progressListener) {
	Thread t = new Thread(new Runnable() {
	    public void run() {
		try {
		    _progressListener.progressStarted();
		    int[] indexes
			= myNodeModifier.copyTo(_sourceSelection,
						_confirmationListener,
						_deepCopy, _progressListener);
		    IContainer targetContainer
			= myNodeModifier.getTargetContainer();
		    myContext.setSelection
			(this,
			 new ContainerSelection(_sourceSelection.getContext(),
						targetContainer, indexes));
		} catch (Object object) {
		    _progressListener.progressCompleted();
		    throw object;
		}
		_progressListener.progressCompleted();
	    }
	});
	t.start();
	return new int[0];
    }
}
