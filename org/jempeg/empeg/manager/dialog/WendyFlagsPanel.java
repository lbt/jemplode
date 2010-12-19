/* WendyFlagsPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

import org.jempeg.nodestore.WendyFilters;

public class WendyFlagsPanel extends JPanel
{
    private JList myWendyList;
    private WendyFilters myWendyFilters;
    
    protected class AddListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    addFlag();
	}
    }
    
    protected class DeleteListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    deleteFlag();
	}
    }
    
    protected class RenameListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    renameFlag();
	}
    }
    
    public WendyFlagsPanel(WendyFilters _filters) {
	myWendyFilters = _filters;
	DefaultListModel wendyListModel = new DefaultListModel();
	myWendyList = new JList(wendyListModel);
	myWendyList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	setLayout(new BorderLayout());
	myWendyList.setSelectionMode(0);
	JScrollPane wendyScrollPane = new JScrollPane(myWendyList);
	JPanel buttonLayoutPanel = new JPanel();
	buttonLayoutPanel.setLayout(new BorderLayout());
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridLayout(0, 1));
	JButton addButton = new JButton("Add");
	addButton.addActionListener(new AddListener());
	buttonPanel.add(addButton);
	JButton renameButton = new JButton("Rename");
	renameButton.addActionListener(new RenameListener());
	buttonPanel.add(renameButton);
	JButton deleteButton = new JButton("Delete");
	deleteButton.addActionListener(new DeleteListener());
	buttonPanel.add(deleteButton);
	buttonLayoutPanel.add(buttonPanel, "North");
	add(wendyScrollPane, "Center");
	add(buttonLayoutPanel, "East");
	setWendyFilters(_filters);
    }
    
    public void setWendyFilters(WendyFilters _wendyFilters) {
	DefaultListModel wendyListModel = new DefaultListModel();
	wendyListModel.removeAllElements();
	String[] wendyFlags = _wendyFilters.getWendyFlags().getFlags();
	for (int i = 0; i < wendyFlags.length; i++)
	    wendyListModel.addElement(wendyFlags[i]);
	myWendyList.setModel(wendyListModel);
	myWendyFilters = _wendyFilters;
    }
    
    public WendyFilters getWendyFilters() {
	return myWendyFilters;
    }
    
    protected String getSelectedWendyFlag() {
	String wendyFlag = (String) myWendyList.getSelectedValue();
	return wendyFlag;
    }
    
    public void addFlag() {
	String wendyFlag = JOptionPane.showInputDialog("Wendy Flag Name: ");
	if (wendyFlag != null) {
	    try {
		myWendyFilters.getWendyFlags().addFlag(wendyFlag);
		setWendyFilters(myWendyFilters);
	    } catch (IllegalArgumentException illegalargumentexception) {
		/* empty */
	    }
	    int index = myWendyFilters.getWendyFlags().getIndexOf(wendyFlag);
	    myWendyList.setSelectedIndex(index);
	}
    }
    
    public void renameFlag() {
	int index = myWendyList.getSelectedIndex();
	if (index != -1) {
	    String origWendyFlag = getSelectedWendyFlag();
	    String newWendyFlag
		= ((String)
		   JOptionPane.showInputDialog(this, "Wendy Flag Name: ",
					       "Wendy Flags", 3, null, null,
					       origWendyFlag));
	    if (newWendyFlag != null) {
		try {
		    myWendyFilters.renameFlag(origWendyFlag, newWendyFlag);
		    setWendyFilters(myWendyFilters);
		} catch (IllegalArgumentException illegalargumentexception) {
		    /* empty */
		}
		index
		    = myWendyFilters.getWendyFlags().getIndexOf(newWendyFlag);
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
}
