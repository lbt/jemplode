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
package org.jempeg.manager.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.FIDChangeSet;

import com.inzyme.model.Reason;
import com.inzyme.table.ReasonsTableModel;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

/**
* ChangeSetDialog provides a GUI for presenting
* a change set (i.e. displaying what nodes were
* skipped, failed, added, etc.)
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class ChangeSetDialog extends JDialog {
  private ConfirmationPanel myConfirmationPanel;

  public ChangeSetDialog(JFrame _frame, String _title, String _operation, FIDChangeSet _changeSet, boolean _showSuccessful, boolean _showFailed) {
    super(_frame, _title, true);

    IFIDNode[] addedNodes = _changeSet.getAddedNodes();
    String[] addedNodeTitles = new String[addedNodes.length];
    for (int i = 0; i < addedNodes.length; i ++) {
    	String title = addedNodes[i].getTitle();
    	addedNodeTitles[i] = title;
    }
    Reason[] skippedReasons = _changeSet.getSkippedReasons();
    Reason[] failedReasons = _changeSet.getFailedReasons();

    Reason[] reasons = new Reason[skippedReasons.length + failedReasons.length];
    System.arraycopy(skippedReasons, 0, reasons, 0, skippedReasons.length);
    System.arraycopy(failedReasons, 0, reasons, skippedReasons.length, failedReasons.length);

    JPanel layoutPanel = new JPanel();
    layoutPanel.setLayout(new BorderLayout());

    JPanel innerLayoutPanel = new JPanel();
    innerLayoutPanel.setLayout(new BoxLayout(innerLayoutPanel, BoxLayout.Y_AXIS));

      JLabel successLabel = new JLabel(ResourceBundleUtils.getUIString(_operation + ".completion.success", new Object[] { new Integer(addedNodes.length) }));
      successLabel.setAlignmentX(0.0f);
      innerLayoutPanel.add(successLabel);

      if (_showSuccessful) {
        JList successList = new JList(addedNodeTitles);
        JScrollPane successScrollPane = new JScrollPane(successList);
        successScrollPane.setAlignmentX(0.0f);
  
        innerLayoutPanel.add(successScrollPane);
  
        innerLayoutPanel.add(Box.createVerticalStrut(20));
      }

      JLabel skippedLabel = new JLabel(ResourceBundleUtils.getUIString(_operation + ".completion.skipped", new Object[] { new Integer(skippedReasons.length) }));
      skippedLabel.setAlignmentX(0.0f);
      JLabel failedLabel = new JLabel(ResourceBundleUtils.getUIString(_operation + ".completion.failed", new Object[] { new Integer(failedReasons.length) }));
      failedLabel.setAlignmentX(0.0f);
      innerLayoutPanel.add(skippedLabel);
      innerLayoutPanel.add(failedLabel);

      if (_showFailed) {
        ReasonsTableModel reasonsTableModel = new ReasonsTableModel(reasons);
        JTable reasonsTable = new JTable(reasonsTableModel);
        JScrollPane reasonsScrollPane = new JScrollPane(reasonsTable);
        reasonsScrollPane.setAlignmentX(0.0f);
  
        innerLayoutPanel.add(reasonsScrollPane);
      }
    layoutPanel.add(innerLayoutPanel, BorderLayout.CENTER);

    myConfirmationPanel = new ConfirmationPanel(layoutPanel);
    myConfirmationPanel.setCancelVisible(false);
    myConfirmationPanel.addOkListener(new CloseDialogListener(this));
    myConfirmationPanel.addCancelListener(new CloseDialogListener(this));

		getContentPane().add(myConfirmationPanel);

		pack();
    setSize(420, 350);
//    splitPane.setDividerLocation(200);
	  DialogUtils.centerWindow(this);
  }
	
  public void addOkListener(ActionListener _listener) {
    myConfirmationPanel.addOkListener(_listener);
  }
  
  public void addCancelListener(ActionListener _listener) {
    myConfirmationPanel.addCancelListener(_listener);
  }
}
