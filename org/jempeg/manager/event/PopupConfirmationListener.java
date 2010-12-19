/* PopupConfirmationListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class PopupConfirmationListener extends AbstractConfirmationListener
{
    private JFrame myFrame;
    private JCheckBox myCheckbox;
    private String myTitle;
    
    public PopupConfirmationListener(JFrame _frame, String _title) {
	this(_frame, _title, 1);
    }
    
    public PopupConfirmationListener(JFrame _frame, String _title,
				     int _defaultValue) {
	super(_defaultValue);
	myFrame = _frame;
	myCheckbox = new JCheckBox();
	myTitle = _title;
    }
    
    protected boolean isCheckboxSelected() {
	return myCheckbox.isSelected();
    }
    
    protected int inputConfirmation(String _message, String _checkboxMessage) {
	JComponent messageComp;
	if (_message.indexOf('\n') == -1)
	    messageComp = new JLabel(_message);
	else {
	    JTextArea textArea = new JTextArea(_message);
	    textArea.setEditable(false);
	    textArea.setBackground(myFrame.getBackground());
	    textArea.setFont(myCheckbox.getFont());
	    messageComp = textArea;
	}
	messageComp.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
	JComponent comp;
	if (_checkboxMessage != null) {
	    myCheckbox.setText(_checkboxMessage);
	    JPanel confirmPanel = new JPanel();
	    confirmPanel.setLayout(new BorderLayout());
	    confirmPanel.add(messageComp, "Center");
	    confirmPanel.add(myCheckbox, "South");
	    comp = confirmPanel;
	} else
	    comp = messageComp;
	int selectedValue
	    = JOptionPane.showOptionDialog(myFrame, comp, myTitle, -1, 2, null,
					   CONFIRM_VALUES,
					   CONFIRM_VALUES[getDefaultValue()]);
	return selectedValue;
    }
}
