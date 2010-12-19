/* AboutAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite.action;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.ApplicationContext;

public class AboutAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public AboutAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	final JDialog aboutDialog
	    = new JDialog(myContext.getFrame(),
			  ResourceBundleUtils.getUIString("about.frameTitle"),
			  true);
	JPanel aboutPanel = new JPanel();
	aboutPanel.setLayout(new GridLayout(0, 1));
	int aboutTextCount
	    = Integer.parseInt(ResourceBundleUtils
				   .getUIString("about.text.count"));
	for (int i = 0; i < aboutTextCount; i++) {
	    JLabel label
		= new JLabel(ResourceBundleUtils.getUIString
			     ("about.text." + i, new Object[] { "20040928" }));
	    aboutPanel.add(label);
	}
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(aboutPanel, false);
	confirmationPanel.addOkListener(new ActionListener() {
	    public void actionPerformed(ActionEvent __event) {
		aboutDialog.hide();
		aboutDialog.dispose();
	    }
	});
	aboutDialog.getContentPane().add(confirmationPanel, "Center");
	aboutDialog.pack();
	DialogUtils.centerWindow(aboutDialog);
	aboutDialog.show();
    }
}
