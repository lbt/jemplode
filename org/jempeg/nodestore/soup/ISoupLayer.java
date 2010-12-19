/* ISoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import com.inzyme.text.CollationKeyCache;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public interface ISoupLayer
{
    public int getType();
    
    public NodeTag getSortTag();
    
    public CollationKeyCache getSortCache();
    
    public boolean isDependentOn(NodeTag nodetag);
    
    public boolean qualifies(IFIDNode ifidnode);
    
    public String toExternalForm();
}
