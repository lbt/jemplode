/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.manager.event;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.inzyme.table.SortedTableModel;

/**
* TableSortListener hooks the JTableHeader up to
* the SortedTableModel so that the use can click on the
* header to adjust the sort order.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class TableSortListener extends MouseAdapter {
  public TableSortListener() {
  }

  public void mouseClicked(MouseEvent _event) {
    if ((_event.getModifiers() & InputEvent.BUTTON1_MASK) > 0) {
      JTableHeader tableHeader = (JTableHeader)_event.getSource();
      JTable table = tableHeader.getTable();
      TableModel tableModel = table.getModel();
      if (tableModel instanceof SortedTableModel) {
	      SortedTableModel sortedTableModel = (SortedTableModel)tableModel;
	      TableColumnModel columnModel = table.getColumnModel();
	      int viewColumn = columnModel.getColumnIndexAtX(_event.getX()); 
	      int column = table.convertColumnIndexToModel(viewColumn); 
	      if (_event.getClickCount() % 2 != 0 && column != -1) {
	        boolean ascending = sortedTableModel.isAscending();
	        boolean clickAscending = true;
	        if (sortedTableModel.getSortingColumn() == column) {
	          clickAscending = !ascending;
	        }
	  
	        int shiftPressed = _event.getModifiers() & InputEvent.SHIFT_MASK; 
	        if (shiftPressed != 0) {
	          clickAscending = !ascending;
	        }
	  
	        sortedTableModel.sortByColumn(column, clickAscending); 
	      }
      }
			tableHeader.repaint();
    }
  }
}
