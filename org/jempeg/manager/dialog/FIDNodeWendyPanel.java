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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.jempeg.manager.ui.ButtonListCellRenderer;
import org.jempeg.manager.ui.ButtonListMouseListener;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.WendyFlags;

/**
* A panel that provides an editor for Wendy
* flags that are set on a node.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class FIDNodeWendyPanel extends JComponent {
	private IFIDNode[] myNodes;
	private JList myWendyList;
	private Vector myChangedFlags;

	public FIDNodeWendyPanel() {
		myWendyList = new JList();
		myWendyList.setCellRenderer(new ButtonListCellRenderer());
		myWendyList.addMouseListener(new ButtonListMouseListener(myWendyList));

		setLayout(new BorderLayout());
		add(new JScrollPane(myWendyList), BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	public FIDNodeWendyPanel(IFIDNode[] _nodes) {
		this();
		setNodes(_nodes);
	}

	public void setNodes(IFIDNode[] _nodes) {
		myNodes = _nodes;
		myChangedFlags = new Vector();
		DefaultListModel wendyFlagListModel = new DefaultListModel();
		WendyFlagActionListener wendyActionListener = new WendyFlagActionListener();

		if (myNodes.length > 0) {
			PlayerDatabase playerDatabase = myNodes[0].getDB();
			WendyFlags wendyFlags = new WendyFlags();
			wendyFlags.readFlags(playerDatabase.getDeviceSettings());

			String[] flags = wendyFlags.getFlags();
			for (int i = 0; i < flags.length; i++) {
				JCheckBox wendyCheckBox = new JCheckBox(flags[i]);
				wendyFlagListModel.addElement(wendyCheckBox);

				boolean multiWendyFlagSet = false;
				boolean mixedWendyFlagSet = false;

				for (int j = 0; !mixedWendyFlagSet && j < _nodes.length; j++) {
					IFIDNode node = _nodes[j];
					boolean wendyFlagSet = wendyFlags.isWendyFlagSet(node.getTags().getIntValue(DatabaseTags.WENDY_TAG, 0), i);
					if (j == 0) {
						multiWendyFlagSet = wendyFlagSet;
					}
					else if (wendyFlagSet != multiWendyFlagSet) {
						mixedWendyFlagSet = true;
					}
				}

				if (mixedWendyFlagSet) {
					wendyCheckBox.setSelected(false);
					wendyCheckBox.setForeground(AbstractFIDNodePanel.MIXED_COLOR);
				}
				else {
					wendyCheckBox.setSelected(multiWendyFlagSet);
				}

				wendyCheckBox.addActionListener(wendyActionListener);
			}
		}
		myWendyList.setModel(wendyFlagListModel);
	}

	public IFIDNode[] getNodes() {
		return myNodes;
	}

	public void ok() {
		int setFlags = 0;
		int unsetFlags = 0;

		ListModel listModel = myWendyList.getModel();
		int size = listModel.getSize();
		for (int i = 0; i < size; i++) {
			int bitmask = (1 << i);

			JCheckBox wendyCheckBox = (JCheckBox) listModel.getElementAt(i);
			if (myChangedFlags.contains(wendyCheckBox)) {
				if (wendyCheckBox.isSelected()) {
					setFlags |= bitmask;
					unsetFlags |= bitmask;
				}
			}
			else {
				unsetFlags |= bitmask;
			}
		}

		for (int i = 0; i < myNodes.length; i++) {
			IFIDNode node = myNodes[i];
			int wendy = node.getTags().getIntValue(DatabaseTags.WENDY_TAG, 0);
			wendy |= setFlags;
			wendy &= unsetFlags;
			node.getTags().setIntValue(DatabaseTags.WENDY_TAG, wendy);
		}
	}

	protected class WendyFlagActionListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			JCheckBox checkBox = (JCheckBox) _event.getSource();
			if (!myChangedFlags.contains(checkBox)) {
				myChangedFlags.addElement(checkBox);
			}
		}
	}
}
