/* TextProgressListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.progress;
import java.awt.event.ActionListener;

import com.inzyme.event.ActionSource;

public class TextProgressListener implements IProgressListener
{
    private String myName;
    private boolean myStopRequested;
    private boolean myInProgress;
    private ActionSource myActionSource;
    
    public TextProgressListener() {
	this("");
    }
    
    public TextProgressListener(String _name) {
	myName = _name;
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
	setStopRequested(false);
	System.out.println("operationStarted (" + myName + "): " + _fmt);
    }
    
    public void operationUpdated(long _relativeProgress) {
	System.out.println("operationUpdated (" + myName + "): "
			   + _relativeProgress);
    }
    
    public void operationUpdated(long _progress, long _total) {
	System.out.println("operationUpdated (" + myName + "): " + _progress
			   + "; " + _total);
    }
    
    public void taskStarted(String _fmt) {
	System.out.println("taskStarted (" + myName + "): " + _fmt);
    }
    
    public void taskUpdated(long _relativeProgress) {
	System.out
	    .println("taskUpdated (" + myName + "): " + _relativeProgress);
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
	System.out.println("taskUpdated (" + myName + "): " + _progress + "; "
			   + _total);
    }
}
