/* ProgressDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

public class ProgressDialog implements ActionListener, IProgressListener
{
    private JFrame myFrame;
    private JDialog myDialog;
    private ProgressPanel myProgressPanel;
    
    public ProgressDialog(JFrame _frame, boolean _dualProgressBars) {
	this(_frame, _dualProgressBars, false);
    }
    
    public ProgressDialog(JFrame _frame, boolean _dualProgressBars,
			  boolean _indeterminate) {
	myProgressPanel
	    = new ProgressPanel(_dualProgressBars, true, _indeterminate);
	myFrame = _frame;
	setStopEnabled(false);
    }
    
    public ProgressPanel getProgressPanel() {
	return myProgressPanel;
    }
    
    public void setWaitState(boolean _wait) {
	myProgressPanel.setWaitState(_wait);
	final boolean finalWait = _wait;
	Runnable runnable = new Runnable() {
	    public void run() {
		if (myFrame != null)
		    myFrame.getGlassPane().setVisible(finalWait);
	    }
	};
	if (SwingUtilities.isEventDispatchThread())
	    runnable.run();
	else
	    SwingUtilities.invokeLater(runnable);
    }
    
    public void progressStarted() {
	setVisibleInBackground(true);
	myProgressPanel.progressStarted();
    }
    
    public boolean isInProgress() {
	if (myDialog != null && myDialog.isVisible())
	    return true;
	return false;
    }
    
    public void progressCompleted() {
	setVisibleInBackground(false);
	myProgressPanel.progressCompleted();
    }
    
    protected void createDialog() {
	myDialog = new JDialog(myFrame, "", false);
	JComponent comp = (JComponent) myDialog.getContentPane();
	comp.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
	comp.setLayout(new BorderLayout());
	comp.add(myProgressPanel, "Center");
	myDialog.pack();
	Dimension size = myDialog.getSize();
	myDialog.setSize(Math.max(400, size.width),
			 Math.max(100, size.height));
	DialogUtils.centerWindow(myDialog);
	myDialog.getContentPane().setCursor(Cursor.getPredefinedCursor(3));
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
	} else if (myDialog != null) {
	    myDialog.setVisible(false);
	    myDialog.dispose();
	    myDialog = null;
	    myProgressPanel.setVisible(_visible);
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
    
    public void progressReported(String _description, long _current,
				 long _maximum) {
	taskStarted(_description);
	taskUpdated(_current, _maximum);
    }
    
    public synchronized void setVisibleInBackground(boolean _visible) {
	setVisible(_visible);
    }
    
    public void actionPerformed(ActionEvent _event) {
	myProgressPanel.actionPerformed(_event);
    }
}
