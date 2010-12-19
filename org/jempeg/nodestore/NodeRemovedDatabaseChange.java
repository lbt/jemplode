package org.jempeg.nodestore;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;

public class NodeRemovedDatabaseChange extends AbstractNodeDatabaseChange {
	public NodeRemovedDatabaseChange(IFIDNode _node) {
		super(_node);
	}
	
	public String getDescription() {
		return ResourceBundleUtils.getUIString("databaseChange.nodeRemoved", new Object[] { getNode().getTitle() });
	}
	
	public long getLength() {
		return 5000;
	}

	public void synchronize(ISynchronizeClient _synchronizeClient, IProtocolClient _protocolClient) throws SynchronizeException {
		try {
			IFIDNode node = getNode();
			_synchronizeClient.synchronizeDelete(node, _protocolClient);
		}
		catch (SynchronizeException e) {
			throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.deleted.failed", new Object[] { this.getDescription() }), e);
		}
	}
}
