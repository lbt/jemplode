/* PasteAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.PopupConfirmationListener;
import org.jempeg.manager.ui.ContainerModifierUI;
import org.jempeg.manager.util.EmplodeClipboard;
import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

public class PasteAction extends AbstractAction
{
    private ApplicationContext myContext;
    private boolean myDeepCopy;
    
    public PasteAction(ApplicationContext _context, boolean _deepCopy) {
	myContext = _context;
	myDeepCopy = _deepCopy;
    }
    
    public void actionPerformed(ActionEvent _event) {
	IContainer container = myContext.getSelectedContainer();
	ContainerSelection selection = myContext.getSelection();
	if (selection != null) {
	    if (selection.getSize() == 1) {
		Object selectedObj = selection.getValueAt(0);
		if (selectedObj instanceof IContainer)
		    container = (IContainer) selectedObj;
	    }
	    PopupConfirmationListener confirmationListener
		= new PopupConfirmationListener(myContext.getFrame(), "Paste");
	    IContainerModifier nodeModifier
		= ContainerModifierFactory.getInstance(container);
	    EmplodeClipboard.getInstance().paste
		(new ContainerModifierUI(myContext, nodeModifier),
		 confirmationListener, myDeepCopy,
		 myContext.getImportFilesProgressListener());
	}
    }
}
