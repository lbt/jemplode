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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.inzyme.util.Timer;

/**
* TableKeyListener provides support for 
* typing in a JTable and jumping to a particular
* row of the table.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class TableKeyListener extends KeyAdapter {
  private StringBuffer mySelection;
  private Timer myTimer;

  public TableKeyListener() {
    mySelection = new StringBuffer();
    myTimer = new Timer(1000, this, "clear");
  }

  public void keyPressed(KeyEvent _event) {
  	if (!(_event.isMetaDown() || _event.isControlDown() || _event.isAltDown())) { 
	  	JTable table = (JTable)_event.getSource();
	    mySelection.append(_event.getKeyChar());
	
	    TableModel tableModel = table.getModel();
	    int size = tableModel.getRowCount();
	    String selectionStr = mySelection.toString().toLowerCase();
	    boolean selected = false;
	    for (int i = 0; !selected && i < size; i ++) {
	      String value = tableModel.getValueAt(i, 0).toString().toLowerCase();
	      if (value.startsWith(selectionStr)) {
	        table.setRowSelectionInterval(i, i);
	        table.scrollRectToVisible(table.getCellRect(i, 0, true));
	        selected = true;
	      }
	    }
	
	    myTimer.mark();
  	}
  }

  public void clear() {
    mySelection.setLength(0);
  }
}
