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
package org.jempeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jempeg.ApplicationContext;

/**
* Inverts the selection in the table
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class InvertSelectionAction extends AbstractAction {
	private ApplicationContext myContext;
	private JTable myTable;

	public InvertSelectionAction(ApplicationContext _context, JTable _table) {
		myContext = _context;
		myTable = _table;
	}
	
	public void actionPerformed(ActionEvent _event) {
		JTable table = (myTable == null) ? myContext.getTable() : myTable;
    int rowCount = table.getRowCount();
    int[] selectedRows = table.getSelectedRows();
    int[] invertedRows = new int[rowCount - selectedRows.length];

    int invertedRowIndex = 0;
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex ++) {
      boolean selected = false;
      for (int selectedRowIndex = 0; !selected && selectedRowIndex < selectedRows.length; selectedRowIndex ++) {
        if (selectedRows[selectedRowIndex] == rowIndex) {
          selected = true;
        }
      }
      if (!selected) {
        invertedRows[invertedRowIndex ++] = rowIndex;
      }
    }

    ListSelectionModel selectionModel = table.getSelectionModel();
    selectionModel.clearSelection();
    for (int i = 0; i < invertedRows.length; i ++) {
      selectionModel.addSelectionInterval(invertedRows[i], invertedRows[i]);
    }
		table.grabFocus();
	}
}
