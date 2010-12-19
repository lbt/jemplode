/* PlayPauseAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.protocol.IProtocolClient;

public class PlayPauseAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public PlayPauseAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    IProtocolClient client
		= myContext.getSynchronizeClient()
		      .getProtocolClient(new SilentProgressListener());
	    client.pause();
	} catch (Exception e) {
	    Debug.println(e);
	}
    }
}
