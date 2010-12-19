/* INodeDatabaseChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;

public interface INodeDatabaseChange extends IDatabaseChange
{
    public IFIDNode getNode();
    
    public boolean nodeEquals(IFIDNode ifidnode);
}
