/* EditConfigIniAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.EditConfigIniDialog;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.PlayerDatabase;

public class EditConfigIniAction extends AbstractAction
{
    private static final String[] WARNINGS
	= { "You can pretty much destroy the universe with this feature.",
	    "You can rain down hellfire on the world with this feature.",
	    "You could kill your friends and family with this feature.",
	    "Surgeon General warns this this dialog can cause cancer.",
	    "Do you care about your family?  Don't do this.",
	    "Why don't you think twice about this one...",
	    "Is it worth it?  Have you thought about the consequences?",
	    "This is a bad, bad idea.", "Nothing good can come of this.",
	    "You increase your chance of heart disease with this feature.",
	    "You should quit while you're ahead.",
	    "Have you thought about the ramifications of your actions?" };
    private ApplicationContext myContext;
    
    public EditConfigIniAction(ApplicationContext _context) {
	super("EditConfig");
	myContext = _context;
    }
    
    public void performAction() {
	JFrame frame = myContext.getFrame();
	String warning
	    = (WARNINGS
	       [(int) Math.floor(Math.random() * (double) WARNINGS.length)]);
	int response
	    = JOptionPane.showConfirmDialog(frame,
					    warning + "  Are you sure?");
	if (response == 0) {
	    PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
	    IDeviceSettings configFile = playerDatabase.getDeviceSettings();
	    EditConfigIniDialog editConfigIniDialog
		= new EditConfigIniDialog(frame);
	    editConfigIniDialog.setConfigFile(configFile);
	    editConfigIniDialog.setVisible(true);
	    editConfigIniDialog.dispose();
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
