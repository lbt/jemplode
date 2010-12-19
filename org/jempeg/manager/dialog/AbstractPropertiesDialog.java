/* AbstractPropertiesDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IFIDNode;

public abstract class AbstractPropertiesDialog extends JDialog
{
    private IFIDNode[] myNodes;
    
    protected class OkListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    ok(false);
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	}
    }
    
    public AbstractPropertiesDialog(JFrame _frame, String _title) {
	super(_frame, _title, true);
	JTabbedPane tabbedPane = new JTabbedPane();
	addTabs(tabbedPane);
	ConfirmationPanel confirmationPanel
	    = createConfirmationPanel(tabbedPane);
	confirmationPanel.addOkListener(new OkListener());
	confirmationPanel.addCancelListener(new CancelListener());
	confirmationPanel.setEnterIsOK();
	getContentPane().add(confirmationPanel);
	pack();
	setSize(500, Math.min(400, getHeight()));
	DialogUtils.centerWindow(this);
    }
    
    protected abstract ConfirmationPanel createConfirmationPanel
	(JTabbedPane jtabbedpane);
    
    protected ConfirmationPanel createRecursiveConfirmationPanel
	(JTabbedPane _tabbedPane) {
	ConfirmationPanel confirmationPanel = new ConfirmationPanel(_tabbedPane) {
	    public void fillInButtonPanel(JPanel _buttonPanel,
					  boolean _showOkButton,
					  boolean _showCancelButton) {
		JButton recursiveOkButton
		    = new JButton(ResourceBundleUtils
				      .getUIString("properties.recursiveOK"));
		recursiveOkButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent _e) {
			AbstractPropertiesDialog.this.ok(true);
		    }
		});
		_buttonPanel.add(recursiveOkButton);
		super.fillInButtonPanel(_buttonPanel, _showOkButton,
					_showCancelButton);
	    }
	};
	return confirmationPanel;
    }
    
    public void setNodes(IFIDNode[] _nodes) {
	myNodes = _nodes;
    }
    
    protected IFIDNode[] getNodes() {
	return myNodes;
    }
    
    protected abstract void addTabs(JTabbedPane jtabbedpane);
    
    public abstract void ok(boolean bool);
    
    public abstract void cancel();
}
