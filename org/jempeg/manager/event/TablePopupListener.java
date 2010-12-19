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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
* PopupListener implements the right-click action on the
* tree and table.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class TablePopupListener extends MouseAdapter {
	public JPopupMenu myPopup;

	public TablePopupListener(JPopupMenu _popup) {
		myPopup = _popup;
	}

	public void mousePressed(MouseEvent _event) {
		maybeShowPopup(_event);
	}

	public void mouseReleased(MouseEvent _event) {
		maybeShowPopup(_event);
	}

	protected void maybeShowPopup(MouseEvent _event) {
		if (_event.isPopupTrigger()) {
      JTable table = (JTable)_event.getComponent();
      if (table.getSelectedRowCount() <= 1) {
        int row = table.rowAtPoint(_event.getPoint());
        if (row != -1) {
          table.setRowSelectionInterval(row, row);
        }
      }
			table.requestFocus();
			myPopup.show(table, _event.getX(), _event.getY());
			_event.consume();
		}
	}
}
