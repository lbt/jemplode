/* ProgressPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.inzyme.progress.IProgressListener;
import com.inzyme.text.ResourceBundleUtils;

public class ProgressPanel extends JPanel
    implements ActionListener, IProgressListener
{
    private JLabel myOperationLabel = new JLabel();
    private JLabel myTaskLabel = new JLabel();
    private JProgressBar myOperationProgressBar = new JProgressBar();
    private JProgressBar myTaskProgressBar = new JProgressBar();
    private JButton myStopButton;
    private boolean myShowStopButton;
    private boolean myStopRequested;
    private boolean myDualProgressBars;
    private boolean myVisible;
    
    public ProgressPanel(boolean _dualProgressBars, boolean _showStopButton) {
	this(_dualProgressBars, _showStopButton, false);
    }
    
    public ProgressPanel(boolean _dualProgressBars, boolean _showStopButton,
			 boolean _indeterminate) {
	if (_indeterminate) {
	    try {
		myOperationProgressBar.setIndeterminate(true);
		myTaskProgressBar.setIndeterminate(true);
	    } catch (Throwable throwable) {
		/* empty */
	    }
	}
	myDualProgressBars = _dualProgressBars;
	myShowStopButton = _showStopButton;
	myStopButton = new JButton(ResourceBundleUtils.getUIString("cancel"));
	setStopEnabled(false);
	setLayout(new BorderLayout());
	JPanel progressBarsPanel = new JPanel();
	GridLayout gridLayout = new GridLayout(0, 1, 0, 3);
	progressBarsPanel.setLayout(gridLayout);
	myOperationLabel.setHorizontalTextPosition(0);
	myTaskLabel.setHorizontalTextPosition(0);
	myStopButton.addActionListener(this);
	if (myDualProgressBars) {
	    progressBarsPanel.add(myOperationLabel);
	    progressBarsPanel.add(myOperationProgressBar);
	    progressBarsPanel.add(Box.createVerticalStrut(5));
	}
	progressBarsPanel.add(myTaskLabel);
	progressBarsPanel.add(myTaskProgressBar);
	add(progressBarsPanel, "Center");
	if (myShowStopButton) {
	    JPanel cancelPanel = new JPanel();
	    cancelPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0,
								  0));
	    cancelPanel.setLayout(new FlowLayout(2));
	    cancelPanel.add(myStopButton);
	    add(cancelPanel, "South");
	}
	setVisible(false);
    }
    
    public JButton getStopButton() {
	return myStopButton;
    }
    
    public void progressStarted() {
	myStopRequested = false;
	setVisible(true);
    }
    
    public boolean isInProgress() {
	return myVisible;
    }
    
    public void progressCompleted() {
	setVisible(false);
    }
    
    public void setWaitState(boolean _waitState) {
	/* empty */
    }
    
    public void setStopEnabled(boolean _stopEnabled) {
	myStopButton.setEnabled(_stopEnabled);
    }
    
    public void setStopRequested(boolean _stopRequested) {
	myStopRequested = _stopRequested;
	if (_stopRequested) {
	    setStopEnabled(false);
	    myStopButton
		.setText(ResourceBundleUtils.getUIString("cancel") + " ...");
	} else
	    myStopButton.setText(ResourceBundleUtils.getUIString("cancel"));
    }
    
    public boolean isStopRequested() {
	return myStopRequested;
    }
    
    public boolean isInteractive() {
	return true;
    }
    
    public void addStopListener(ActionListener _stopListener) {
	myStopButton.addActionListener(_stopListener);
    }
    
    public synchronized void setVisible(boolean _visible) {
	if (_visible) {
	    if (!myVisible)
		setStopRequested(false);
	} else {
	    myTaskLabel.setText("");
	    myOperationLabel.setText("");
	    myTaskProgressBar.setValue(0);
	    myOperationProgressBar.setValue(0);
	    setStopEnabled(false);
	}
	myVisible = _visible;
	myOperationProgressBar.setEnabled(_visible);
	myTaskProgressBar.setEnabled(_visible);
    }
    
    public synchronized void operationStarted(String _fmt) {
	myOperationLabel.setText(_fmt);
	myOperationLabel.repaint();
	setVisible(true);
    }
    
    public synchronized void operationUpdated(long _relativeProgress) {
	updateProgress(myOperationProgressBar, _relativeProgress);
    }
    
    public synchronized void operationUpdated(long _progress, long _total) {
	updateProgress(myOperationProgressBar, _progress, _total);
    }
    
    public synchronized void taskStarted(String _fmt) {
	myTaskLabel.setText(_fmt);
	myTaskLabel.repaint();
	setVisible(true);
    }
    
    public synchronized void taskUpdated(long _relativeProgress) {
	updateProgress(myTaskProgressBar, _relativeProgress);
    }
    
    public synchronized void taskUpdated(long _progress, long _total) {
	updateProgress(myTaskProgressBar, _progress, _total);
    }
    
    public void progressReported(long _current, long _maximum) {
	taskUpdated(_current, _maximum);
    }
    
    public void progressReported(String _description, long _current,
				 long _maximum) {
	taskStarted(_description);
	taskUpdated(_current, _maximum);
    }
    
    protected synchronized void updateProgress(JProgressBar _progressBar,
					       long _relativeValue) {
	_progressBar.setValue(_progressBar.getValue() + (int) _relativeValue);
    }
    
    protected synchronized void updateProgress(JProgressBar _progressBar,
					       long _value, long _maximum) {
	_progressBar.setMaximum((int) _maximum);
	_progressBar.setValue((int) _value);
    }
    
    public void actionPerformed(ActionEvent _event) {
	setStopRequested(true);
    }
}
