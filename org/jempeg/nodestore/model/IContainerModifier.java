/* IContainerModifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;

public interface IContainerModifier
{
    public IContainer getTargetContainer();
    
    public void delete(ContainerSelection containerselection,
		       IConfirmationListener iconfirmationlistener);
    
    public FIDChangeSet importFiles
	(IImportFile[] iimportfiles,
	 IConfirmationListener iconfirmationlistener,
	 IProgressListener iprogresslistener, boolean bool);
    
    public void importFiles(IContainer icontainer, IImportFile[] iimportfiles,
			    FIDChangeSet fidchangeset,
			    IProgressListener iprogresslistener, boolean bool);
    
    public void moveFrom(ContainerSelection containerselection);
    
    public int[] moveTo(ContainerSelection containerselection,
			IConfirmationListener iconfirmationlistener,
			IProgressListener iprogresslistener);
    
    public int[] copyTo(ContainerSelection containerselection,
			IConfirmationListener iconfirmationlistener,
			boolean bool, IProgressListener iprogresslistener);
}
