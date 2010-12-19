package com.inzyme.progress;

import java.awt.event.ActionListener;

public class CompositeProgressListener implements IProgressListener {
	private IProgressListener myProgressListener1;
	private IProgressListener myProgressListener2;

	public CompositeProgressListener(IProgressListener _progressListener1, IProgressListener _progressListener2) {
		myProgressListener1 = (_progressListener1 == null) ? new SilentProgressListener() : _progressListener1;
		myProgressListener2 = (_progressListener2 == null) ? new SilentProgressListener() : _progressListener2;
	}
	
	public boolean isInteractive() {
		return myProgressListener1.isInteractive() || myProgressListener2.isInteractive();
	}
	
	public boolean isStopRequested() {
		return myProgressListener1.isStopRequested() || myProgressListener2.isStopRequested();
	}
	
	public void operationStarted(String _fmt) {
		myProgressListener1.operationStarted(_fmt);
		myProgressListener2.operationStarted(_fmt);
	}
	
	public void operationUpdated(long _progress, long _total) {
		myProgressListener1.operationUpdated(_progress, _total);
		myProgressListener2.operationUpdated(_progress, _total);
	}
	
	public void operationUpdated(long _relativeProgress) {
		myProgressListener1.operationUpdated(_relativeProgress);
		myProgressListener2.operationUpdated(_relativeProgress);
	}
	
	public void progressCompleted() {
		myProgressListener1.progressCompleted();
		myProgressListener2.progressCompleted();
	}
	
	public boolean isInProgress() {
		return myProgressListener1.isInProgress() || myProgressListener2.isInProgress();
	}
	
	public void progressStarted() {
		myProgressListener1.progressStarted();
		myProgressListener2.progressStarted();
	}
	
	public void setStopEnabled(boolean _stopEnabled) {
		myProgressListener1.setStopEnabled(_stopEnabled);
		myProgressListener2.setStopEnabled(_stopEnabled);
	}
	
	public void setStopRequested(boolean _stopRequested) {
		myProgressListener1.setStopRequested(_stopRequested);
		myProgressListener2.setStopRequested(_stopRequested);
	}
	
	public void setWaitState(boolean _waitState) {
		myProgressListener1.setWaitState(_waitState);
		myProgressListener2.setWaitState(_waitState);
	}
	
	public void taskStarted(String _fmt) {
		myProgressListener1.taskStarted(_fmt);
		myProgressListener2.taskStarted(_fmt);
	}
	
	public void taskUpdated(long _progress, long _total) {
		myProgressListener1.taskUpdated(_progress, _total);
		myProgressListener2.taskUpdated(_progress, _total);
	}
	
	public void taskUpdated(long _relativeProgress) {
		myProgressListener1.taskUpdated(_relativeProgress);
		myProgressListener2.taskUpdated(_relativeProgress);
	}
	
	public void progressReported(long _current, long _maximum) {
		myProgressListener1.progressReported(_current, _maximum);
		myProgressListener2.progressReported(_current, _maximum);
	}

	public void progressReported(String _description, long _current, long _maximum) {
		myProgressListener1.progressReported(_description, _current, _maximum);
		myProgressListener2.progressReported(_description, _current, _maximum);
	}
	
	public void addStopListener(ActionListener _stopListener) {
		myProgressListener1.addStopListener(_stopListener);
		myProgressListener2.addStopListener(_stopListener);
	}

}
