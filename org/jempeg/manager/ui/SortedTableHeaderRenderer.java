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
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.inzyme.table.SortedTableModel;

/**
* SortedTableHeaderRenderer implements the custom renderer that is
* used to draw sort arrows in the table headers.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class SortedTableHeaderRenderer implements TableCellRenderer {
  private TableCellRenderer myOriginalRenderer;

	public SortedTableHeaderRenderer(TableCellRenderer _originalRenderer) {
    myOriginalRenderer = _originalRenderer;
	}

	public Component getTableCellRendererComponent(JTable _table, Object _value, boolean _isSelected, boolean _hasFocus, int _row, int _column) {
		Component comp = myOriginalRenderer.getTableCellRendererComponent(_table, _value, _isSelected, _hasFocus, _row, _column);
		
		if (comp instanceof JLabel) {
			JLabel label = (JLabel)comp;
			Object tableModel = _table.getModel();
			if (tableModel instanceof SortedTableModel) {
				SortedTableModel sortedTableModel = (SortedTableModel)tableModel;
				int sortingColumn = sortedTableModel.getSortingColumn();
				if (sortingColumn == _column) {
					boolean ascending = sortedTableModel.isAscending();
					Color background = label.getBackground();
					if (background == null) {
						background = Color.white;
					}
					if (ascending) {
						label.setIcon(new ArrowIcon(8, 8, SwingConstants.NORTH, background.brighter(), background.darker(), background));
					} else {
						label.setIcon(new ArrowIcon(8, 8, SwingConstants.SOUTH, background.brighter(), background.darker(), background));
					}
					label.setHorizontalTextPosition(SwingConstants.LEFT);
					label.setIconTextGap(10);
				} else {
					label.setIcon(null);
				}
			}
		}
    
		return comp;
	}
}
 
	
