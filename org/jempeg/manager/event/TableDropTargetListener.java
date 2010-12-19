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

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import javax.swing.JTable;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.model.IPlaylistTableModel;

import com.inzyme.container.IContainer;
import com.inzyme.table.SortedTableModel;

/**
* TableDropTargetListener defines the specifics of
* dragging-and-dropping from the native filesystem
* onto the playlist table.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class TableDropTargetListener extends AbstractFileDropTargetListener {
	private JTable myTable;
	
	public TableDropTargetListener(ApplicationContext _context, JTable _table) {
		super(_context);
		myTable = _table;
	}

  public void dragEnter(DropTargetDragEvent _event) {
    super.dragEnter(_event);
    myTable.requestFocus();
  }

  protected boolean isValid(DropTargetEvent _event) {
    return (myTable.getModel() instanceof IPlaylistTableModel || getContext().getPlayerDatabase() != null);
  }

	protected IContainer getTargetContainer(DropTargetDropEvent _event) {
		IContainer targetContainer = null;
		Object tableModel = myTable.getModel();
		if (tableModel instanceof SortedTableModel) {
			targetContainer = (IContainer)((SortedTableModel)tableModel).getModel();
		} else if (tableModel instanceof IContainer) {
			targetContainer = (IContainer)tableModel;
		} else {
			targetContainer = getContext().getPlayerDatabase();
		}
		return targetContainer;
	}
}
