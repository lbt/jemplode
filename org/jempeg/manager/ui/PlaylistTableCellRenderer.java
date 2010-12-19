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
package org.jempeg.manager.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.IPlaylistTableModel;

import com.inzyme.container.IMutableTypeContainer;
import com.inzyme.table.ISortableTableModel;
import com.inzyme.tree.IContainerTreeNode;

/**
* PlaylistTableCellRenderer implements the custom renderer that is
* used in all playlist table views (colorizing, icons, etc)
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class PlaylistTableCellRenderer implements TableCellRenderer {
	private TableCellRenderer myOriginalRenderer;

	public PlaylistTableCellRenderer(TableCellRenderer _originalRenderer) {
		myOriginalRenderer = _originalRenderer;
	}

	public Component getTableCellRendererComponent(JTable _table, Object _value, boolean _isSelected, boolean _hasFocus, int _row, int _column) {
		Component comp = myOriginalRenderer.getTableCellRendererComponent(_table, _value, _isSelected, _hasFocus, _row, _column);
		try {
			int type;

			Object rowObject;
			Object tableModelObj = _table.getModel();
			if (tableModelObj instanceof IPlaylistTableModel) {
				IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) tableModelObj;
				IFIDNode fidNode = playlistTableModel.getNodeAt(_row);
				type = fidNode.getType();
				rowObject = fidNode;
			} else if (tableModelObj instanceof ISortableTableModel) {
				ISortableTableModel sortableTableModel = (ISortableTableModel) tableModelObj;
				rowObject = sortableTableModel.getValueAt(_row);
				if (rowObject instanceof IMutableTypeContainer) {
					IMutableTypeContainer containerTreeNode = (IMutableTypeContainer) rowObject;
					type = containerTreeNode.getType();
				} else {
					type = IContainerTreeNode.TYPE_FOLDER;
				}
			} else {
				type = IContainerTreeNode.TYPE_FOLDER;
				rowObject = null;
			}

			if (comp instanceof JLabel) {
				JLabel label = (JLabel) comp;
				if (_column == 0) {
					label.setIcon(PlaylistTreeCellRenderer.ICONS[type]);
				} else {
					label.setIcon(null);
				}
				label.setOpaque(true);
			}

			Color foreground;
			Color background;
			if (_isSelected) {
				foreground = _table.getSelectionForeground();
				background = _table.getSelectionBackground();
			} else {
				foreground = _table.getForeground();
				background = _table.getBackground();
			}

			double darkenPercentage = 0.95;
			Color altBackground = new Color((int) (background.getRed() * darkenPercentage), (int) (background.getGreen() * darkenPercentage), (int) (background.getBlue() * darkenPercentage));
			if (_row % 2 == 0) {
				comp.setBackground(background);
			} else {
				comp.setBackground(altBackground);
			}

			NodeColorizer.colorize(comp, rowObject, foreground, _isSelected);
		} catch (Throwable t) {
			// Doesn't matter ... 
		}

		return comp;
	}
}
