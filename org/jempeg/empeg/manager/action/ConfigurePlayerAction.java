/* ConfigurePlayerAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.ConfigurePlayerDialog;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.PlayerDatabase;

public class ConfigurePlayerAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public ConfigurePlayerAction(ApplicationContext _context) {
	super("ConfigurePlayer");
	myContext = _context;
    }
    
    public void performAction() {
	JFrame frame = myContext.getFrame();
	PlayerDatabase db = myContext.getPlayerDatabase();
	IDeviceSettings configFile = db.getDeviceSettings();
	ConfigurePlayerDialog configurePlayerDialog
	    = new ConfigurePlayerDialog(frame);
	configurePlayerDialog.setConfigFile(configFile);
	configurePlayerDialog.setVisible(true);
	configurePlayerDialog.dispose();
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
