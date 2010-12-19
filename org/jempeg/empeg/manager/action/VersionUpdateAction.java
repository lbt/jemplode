/* VersionUpdateAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.versiontracker.VersionTrackerUtils;

public class VersionUpdateAction extends AbstractAction implements Runnable
{
    private ApplicationContext myContext;
    private boolean myRestartRequired;
    
    public VersionUpdateAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	Thread t = new Thread(this);
	t.start();
    }
    
    public boolean isRestartRequired() {
	return myRestartRequired;
    }
    
    public void run() {
	myRestartRequired = VersionTrackerUtils.upgrade(myContext, false);
    }
}
