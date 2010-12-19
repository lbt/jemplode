/* ColumnEditorDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.manager.ui.ButtonListCellRenderer;
import org.jempeg.manager.ui.ButtonListMouseListener;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.PlaylistTableUtils;

public class ColumnEditorDialog extends JDialog
{
    private JList myColumnList;
    private String[] myDefaultColumns;
    private JTextField myWidthTextField;
    private Hashtable myTagToWidth = new Hashtable();
    
    protected class MoveUpListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    moveUpColumn();
	}
    }
    
    protected class MoveDownListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    moveDownColumn();
	}
    }
    
    protected class ShowListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    showColumn();
	}
    }
    
    protected class HideListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    hideColumn();
	}
    }
    
    protected class ResetListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    resetColumns();
	}
    }
    
    protected class WidthListener
	implements ListSelectionListener, DocumentListener
    {
	public void valueChanged(ListSelectionEvent _event) {
	    if (!_event.getValueIsAdjusting()) {
		ColumnEntry entry
		    = (ColumnEntry) myColumnList.getSelectedValue();
		if (entry != null) {
		    NodeTag nodeTag = entry.getNodeTag();
		    String tagName = nodeTag.getName();
		    int width = getColumnWidth(tagName);
		    String widthStr;
		    if (width == -1)
			widthStr = "";
		    else
			widthStr = String.valueOf(width);
		    myWidthTextField.getDocument()
			.removeDocumentListener(this);
		    myWidthTextField.setText(widthStr);
		    myWidthTextField.getDocument().addDocumentListener(this);
		}
	    }
	}
	
	public void changedUpdate(DocumentEvent _event) {
	    ColumnEntry entry = (ColumnEntry) myColumnList.getSelectedValue();
	    if (entry != null) {
		NodeTag nodeTag = entry.getNodeTag();
		String tagName = nodeTag.getName();
		String widthStr = myWidthTextField.getText();
		int width;
		try {
		    width = Integer.parseInt(widthStr);
		} catch (NumberFormatException e) {
		    width = -1;
		}
		myTagToWidth.put(tagName, new Integer(width));
	    }
	}
	
	public void insertUpdate(DocumentEvent _event) {
	    changedUpdate(_event);
	}
	
	public void removeUpdate(DocumentEvent _event) {
	    changedUpdate(_event);
	}
    }
    
    protected class ColumnEntry extends JCheckBox
    {
	private NodeTag myNodeTag;
	
	public ColumnEntry(NodeTag _nodeTag) {
	    super(_nodeTag.getDescription());
	    myNodeTag = _nodeTag;
	}
	
	public NodeTag getNodeTag() {
	    return myNodeTag;
	}
    }
    
    public ColumnEditorDialog(JFrame _frame) {
	this(_frame, PlaylistTableUtils.DEFAULT_COLUMNS);
    }
    
    public ColumnEditorDialog(JFrame _frame, String[] _columns) {
	super(_frame,
	      ResourceBundleUtils.getUIString("columnEditor.frameTitle"),
	      true);
	myColumnList = new JList();
	myColumnList.addListSelectionListener(new WidthListener());
	myDefaultColumns = _columns;
	setColumns(_columns);
	JPanel columnEditorPanel = new JPanel();
	columnEditorPanel.setLayout(new BorderLayout());
	ButtonListMouseListener columnListMouseListener
	    = new ButtonListMouseListener(myColumnList);
	myColumnList.setSelectionMode(0);
	myColumnList.setCellRenderer(new ButtonListCellRenderer());
	myColumnList.addListSelectionListener(columnListMouseListener);
	myColumnList.addKeyListener(columnListMouseListener);
	myColumnList.addMouseListener(columnListMouseListener);
	JScrollPane columnScrollPane = new JScrollPane(myColumnList);
	JPanel widthPanel = new JPanel();
	widthPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
	widthPanel.add(new JLabel(ResourceBundleUtils.getUIString
				  ("columnEditor.width.start")));
	myWidthTextField = new JTextField();
	myWidthTextField.setColumns(4);
	widthPanel.add(myWidthTextField);
	widthPanel.add(new JLabel(ResourceBundleUtils
				      .getUIString("columnEditor.width.end")));
	JPanel buttonLayoutPanel = new JPanel();
	buttonLayoutPanel.setLayout(new BorderLayout());
	JPanel buttonPanel = new JPanel();
	GridLayout gridLayout = new GridLayout(0, 1);
	gridLayout.setVgap(5);
	buttonPanel.setLayout(gridLayout);
	JButton moveUpButton
	    = new JButton(ResourceBundleUtils
			      .getUIString("columnEditor.moveUp.text"));
	moveUpButton.addActionListener(new MoveUpListener());
	buttonPanel.add(moveUpButton);
	JButton moveDownButton
	    = new JButton(ResourceBundleUtils
			      .getUIString("columnEditor.moveDown.text"));
	moveDownButton.addActionListener(new MoveDownListener());
	buttonPanel.add(moveDownButton);
	JButton showButton
	    = new JButton(ResourceBundleUtils
			      .getUIString("columnEditor.show.text"));
	showButton.addActionListener(new ShowListener());
	buttonPanel.add(showButton);
	JButton hideButton
	    = new JButton(ResourceBundleUtils
			      .getUIString("columnEditor.hide.text"));
	hideButton.addActionListener(new HideListener());
	buttonPanel.add(hideButton);
	JButton resetButton
	    = new JButton(ResourceBundleUtils
			      .getUIString("columnEditor.reset.text"));
	resetButton.addActionListener(new ResetListener());
	buttonPanel.add(resetButton);
	buttonLayoutPanel.add(buttonPanel, "North");
	buttonLayoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,
								    5));
	columnEditorPanel.add(columnScrollPane, "Center");
	columnEditorPanel.add(buttonLayoutPanel, "East");
	columnEditorPanel.add(widthPanel, "South");
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(columnEditorPanel);
	confirmationPanel.addOkListener(new CloseDialogListener(this) {
	    public void actionPerformed(ActionEvent _event) {
		myColumnList.clearSelection();
		super.actionPerformed(_event);
	    }
	});
	confirmationPanel.addCancelListener(new CloseDialogListener(this) {
	    public void actionPerformed(ActionEvent _event) {
		setColumns(myDefaultColumns);
		super.actionPerformed(_event);
	    }
	});
	getContentPane().add(confirmationPanel);
	pack();
	DialogUtils.centerWindow(this);
    }
    
    public void setColumns(String[] _columns) {
	DefaultListModel listModel = new DefaultListModel();
	NodeTag[] nodeTags = NodeTag.getNodeTags();
	for (int i = 0; i < nodeTags.length; i++) {
	    String name = nodeTags[i].getName();
	    ColumnEntry columnEntry = new ColumnEntry(nodeTags[i]);
	    if (name.equals("title")) {
		columnEntry.setSelected(true);
		columnEntry.setEnabled(false);
	    } else {
		for (int j = 0;
		     !columnEntry.isSelected() && j < _columns.length; j++) {
		    if (_columns[j].equals(name))
			columnEntry.setSelected(true);
		}
	    }
	    listModel.addElement(columnEntry);
	}
	myColumnList.setModel(listModel);
	myColumnList.repaint();
    }
    
    public String[] getColumns() {
	Vector columnsVec = new Vector();
	ListModel listModel = myColumnList.getModel();
	int size = listModel.getSize();
	for (int i = 0; i < size; i++) {
	    ColumnEntry columnEntry = (ColumnEntry) listModel.getElementAt(i);
	    if (columnEntry.isSelected()) {
		String tagName = columnEntry.getNodeTag().getName();
		columnsVec.addElement(tagName);
	    }
	}
	String[] columnNames = new String[columnsVec.size()];
	columnsVec.copyInto(columnNames);
	return columnNames;
    }
    
    public void showColumn() {
	int selectedIndex = myColumnList.getSelectedIndex();
	if (selectedIndex > 0) {
	    ColumnEntry columnEntry
		= (ColumnEntry) myColumnList.getSelectedValue();
	    columnEntry.setSelected(true);
	    myColumnList.repaint();
	}
    }
    
    public void hideColumn() {
	int selectedIndex = myColumnList.getSelectedIndex();
	if (selectedIndex > 0) {
	    ColumnEntry columnEntry
		= (ColumnEntry) myColumnList.getSelectedValue();
	    columnEntry.setSelected(false);
	    myColumnList.repaint();
	}
    }
    
    public void moveUpColumn() {
	int selectedIndex = myColumnList.getSelectedIndex();
	if (selectedIndex > 1) {
	    DefaultListModel listModel
		= (DefaultListModel) myColumnList.getModel();
	    ColumnEntry columnEntry
		= (ColumnEntry) myColumnList.getSelectedValue();
	    listModel.removeElementAt(selectedIndex);
	    listModel.insertElementAt(columnEntry, selectedIndex - 1);
	    myColumnList.setSelectedIndex(selectedIndex - 1);
	    myColumnList.ensureIndexIsVisible(selectedIndex - 1);
	    myColumnList.repaint();
	}
    }
    
    public void moveDownColumn() {
	int selectedIndex = myColumnList.getSelectedIndex();
	int size = myColumnList.getModel().getSize();
	if (selectedIndex > 0 && selectedIndex < size - 1) {
	    DefaultListModel listModel
		= (DefaultListModel) myColumnList.getModel();
	    ColumnEntry columnEntry
		= (ColumnEntry) myColumnList.getSelectedValue();
	    listModel.removeElementAt(selectedIndex);
	    listModel.insertElementAt(columnEntry, selectedIndex + 1);
	    myColumnList.setSelectedIndex(selectedIndex + 1);
	    myColumnList.ensureIndexIsVisible(selectedIndex + 1);
	    myColumnList.repaint();
	}
    }
    
    public void resetColumns() {
	setColumns(myDefaultColumns);
	myTagToWidth.clear();
	myColumnList.ensureIndexIsVisible(0);
    }
    
    public int getColumnWidth(String _tagName) {
	Integer widthInteger = (Integer) myTagToWidth.get(_tagName);
	int width;
	if (widthInteger == null) {
	    width = PlaylistTableUtils.getColumnWidth(_tagName);
	    myTagToWidth.put(_tagName, new Integer(width));
	} else
	    width = widthInteger.intValue();
	return width;
    }
}
