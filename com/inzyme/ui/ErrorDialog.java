/* ErrorDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.text.ResourceBundleUtils;

public class ErrorDialog extends JPanel
{
    private JScrollPane myDetailsScrollPane;
    private JButton myDetailsButton;
    private boolean myDetailsVisible;
    
    protected class DetailsListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myDetailsVisible = !myDetailsVisible;
	    detailsVisiblityChanged(true);
	}
    }
    
    protected ErrorDialog(Throwable _exception) {
	this(ExceptionUtils.getParagraph(_exception),
	     ExceptionUtils.getChainedStackTrace(_exception));
    }
    
    protected ErrorDialog(String _paragraph, String _details) {
	BorderLayout layout = new BorderLayout();
	layout.setVgap(10);
	setLayout(layout);
	JTextArea paragraphTA = new JTextArea(_paragraph);
	paragraphTA.setEditable(false);
	paragraphTA.setLineWrap(true);
	paragraphTA.setWrapStyleWord(true);
	paragraphTA.setColumns(80);
	paragraphTA.setRows(3);
	paragraphTA.setOpaque(false);
	paragraphTA.setFont(new JLabel().getFont());
	add(paragraphTA, "North");
	myDetailsButton = new JButton();
	if (_details != null) {
	    JTextArea detailsTA = new JTextArea(_details);
	    detailsTA.setEditable(false);
	    detailsTA.setColumns(80);
	    detailsTA.setRows(10);
	    detailsTA.setMargin(new Insets(5, 5, 5, 5));
	    detailsTA.setFont(paragraphTA.getFont());
	    myDetailsScrollPane = new JScrollPane(detailsTA);
	    JPanel detailsPanel = new JPanel();
	    FlowLayout fl = new FlowLayout();
	    fl.setAlignment(2);
	    detailsPanel.setLayout(fl);
	    myDetailsButton.addActionListener(new DetailsListener());
	    detailsPanel.add(myDetailsButton);
	    add(detailsPanel, "South");
	}
	myDetailsVisible = false;
	detailsVisiblityChanged(false);
    }
    
    protected void detailsVisiblityChanged(boolean _pack) {
	if (myDetailsScrollPane != null) {
	    if (myDetailsVisible) {
		add(myDetailsScrollPane, "Center");
		myDetailsButton.setText(ResourceBundleUtils.getUIString
					("error.hideDetails.text"));
	    } else {
		remove(myDetailsScrollPane);
		myDetailsButton.setText(ResourceBundleUtils.getUIString
					("error.showDetails.text"));
	    }
	}
	if (_pack) {
	    Window topLevelAncestor = (Window) getTopLevelAncestor();
	    topLevelAncestor.pack();
	}
    }
    
    public static void showErrorDialog(Window _parentFrame, String _message,
				       boolean _modal) {
	ErrorDialog errorPanel = new ErrorDialog(_message, null);
	JOptionPane optionPane = new JOptionPane(errorPanel, 0);
	JDialog dialog
	    = optionPane.createDialog(_parentFrame,
				      ResourceBundleUtils
					  .getUIString("error.frameTitle"));
	dialog.setModal(_modal);
	dialog.pack();
	dialog.show();
    }
    
    public static void showErrorDialog(Window _parentFrame,
				       Throwable _throwable, boolean _modal) {
	ErrorDialog errorPanel = new ErrorDialog(_throwable);
	JOptionPane optionPane = new JOptionPane(errorPanel, 0);
	JDialog dialog
	    = optionPane.createDialog(_parentFrame,
				      ResourceBundleUtils
					  .getUIString("error.frameTitle"));
	dialog.setModal(_modal);
	dialog.pack();
	dialog.show();
    }
}
