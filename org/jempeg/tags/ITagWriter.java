/* ITagWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.File;
import java.io.IOException;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public interface ITagWriter
{
    public boolean isTagWriterFor(IFIDNode ifidnode);
    
    public void writeTags(IFIDNode ifidnode, FIDPlaylist fidplaylist,
			  File file) throws IOException;
}
