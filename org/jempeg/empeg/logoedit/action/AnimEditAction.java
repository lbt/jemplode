/* AnimEditAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit.action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.inzyme.ui.DialogUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.logoedit.AnimEdit;
import org.jempeg.empeg.manager.plugins.JEmplodePlugin;
import org.jempeg.protocol.IConnection;

public class AnimEditAction extends AbstractAction implements JEmplodePlugin
{
    private ApplicationContext myContext;
    private JFrame myAnimEditFrame;
    
    protected class ExitAction implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myAnimEditFrame.dispose();
	    myAnimEditFrame = null;
	}
    }
    
    public AnimEditAction(ApplicationContext _context) {
	super("AnimEdit");
	myContext = _context;
    }
    
    public void performAction() {
	IConnection conn
	    = myContext.getSynchronizeClient().getConnectionFactory()
		  .createConnection();
	if (myAnimEditFrame == null) {
	    myAnimEditFrame = AnimEdit.createFrame(new ExitAction(), conn);
	    DialogUtils.centerWindow(myAnimEditFrame);
	    myAnimEditFrame.setVisible(true);
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
