/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.nodestore;

import java.io.IOException;

import javax.swing.tree.TreePath;

import org.jempeg.protocol.IProtocolClient;

import com.inzyme.model.Reason;

/**
* FIDRemoteTune represents an MP3, WMA, or other media file
* that exists on the Empeg (as opposed to a tune that exists
* on your local machin, which would be an FIDLocalFile).
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class FIDRemoteTune extends AbstractFIDNode {
	static final long serialVersionUID = 434770252703075610L;
	
	public FIDRemoteTune(PlayerDatabase _db, long _fid, NodeTags _tags) {
		super(_db, _fid, _tags);
		addToDatabase(false);
		setIdentified(true);
		setType(IFIDNode.TYPE_TUNE);
	}
	
	FIDRemoteTune(FIDLocalFile _localFile) {
		super(_localFile.getDB(), _localFile.getFID(), _localFile.getTags());
		addToDatabase(false);
		copyAttributes(_localFile);
		setType(IFIDNode.TYPE_TUNE);
		setIdentified(true);
	}
	
  public Reason[] checkForProblems(boolean _repair, TreePath _path) {
		return Reason.NO_REASONS;
	}
	
  public void identify(IProtocolClient _protocolClient) throws IOException {
    RemoteImportFile remoteImportFile = RemoteImportFile.createInstance(this, _protocolClient);
    identifyFile(remoteImportFile, true, false);
  }
  
	protected boolean isAddedToDatabase() {
		return true;
	}

}
