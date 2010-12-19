/* SilentProgressListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.progress;
import java.awt.event.ActionListener;

import com.inzyme.event.ActionSource;

public class SilentProgressListener implements IProgressListener
{
    private boolean myStopRequested;
    private boolean myInProgress;
    private ActionSource myActionSource = new ActionSource(this);
    
    public void setWaitState(boolean _waitState) {
	/* empty */
    }
    
    public void setStopEnabled(boolean _stopEnabled) {
	myStopRequested = false;
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
	/* empty */
    }
    
    public void operationUpdated(long relativeProgres) {
	/* empty */
    }
    
    public void operationUpdated(long _progress, long _total) {
	/* empty */
    }
    
    public void taskStarted(String _fmt) {
	/* empty */
    }
    
    public void taskUpdated(long _relativeProgress) {
	/* empty */
    }
    
    public void taskUpdated(long _progress, long _total) {
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
}
