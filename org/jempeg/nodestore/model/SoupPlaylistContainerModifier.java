/* SoupPlaylistContainerModifier - Decompiled by JODE
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
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.filesystem.M3UImportFile;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.soup.NoNonSoupPlaylistsTraversalFilter;

public class SoupPlaylistContainerModifier
    extends AbstractPlaylistContainerModifier
{
    public SoupPlaylistContainerModifier(FIDPlaylist _targetSoupPlaylist) {
	super(_targetSoupPlaylist);
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
			      ("deleteConfirmation.soupMessage",
			       new Object[] { new Integer(size), title }));
	    confirmation = _confirmationListener.confirm(message, null, null);
	}
	if (confirmation == 0) {
	    Vector deleteVec = new Vector();
	    for (int i = 0; i < size; i++)
		ContainerUtils.traverse
		    (_selection.getValueAt(i), deleteVec, new AllowAllFilter(),
		     new NoNonSoupPlaylistsTraversalFilter());
	    Enumeration deletedNodesEnum = deleteVec.elements();
	    while (deletedNodesEnum.hasMoreElements()) {
		IFIDNode fidNode = (IFIDNode) deletedNodesEnum.nextElement();
		if (fidNode != null) {
		    boolean delete = true;
		    if (fidNode instanceof FIDPlaylist) {
			FIDPlaylist deletePlaylist = (FIDPlaylist) fidNode;
			if (deletePlaylist.isSoup())
			    delete = false;
		    }
		    if (delete)
			fidNode.delete();
		}
	    }
	}
    }
    
    protected boolean importFile(IContainer _targetContainer, IFIDNode _node,
				 FIDChangeSet _changeSet,
				 CollationKeyCache _cache) {
	return true;
    }
    
    protected IContainer importContainer
	(IContainer _targetContainer, IImportFolder _sourceFolder,
	 FIDChangeSet _changeSet, IProgressListener _progressListener) {
	IContainer targetContainer;
	if (_sourceFolder instanceof M3UImportFile) {
	    FIDPlaylist rootPlaylist = getPlayerDatabase().getRootPlaylist();
	    int newPlaylistIndex
		= rootPlaylist.getPlaylistIndex(_sourceFolder.getName(), 2, 3,
						null, false, true,
						CollationKeyCache
						    .createDefaultCache());
	    targetContainer = rootPlaylist.getPlaylistAt(newPlaylistIndex);
	} else
	    targetContainer = _targetContainer;
	return targetContainer;
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
