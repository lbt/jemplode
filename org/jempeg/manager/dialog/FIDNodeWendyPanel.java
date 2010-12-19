/* FIDNodeWendyPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.WendyFlags;

public class FIDNodeWendyPanel extends JComponent
{
    private IFIDNode[] myNodes;
    private JList myWendyList = new JList();
    private Vector myChangedFlags;
    
    protected class WendyFlagActionListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    JCheckBox checkBox = (JCheckBox) _event.getSource();
	    if (!myChangedFlags.contains(checkBox))
		myChangedFlags.addElement(checkBox);
	}
    }
    
    public FIDNodeWendyPanel() {
	myWendyList.setCellRenderer(new ButtonListCellRenderer());
	myWendyList.addMouseListener(new ButtonListMouseListener(myWendyList));
	setLayout(new BorderLayout());
	add(new JScrollPane(myWendyList), "Center");
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
	WendyFlagActionListener wendyActionListener
	    = new WendyFlagActionListener();
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
		    boolean wendyFlagSet
			= (wendyFlags.isWendyFlagSet
			   (node.getTags().getIntValue("wendy", 0), i));
		    if (j == 0)
			multiWendyFlagSet = wendyFlagSet;
		    else if (wendyFlagSet != multiWendyFlagSet)
			mixedWendyFlagSet = true;
		}
		if (mixedWendyFlagSet) {
		    wendyCheckBox.setSelected(false);
		    wendyCheckBox
			.setForeground(AbstractFIDNodePanel.MIXED_COLOR);
		} else
		    wendyCheckBox.setSelected(multiWendyFlagSet);
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
	    int bitmask = 1 << i;
	    JCheckBox wendyCheckBox = (JCheckBox) listModel.getElementAt(i);
	    if (myChangedFlags.contains(wendyCheckBox)) {
		if (wendyCheckBox.isSelected()) {
		    setFlags |= bitmask;
		    unsetFlags |= bitmask;
		}
	    } else
		unsetFlags |= bitmask;
	}
	for (int i = 0; i < myNodes.length; i++) {
	    IFIDNode node = myNodes[i];
	    int wendy = node.getTags().getIntValue("wendy", 0);
	    wendy |= setFlags;
	    wendy &= unsetFlags;
	    node.getTags().setIntValue("wendy", wendy);
	}
    }
}
