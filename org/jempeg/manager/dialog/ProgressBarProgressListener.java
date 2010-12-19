/* ProgressBarProgressListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.inzyme.event.ActionSource;
import com.inzyme.progress.IProgressListener;

public class ProgressBarProgressListener implements IProgressListener
{
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
    
    public ProgressBarProgressListener(JProgressBar _progressBar,
				       JLabel _statusLabel, boolean _showTask,
				       boolean _showOperation) {
	myProgressBar = _progressBar;
	if (_statusLabel == null)
	    myStatusLabel = new JLabel();
	else
	    myStatusLabel = _statusLabel;
	myShowOperation = _showOperation;
	myShowTask = _showTask;
	myActionSource = new ActionSource(this);
	progressCompleted();
    }
    
    public void setWaitState(boolean _waitState) {
	/* empty */
    }
    
    public void setStopEnabled(boolean _stopEnabled) {
	/* empty */
    }
    
    public void setStopRequested(boolean _stopRequested) {
	myStopRequested = _stopRequested;
	if (myStopRequested)
	    myActionSource.fireActionPerformed();
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
	if (myShowTask)
	    myProgressBar
		.setValue(myProgressBar.getValue() + (int) _relativeProgress);
    }
    
    public void taskUpdated(long _progress, long _total) {
	if (myShowTask) {
	    myProgressBar.setMaximum((int) _total);
	    myProgressBar.setValue((int) _progress);
	}
    }
    
    public void progressReported(long _current, long _maximum) {
	if (myShowTask)
	    taskUpdated(_current, _maximum);
    }
    
    public void progressReported(String _description, long _current,
				 long _maximum) {
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
	if (myShowOperation)
	    myProgressBar
		.setValue(myProgressBar.getValue() + (int) _relativeProgress);
    }
    
    public void operationUpdated(long _progress, long _total) {
	if (myShowOperation) {
	    myProgressBar.setMaximum((int) _total);
	    myProgressBar.setValue((int) _progress);
	}
    }
}
