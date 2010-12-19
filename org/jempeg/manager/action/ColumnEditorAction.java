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
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JTable;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.ColumnEditorDialog;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.model.IPlaylistTableModel;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.PlaylistTableUtils;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

/**
* ColumnEditorAction hooks the JTableHeader up to
* the column editor so that the use can click on the
* header to adjust the columns.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class ColumnEditorAction extends MouseAdapter implements ActionListener {
  private ApplicationContext myContext;
  private JTable myTable;

  public ColumnEditorAction(ApplicationContext _context, JTable _table) {
  	myContext = _context;
  	myTable = _table;
  }

  public void mouseClicked(MouseEvent _event) {
    if ((_event.getModifiers() & InputEvent.META_MASK) > 0) {
      editColumns();
    }
  }

  public void actionPerformed(ActionEvent _event) {
    editColumns();
  }

  public void editColumns() {
		JTable table = (myTable == null) ? myContext.getTable() : myTable;

  	int fidType;
		String[] originalColumns;
		IPlaylistTableModel playlistTableModel = PlaylistTableUtils.getPlaylistTableModel(table.getModel());
		IFIDPlaylistWrapper playlistWrapper = PlaylistTableUtils.getPlaylistWrapper(table.getModel());
		if (playlistWrapper != null) {
			fidType = playlistWrapper.getPlaylist().getContainedType();
		} else {
			fidType = -1;
		}
		
		if (playlistTableModel != null) {
			originalColumns = playlistTableModel.getColumnTagNames();
		} else {
			originalColumns = PlaylistTableUtils.getColumnTagNames();
		}

    ColumnEditorDialog columnEditor = new ColumnEditorDialog(myContext.getFrame(), originalColumns);
    columnEditor.setVisible(true);

		String[] newColumns = columnEditor.getColumns();
		PlaylistTableUtils.setColumnTagNames(fidType, newColumns);
		NodeTag[] nodeTags = NodeTag.getNodeTags();
		for (int i = 0; i < nodeTags.length; i ++) {
			String tagName = nodeTags[i].getName();
			int width = columnEditor.getColumnWidth(tagName);
			PlaylistTableUtils.setColumnWidth(tagName, width);
		}
		
    try {
      PropertiesManager.getInstance().save();
    }
    catch (IOException e) {
    	Debug.println(e);
    }
    
    if (playlistTableModel != null) {
    	playlistTableModel.setColumnTagNames(newColumns);
    }
    
    columnEditor.dispose();
  }
}
