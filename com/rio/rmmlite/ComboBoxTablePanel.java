/* ComboBoxTablePanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.tree.TreeNode;

import com.inzyme.tree.TreeComboBoxModel;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.PlaylistComboBoxSelectionListener;
import org.jempeg.manager.ui.ComponentConfiguration;
import org.jempeg.manager.ui.PlaylistTreeCellRenderer;
import org.jempeg.nodestore.model.FIDPlaylistTreeComboBoxModel;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;

public class ComboBoxTablePanel extends JComponent
{
    private JComboBox myComboBox;
    private JTable myTable;
    
    public ComboBoxTablePanel(ApplicationContext _context) {
	setLayout(new BorderLayout());
	myComboBox = new JComboBox();
	myComboBox.setRenderer(new DefaultListCellRenderer() {
	    public Component getListCellRendererComponent
		(JList list, Object value, int index, boolean isSelected,
		 boolean cellHasFocus) {
		JLabel label
		    = ((JLabel)
		       super.getListCellRendererComponent(list, value, index,
							  isSelected,
							  cellHasFocus));
		if (value instanceof FIDPlaylistTreeNode) {
		    FIDPlaylistTreeNode treeNode = (FIDPlaylistTreeNode) value;
		    if (treeNode != null) {
			label.setIcon(PlaylistTreeCellRenderer.ICONS
				      [treeNode.getPlaylist().getType()]);
			int level = 0;
			for (TreeNode node = treeNode.getParent();
			     node != null; node = node.getParent())
			    level++;
			label.setBorder(BorderFactory.createEmptyBorder(0,
									(level
									 * 10),
									0, 0));
		    }
		}
		return label;
	    }
	});
	try {
	    myComboBox.setPrototypeDisplayValue(new Object());
	} catch (NoSuchMethodError e) {
	    Debug.println
		(8,
		 "Not running w/ JDK 1.4.  Unable to set a prototype display value.");
	}
	myComboBox.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent _event) {
		if (_event.getStateChange() == 1) {
		    TreeNode selectedNode = (TreeNode) _event.getItem();
		    TreeComboBoxModel comboBoxModel
			= (TreeComboBoxModel) myComboBox.getModel();
		    comboBoxModel.openNode(selectedNode, true);
		}
	    }
	});
	JPanel spacingPanel = new JPanel();
	spacingPanel.setLayout(new BorderLayout());
	spacingPanel.add(myComboBox, "North");
	spacingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
	myTable = new JTable();
	JScrollPane tableScrollPane = new JScrollPane(myTable);
	ComponentConfiguration.configureTable(_context, myTable);
	tableScrollPane.setVerticalScrollBarPolicy(22);
	add(spacingPanel, "North");
	add(tableScrollPane, "Center");
	myComboBox.addItemListener
	    (new PlaylistComboBoxSelectionListener(_context, myTable));
    }
    
    public JTable getTable() {
	return myTable;
    }
    
    public JComboBox getComboBox() {
	return myComboBox;
    }
    
    public void setComboBoxModel(FIDPlaylistTreeComboBoxModel _comboBoxModel) {
	javax.swing.ComboBoxModel oldModel = myComboBox.getModel();
	if (oldModel instanceof FIDPlaylistTreeComboBoxModel) {
	    FIDPlaylistTreeComboBoxModel oldPlaylistModel
		= (FIDPlaylistTreeComboBoxModel) oldModel;
	    oldPlaylistModel.removeAllListeners();
	}
	myComboBox.setModel(_comboBoxModel);
	myComboBox.setSelectedIndex(-1);
	if (_comboBoxModel != null && _comboBoxModel.getSize() > 0
	    && _comboBoxModel.getSelectedItem() == null)
	    myComboBox.setSelectedIndex(0);
    }
    
    public FIDPlaylistTreeComboBoxModel getComboBoxModel() {
	javax.swing.ComboBoxModel model = myComboBox.getModel();
	FIDPlaylistTreeComboBoxModel playlistModel;
	if (model instanceof FIDPlaylistTreeComboBoxModel)
	    playlistModel = (FIDPlaylistTreeComboBoxModel) model;
	else
	    playlistModel = null;
	return playlistModel;
    }
}
