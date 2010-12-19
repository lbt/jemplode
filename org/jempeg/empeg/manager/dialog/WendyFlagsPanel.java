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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jempeg.nodestore.WendyFilters;

/**
* WendyFlagsPanel is the GUI component that provides
* an interface for managing Wendy flags.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class WendyFlagsPanel extends JPanel {
  private JList myWendyList;
  private WendyFilters myWendyFilters;

  public WendyFlagsPanel(WendyFilters _filters) {
  	myWendyFilters = _filters;
  	
  	DefaultListModel wendyListModel = new DefaultListModel();
    myWendyList = new JList(wendyListModel);
    myWendyList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setLayout(new BorderLayout());
    myWendyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane wendyScrollPane = new JScrollPane(myWendyList);

    JPanel buttonLayoutPanel = new JPanel();
      buttonLayoutPanel.setLayout(new BorderLayout());
      JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1));
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new AddListener());
        buttonPanel.add(addButton);
  
        JButton renameButton = new JButton("Rename");
        renameButton .addActionListener(new RenameListener());
        buttonPanel.add(renameButton);
  
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteListener());
        buttonPanel.add(deleteButton);
      buttonLayoutPanel.add(buttonPanel, BorderLayout.NORTH);

    add(wendyScrollPane, BorderLayout.CENTER);
    add(buttonLayoutPanel, BorderLayout.EAST);
    
    setWendyFilters(_filters);
  }
  
  public void setWendyFilters(WendyFilters _wendyFilters) {
  	DefaultListModel wendyListModel = new DefaultListModel();
  	wendyListModel.removeAllElements();
  	String[] wendyFlags = _wendyFilters.getWendyFlags().getFlags();
  	for (int i = 0; i < wendyFlags.length; i ++) {
  		wendyListModel.addElement(wendyFlags[i]);
  	}
  	myWendyList.setModel(wendyListModel);
  	myWendyFilters = _wendyFilters;
  }
  
  public WendyFilters getWendyFilters() {
  	return myWendyFilters;
  }

	protected String getSelectedWendyFlag() {
		String wendyFlag = (String)myWendyList.getSelectedValue();
  	return wendyFlag;
	}
		
  public void addFlag() {
  	String wendyFlag = JOptionPane.showInputDialog("Wendy Flag Name: ");
  	if (wendyFlag != null) {
	  	try {
		  	myWendyFilters.getWendyFlags().addFlag(wendyFlag);
	  		setWendyFilters(myWendyFilters);
	  	}
	  	catch (IllegalArgumentException e) {
	  	}
	  	int index = myWendyFilters.getWendyFlags().getIndexOf(wendyFlag);
	  	myWendyList.setSelectedIndex(index);
  	}
  }
  
  public void renameFlag() {
  	int index = myWendyList.getSelectedIndex();
  	if (index != -1) {
	  	String origWendyFlag = getSelectedWendyFlag();
	  	String newWendyFlag = (String)JOptionPane.showInputDialog(this, "Wendy Flag Name: ", "Wendy Flags", JOptionPane.QUESTION_MESSAGE, null, null, origWendyFlag);
	  	if (newWendyFlag != null) {
		  	try {
			  	myWendyFilters.renameFlag(origWendyFlag, newWendyFlag);
			  	setWendyFilters(myWendyFilters);
		  	}
		  	catch (IllegalArgumentException e) {
		  	}
	  		index = myWendyFilters.getWendyFlags().getIndexOf(newWendyFlag);
		  	myWendyList.setSelectedIndex(index);
	  	}
  	}
  }

  public void deleteFlag() {
  	int index = myWendyList.getSelectedIndex();
  	if (index != -1) {
	  	String wendyFlag = getSelectedWendyFlag();
	  	myWendyFilters.removeFlag(wendyFlag);
	  	setWendyFilters(myWendyFilters);
  	}
  }

  protected class AddListener implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      addFlag();
    }
  }

  protected class DeleteListener implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      deleteFlag();
    }
  }

  protected class RenameListener implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      renameFlag();
    }
  }
}
