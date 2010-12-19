/* ExitAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.PlayerDatabase;

public class ExitAction extends AbstractAction implements WindowListener
{
    private static int WINDOW_COUNT;
    private ApplicationContext myContext;
    private boolean mySystemExit;
    
    public ExitAction(ApplicationContext _context, boolean _systemExit) {
	myContext = _context;
	mySystemExit = _systemExit;
	WINDOW_COUNT++;
    }
    
    public void actionPerformed(ActionEvent _event) {
	confirm();
    }
    
    public void windowClosing(WindowEvent _event) {
	confirm();
    }
    
    public static int getWindowCount() {
	return WINDOW_COUNT;
    }
    
    protected void confirm() {
	boolean shouldExit = false;
	PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
	if (playerDatabase == null)
	    shouldExit = true;
	else if (playerDatabase.isDirty()) {
	    int confirm
		= (JOptionPane.showConfirmDialog
		   (myContext.getFrame(),
		    ResourceBundleUtils
			.getString("ui", "exit.databaseDirtyConfirmation")));
	    if (confirm == 0)
		new SynchronizeUI
		    (myContext.getPlayerDatabase(),
		     myContext.getSynchronizeClient(), myContext.getFrame())
		    .synchronizeInBackground
		    (true, myContext.getSynchronizeProgressListener());
	    else if (confirm == 1)
		shouldExit = true;
	} else
	    shouldExit = true;
	if (shouldExit) {
	    myContext.getFrame().setVisible(false);
	    myContext.getFrame().dispose();
	    WINDOW_COUNT--;
	    if (mySystemExit && WINDOW_COUNT == 0)
		System.exit(0);
	}
    }
    
    public void windowClosed(WindowEvent _event) {
	/* empty */
    }
    
    public void windowOpened(WindowEvent _event) {
	/* empty */
    }
    
    public void windowIconified(WindowEvent _event) {
	/* empty */
    }
    
    public void windowDeiconified(WindowEvent _event) {
	/* empty */
    }
    
    public void windowActivated(WindowEvent _event) {
	/* empty */
    }
    
    public void windowDeactivated(WindowEvent _event) {
	/* empty */
    }
}
