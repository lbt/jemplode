/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.progress;

import java.awt.event.ActionListener;

import com.inzyme.event.ActionSource;


/**
* SilentProgressListener is an implementation of
* DatabaseProgressListener that doesn't do anything
* (i.e. it just swallows events)
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class SilentProgressListener implements IProgressListener {
	private boolean myStopRequested;
	private boolean myInProgress;
	private ActionSource myActionSource;
	
	public SilentProgressListener() {
		myActionSource = new ActionSource(this);
	}
	
	public void setWaitState(boolean _waitState) {
	}
	
  public void setStopEnabled(boolean _stopEnabled) {
  	myStopRequested = false;
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
		return false;
	}
	
	public void addStopListener(ActionListener _stopListener) {
		myActionSource.addActionListener(_stopListener);
	}
	
	public void progressStarted() {
		myStopRequested = false;
		myInProgress = true;
	}
	
	public boolean isInProgress() {
		return myInProgress;
	}
	
	public void progressCompleted() {
		myInProgress = false;
	}
	
	public void operationStarted(String _fmt) {
	}

	public void operationUpdated(long relativeProgres) {
	}
	
	public void operationUpdated(long _progress, long _total) {
	}
	
	public void taskStarted(String _fmt) {
	}
	
	public void taskUpdated(long _relativeProgress) {
	}
	
	public void taskUpdated(long _progress, long _total) {
	}
	
	public void progressReported(long _current, long _maximum) {
		taskUpdated(_current, _maximum);
	}
	
	public void progressReported(String _description, long _current, long _maximum) {
		taskStarted(_description);
		taskUpdated(_current, _maximum);
	}
}
