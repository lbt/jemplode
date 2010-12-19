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
package org.jempeg.empeg.manager.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.jempeg.empeg.versiontracker.IVersionTracker;
import org.jempeg.empeg.versiontracker.VersionChange;
import org.jempeg.empeg.versiontracker.VersionChangeTableModel;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

/**
* VersionChangeDialog provides a GUI for presenting
* a set of upgrades to a 3rd-party product.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class VersionChangeDialog extends JDialog {
  private boolean myShouldUpgrade;

  public VersionChangeDialog(JFrame _frame, IVersionTracker _versionTracker) throws IOException {
    super(_frame, _versionTracker.getProductName() + " Has Changed", true);
    String productName = _versionTracker.getProductName();
    VersionChange[] changes = _versionTracker.getChanges();

    JPanel layoutPanel = new JPanel();
    layoutPanel.setLayout(new BorderLayout());

    JPanel innerLayoutPanel = new JPanel();
    innerLayoutPanel.setLayout(new BoxLayout(innerLayoutPanel, BoxLayout.Y_AXIS));

      JLabel productLabel = new JLabel(productName + " v" + _versionTracker.getLatestVersion());
      productLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
      productLabel.setAlignmentX(0.0f);
      innerLayoutPanel.add(productLabel);

      innerLayoutPanel.add(Box.createVerticalStrut(20));

      JLabel releaseNotesLabel = new JLabel("Release Notes");
      releaseNotesLabel.setAlignmentX(0.0f);
      innerLayoutPanel.add(releaseNotesLabel);

      JTextArea releaseNotesTextArea = new JTextArea(_versionTracker.getReleaseNotes());
      releaseNotesTextArea.setEditable(false);
      JScrollPane releaseNotesScrollPane = new JScrollPane(releaseNotesTextArea);
      releaseNotesScrollPane.setAlignmentX(0.0f);
      innerLayoutPanel.add(releaseNotesScrollPane);

      innerLayoutPanel.add(Box.createVerticalStrut(20));

      JLabel changesLabel = new JLabel("Change Log");
      changesLabel.setAlignmentX(0.0f);
      innerLayoutPanel.add(changesLabel);

      VersionChangeTableModel versionChangeTableModel = new VersionChangeTableModel(changes);
      JTable versionChangeTable = new JTable(versionChangeTableModel);
      JScrollPane versionChangeScrollPane = new JScrollPane(versionChangeTable);
      versionChangeScrollPane.setAlignmentX(0.0f);
  
      innerLayoutPanel.add(versionChangeScrollPane);
    layoutPanel.add(innerLayoutPanel, BorderLayout.CENTER);

    ConfirmationPanel confirmationPanel = new ConfirmationPanel(layoutPanel);
    confirmationPanel.setOkText("Upgrade Now");
    confirmationPanel.setCancelText("Upgrade Later");
    confirmationPanel.addOkListener(new OkListener());
    confirmationPanel.addCancelListener(new CancelListener());

		getContentPane().add(confirmationPanel);

		pack();
    setSize(Math.max(420, getSize().width), 350);
	  DialogUtils.centerWindow(this);
  }
	
  public boolean shouldUpgrade() {
    return myShouldUpgrade;
  }

	protected class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
      myShouldUpgrade = true;
			setVisible(false);
		}
	}
	
	protected class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
      myShouldUpgrade = false;
			setVisible(false);
		}
	}
}
