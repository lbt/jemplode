/* FixedConfirmationListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import com.inzyme.progress.IConfirmationListener;

public class FixedConfirmationListener implements IConfirmationListener
{
    private int myConfirmationValue;
    
    public FixedConfirmationListener(int _confirmationValue) {
	myConfirmationValue = _confirmationValue;
    }
    
    public int confirm(Object _target) {
	return myConfirmationValue;
    }
    
    public int confirm(String _message, Object _target) {
	return myConfirmationValue;
    }
    
    public int confirm(String _message, String _checkboxMessage,
		       Object _target) {
	return myConfirmationValue;
    }
}
