/* FIDRemoteTune - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.IOException;

import javax.swing.tree.TreePath;

import com.inzyme.model.Reason;

import org.jempeg.protocol.IProtocolClient;

public class FIDRemoteTune extends AbstractFIDNode
{
    static final long serialVersionUID = 434770252703075610L;
    
    public FIDRemoteTune(PlayerDatabase _db, long _fid, NodeTags _tags) {
	super(_db, _fid, _tags);
	addToDatabase(false);
	setIdentified(true);
	setType(3);
    }
    
    FIDRemoteTune(FIDLocalFile _localFile) {
	super(_localFile.getDB(), _localFile.getFID(), _localFile.getTags());
	addToDatabase(false);
	copyAttributes(_localFile);
	setType(3);
	setIdentified(true);
    }
    
    public Reason[] checkForProblems(boolean _repair, TreePath _path) {
	return Reason.NO_REASONS;
    }
    
    public void identify(IProtocolClient _protocolClient) throws IOException {
	RemoteImportFile remoteImportFile
	    = RemoteImportFile.createInstance(this, _protocolClient);
	identifyFile(remoteImportFile, true, false);
    }
    
    protected boolean isAddedToDatabase() {
	return true;
    }
}
