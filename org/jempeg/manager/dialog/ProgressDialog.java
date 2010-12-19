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
package org.jempeg.manager.dialog;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.inzyme.progress.IProgressListener;
import com.inzyme.ui.DialogUtils;

/**
* ProgressDialog is an implementation of 
* IProgressListener that renders
* a set of labels and JProgressBars to
* represents the status of a download or
* upload.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class ProgressDialog implements ActionListener, IProgressListener {
	private JFrame myFrame;
  private JDialog myDialog;
	private ProgressPanel myProgressPanel;
	
	public ProgressDialog(JFrame _frame, boolean _dualProgressBars) {
		this(_frame, _dualProgressBars, false);
	}
	
	public ProgressDialog(JFrame _frame, boolean _dualProgressBars, boolean _indeterminate) {
		myProgressPanel = new ProgressPanel(_dualProgressBars, true, _indeterminate);
		myFrame = _frame;
    setStopEnabled(false);
	}
	
	public ProgressPanel getProgressPanel() {
		return myProgressPanel;
	}

  /**
  * Enables or disables the wait cursor.
  *
  * @param _wait true to go into the wait state, false to leave the wait state
  */
  public void setWaitState(boolean _wait) {
  	myProgressPanel.setWaitState(_wait);
    final boolean finalWait = _wait;
    
    Runnable runnable = new Runnable() {
      public void run() {
      	if (myFrame != null) {
	        myFrame.getGlassPane().setVisible(finalWait);
      	}
      }
    };
    
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      SwingUtilities.invokeLater(runnable);
    }
  }
	
	public void progressStarted() {
		setVisibleInBackground(true);
		myProgressPanel.progressStarted();
	}
	
	public boolean isInProgress() {
		return (myDialog != null && myDialog.isVisible());
	}
	
	public void progressCompleted() {
		setVisibleInBackground(false);
		myProgressPanel.progressCompleted();
	}
	
  protected void createDialog() {
		myDialog = new JDialog(myFrame, "", false);

		JComponent comp = (JComponent)myDialog.getContentPane();
		comp.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
    comp.setLayout(new BorderLayout());
    
    comp.add(myProgressPanel, BorderLayout.CENTER);

    myDialog.pack();
    Dimension size = myDialog.getSize();
		myDialog.setSize(Math.max(400, size.width), Math.max(100, size.height));
        
    DialogUtils.centerWindow(myDialog);

    myDialog.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }
	
  public void setStopEnabled(boolean _stopEnabled) {
  	myProgressPanel.setStopEnabled(_stopEnabled);
  }

  public void setStopRequested(boolean _stopRequested) {
  	myProgressPanel.setStopRequested(_stopRequested);
  }

  public boolean isStopRequested() {
    return myProgressPanel.isStopRequested();
  }

	public boolean isInteractive() {
		return true;
	}
	
	public void addStopListener(ActionListener _stopListener) {
		myProgressPanel.addStopListener(_stopListener);
	}
	
	public synchronized void setVisible(boolean _visible) {
    if (_visible) {
      if (myDialog == null) {
        createDialog();
        myDialog.setVisible(true);
        myProgressPanel.setVisible(_visible);
      }
    } else {
      if (myDialog != null) {
        myDialog.setVisible(false);
        myDialog.dispose();
        myDialog = null;
				myProgressPanel.setVisible(_visible);
      }
    }
	}

	public synchronized void operationStarted(String _fmt) {
		myProgressPanel.operationStarted(_fmt);
		setVisibleInBackground(true);
	}

	public synchronized void operationUpdated(long _relativeProgress) {
		myProgressPanel.operationUpdated(_relativeProgress);
	}
	
	public synchronized void operationUpdated(long _progress, long _total) {
		myProgressPanel.operationUpdated(_progress, _total);
	}
	
	public synchronized void taskStarted(String _fmt) {
		myProgressPanel.taskStarted(_fmt);
		setVisibleInBackground(true);
	}

	public synchronized void taskUpdated(long _relativeProgress) {
		myProgressPanel.taskUpdated(_relativeProgress);
	}

	public synchronized void taskUpdated(long _progress, long _total) {
		myProgressPanel.taskUpdated(_progress, _total);
	}
	
	public void progressReported(long _current, long _maximum) {
		taskUpdated(_current, _maximum);
	}
	
	public void progressReported(String _description, long _current, long _maximum) {
		taskStarted(_description);
		taskUpdated(_current, _maximum);
	}

  /**
   * Calls setVisible() in a thread other than the one which called
   * this method.
   *
   * @param _visible The new visible state.
   * @ensures a request to set the dialog to the new visible state is
   *  enqueued in the Swing event queue.
   **/
   
  public synchronized void setVisibleInBackground(boolean _visible) {
    setVisible(_visible);

//        final boolean finalVisible = _visible;
//        
//        Runnable runnable = new Runnable() {
//          public void run() {
//            setVisible(finalVisible);
//          }
//        };
//        
//        SwingUtilities.invokeLater(runnable);
  }

  public void actionPerformed(ActionEvent _event) {
  	myProgressPanel.actionPerformed(_event);
  }
}
