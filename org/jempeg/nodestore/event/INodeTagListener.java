/* INodeTagListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.event;
import java.util.EventListener;

import org.jempeg.nodestore.IFIDNode;

public interface INodeTagListener extends EventListener
{
    public void nodeIdentified(IFIDNode ifidnode);
    
    public void beforeNodeTagModified(IFIDNode ifidnode, String string,
				      String string_0_, String string_1_);
    
    public void afterNodeTagModified(IFIDNode ifidnode, String string,
				     String string_2_, String string_3_);
}
