/* PercentageProgressListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.progress;
import java.awt.event.ActionListener;

import com.inzyme.event.ActionSource;

public class PercentageProgressListener implements IProgressListener
{
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
	return false;
    }
    
    public boolean isInProgress() {
	return myInProgress;
    }
    
    public void progressStarted() {
	myInProgress = true;
	myStopRequested = false;
	myLastOperationPercentage = 0L;
	myLastTaskPercentage = 0L;
    }
    
    public void progressCompleted() {
	myInProgress = false;
    }
    
    public void operationStarted(String _fmt) {
	myLastOperationPercentage = 0L;
	setStopRequested(false);
	System.out.println();
	System.out.print("Operation: " + _fmt);
    }
    
    public void operationUpdated(long _relativeProgress) {
	/* empty */
    }
    
    public void operationUpdated(long _progress, long _total) {
	int percentage = (int) ((double) _progress / (double) _total * 100.0);
	if (Math.abs((long) percentage - myLastOperationPercentage)
	    >= (long) myGranularity) {
	    System.out.print(String.valueOf(percentage) + "% ");
	    myLastOperationPercentage = (long) percentage;
	}
    }
    
    public void taskStarted(String _fmt) {
	myLastTaskPercentage = 0L;
	System.out.println();
	System.out.print("Task: " + _fmt);
    }
    
    public void taskUpdated(long _relativeProgress) {
	/* empty */
    }
    
    public void progressReported(long _current, long _maximum) {
	taskUpdated(_current, _maximum);
    }
    
    public void progressReported(String _description, long _current,
				 long _maximum) {
	taskStarted(_description);
	taskUpdated(_current, _maximum);
    }
    
    public void taskUpdated(long _progress, long _total) {
	int percentage = (int) ((double) _progress / (double) _total * 100.0);
	if (Math.abs((long) percentage - myLastTaskPercentage)
	    >= (long) myGranularity) {
	    System.out.print(String.valueOf(percentage) + "% ");
	    myLastTaskPercentage = (long) percentage;
	}
    }
    
    public void addStopListener(ActionListener _stopListener) {
	myActionSource.addActionListener(_stopListener);
    }
}
