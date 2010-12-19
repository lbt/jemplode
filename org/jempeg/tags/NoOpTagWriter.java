/* NoOpTagWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.File;
import java.io.IOException;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public class NoOpTagWriter implements ITagWriter
{
    public boolean isTagWriterFor(IFIDNode _node) {
	return true;
    }
    
    public void writeTags(IFIDNode _node, FIDPlaylist _parentPlaylist,
			  File _file) throws IOException {
	/* empty */
    }
}
