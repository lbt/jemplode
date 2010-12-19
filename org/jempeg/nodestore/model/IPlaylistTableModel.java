/* IPlaylistTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import com.inzyme.table.ISortableTableModel;

import org.jempeg.nodestore.IFIDNode;

public interface IPlaylistTableModel extends ISortableTableModel
{
    public NodeTag getNodeTag(int i);
    
    public IFIDNode getNodeAt(int i);
    
    public String[] getColumnTagNames();
    
    public void setColumnTagNames(String[] strings);
}
