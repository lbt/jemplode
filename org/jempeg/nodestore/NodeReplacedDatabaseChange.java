/* NodeReplacedDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.BufferedInputStream;
import java.io.IOException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ISynchronizeClient;

public class NodeReplacedDatabaseChange extends AbstractNodeDatabaseChange
{
    private IImportFile myImportFile;
    private long myLength;
    
    public NodeReplacedDatabaseChange
	(IFIDNode _node, IImportFile _importFile) throws IOException {
	super(_node);
	myImportFile = _importFile;
	myLength = _importFile.getLength();
    }
    
    public String getDescription() {
	return ResourceBundleUtils.getUIString("databaseChange.nodeReplaced",
					       (new Object[]
						{ getNode().getTitle() }));
    }
    
    public void synchronize
	(ISynchronizeClient _synchronizeClient,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	try {
	    IFIDNode node = getNode();
	    long fid = node.getFID();
	    java.io.InputStream is
		= new BufferedInputStream(myImportFile.getInputStream());
	    try {
		_protocolClient.write(0L, fid, 0L, myLength, is, myLength);
	    } catch (Object object) {
		is.close();
		throw object;
	    }
	    is.close();
	    _synchronizeClient.synchronizeTags(node, _protocolClient);
	    node.setDirty(false);
	} catch (Exception e) {
	    throw new SynchronizeException
		      (new ResourceBundleKey("errors",
					     "synchronize.localFile.failed",
					     (new Object[]
					      { getDescription() })),
		       e);
	}
    }
    
    public long getLength() {
	return myLength;
    }
}
