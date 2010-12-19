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
package org.jempeg.empeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.protocol.IProtocolClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

/**
* Play the current FID on the Empeg.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class PlayAction extends AbstractAction {
	public static final int PLAYMODE_INSERT = 0;
	public static final int PLAYMODE_APPEND = 1;
	public static final int PLAYMODE_REPLACE = 2;

	private ApplicationContext myContext;
  private int myMode;

	public PlayAction(ApplicationContext _context, int _mode) {
		myContext = _context;
    myMode = _mode;
	}
	
	public void actionPerformed(ActionEvent _event) {
    try {
    	ContainerSelection selection = myContext.getSelection();
    	if (selection != null) {
    		IProtocolClient client = myContext.getSynchronizeClient().getProtocolClient(new SilentProgressListener());
    		Object[] selectedValues = selection.getSelectedValues();
    		for (int i = 0; i < selectedValues.length; i ++) {
    			if (selectedValues[i] instanceof IFIDNode) {
    				IFIDNode node = (IFIDNode)selectedValues[i];
    				if (myMode == PlayAction.PLAYMODE_APPEND) {
    					client.playAppend(node.getFID());
    				} else if (myMode == PlayAction.PLAYMODE_REPLACE) {
    					client.playReplace(node.getFID());
    				} else if (myMode == PlayAction.PLAYMODE_INSERT) {
    					client.playInsert(node.getFID());
    				}
    			}
    		}
    	}
    }
    catch (Exception e) {
      Debug.println(e);
    }
	}
}
