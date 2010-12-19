/* OptionsAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.OptionsDialog;

public class OptionsAction extends AbstractAction
{
    private JFrame myFrame;
    private ApplicationContext myContext;
    
    public OptionsAction(ApplicationContext _context, JFrame _frame) {
	super("Options");
	myContext = _context;
	myFrame = _frame;
    }
    
    public void performAction() {
	try {
	    OptionsDialog optionsDialog
		= new OptionsDialog(myContext, myFrame);
	    optionsDialog.setVisible(true);
	    optionsDialog.dispose();
	    if (optionsDialog.isChanged())
		PropertiesManager.getInstance().save();
	} catch (IOException e) {
	    Debug.handleError(myFrame, e, true);
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
