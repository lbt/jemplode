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
package com.inzyme.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.inzyme.model.Reason;
import com.inzyme.table.ReasonsTableModel;

/**
* ReasonsDialog provides a GUI for presenting
* a set of reasons.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class ReasonsDialog extends JDialog {
  private ConfirmationPanel myConfirmationPanel;

  public ReasonsDialog(JFrame _frame, String _title, String _label, Reason[] _reasons, boolean _showFilenames) {
    this(_frame, _title, _label, "OK", "Cancel", _reasons, _showFilenames);
  }
  
  public ReasonsDialog(JFrame _frame, String _title, String _label, String _okLabel, String _cancelLabel, Reason[] _reasons, boolean _showFilenames) {
    super(_frame, _title, true);

    JPanel layoutPanel = new JPanel();
    layoutPanel.setLayout(new BorderLayout());

    JLabel label = new JLabel(_label);
    layoutPanel.add(label, BorderLayout.NORTH);

    if (_showFilenames) {
      ReasonsTableModel reasonsTableModel = new ReasonsTableModel(_reasons);
      JTable reasonsTable = new JTable(reasonsTableModel);
      JScrollPane reasonsScrollPane = new JScrollPane(reasonsTable);
      layoutPanel.add(reasonsScrollPane, BorderLayout.CENTER);
    } else {
      Vector reasons = new Vector();
      for (int i = 0; i < _reasons.length; i ++) {
        reasons.addElement(_reasons[i].getReason());
      }
      JList reasonsList = new JList(reasons);
      JScrollPane reasonsScrollPane = new JScrollPane(reasonsList);
      layoutPanel.add(reasonsScrollPane, BorderLayout.CENTER);
    }

    myConfirmationPanel = new ConfirmationPanel(layoutPanel);
    myConfirmationPanel.addOkListener(new CloseDialogListener(this));
    myConfirmationPanel.addCancelListener(new CloseDialogListener(this));
    if (_okLabel == null) {
      myConfirmationPanel.setOkVisible(false);
    } else {
      myConfirmationPanel.setOkText(_okLabel);
    }
    if (_cancelLabel == null) {
      myConfirmationPanel.setCancelVisible(false);
    } else {
      myConfirmationPanel.setCancelText(_cancelLabel);
    }
    
		getContentPane().add(myConfirmationPanel);

		pack();
    setSize(Math.max(420, getSize().width), 350);
	  DialogUtils.centerWindow(this);
  }

  public int getSelectedOption() {
    return myConfirmationPanel.getSelectedOption();
  }
  
  public void addOkListener(ActionListener _listener) {
    myConfirmationPanel.addOkListener(_listener);
  }
  
  public void addCancelListener(ActionListener _listener) {
    myConfirmationPanel.addCancelListener(_listener);
  }
}
