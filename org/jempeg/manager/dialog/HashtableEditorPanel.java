package org.jempeg.manager.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jempeg.nodestore.model.HashtableTableModel;

import com.inzyme.ui.DialogUtils;

public class HashtableEditorPanel extends JPanel {
	private JTable myTable;
	private HashtableTableModel myTableModel;
	private JButton myAddButton;
	private JButton myDeleteButton;
	private JTextField myNameTextField;
	private JTextField myValueTextField;
		
	public HashtableEditorPanel() {
		this(new Hashtable());
	}
	
	public HashtableEditorPanel(Hashtable _hashtable) {
		setLayout(new BorderLayout());
		
		myTableModel = new HashtableTableModel(_hashtable);
		myTable = new JTable(myTableModel);
		myTable.getSelectionModel().addListSelectionListener(new SelectionListener());

		TextFieldListener textFieldListener = new TextFieldListener();
		myNameTextField = new JTextField();
		myNameTextField.getDocument().addDocumentListener(textFieldListener);
		myValueTextField = new JTextField();
		myValueTextField.getDocument().addDocumentListener(textFieldListener);

		JPanel editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		JPanel textFieldPanel = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    textFieldPanel.setLayout(gridBagLayout);
    DialogUtils.addRow(new JLabel("New Name"), myNameTextField, gridBagLayout, textFieldPanel);
    DialogUtils.addRow(new JLabel("New Value"), myValueTextField, gridBagLayout, textFieldPanel);
    
    JPanel buttonPanel = new JPanel();
    AddListener addListener = new AddListener();
    myAddButton = new JButton("Add");
    myAddButton.addActionListener(addListener);
    myDeleteButton = new JButton("Delete");
    myDeleteButton.addActionListener(new DeleteListener());
    buttonPanel.add(myAddButton);
    buttonPanel.add(myDeleteButton);
    
		myNameTextField.addActionListener(addListener);
		myValueTextField.addActionListener(addListener);
    
    editorPanel.add(textFieldPanel, BorderLayout.CENTER);
    editorPanel.add(buttonPanel, BorderLayout.SOUTH);

		JScrollPane tableScrollPane = new JScrollPane(myTable);
		add(tableScrollPane, BorderLayout.CENTER);
		add(editorPanel, BorderLayout.SOUTH);
		
		validateAdd();
		validateDelete();
	}

	public void setHashtable(Hashtable _hashtable) {
		myTableModel.setHashtable(_hashtable);
	}
	
	public Hashtable getHashtable() {
		return myTableModel.getHashtable();
	}
	
	public Enumeration getModifiedKeys() {
		return myTableModel.getModifiedKeys();
	}
	
	protected boolean isAllowedToAdd() {
		String name = myNameTextField.getText();
		String value = myValueTextField.getText();
		
		boolean valid = (name != null && value != null && name.length() > 0 && value.length() > 0);
		return valid;
	}

	protected boolean isAllowedToDelete() {
		int[] rows = myTable.getSelectedRows();
		boolean valid = (rows != null && rows.length > 0);
		return valid;
	}

	protected void validateAdd() {
		myAddButton.setEnabled(isAllowedToAdd());
	}
	
	protected void validateDelete() {	
		myDeleteButton.setEnabled(isAllowedToDelete());
	}
	
	protected void add() {
		if (isAllowedToAdd()) {
			String name = myNameTextField.getText();
			String value = myValueTextField.getText();
			
			myTableModel.put(name, value);
		}
	}

	protected void delete() {
		if (isAllowedToDelete()) {
			int[] rows = myTable.getSelectedRows();
			Object[] keys = new Object[rows.length];
			for (int i = 0; i < rows.length; i ++) {
				int row = rows[i];
				keys[i] = myTableModel.getValueAt(row, 0);
			}
			for (int i = 0; i < keys.length; i ++) {
				myTableModel.remove(keys[i]);
			}
		}
	}

	protected class TextFieldListener implements DocumentListener {
		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent _event) {
			validateAdd();
		}
		
		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent _event) {
			validateAdd();
		}
		
		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent _event) {
			validateAdd();
		}
	}

	protected class SelectionListener implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent _event) {
			validateDelete();
		}
	}
		
	protected class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			add();
		}
	}
	
	protected class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			int option = JOptionPane.showConfirmDialog(HashtableEditorPanel.this, "Are you sure you want to delete the selected rows?");
			if (option == JOptionPane.YES_OPTION) {
				delete();
			}
		}
	}

	/*
	public static void main(String[] _args) {
		Properties p = new Properties();
		p.put("test1", "value1");
		p.put("test2", "value2");
		p.put("test3", "value3");
		p.put("test4", "value4");
		p.put("test5", "value5");
		p.put("test6", "value6");
		p.put("test7", "value7");
		p.put("test8", "value8");
		p.put("test9", "value9");
		p.put("test10", "value10");
		p.put("test11", "value11");
		p.put("test12", "value12");
		
		HashtableEditorPanel pep = new HashtableEditorPanel(p);
		JFrame jf = new JFrame("Test");
		jf.getContentPane().add(pep);
		jf.pack();
		jf.show();
		
	}		
	/**/
}
