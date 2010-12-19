/* AbstractPlaylistContainerModifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.text.CollationKeyCache;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.RemoteImportFile;

public abstract class AbstractPlaylistContainerModifier
    extends AbstractContainerModifier
{
    public AbstractPlaylistContainerModifier(FIDPlaylist _targetPlaylist) {
	super(_targetPlaylist.getDB(), _targetPlaylist);
    }
    
    protected FIDPlaylist getTargetPlaylist() {
	return (FIDPlaylist) getTargetContainer();
    }
    
    protected int copyTo
	(ApplicationContext _sourceContext, FIDPlaylist _targetPlaylist,
	 Object _sourceObj, boolean _deepCopy,
	 IProgressListener _progressListener, CollationKeyCache _cache) {
	PlayerDatabase localDatabase = _targetPlaylist.getDB();
	int copiedToIndex = -1;
	if (_sourceObj instanceof IContainer) {
	    if (_sourceObj instanceof IFIDPlaylistWrapper && !_deepCopy
		&& (((IFIDPlaylistWrapper) _sourceObj).getPlaylist().getDB()
		    == localDatabase)) {
		FIDPlaylist playlist
		    = ((IFIDPlaylistWrapper) _sourceObj).getPlaylist();
		copiedToIndex
		    = _targetPlaylist.addNode(playlist, true, _cache);
	    } else {
		IContainer container = (IContainer) _sourceObj;
		int size = container.getSize();
		String name = container.getName();
		FIDPlaylist newTargetPlaylist;
		CollationKeyCache newCache;
		if (localDatabase.isNestedPlaylistAllowed()) {
		    copiedToIndex
			= _targetPlaylist.getPlaylistIndex(name, false, true,
							   _cache);
		    newTargetPlaylist
			= _targetPlaylist.getPlaylistAt(copiedToIndex);
		    newCache = CollationKeyCache.createDefaultCache();
		} else {
		    newTargetPlaylist = _targetPlaylist;
		    newCache = _cache;
		}
		int childCopiedToIndex = -1;
		for (int i = 0; ((_progressListener == null
				  || !_progressListener.isStopRequested())
				 && i < size); i++)
		    childCopiedToIndex
			= copyTo(_sourceContext, newTargetPlaylist,
				 container.getValueAt(i), _deepCopy,
				 _progressListener, newCache);
		if (!localDatabase.isNestedPlaylistAllowed())
		    copiedToIndex = childCopiedToIndex;
	    }
	} else if (_sourceObj instanceof IFIDNode) {
	    IFIDNode node = (IFIDNode) _sourceObj;
	    if (node.getDB() != localDatabase) {
		IImportFile importFile;
		if (node instanceof FIDLocalFile)
		    importFile = ((FIDLocalFile) node).getFile();
		else
		    importFile = (RemoteImportFile.createInstance
				  (node, (_sourceContext.getSynchronizeClient
					      ().getProtocolClient
					  (new SilentProgressListener()))));
		ContainerModifierFactory.getInstance(_targetPlaylist)
		    .importFiles
		    (new IImportFile[] { importFile }, null, _progressListener,
		     true);
	    } else
		copiedToIndex = _targetPlaylist.addNode(node, true, _cache);
	} else
	    copiedToIndex = -1;
	return copiedToIndex;
    }
}
