package org.jempeg.nodestore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;

public class NodeReplacedDatabaseChange extends AbstractNodeDatabaseChange {
	private IImportFile myImportFile;
	private long myLength;

	public NodeReplacedDatabaseChange(IFIDNode _node, IImportFile _importFile) throws IOException {
		super(_node);
		myImportFile = _importFile;
		myLength = _importFile.getLength();
	}

	public String getDescription() {
		return ResourceBundleUtils.getUIString("databaseChange.nodeReplaced", new Object[]{ getNode().getTitle() });
	}

	public void synchronize(ISynchronizeClient _synchronizeClient, IProtocolClient _protocolClient) throws SynchronizeException {
		try {
			IFIDNode node = getNode();
			long fid = node.getFID();
			InputStream is = new BufferedInputStream(myImportFile.getInputStream());
			try {
				_protocolClient.write(IProtocolClient.STORAGE_ZERO, fid, 0, myLength, is, myLength);
			}
			finally {
				is.close();
			}
      
      _synchronizeClient.synchronizeTags(node, _protocolClient);

			node.setDirty(false);
		}
		catch (Exception e) {
			throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.localFile.failed", new Object[]{ this.getDescription() }), e);
		}
	}

	public long getLength() {
		return myLength;
	}
}
