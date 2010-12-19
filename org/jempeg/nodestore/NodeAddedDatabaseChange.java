/* NodeAddedDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class NodeAddedDatabaseChange extends AbstractNodeAddedDatabaseChange
{
    public NodeAddedDatabaseChange(FIDLocalFile _localFile) {
	super((IFIDNode) _localFile);
    }
    
    public String getDescription() {
	return ResourceBundleUtils.getUIString("databaseChange.nodeAdded",
					       (new Object[]
						{ getNode().getTitle() }));
    }
    
    public void synchronize
	(ISynchronizeClient _synchronizeClient,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	FIDLocalFile localFileNode = (FIDLocalFile) getNode();
	_synchronizeClient.synchronizeFile(localFileNode, _protocolClient);
	_synchronizeClient.synchronizeTags(localFileNode, _protocolClient);
	FIDRemoteTune remoteTune = new FIDRemoteTune(localFileNode);
	remoteTune.setDirty(false);
	localFileNode.setDirty(false);
    }
}
