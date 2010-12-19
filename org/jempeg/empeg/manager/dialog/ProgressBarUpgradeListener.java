/* ProgressBarUpgradeListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import javax.swing.JProgressBar;

import org.jempeg.empeg.protocol.UpgradeListenerIfc;

public class ProgressBarUpgradeListener implements UpgradeListenerIfc
{
    private JProgressBar myProgressBar;
    
    public ProgressBarUpgradeListener(JProgressBar _progressBar) {
	myProgressBar = _progressBar;
    }
    
    public void textLoaded(String _info, String _what, String _release,
			   String _version) {
	/* empty */
    }
    
    public void showStatus(int _section, int _operation, int _current,
			   int _maximum) {
	String operationStr;
	switch (_operation) {
	case 0:
	    operationStr = "Checking file...";
	    break;
	case 1:
	    operationStr = "Checked file...";
	    break;
	case 2:
	    operationStr = "Finding unit...";
	    break;
	case 3:
	    operationStr = "Checking ID...";
	    break;
	case 4:
	    operationStr = "Erasing...";
	    break;
	case 5:
	    operationStr = "Programming...";
	    break;
	case 6:
	    operationStr = "Restarting...";
	    break;
	case 7:
	    operationStr = "Finding Pump...";
	    break;
	case 8:
	    operationStr = "Pump Wait...";
	    break;
	case 9:
	    operationStr = "Pump Select...";
	    break;
	case 10:
	    operationStr = "Pumping...";
	    break;
	case 11:
	    operationStr = "Pumping Done.";
	    break;
	case 12:
	    operationStr = "Done.";
	    break;
	case 13:
	    operationStr = "Errors.";
	    break;
	default:
	    operationStr = "?";
	}
	myProgressBar.setString(operationStr);
	myProgressBar.setMaximum(_maximum);
	myProgressBar.setValue(_current);
    }
    
    public void showError(int _section, int _error) {
	/* empty */
    }
}
