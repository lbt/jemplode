/* NodeRemovedDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class NodeRemovedDatabaseChange extends AbstractNodeDatabaseChange
{
    public NodeRemovedDatabaseChange(IFIDNode _node) {
	super(_node);
    }
    
    public String getDescription() {
	return ResourceBundleUtils.getUIString("databaseChange.nodeRemoved",
					       (new Object[]
						{ getNode().getTitle() }));
    }
    
    public long getLength() {
	return 5000L;
    }
    
    public void synchronize
	(ISynchronizeClient _synchronizeClient,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	try {
	    IFIDNode node = getNode();
	    _synchronizeClient.synchronizeDelete(node, _protocolClient);
	} catch (SynchronizeException e) {
	    throw new SynchronizeException
		      (new ResourceBundleKey("errors",
					     "synchronize.deleted.failed",
					     (new Object[]
					      { getDescription() })),
		       e);
	}
    }
}
