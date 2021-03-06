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
package org.jempeg.empeg.logoedit.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.logoedit.LogoEdit;
import org.jempeg.empeg.manager.plugins.JEmplodePlugin;
import org.jempeg.protocol.IConnection;

import com.inzyme.ui.DialogUtils;

/**
* LogoEditAction displays the Logo Editor.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class LogoEditAction extends AbstractAction implements JEmplodePlugin {
  private ApplicationContext myContext;
  private JFrame myLogoEditFrame;

	public LogoEditAction(ApplicationContext _context) {
		super("LogoEdit");
		myContext = _context;
	}
	
	public void performAction() {
    IConnection conn = myContext.getSynchronizeClient().getConnectionFactory().createConnection();
    if (myLogoEditFrame == null) {
      myLogoEditFrame = LogoEdit.createFrame(new ExitAction(), conn);
      DialogUtils.centerWindow(myLogoEditFrame);
      myLogoEditFrame.setVisible(true);
    }
	}

	public void actionPerformed(ActionEvent _event) {
		performAction();
	}

  protected class ExitAction implements ActionListener {
    public void actionPerformed(ActionEvent _event) {
      myLogoEditFrame.dispose();
      myLogoEditFrame = null;
    }
  }
}
