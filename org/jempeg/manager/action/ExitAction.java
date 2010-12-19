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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.PlayerDatabase;

import com.inzyme.text.ResourceBundleUtils;

/**
* Exits the application
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.3 $
*/
public class ExitAction extends AbstractAction implements WindowListener {
	private static int WINDOW_COUNT;
	private ApplicationContext myContext;
	private boolean mySystemExit;

	public ExitAction(ApplicationContext _context, boolean _systemExit) {
		myContext = _context;
		mySystemExit = _systemExit;
		WINDOW_COUNT ++;
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

		if (playerDatabase == null) {
			shouldExit = true;
		}
		else if (playerDatabase.isDirty()) {
			int confirm = JOptionPane.showConfirmDialog(myContext.getFrame(), ResourceBundleUtils.getString(ResourceBundleUtils.UI_KEY, "exit.databaseDirtyConfirmation"));
			if (confirm == JOptionPane.YES_OPTION) {
				new SynchronizeUI(myContext.getPlayerDatabase(), myContext.getSynchronizeClient(), myContext.getFrame()).synchronizeInBackground(true, myContext.getSynchronizeProgressListener());
			}
			else if (confirm == JOptionPane.NO_OPTION) {
				shouldExit = true;
			}
			else if (confirm == JOptionPane.CANCEL_OPTION) {
			}
		}
		else {
			shouldExit = true;
		}

		if (shouldExit) {
			myContext.getFrame().setVisible(false);
			myContext.getFrame().dispose();
			WINDOW_COUNT --;
			if (mySystemExit && WINDOW_COUNT == 0) {
				System.exit(0);
			}
		}
	}

	public void windowClosed(WindowEvent _event) {
	}

	public void windowOpened(WindowEvent _event) {
	}

	public void windowIconified(WindowEvent _event) {
	}

	public void windowDeiconified(WindowEvent _event) {
	}

	public void windowActivated(WindowEvent _event) {
	}

	public void windowDeactivated(WindowEvent _event) {
	}
}
