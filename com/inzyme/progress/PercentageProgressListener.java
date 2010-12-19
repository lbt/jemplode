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
* A simple implementation of IProgressListener
* that just prints status out to the console.
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class PercentageProgressListener implements IProgressListener {
  private boolean myStopRequested;
  private long myLastOperationPercentage;
  private long myLastTaskPercentage;
  private int myGranularity;
  private ActionSource myActionSource;
  private boolean myInProgress;
  
	public PercentageProgressListener(int _granularity) {
		myGranularity = _granularity;
		myActionSource = new ActionSource(this);
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
		return false;
	}
	
	public boolean isInProgress() {
		return myInProgress;
	}
	
	public void progressStarted() {
		myInProgress = true;
		myStopRequested = false;
		myLastOperationPercentage = 0;
		myLastTaskPercentage = 0;
	}
	
	public void progressCompleted() {
		myInProgress = false;
	}

	public void operationStarted(String _fmt) {
		myLastOperationPercentage = 0;
    setStopRequested(false);
		System.out.println();
    System.out.print("Operation: " + _fmt);
	}

	public void operationUpdated(long _relativeProgress) {
	}
	
	public void operationUpdated(long _progress, long _total) {
		int percentage = (int)(((double)_progress / (double)_total) * 100.0);
		if (Math.abs(percentage - myLastOperationPercentage) >= myGranularity) {
			System.out.print(percentage + "% ");
			myLastOperationPercentage = percentage; 
		}
	}
	
	public void taskStarted(String _fmt) {
		myLastTaskPercentage = 0;
		System.out.println();
		System.out.print("Task: " + _fmt);
	}
	
	public void taskUpdated(long _relativeProgress) {
	}
	
	public void progressReported(long _current, long _maximum) {
		taskUpdated(_current, _maximum);
	}
	
	public void progressReported(String _description, long _current, long _maximum) {
		taskStarted(_description);
		taskUpdated(_current, _maximum);
	}
	
	public void taskUpdated(long _progress, long _total) {
		int percentage = (int)(((double)_progress / (double)_total) * 100.0);
		if (Math.abs(percentage - myLastTaskPercentage) >= myGranularity) {
			System.out.print(percentage + "% ");
			myLastTaskPercentage = percentage; 
		}
	}
	
	public void addStopListener(ActionListener _stopListener) {
		myActionSource.addActionListener(_stopListener);
	}
}
