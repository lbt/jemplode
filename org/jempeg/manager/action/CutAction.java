/* CutAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.util.EmplodeClipboard;

public class CutAction extends AbstractAction
{
    private ApplicationContext myContext;
    private boolean myAllowContainers;
    
    public CutAction(ApplicationContext _context, boolean _allowContainers) {
	myContext = _context;
	myAllowContainers = _allowContainers;
    }
    
    public void actionPerformed(ActionEvent _event) {
	ContainerSelection selection = myContext.getSelection();
	if (!myAllowContainers)
	    selection = selection.pruneContainers();
	EmplodeClipboard.getInstance().cut(selection);
    }
}
