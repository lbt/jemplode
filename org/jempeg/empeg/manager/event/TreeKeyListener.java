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
package org.jempeg.empeg.manager.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTree;

/**
* TreeKeyListener provides support for 
* typing in a JTree and jumping to a particular
* row of the tree.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class TreeKeyListener extends KeyAdapter {
  public TreeKeyListener() {
  }

  public synchronized void keyPressed(KeyEvent _event) {
  	if (!(_event.isMetaDown() || _event.isControlDown() || _event.isAltDown())) { 
	  	JTree tree = (JTree)_event.getSource();
	    String selectionStr = new String(new char[] { _event.getKeyChar() }).toLowerCase();
	
	    int size = tree.getRowCount();
	    int[] selectionRows = tree.getSelectionRows();
	    int selectionRow;
	    if (selectionRows != null && selectionRows.length > 0) {
	      selectionRow = selectionRows[0];
	    } else {
	      selectionRow = 0;
	    }
	
	    boolean done = false;
	    boolean selected = false;
	    do {
	      for (int i = selectionRow + 1; !selected && i < size; i ++) {
	        String value = tree.getPathForRow(i).getLastPathComponent().toString().toLowerCase();
	        if (value.startsWith(selectionStr)) {
	          tree.setSelectionInterval(i, i);
	          tree.scrollRowToVisible(i);
	          selected = true;
	        }
	      }
	      if (!selected && selectionRow != 0) {
	        selectionRow = 0;
	      } else {
	        done = true;
	      }
	    } while (!selected && !done);
  	}
  }
}
