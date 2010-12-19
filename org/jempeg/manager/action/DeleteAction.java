/* DeleteAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.PopupConfirmationListener;
import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

public class DeleteAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public DeleteAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	ContainerSelection selection = myContext.getSelection();
	if (selection != null) {
	    IContainerModifier nodeModifier
		= ContainerModifierFactory.getInstance(selection);
	    PopupConfirmationListener confirmationListener
		= (new PopupConfirmationListener
		   (myContext.getFrame(),
		    ResourceBundleUtils
			.getUIString("deleteConfirmation.frameTitle")));
	    nodeModifier.delete(selection, confirmationListener);
	}
    }
}
