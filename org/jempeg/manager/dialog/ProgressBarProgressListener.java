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

import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.inzyme.event.ActionSource;
import com.inzyme.progress.IProgressListener;

/**
* An implementation of UpgradeListener that updates a 
* progress bar with status.
*
* @author Mike Schrag
* @version $Revision: 1.2 $
**/
public class ProgressBarProgressListener implements IProgressListener {
  private JProgressBar myProgressBar;
  private JLabel myStatusLabel;
  private boolean myStopRequested;
  private boolean myShowTask;
  private boolean myShowOperation;
  private boolean myInProgress;
  private ActionSource myActionSource;
  
  public ProgressBarProgressListener(JProgressBar _progressBar) {
  	this(_progressBar, null, true, true);
  }
  
  public ProgressBarProgressListener(JProgressBar _progressBar, JLabel _statusLabel, boolean _showTask, boolean _showOperation) {
    myProgressBar = _progressBar;
    if (_statusLabel == null) {
    	myStatusLabel = new JLabel();
    } else {
    	myStatusLabel = _statusLabel;
    }
    myShowOperation = _showOperation;
    myShowTask = _showTask;
		myActionSource = new ActionSource(this);
    progressCompleted();
  }

	public void setWaitState(boolean _waitState) {
	}
	
  public void setStopEnabled(boolean _stopEnabled) {
  }

  public void setStopRequested(boolean _stopRequested) {
    myStopRequested = _stopRequested;
    if (myStopRequested) {
    	myActionSource.fireActionPerformed();
    }
  }

  public boolean isStopRequested() {
    return myStopRequested;
  }
	
	public boolean isInteractive() {
		return true;
	}
	
	public void addStopListener(ActionListener _stopListener) {
		myActionSource.addActionListener(_stopListener);
	}
	
	public void progressStarted() {
		myStopRequested = false;
		myInProgress = true;
		myProgressBar.setEnabled(true);
	}
	
	public boolean isInProgress() {
		return myInProgress;
	}
	
	public void progressCompleted() {
		myInProgress = false;
		myStatusLabel.setText("Ready.");
		myProgressBar.setEnabled(false);
		myProgressBar.setValue(0);
	}
	
  public void taskStarted(String _fmt) {
  	if (myShowTask) {
	    myProgressBar.setString(_fmt);
	    myStatusLabel.setText(_fmt);
  	}
  }

  public void taskUpdated(long _relativeProgress) {
  	if (myShowTask) {
  		myProgressBar.setValue(myProgressBar.getValue() + (int)_relativeProgress);
  	}
  }
  
  public void taskUpdated(long _progress, long _total) {
  	if (myShowTask) {
	    myProgressBar.setMaximum((int)_total);
	    myProgressBar.setValue((int)_progress);
  	}
  }

  public void progressReported(long _current, long _maximum) {
  	if (myShowTask) {
	  	taskUpdated(_current, _maximum);
  	}
  }

	public void progressReported(String _description, long _current, long _maximum) {
		taskStarted(_description);
		taskUpdated(_current, _maximum);
	}

  public void operationStarted(String _fmt) {
  	if (myShowOperation) {
	    myProgressBar.setString(_fmt);
	    myStatusLabel.setText(_fmt);
  	}
  }

  public void operationUpdated(long _relativeProgress) {
  	if (myShowOperation) {
  		myProgressBar.setValue(myProgressBar.getValue() + (int)_relativeProgress);
  	}
  }
  
  public void operationUpdated(long _progress, long _total) {
  	if (myShowOperation) {
	    myProgressBar.setMaximum((int)_total);
	    myProgressBar.setValue((int)_progress);
  	}
  }
}

