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
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.EditConfigIniDialog;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.PlayerDatabase;

/**
* EditConfigIniAction displays the "Edit config.ini" dialog for the user
* to modify settings of the Empeg.
*
* @author Mike Schrag
* @version $Revision: 1.7 $
*/
public class EditConfigIniAction extends AbstractAction {
  private static final String[] WARNINGS = new String[] {
    "You can pretty much destroy the universe with this feature.",
    "You can rain down hellfire on the world with this feature.",
    "You could kill your friends and family with this feature.",
    "Surgeon General warns this this dialog can cause cancer.",
    "Do you care about your family?  Don't do this.",
    "Why don't you think twice about this one...",
    "Is it worth it?  Have you thought about the consequences?",
    "This is a bad, bad idea.",
    "Nothing good can come of this.",
    "You increase your chance of heart disease with this feature.",
    "You should quit while you're ahead.",
    "Have you thought about the ramifications of your actions?"
  };
    
	private ApplicationContext myContext;

	public EditConfigIniAction(ApplicationContext _context) {
		super("EditConfig");
		myContext = _context;
	}
	
	public void performAction() {
    JFrame frame = myContext.getFrame();
    String warning = EditConfigIniAction.WARNINGS[(int)Math.floor(Math.random() * WARNINGS.length)];
    
    int response = JOptionPane.showConfirmDialog(frame, warning + "  Are you sure?");
    if (response == JOptionPane.YES_OPTION) {
    	PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
    	IDeviceSettings configFile = playerDatabase.getDeviceSettings();
      EditConfigIniDialog editConfigIniDialog = new EditConfigIniDialog(frame);
      editConfigIniDialog.setConfigFile(configFile);
      editConfigIniDialog.setVisible(true);
      editConfigIniDialog.dispose();
    }
	}

	public void actionPerformed(ActionEvent _event) {
		performAction();
	}
}
