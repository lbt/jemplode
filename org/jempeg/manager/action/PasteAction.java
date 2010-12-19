/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.PopupConfirmationListener;
import org.jempeg.manager.ui.ContainerModifierUI;
import org.jempeg.manager.util.EmplodeClipboard;
import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

/**
* Paste clipboard action
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class PasteAction extends AbstractAction {
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
				if (selectedObj instanceof IContainer) {
					// TODO: Is this legit to override the selection if you are actively selecting a container?
					container = (IContainer) selectedObj;
				}
			}

			PopupConfirmationListener confirmationListener = new PopupConfirmationListener(myContext.getFrame(), "Paste");
			IContainerModifier nodeModifier = ContainerModifierFactory.getInstance(container);
			EmplodeClipboard.getInstance().paste(new ContainerModifierUI(myContext, nodeModifier), confirmationListener, myDeepCopy, myContext.getImportFilesProgressListener());
		}
	}
}
