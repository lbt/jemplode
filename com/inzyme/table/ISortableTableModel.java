/* ISortableTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.table;
import javax.swing.table.TableModel;

import com.inzyme.container.IContainer;

public interface ISortableTableModel extends IContainer, TableModel
{
    public Class getSortColumnClass(int i);
    
    public Object getSortValueAt(int i, int i_0_);
}
