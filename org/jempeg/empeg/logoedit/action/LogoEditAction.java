/* LogoEditAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit.action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.inzyme.ui.DialogUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.logoedit.LogoEdit;
import org.jempeg.empeg.manager.plugins.JEmplodePlugin;
import org.jempeg.protocol.IConnection;

public class LogoEditAction extends AbstractAction implements JEmplodePlugin
{
    private ApplicationContext myContext;
    private JFrame myLogoEditFrame;
    
    protected class ExitAction implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myLogoEditFrame.dispose();
	    myLogoEditFrame = null;
	}
    }
    
    public LogoEditAction(ApplicationContext _context) {
	super("LogoEdit");
	myContext = _context;
    }
    
    public void performAction() {
	IConnection conn
	    = myContext.getSynchronizeClient().getConnectionFactory()
		  .createConnection();
	if (myLogoEditFrame == null) {
	    myLogoEditFrame = LogoEdit.createFrame(new ExitAction(), conn);
	    DialogUtils.centerWindow(myLogoEditFrame);
	    myLogoEditFrame.setVisible(true);
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
