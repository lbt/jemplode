/* ConfirmationPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.ui;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.inzyme.event.ActionSource;
import com.inzyme.text.ResourceBundleUtils;

public class ConfirmationPanel extends JPanel
{
    public static final int OK_OPTION = 1;
    public static final int CANCEL_OPTION = 0;
    private ActionSource myOkActionSource = new ActionSource(this);
    private ActionSource myCancelActionSource = new ActionSource(this);
    private int mySelectedOption = -1;
    private JButton myOkButton;
    private JButton myCancelButton;
    
    protected class OkListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    ok();
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    cancel();
	}
    }
    
    public ConfirmationPanel(JComponent _contentPane) {
	this(_contentPane, true);
    }
    
    public ConfirmationPanel(JComponent _contentPane,
			     boolean _showCancelButton) {
	this(_contentPane, true, _showCancelButton);
    }
    
    public ConfirmationPanel(JComponent _contentPane, boolean _showOkButton,
			     boolean _showCancelButton) {
	setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	setLayout(new BoxLayout(this, 1));
	Box buttonBox = Box.createHorizontalBox();
	JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
	fillInButtonPanel(buttonPanel, _showOkButton, _showCancelButton);
	buttonBox.add(Box.createHorizontalGlue());
	buttonBox.add(buttonPanel);
	add(_contentPane);
	add(Box.createVerticalStrut(5));
	add(new JSeparator());
	add(Box.createVerticalStrut(10));
	add(buttonBox);
	registerKeyboardAction(new CancelListener(),
			       KeyStroke.getKeyStroke(27, 0), 2);
    }
    
    public void fillInButtonPanel(JPanel _buttonPanel, boolean _showOkButton,
				  boolean _showCancelButton) {
	myOkButton = new JButton(ResourceBundleUtils.getUIString("ok"));
	if (_showOkButton) {
	    myOkButton.addActionListener(new OkListener());
	    _buttonPanel.add(myOkButton);
	}
	myCancelButton
	    = new JButton(ResourceBundleUtils.getUIString("cancel"));
	if (_showCancelButton) {
	    myCancelButton.addActionListener(new CancelListener());
	    _buttonPanel.add(myCancelButton);
	}
    }
    
    public void setEnterIsOK() {
	registerKeyboardAction(new OkListener(), KeyStroke.getKeyStroke(10, 0),
			       2);
    }
    
    public void setCancelVisible(boolean _visible) {
	myCancelButton.setVisible(_visible);
    }
    
    public void setOkVisible(boolean _visible) {
	myOkButton.setVisible(_visible);
    }
    
    public int getSelectedOption() {
	return mySelectedOption;
    }
    
    public void setOkText(String _okText) {
	myOkButton.setText(_okText);
    }
    
    public void setCancelText(String _cancelText) {
	myCancelButton.setText(_cancelText);
    }
    
    public void ok() {
	mySelectedOption = 1;
	myOkActionSource.fireActionPerformed();
    }
    
    public void cancel() {
	mySelectedOption = 0;
	myCancelActionSource.fireActionPerformed();
    }
    
    public void addOkListener(ActionListener _listener) {
	myOkActionSource.addActionListener(_listener);
    }
    
    public void addCancelListener(ActionListener _listener) {
	myCancelActionSource.addActionListener(_listener);
    }
}
