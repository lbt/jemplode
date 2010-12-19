/* AbstractConfirmationListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.text.MessageFormat;

import com.inzyme.progress.IConfirmationListener;

import org.jempeg.nodestore.IFIDNode;

public abstract class AbstractConfirmationListener
    implements IConfirmationListener
{
    public static final int OPTION_YES = 0;
    public static final int OPTION_NO = 1;
    public static final int OPTION_YES_TO_ALL = 2;
    public static final int OPTION_NO_TO_ALL = 3;
    public static final int OPTION_CANCEL = 4;
    public static final String[] CONFIRM_VALUES
	= { "Yes", "No", "Yes to All", "No to All", "Cancel" };
    private int myDefaultValue;
    private int mySelectedValue = -1;
    
    public AbstractConfirmationListener() {
	this(1);
    }
    
    public AbstractConfirmationListener(int _defaultValue) {
	myDefaultValue = _defaultValue;
    }
    
    public int confirm(String _message, Object _target) {
	return confirm(_message, null, _target);
    }
    
    protected int getDefaultValue() {
	return myDefaultValue;
    }
    
    public int confirm(String _message, String _checkboxMessage,
		       Object _target) {
	int confirmation;
	if (mySelectedValue == 2) {
	    if (_checkboxMessage != null && isCheckboxSelected())
		confirmation = 4;
	    else
		confirmation = 0;
	} else if (mySelectedValue == 3)
	    confirmation = 1;
	else if (mySelectedValue == 4)
	    confirmation = 3;
	else {
	    String name;
	    if (_target == null)
		name = "";
	    else if (_target instanceof IFIDNode)
		name = ((IFIDNode) _target).getTitle();
	    else
		name = _target.toString();
	    String message
		= MessageFormat.format(_message, new Object[] { name });
	    mySelectedValue = inputConfirmation(message, _checkboxMessage);
	    if (mySelectedValue == 0 || mySelectedValue == 2) {
		if (_checkboxMessage != null && isCheckboxSelected())
		    confirmation = 4;
		else
		    confirmation = 0;
	    } else if (mySelectedValue == 1 || mySelectedValue == 3)
		confirmation = 1;
	    else
		confirmation = 3;
	}
	return confirmation;
    }
    
    protected abstract boolean isCheckboxSelected();
    
    protected abstract int inputConfirmation(String string, String string_0_);
}
